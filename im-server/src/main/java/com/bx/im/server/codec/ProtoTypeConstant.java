package com.bx.im.server.codec;

public class ProtoTypeConstant {
    /*
     * 0 - Login
     * 1 - ChatMsg
     * 2 - MsgAck
     * 3 - MsgAckedNotice
     * 4 - MsgRead
     * */
    public static final int LOGIN_TYPE = 0;
    public static final int CHATMSG_TYPE = 1;
    public static final int MSGACK_TYPE = 2;
    public static final int MSGACKNOTICE_TYPE = 3;
    public static final int MSGREAD_TYPE = 4;
}