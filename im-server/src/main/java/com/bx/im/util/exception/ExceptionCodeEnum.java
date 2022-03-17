package com.bx.im.util.exception;

/*
* 系统业务逻辑抛出的各种异常枚举，包含状态码和异常信息
* */
public enum  ExceptionCodeEnum implements IExceptionCode {

    SERVER_ERROR(400, "服务器出现错误，请稍后再试"),
    REQUEST_ERROR(4001, "请求处理错误，请稍后再试"),
    NO_SUCH_TYPE(1001, "指定的类型错误"),
    LOGIN_FAILED(1002, "登录失败，密码或账号错误"),
    APPLY_NOT_EXIST_OR_DEALED(1003, "申请不存在，或已被处理"),
    NO_SUCH_GROUP(1004, "该群不存在"),
    PARAM_ERROR(1005, "参数值错误"),
    PERMISSION_DENIED_FOR_NOT_MASTER(1006, "不是群主，没有权限操作"),
    LOGIN_ILLEGAL(1007, "非法登录"),
    DENIED_OPERATION_FOR_GROUP_MASTER(1007, "不能对群主执行该操作"),
    OPERATION_ILLEGAL(1008, "非法操作"),
    NOT_FRIEND_RELATIONSHIP(1009, "该用户不是逆的好友"),
    NOT_GROUP_MEMBER(1010, "你不是该群成员"),
    NO_SUCH_USER(1011, "该用户不存在"),
    HAD_IN_FRIEND_RELATIONSHIP(1012, "已经是好友关系"),
    HAD_SEND_APPLY(1013, "已发出申请，请等待处理"),
    HAD_BEEN_A_MEMBER(1014, "已经是该群成员"),
    DENIED_FOR_NO_LOGIN(1015, "未登录");


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
