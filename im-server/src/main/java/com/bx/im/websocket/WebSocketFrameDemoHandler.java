package com.bx.im.websocket;

import com.bx.im.proto.demo.Outter;
import com.google.protobuf.Parser;
import com.google.protobuf.util.JsonFormat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Date;

public class WebSocketFrameDemoHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
        // BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
        // ByteBuf content = binaryWebSocketFrame.content();
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            System.out.println("收到客户端消息：【" + text + "】");

            // String response = "【服务器时间 " + new Date() + "】服务器收到了消息：" + text;
            // byte[] bytes = response.getBytes();
            // ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(bytes.length);
            // buf.writeBytes(bytes);
            // BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buf);
            // context.channel().writeAndFlush(binaryWebSocketFrame);
            String response = "【服务器时间 " + new Date() + "】服务器收到了消息：" + text;
            TextWebSocketFrame respFrame = new TextWebSocketFrame(response);
            context.channel().writeAndFlush(respFrame);
        } else if (frame instanceof PingWebSocketFrame) {
            // 浏览器的JavaScript中没有提供发送PING帧的API
            System.out.println("收到PING帧");
            context.channel().writeAndFlush(new PongWebSocketFrame());
        } else if (frame instanceof BinaryWebSocketFrame) {
            ByteBuf content = frame.content();
            int len = content.capacity();
            byte[] bytes = new byte[len];
            content.readBytes(bytes);
            //
            // Outter.Class aClass = Outter.Class.newBuilder().mergeFrom(bytes).build();
            // System.out.println(JsonFormat.printer().print(aClass));

            Parser<Outter.WSRequest> parser = Outter.WSRequest.parser();

            Outter.WSRequest wsRequest = Outter.WSRequest.parseFrom(bytes);
            System.out.println("type: " + wsRequest.getType());
            System.out.println("token: " + wsRequest.getToken());

            byte[] classBytes = wsRequest.getData().toByteArray();
            Outter.Class inClass = Outter.Class.parseFrom(classBytes);
            System.out.println(JsonFormat.printer().print(inClass));

            /*
            * 根据byte数组创建ByteString：
            * */
            // Outter.WSRequest wsRequest = Outter.WSRequest.newBuilder().setData(ByteString.copyFrom(new byte[]{1, 1, 1, 1, 1, 1})).build();
            // byte[] bytes = wsRequest.getData().toByteArray();
        }
    }
}
