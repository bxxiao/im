package com.bx.im.util.exception;

/*
* 系统业务逻辑抛出的各种异常枚举，包含状态码和异常信息
* */
public enum  ExceptionCodeEnum implements IExceptionCode {

    SERVER_ERROR(400, "服务器出现错误，请稍后再试"),
    REQUEST_ERROR(4001, "请求处理错误，请稍后再试"),
    NO_SUCH_TYPE(1001, "指定的类型错误"),
    LOGIN_FAILED(1002, "登录失败，密码或账号错误");


    private Integer code;
    private String msg;

    ExceptionCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
