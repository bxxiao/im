package com.bx.im.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.ApplyDTO;
import com.bx.im.dto.FriendDTO;
import com.bx.im.dto.GroupDTO;
import com.bx.im.dto.ItemDTO;
import com.bx.im.entity.*;
import com.bx.im.service.application.FriendHandleService;
import com.bx.im.service.bean.*;
import com.bx.im.util.IMConstant;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bx.im.util.SpringUtils.checkUser;
import static com.bx.im.util.SpringUtils.getUidInToken;

@Service
public class FriendHandleServiceImpl implements FriendHandleService {

    @Autowired
    private IApplyService applyService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IGroupInfoService groupInfoService;

    @Autowired
    private IUserFriendService userFriendService;

    @Autowired
    private IGroupUsersService groupUsersService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IChatSessionService chatSessionService;


    // TODO：可以不用uid这个参数
    @Override
    public List<ApplyDTO> listApplys(Long uid) {
        checkUser(uid);
        QueryWrapper<Apply> wrapper = new QueryWrapper<>();
        /*
        * 查询发给自己的
        * TODO：同时查询自己发出的申请【.or().eq("sender_uid", uid)】，并在前端作区分处理
        * */
        wrapper.eq("to_uid", uid);
        List<Apply> list = applyService.list(wrapper);
        if (list == null)
            return null;

        if (list.size() == 0)
            return new ArrayList<>();

        List<ApplyDTO> collect = list.stream().map(item -> toApplyDTO(item)).collect(Collectors.toList());
        return collect;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean dealApply(Integer id, Integer dealResult) {
        // 指定的处理类型错误
        if (dealResult == null || (dealResult != Apply.AGREED && dealResult != Apply.REJECTED))
            throw new IMException(ExceptionCodeEnum.NO_SUCH_TYPE);

        // 若对应数据库记录不存在，或已被处理，则返回false
        Apply apply = applyService.getById(id);
        if (apply == null || apply.getStatus() != Apply.DEALING)
            throw new IMException(ExceptionCodeEnum.APPLY_NOT_EXIST_OR_DEALED);

        // 不是发给自己的apply，不能操作
        Long curUid = getUidInToken();
        if (!apply.getToUid().equals(curUid))
            throw new IMException(ExceptionCodeEnum.OPERATION_ILLEGAL);

        // 同意添加好友、或接受群邀请、或统一入群申请
        if (dealResult == Apply.AGREED) {
            // 好友申请
            if (apply.getType() == Apply.FRIEND_APPLY) {
                UserFriend entity = new UserFriend();
                entity.setUid(apply.getSenderUid());
                entity.setFriendUid(apply.getToUid());
                entity.setCreateTime(LocalDateTime.now());
                entity.setModifiedTime(LocalDateTime.now());
                userFriendService.save(entity);
                /*
                 * 同意添加好友后，双方即互为好友，所以插入2条记录
                 * 另外，插入后会自动给bean对象设置主键id，所以这里要置null
                 * */
                entity.setUid(apply.getToUid());
                entity.setFriendUid(apply.getSenderUid());
                entity.setId(null);
                userFriendService.save(entity);
                /*
                * TODO：通过WebSocket发送通知？
                * */
            // 群聊邀请
            } else if (apply.getType() == Apply.GROUP_INVITATION) {
                GroupUsers groupUsers = new GroupUsers();
                groupUsers.setGroupId(apply.getGroupId());
                groupUsers.setUserId(apply.getToUid());
                groupUsersService.save(groupUsers);
                // redis中设置该用户的last_MsgSeq
                redisService.initLastSeq(apply.getToUid(), apply.getGroupId());
                /*
                * TODO：发一个新成员入群通知？
                * */
            // 入群申请
            } else if (apply.getType() == Apply.GROUP_APPLY) {
                QueryWrapper<GroupInfo> groupInfoWrapper = new QueryWrapper<>();
                groupInfoWrapper.eq("id", apply.getGroupId()).select("master_uid");
                GroupInfo groupInfo = groupInfoService.getOne(groupInfoWrapper);
                if (groupInfo == null)
                    throw new IMException(ExceptionCodeEnum.NO_SUCH_GROUP);
                // 是否是群主在进行操作
                if (!groupInfo.getMasterUid().equals(curUid))
                    throw new IMException(ExceptionCodeEnum.PERMISSION_DENIED_FOR_NOT_MASTER);
                // 添加新的群成员
                GroupUsers groupUsers = new GroupUsers();
                groupUsers.setGroupId(apply.getGroupId());
                groupUsers.setUserId(apply.getSenderUid());
                if (!groupUsersService.save(groupUsers))
                    throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
                // redis中设置该用户的last_MsgSeq
                redisService.initLastSeq(apply.getSenderUid(), apply.getGroupId());
            } else
                throw new IMException(ExceptionCodeEnum.NO_SUCH_TYPE);
        }

        UpdateWrapper<Apply> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set("status", dealResult);
        applyService.update(wrapper);
        return true;
    }

    @Override
    public List<FriendDTO> listFriends(Long uid, Boolean online) {
        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).select("friend_uid");
        /*
        * listObjs只会拿每条记录的第一个字段
        * */
        List<Object> friendIds = userFriendService.listObjs(wrapper);

        if (friendIds.size() == 0)
            return new ArrayList<>();

        QueryWrapper<User> wrapper1 = new QueryWrapper<>();
        wrapper1.in("id", friendIds).select("id", "name", "avatar", "intro");
        List<User> users = userService.list(wrapper1);
        List<FriendDTO> friendDTOS = users.stream().map(user -> {
            FriendDTO friendDTO = new FriendDTO();
            BeanUtils.copyProperties(user, friendDTO);
            // 从redis中查询用户是否在线
            if (online)
                friendDTO.setIsOnline(redisService.isUserOnline(friendDTO.getId()));
            return friendDTO;
        }).collect(Collectors.toList());

        return friendDTOS;
    }

    @Override
    public List<GroupDTO> listGroups(Long uid) {
        QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", uid).select("group_id");
        List<Object> groupIds = groupUsersService.listObjs(wrapper);
        List<GroupDTO> groupDTOS;

        if (groupIds.size() > 0) {
            QueryWrapper<GroupInfo> wrapper1 = new QueryWrapper<>();
            wrapper1.in("id", groupIds).select("id", "name", "avatar");
            List<GroupInfo> infoList = groupInfoService.list(wrapper1);
            groupDTOS = infoList.stream().map(groupInfo -> {
                GroupDTO dto = new GroupDTO();
                BeanUtils.copyProperties(groupInfo, dto);
                return dto;
            }).collect(Collectors.toList());
        } else
            groupDTOS = new ArrayList<>(0);

        return groupDTOS;
    }

    @Transactional
    @Override
    public void deleteGroupMember(Long uid, Long groupId, Long deleted) {
        /*
        * 判断uid是否是群主id
        * */
        if (!isMaster(groupId, uid))
            throw new IMException(ExceptionCodeEnum.PERMISSION_DENIED_FOR_NOT_MASTER);
        /*
        * 参数中的uid跟发出请求的用户（即uidInToken对应的用户）不一致，拒绝操作
        *   （若不进行该判断，则任何已登录用户都可以发出一个uid是群主的请求来删除群成员）
        * */
        checkUser(uid);

        if (deleted.equals(uid))
            throw new IMException(ExceptionCodeEnum.DENIED_OPERATION_FOR_GROUP_MASTER);

        // TODO：抽取公共代码（quitGroup中）
        QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId).eq("user_id", deleted);
        if (!groupUsersService.remove(wrapper))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
        // 移除在 USER_LAST_GMSG_SEQ_ 中对应的字段
        redisService.removeLastSeqKey(deleted, groupId);

        // 移除会话
        QueryWrapper<ChatSession> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", uid).eq("to_id", groupId).eq("type", IMConstant.GROUP_CHAT_TYPE);
        chatSessionService.remove(wrapper1);
    }

    @Transactional
    @Override
    public void deleteFriend(Long uid, Long friendUid) {
        checkUser(uid);

        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).eq("friend_uid", friendUid);
        // 若不是好友关系
        if (userFriendService.count(wrapper) <= 0)
            throw new IMException(ExceptionCodeEnum.NOT_FRIEND_RELATIONSHIP);

        userFriendService.remove(wrapper);
        // 两边关系互删
        wrapper.clear();
        wrapper.eq("uid", friendUid).eq("friend_uid", uid);
        userFriendService.remove(wrapper);


        // 移除会话，会话项同样互删
        QueryWrapper<ChatSession> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", uid).eq("to_id", friendUid).eq("type", IMConstant.SINGLE_CHAT_TYPE);
        chatSessionService.remove(wrapper1);

        wrapper1.clear();
        wrapper1.eq("user_id", friendUid).eq("to_id", uid).eq("type", IMConstant.SINGLE_CHAT_TYPE);
        chatSessionService.remove(wrapper1);
    }

    @Transactional
    @Override
    public void quitGroup(Long uid, Long groupId) {
        checkUser(uid);

        QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId).eq("user_id", uid);

        if (groupUsersService.count(wrapper) <= 0)
            throw new IMException(ExceptionCodeEnum.NOT_GROUP_MEMBER);

        // TODO：群主暂时不支持该操作（可以改为群主退出群聊时自动选择一个新群主）
        // TODO：group_users表加个时间字段
        if (isMaster(groupId, uid))
            throw new IMException(ExceptionCodeEnum.DENIED_OPERATION_FOR_GROUP_MASTER);

        if (!groupUsersService.remove(wrapper))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
        // 移除在 USER_LAST_GMSG_SEQ_ 中对应的字段
        redisService.removeLastSeqKey(uid, groupId);

        // 移除会话
        QueryWrapper<ChatSession> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", uid).eq("to_id", groupId).eq("type", IMConstant.GROUP_CHAT_TYPE);
        chatSessionService.remove(wrapper1);
    }

    @Override
    public void sendApply(Long targetId, Integer type) {
        // 只处理好友申请和入群申请
        if (type != Apply.FRIEND_APPLY && type != Apply.GROUP_APPLY)
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        Long curUid = getUidInToken();
        GroupInfo groupInfo = null;

        // 好友申请
        if (type == Apply.FRIEND_APPLY) {
            // 不能自己添加自己
            if (curUid != null && curUid.equals(targetId))
                throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

            // 判断指定好友是否存在
            QueryWrapper<User> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("id", targetId);
            if (userService.count(wrapper1) <= 0)
                throw new IMException(ExceptionCodeEnum.NO_SUCH_USER);

            // 是否已经是好友关系
            QueryWrapper<UserFriend> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("uid", curUid).eq("friend_uid", targetId);
            if (userFriendService.count(wrapper2) > 0)
                throw new IMException(ExceptionCodeEnum.HAD_IN_FRIEND_RELATIONSHIP);
        // 入群申请
        } else {
            // 群是否存在以及是否已经是群员
            QueryWrapper<GroupInfo> wrapper3 = new QueryWrapper<>();
            // 同时查出群主id，需要将其作为apply记录的to_uid
            wrapper3.eq("id", targetId).select("master_uid");
            groupInfo = groupInfoService.getOne(wrapper3);
            if (groupInfo == null)
                throw new IMException(ExceptionCodeEnum.NO_SUCH_GROUP);

            QueryWrapper<GroupUsers> wrapper4 = new QueryWrapper<>();
            wrapper4.eq("group_id", targetId).eq("user_id", curUid);
            if (groupUsersService.count(wrapper4) > 0)
                throw new IMException(ExceptionCodeEnum.HAD_BEEN_A_MEMBER);
        }

        // 是否已发出过申请并且还未处理
        QueryWrapper<Apply> wrapper5 = new QueryWrapper<>();
        Apply condition = new Apply();
        condition.setSenderUid(curUid);
        // 类型不同，targetId代表的含义不同
        if (type == Apply.FRIEND_APPLY)
            condition.setToUid(targetId);
        else {
            condition.setToUid(groupInfo.getMasterUid());
            condition.setGroupId(targetId);
        }

        condition.setType(type);
        /*
        * 这里状态限定为【处理中】，若是已同意，则在上个if中会抛异常
        * 若是已被拒绝，则可以再次添加
        * */
        condition.setStatus(Apply.DEALING);
        wrapper5.setEntity(condition);
        if (applyService.count(wrapper5) > 0)
            throw new IMException(ExceptionCodeEnum.HAD_SEND_APPLY);

        /*
        * 不存在则插入申请记录
        * */
        condition.setId(null);
        condition.setStatus(Apply.DEALING);
        condition.setTime(LocalDateTime.now());

        if (!applyService.save(condition))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @Override
    public List<ItemDTO> searchUserAndGroup(String keyword) {
        Long curUid = getUidInToken();
        // 当前用户好友id
        Set<Long> friendUidSet = getFriendUidSet(curUid);

        /*
         * 1. 查询用户
         * */
        QueryWrapper<User> wrapper1 = new QueryWrapper<>();
        wrapper1.like("name", keyword).or().like("phone", keyword).select("id", "name", "phone", "avatar");
        List<User> users = userService.list(wrapper1);
        List<ItemDTO> collect = users.stream()
                // 过滤掉自己跟好友
                .filter(user -> !user.getId().equals(curUid) && !friendUidSet.contains(user.getId()))
                .map(user -> userToItemDTO(user))
                .collect(Collectors.toList());

        /*
         * 2. 查询群
         * */
        QueryWrapper<GroupInfo> wrapper2 = new QueryWrapper<>();
        wrapper2.like("group_number", keyword).or().like("name", keyword)
                .select("id", "group_number", "name", "avatar");
        List<GroupInfo> groupInfos = groupInfoService.list(wrapper2);
        List<ItemDTO> groupInfoItems = groupInfos.stream()
                // TODO:过滤已加入的群
                .map(info -> groupInfoToItemDTO(info))
                .collect(Collectors.toList());

        collect.addAll(groupInfoItems);

        return collect;
    }

    private Set<Long> getFriendUidSet(Long uid) {
        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).select("friend_uid");
        List<Object> friendIds = userFriendService.listObjs(wrapper);
        // 将List转为Set
        Set<Long> idSet = friendIds.stream()
                .map(obj -> (Long) obj)
                .collect(Collectors.toSet());
        return idSet;
    }

    private ItemDTO userToItemDTO(User user) {
        ItemDTO dto = ItemDTO.builder()
                .type(1)
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .build();
        return dto;
    }

    private ItemDTO groupInfoToItemDTO(GroupInfo info) {
        ItemDTO dto = ItemDTO.builder()
                .type(2)
                .id(info.getId())
                .name(info.getName())
                .avatar(info.getAvatar())
                .groupNumber(info.getGroupNumber())
                .build();
        return dto;
    }



    /**
     * 指定用户是否是指定群的群主
     * @param groupId
     * @param uid
     * @return
     */
    private boolean isMaster(Long groupId, Long uid) {
        QueryWrapper<GroupInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id", groupId).select("master_uid");
        GroupInfo groupInfo = groupInfoService.getOne(wrapper);
        if (groupInfo == null)
            throw new IMException(ExceptionCodeEnum.NO_SUCH_GROUP);

        return groupInfo.getMasterUid().equals(uid);
    }

    private ApplyDTO toApplyDTO(Apply entity) {
        ApplyDTO dto = new ApplyDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setTime(entity.getTime().toString());
        /*
        * 查询发送申请者的头像，名字
        * */
        Long senderUid = entity.getSenderUid();
        QueryWrapper<User> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("id", senderUid).select("name", "avatar");
        User user = userService.getOne(wrapper1);
        dto.setSenderName(user.getName());
        dto.setSenderAvatar(user.getAvatar());

        /*
        * 若是群邀请或入群申请，查询群名
        * */
        Integer type = dto.getType();
        if (type.equals(Apply.GROUP_INVITATION) || type.equals(Apply.GROUP_APPLY)) {
            QueryWrapper<GroupInfo> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("id", dto.getGroupId()).select("name");
            GroupInfo info = groupInfoService.getOne(wrapper2);
            if (info == null)
                throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
            dto.setGroupName(info.getName());
        }

        return dto;
    }
}
