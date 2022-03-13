package com.bx.im.websocket.codec;

import com.bx.im.proto.*;
import com.bx.im.util.IMConstant;
import com.bx.im.util.IMUtil;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSFrameDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    // 存储ProtoBuf类型的解析器，用于反序列化
    private Map<Integer, Parser> parserMap;

    {
        parserMap = new HashMap<>();
        /*
        * 0 - Login
        * 1 - ChatMsg
        * 2 - MsgAck
        * 3 - MsgAckedNotice
        * 4 - MsgRead
        * 401 - data无内容，表示登录时提交的token错误，发给客户端后channel关闭
        * */
        parserMap.put(IMConstant.LOGIN_PROTOBUF_TYPE, LoginProto.Login.parser());
        parserMap.put(IMConstant.CHATMSG_PROTOBUF_TYPE, ChatMsgProto.ChatMsg.parser());
        parserMap.put(IMConstant.MSGACK_PROTOBUF_TYPE, MsgAckProto.MsgAck.parser());
        parserMap.put(IMConstant.MSGACKNOTICE_PROTOBUF_TYPE, MsgAckedNoticeProto.MsgAckedNotice.parser());
        parserMap.put(IMConstant.MSGREAD_PROTOBUF_TYPE, MsgReadProto.MsgRead.parser());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (msg instanceof BinaryWebSocketFrame)
            handleBinaryFrame(ctx, (BinaryWebSocketFrame) msg, out);
    }

    /*
    * 从WebSocket二进制帧中拿出字节，解析为IMPacket，再根据其type获取对应Proto类型的解析器，
    * 解析（反序列化）后将其放给下一个Handler
    * */
    private void handleBinaryFrame(ChannelHandlerContext ctx, BinaryWebSocketFrame frame,
                                   List<Object> out) throws InvalidProtocolBufferException {
        ByteBuf content = frame.content();
        int len = content.capacity();
        byte[] bytes = new byte[len];
        content.readBytes(bytes);

        IMPacketProto.IMPacket packet = IMPacketProto.IMPacket.parseFrom(bytes);
        int packetType = packet.getType();
        // 心跳包处理
        if (packetType == IMConstant.PING_PROTOBUF_TYPE) {
            IMPacketProto.IMPacket pongPacket = IMUtil.createPongPacket();
            ctx.channel().writeAndFlush(pongPacket);
            return;
        }

        ByteString data = packet.getData();

        Parser parser = parserMap.get(packetType);
        Object obj = parser.parseFrom(data);
        out.add(obj);
    }

}
