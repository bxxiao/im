package com.bx.im.util;

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

    public static <T> CommonResult<T> success(ResultEnum result, T data) {
        return new CommonResult<T>(result.getCode(), result.getMsg(), data);
    }

    public static <T> CommonResult<T> failed(ResultEnum result) {
        return new CommonResult<>(result.getCode(), result.getMsg(), null);
    }

}
