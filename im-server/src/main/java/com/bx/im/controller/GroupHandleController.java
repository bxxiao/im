package com.bx.im.controller;

import com.bx.im.dto.GroupCreateParam;
import com.bx.im.dto.GroupInviteParam;
import com.bx.im.service.application.GroupHandleService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/group/")
public class GroupHandleController {

    @Autowired
    private GroupHandleService groupHandleService;

    /**
     * 请求从content-type是表单类型，不是json，所以不用使用@RequestBody
     * @param param
     * @return
     */
    @PostMapping("/create")
    public CommonResult createGroup(GroupCreateParam param) {
        if (param.getMasterUid() == null || param.getMembers() == null
                || param.getMembers().size() == 0
                || param.getGroupName() == null || param.getGroupName().trim().equals(""))
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        groupHandleService.createGroup(param.getMasterUid(), param.getMembers(), param.getGroupName());
        return CommonResult.success();
    }

    @PostMapping("/invite")
    public CommonResult inviteFriends(GroupInviteParam param) {
        groupHandleService.invite(param.getFriendIds(), param.getGroupId());
        return CommonResult.success();
    }

    @PostMapping("/dissolve")
    public CommonResult dissolveGroup(Long masterUid, Long groupId) {
        groupHandleService.dissolveGroup(masterUid, groupId);
        return CommonResult.success();
    }
}
