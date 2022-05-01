package com.bx.im.controller;

import com.bx.im.dto.UserDTO;
import com.bx.im.service.application.ChatService;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ChatService chatService;

    @PostMapping("/login")
    public CommonResult<UserDTO> login(String phone, String password) {
        UserDTO dto = userService.login(phone, password);
        if (dto == null)
            return CommonResult.error(ExceptionCodeEnum.LOGIN_FAILED);
        else
            return CommonResult.success(dto);
    }

    @GetMapping("/info")
    public CommonResult<UserDTO> getUserInfo(Long uid) {
        UserDTO info = userService.getUserInfo(uid);
        if (info != null)
            return CommonResult.success(info);
        else
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @PostMapping("/registry")
    public CommonResult doRegistry(String username, String phone, String password) {
        userService.doRegistry(username, phone, password);
        return CommonResult.success();
    }

    @PostMapping("/edit")
    public CommonResult editInfo(Long uid, String name, String phone, String intro) {
        userService.editInfo(uid, name, phone, intro);
        return CommonResult.success();
    }

    @PostMapping("/editPwd")
    public CommonResult editPwd(Long uid, String oldPwd, String newPwd) {
        userService.editPwd(uid, oldPwd, newPwd);
        return CommonResult.success();
    }

}
