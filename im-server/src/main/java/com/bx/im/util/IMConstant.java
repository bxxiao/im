package com.bx.im.util;

public class IMConstant {
    /*
    * 除了 ChatMsgProto中的type是 0-单聊；1-群聊
    * 其它地方都是 1-单聊；2-群聊。。。
    * */
    public static final int SINGLE_CHAT_TYPE = 1;
    public static final int GROUP_CHAT_TYPE = 2;

    /*
     * Proto类型
     * 0 - Login
     * 1 - ChatMsg
     * 2 - MsgAck
     * 3 - MsgAckedNotice
     * 4 - MsgRead
     *
     * 401 - data无内容，表示登录时提交的token错误，发给客户端后channel关闭
     * 111 - ping包，data无内容
     * 222 - pong包，data无内容
     * */
    public static final int LOGIN_PROTOBUF_TYPE = 0;
    public static final int CHATMSG_PROTOBUF_TYPE = 1;
    public static final int MSGACK_PROTOBUF_TYPE = 2;
    public static final int MSGACKNOTICE_PROTOBUF_TYPE = 3;
    public static final int MSGREAD_PROTOBUF_TYPE = 4;
    public static final int PING_PROTOBUF_TYPE = 111;
    public static final int PONG_PROTOBUF_TYPE = 222;
    public static final int LOGIN_TOKEN_ERROR_PROTOBUF_TYPE = 401;
}
