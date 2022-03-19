package com.bx.im.service.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.entity.Apply;
import com.bx.im.entity.GroupInfo;
import com.bx.im.entity.GroupUsers;
import com.bx.im.service.application.GroupHandleService;
import com.bx.im.service.bean.*;
import com.bx.im.util.IMConstant;
import com.bx.im.util.SpringUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

        /*
        * 发送申请（群聊邀请类型）
        * */
        List<Apply> applies = members.stream().map(id -> {
            Apply apply = new Apply();
            apply.setSenderUid(masterUid);
            apply.setToUid(id);
            apply.setGroupId(groupId);
            apply.setType(Apply.GROUP_INVITATION);
            apply.setStatus(Apply.DEALING);
            apply.setTime(LocalDateTime.now());
            return apply;
        }).collect(Collectors.toList());

        applyService.saveBatch(applies);

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
