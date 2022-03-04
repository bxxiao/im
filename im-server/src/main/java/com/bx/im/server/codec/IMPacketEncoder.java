package com.bx.im.server.codec;

import com.bx.im.proto.IMPacketProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * 将 IMPacket 对象转换为 BinaryWebSocketFrame
 */
public class IMPacketEncoder extends MessageToMessageEncoder<IMPacketProto.IMPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IMPacketProto.IMPacket msg, List<Object> out) throws Exception {
        /*
        * 把IMPacket对象序列化，然后创建二进制帧发送数据
        * */
        byte[] data = msg.toByteArray();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(data.length);
        buf.writeBytes(data);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buf);
        out.add(frame);
    }
}
