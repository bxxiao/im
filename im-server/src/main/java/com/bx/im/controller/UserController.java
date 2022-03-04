package com.bx.im.controller;

import com.bx.im.dto.UserDTO;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public CommonResult<UserDTO> login(String phone, String password) {
        UserDTO dto = userService.login(phone, password);
        if (dto == null)
            return CommonResult.failed(ResultEnum.LOGIN_FAILED);
        else
            return CommonResult.success(ResultEnum.SUCCESS, dto);
    }
}
