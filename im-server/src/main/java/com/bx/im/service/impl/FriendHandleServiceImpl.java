package com.bx.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bx.im.dto.ApplyDTO;
import com.bx.im.entity.*;
import com.bx.im.service.FriendHandleService;
import com.bx.im.service.bean.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public boolean dealApply(Integer id, Integer dealResult) {
        // 若对应数据库记录不存在，或已被处理，则返回false
        Apply apply = applyService.getById(id);
        if (apply == null || apply.getStatus() != Apply.DEALING)
            return false;

        // 指定的处理类型错误
        /*
        * TODO：在controller进行参数验证
        * */
        if (dealResult == null || (dealResult != Apply.AGREED && dealResult != Apply.REJECTED))
            return false;

        /*
        * TODO：事务 + 全局异常处理
        * */
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
                return false;
        }

        UpdateWrapper<Apply> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set("status", dealResult);
        applyService.update(wrapper);
        return true;
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
