package com.bx.im.util.exception;

public class IMException extends RuntimeException {
    private Integer code;
    private String msg;

    public IMException(IExceptionCode enumObj) {
        this.code = enumObj.getCode();
        this.msg = enumObj.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

    @Override
    public String toString() {
        return "【" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '】';
    }
}
