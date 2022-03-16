package com.bx.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.ApplyDTO;
import com.bx.im.dto.FriendDTO;
import com.bx.im.dto.GroupDTO;
import com.bx.im.entity.*;
import com.bx.im.service.FriendHandleService;
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
import java.util.stream.Collectors;

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


    @Override
    public List<ApplyDTO> listApplys(Long uid) {
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
        // 若对应数据库记录不存在，或已被处理，则返回false
        Apply apply = applyService.getById(id);
        if (apply == null || apply.getStatus() != Apply.DEALING)
            throw new IMException(ExceptionCodeEnum.APPLY_NOT_EXIST_OR_DEALED);

        // 指定的处理类型错误
        /*
        * TODO：在controller进行参数验证
        * */
        if (dealResult == null || (dealResult != Apply.AGREED && dealResult != Apply.REJECTED))
            throw new IMException(ExceptionCodeEnum.NO_SUCH_TYPE);

        // 同意添加好友（或接收邀请）
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
            } else if (apply.getType() == Apply.GROUP_APPLY) {
                GroupUsers groupUsers = new GroupUsers();
                groupUsers.setGroupId(apply.getGroupId());
                groupUsers.setUserId(apply.getToUid());
                groupUsersService.save(groupUsers);
                /*
                * TODO：发一个新成员入群通知？
                * */
            } else
                throw new IMException(ExceptionCodeEnum.NO_SUCH_TYPE);
        }

        UpdateWrapper<Apply> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set("status", dealResult);
        applyService.update(wrapper);
        return true;
    }

    @Override
    public List<FriendDTO> listFriends(Long uid) {
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

        QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId).eq("user_id", deleted);
        if (!groupUsersService.remove(wrapper))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);

    }

    @Override
    public void deleteFriend(Long uid, Long friendUid) {
        checkUser(uid);

        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).eq("friend_uid", friendUid);
        // 若不是好友关系
        if (userFriendService.count(wrapper) <= 0)
            throw new IMException(ExceptionCodeEnum.NOT_FRIEND_RELATIONSHIP);

        if (!userFriendService.remove(wrapper))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @Override
    public void quitGroup(Long uid, Long groupId) {
        checkUser(uid);

        QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId).eq("user_id", uid);

        if (groupUsersService.count(wrapper) <= 0)
            throw new IMException(ExceptionCodeEnum.NOT_GROUP_MEMBER);

        // TODO：群主暂时不支持该操作（可以改为群主退出群聊时自动选择一个新群主）
        // TODO：group_users表加个事件字段
        if (isMaster(groupId, uid))
            throw new IMException(ExceptionCodeEnum.DENIED_OPERATION_FOR_GROUP_MASTER);

        if (!groupUsersService.remove(wrapper))
            throw new IMException(ExceptionCodeEnum.REQUEST_ERROR);
    }

    /**
     * 检查发出请求的用户（token中的uid）跟请求中的用户id参数是否一致，不一致则抛出异常
     * @param uid
     */
    private void checkUser(Long uid) {
        HttpServletRequest request =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Long uidInToken = (Long) request.getAttribute(IMConstant.TOKEN_UID_KEY);
        if (uidInToken == null || !uidInToken.equals(uid))
            throw new IMException(ExceptionCodeEnum.OPERATION_ILLEGAL);
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
        * 若是群邀请，查询群名
        * */
        if (dto.getType().equals(Apply.GROUP_APPLY)) {
            QueryWrapper<GroupInfo> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("id", dto.getGroupId()).select("name");
            GroupInfo info = groupInfoService.getOne(wrapper2);
            dto.setGroupName(info.getName());
        }

        return dto;
    }
}
