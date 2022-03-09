package com.bx.im.server.codec;

public class ProtoTypeConstant {
    /*
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
    public static final int LOGIN_TYPE = 0;
    public static final int CHATMSG_TYPE = 1;
    public static final int MSGACK_TYPE = 2;
    public static final int MSGACKNOTICE_TYPE = 3;
    public static final int MSGREAD_TYPE = 4;
    public static final int PING_PACKET = 111;
    public static final int PONG_PACKET = 222;
}
