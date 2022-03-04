package com.bx.im.util;

public enum ResultEnum {
    SUCCESS(200, "操作成功"),
    FAILD(400, "操作失败"),
    LOGIN_FAILED(300, "账号或密码错误");
    ;
    private long code;
    private String msg;

    ResultEnum(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
