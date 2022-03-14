package com.bx.im.controller;

import com.bx.im.dto.ApplyDTO;
import com.bx.im.dto.FriendDTO;
import com.bx.im.dto.GroupDTO;
import com.bx.im.service.FriendHandleService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friend/")
public class FriendController {

    @Autowired
    private FriendHandleService friendHandleService;

    @GetMapping("/listApply")
    public CommonResult<List<ApplyDTO>> listApplys(Long uid) {
        List<ApplyDTO> applyDTOS = friendHandleService.listApplys(uid);
        if (applyDTOS == null)
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
        else
            return CommonResult.success(applyDTOS);
    }

    @PostMapping("/dealApply")
    public CommonResult dealApply(Integer applyId, Integer dealResult) {
        if (friendHandleService.dealApply(applyId, dealResult))
            return CommonResult.success(null);
        else
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @GetMapping("/listFriends")
    public CommonResult<List<FriendDTO>> listFriends(Long uid) {
        List<FriendDTO> dtos = friendHandleService.listFriends(uid);
        return CommonResult.success(dtos);
    }

    @GetMapping("/listGroups")
    public CommonResult<List<GroupDTO>> listGroups(Long uid) {
        List<GroupDTO> dtos = friendHandleService.listGroups(uid);
        return CommonResult.success(dtos);
    }

}
