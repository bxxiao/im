package com.bx.im.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.entity.Apply;
import com.bx.im.entity.ChatSession;
import com.bx.im.entity.GroupInfo;
import com.bx.im.entity.GroupUsers;
import com.bx.im.service.application.GroupHandleService;
import com.bx.im.service.bean.*;
import com.bx.im.util.IMConstant;
import com.bx.im.util.SpringUtils;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupHandleServiceImpl implements GroupHandleService {
    // 群号长度
    private static final int GROUP_NUMBER_LENGTH = 10;

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
    private IChatSessionService chatSessionService;

    @Autowired
    private RedisService redisService;

    @Transactional
    @Override
    public void createGroup(Long masterUid, List<Long> members, String groupName) {
        SpringUtils.checkUser(masterUid);
        /*
        * 插入group_info记录
        * */
        GroupInfo entity = new GroupInfo();
        entity.setGroupNumber(getUniqueGroupNumber());
        entity.setName(groupName);
        entity.setAvatar("");
        entity.setMasterUid(masterUid);
        entity.setMemberNum(1L);
        entity.setCreateTime(LocalDateTime.now());

        groupInfoService.save(entity);
        Long groupId = entity.getId();
        // 发送群邀请
        sendGroupInvatations(masterUid, members, groupId);

        /*
        * 插入群成员（群主）
        * */
        GroupUsers groupUsers = new GroupUsers();
        groupUsers.setGroupId(groupId);
        groupUsers.setUserId(masterUid);
        groupUsersService.save(groupUsers);

        /*
        * 设置群消息序列号自增key
        * 设置用户在该群的last_msgSeq
        * */
        redisService.initGroupSeqKey(groupId);
        redisService.initLastSeq(masterUid, groupId);
    }

    @Override
    public void invite(List<Long> friendIds, Long groupId) {
        Long uid = SpringUtils.getUidInToken();
        sendGroupInvatations(uid, friendIds, groupId);
    }

    @Transactional
    @Override
    public void dissolveGroup(Long masterUid, Long groupId) {
        SpringUtils.checkUser(masterUid);
        QueryWrapper<GroupInfo> infoWrapper = new QueryWrapper<>();
        infoWrapper.eq("id", groupId).select("master_uid");
        GroupInfo groupInfo = groupInfoService.getOne(infoWrapper);
        // 群是否存在
        if (groupInfo == null)
            throw new IMException(ExceptionCodeEnum.NO_SUCH_GROUP);

        // 当前操作用户是否是群主
        if (!groupInfo.getMasterUid().equals(masterUid))
            throw new IMException(ExceptionCodeEnum.PERMISSION_DENIED_FOR_NOT_MASTER);

        /*
        * 删除群信息
        * TODO：这一步暂且不删，删除群聊后，在获取申请列表时需要查询群名，会导致NPE
        *  2种解决方法：
        *   apply增加一个冗余字段存储群名
        *   对group_info的记录删除使用逻辑删除
        * */
        // groupInfoService.removeById(groupId);

        QueryWrapper<GroupUsers> groupUsersWrapper = new QueryWrapper<>();
        groupUsersWrapper.eq("group_id", groupId).select("user_id");
        List<Long> memberIds = groupUsersService.listObjs(groupUsersWrapper)
                .stream()
                .map(obj -> (Long)obj)
                .collect(Collectors.toList());

        // 移除所有群成员
        groupUsersService.remove(groupUsersWrapper);

        /*
        * TODO：
        *   暂定为把所有相关会话删除，可以不删除，当用户打开会话时提示群已解散；
        *   发送一条通知（新建notice表）
        * */
        //删除会话
        QueryWrapper<ChatSession> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.eq("to_id", groupId).eq("type", IMConstant.GROUP_CHAT_TYPE);
        chatSessionService.remove(sessionWrapper);

        // redis中删除该群相关数据
        redisService.dissolveGroup(groupId, memberIds);
    }

    /**
     * 发出入群邀请
     * @param sender 邀请者
     * @param toIds 被邀请者
     * @param groupId 群id
     */
    private void sendGroupInvatations(Long sender, List<Long> toIds, Long groupId) {
        /*
        * 发送申请（群聊邀请类型）
        * */
        List<Apply> applies = toIds.stream().map(id -> {
            Apply apply = new Apply();
            apply.setSenderUid(sender);
            apply.setToUid(id);
            apply.setGroupId(groupId);
            apply.setType(Apply.GROUP_INVITATION);
            apply.setStatus(Apply.DEALING);
            apply.setTime(LocalDateTime.now());
            return apply;
        }).collect(Collectors.toList());

        applyService.saveBatch(applies);
    }

    /**
     * 随机生成一个唯一的群号
     * @return
     */
    private String getUniqueGroupNumber() {
        String groupNumber = SpringUtils.randomNumberSeq(GROUP_NUMBER_LENGTH);
        QueryWrapper<GroupInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("group_number", groupNumber);
        while (groupInfoService.count(wrapper) > 0) {
            groupNumber = SpringUtils.randomNumberSeq(GROUP_NUMBER_LENGTH);
            wrapper.clear();
            wrapper.eq("group_number", groupNumber);
        }

        return groupNumber;
    }
}
