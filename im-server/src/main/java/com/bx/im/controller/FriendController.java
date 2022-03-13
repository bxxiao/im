package com.bx.im.controller;

import com.bx.im.dto.ApplyDTO;
import com.bx.im.service.FriendHandleService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.ResultEnum;
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

    @GetMapping("/list")
    public CommonResult<List<ApplyDTO>> listApplys(Long uid) {
        List<ApplyDTO> applyDTOS = friendHandleService.listApplys(uid);
        if (applyDTOS == null)
            return CommonResult.failed(ResultEnum.FAILD);
        else
            return CommonResult.success(ResultEnum.SUCCESS, applyDTOS);
    }

    @PostMapping("/dealApply")
    public CommonResult dealApply(Integer applyId, Integer dealResult) {
        if (friendHandleService.dealApply(applyId, dealResult))
            return CommonResult.success(ResultEnum.SUCCESS, null);
        else
            return CommonResult.failed(ResultEnum.FAILD);
    }

}
