package com.bx.im.util;

import com.bx.im.proto.IMPacketProto;
import com.bx.im.websocket.ChannelContext;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;

public class WSUtils {

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
                .setType(IMConstant.LOGIN_TOKEN_ERROR_PROTOBUF_TYPE)
                .build();

        return packet;
    }

    public static long getOnlineUserId(Channel channel) {
        return channel.attr(ChannelContext.SESSION_KEY).get().getId();
    }

    public static IMPacketProto.IMPacket createPongPacket() {
        IMPacketProto.IMPacket packet = IMPacketProto.IMPacket.newBuilder()
                .setType(IMConstant.PONG_PROTOBUF_TYPE)
                .build();

        return packet;
    }
}
