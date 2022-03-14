package com.bx.im.util;

import com.bx.im.util.exception.IExceptionCode;
import lombok.Data;

/**
 * 封装返回结果
 * @param <T> 数据类型
 */
@Data
public class CommonResult<T> {
    /**
     * 状态码
     */
    private long code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 数据封装
     */
    private T data;

    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResult(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(200, "操作成功", data);
    }

    public static <T> CommonResult error(long code, String message) {
        CommonResult result = new CommonResult(code, message);
        return result;
    }

    public static CommonResult error(IExceptionCode exceptionCode) {
        return new CommonResult(exceptionCode.getCode(), exceptionCode.getMsg());
    }
}
