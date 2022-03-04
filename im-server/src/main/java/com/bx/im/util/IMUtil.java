package com.bx.im.util;

import com.bx.im.proto.IMPacketProto;
import com.bx.im.server.ChannelContext;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class IMUtil {
    public static final int LOGIN_TYPE = 0;
    public static final int CHATMSG_TYPE = 1;
    public static final int MSGACK_TYPE = 2;
    public static final int MSGACKEDNOTICE_TYPE = 3;
    public static final int LOGIN_TOKEN_ERROR_TYPE = 401;

    public static IMPacketProto.IMPacket createIMPacket(int type, Long uid, GeneratedMessageV3 data) {
        IMPacketProto.IMPacket packet = IMPacketProto.IMPacket.newBuilder()
                .setType(type)
                // .setUid(uid)
                .setData(data.toByteString())
                .build();

        return packet;
    }

    public static IMPacketProto.IMPacket createLoginFailedPacket() {
        IMPacketProto.IMPacket packet = IMPacketProto.IMPacket.newBuilder()
                .setType(LOGIN_TOKEN_ERROR_TYPE)
                .build();

        return packet;
    }

    public static long getOnlineUserId(Channel channel) {
        return channel.attr(ChannelContext.SESSION_KEY).get().getId();
    }
}
