package com.bx.im.config;

import com.bx.im.util.CommonResult;
import com.bx.im.util.SpringUtils;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* TODO：添加日志
* */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IMException.class)
    @ResponseBody
    public CommonResult handleIMException(HttpServletRequest request, HttpServletResponse response, IMException exception) {
        Long uid = SpringUtils.getUidInToken();
        System.out.println("捕获到异常：【{uid-" + uid + "} - " + request.getRequestURL() + "】- " + exception.toString());
        /*
        * TODO:用更好的方法打印异常位置？
        * */
        // exception.printStackTrace();
        return CommonResult.error(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        exception.printStackTrace();
        return CommonResult.error(ExceptionCodeEnum.SERVER_ERROR);
    }
}
