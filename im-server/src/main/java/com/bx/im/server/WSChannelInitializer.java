package com.bx.im.server;

import com.bx.im.server.codec.IMPacketEncoder;
import com.bx.im.server.handler.ChatMsgHandler;
import com.bx.im.server.handler.LoginHandler;
import com.bx.im.server.codec.WSFrameDecoder;
import com.bx.im.server.handler.MsgReadHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class WSChannelInitializer extends ChannelInitializer<NioSocketChannel> implements ApplicationContextAware {

    /*
    * 如果Handler不能共用，则需要设置为多例，但这里使用@Autowired注入并不能生效
    * 当WSChannelInitializer对象被创建时，会注入Handler，后续会一直使用该bean
    * 每次创建新的pipeline并不会从spring容器中获取一个新的bean
    * 可以通过Aware接口注入ApplicationContext，创建Handler时从中获取
    * */

    // @Autowired
    // private ChatMsgHandler chatMsgHandler;

    @Autowired
    private LoginHandler loginHandler;

    private ApplicationContext appContext;

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        /*
        * Http编解码器；作用是为了能完成WebSocket协议的握手过程
        * */
        pipeline.addLast(new HttpServerCodec());
        // 可以将一个Http请求的多个部分的数据组合为一个对象
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new ChunkedWriteHandler());

        // Netty WebSocket协议处理器
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 10 * 1024));
        pipeline.addLast(new WSFrameDecoder());
        pipeline.addLast(this.loginHandler);
        pipeline.addLast(appContext.getBean(ChatMsgHandler.class));
        pipeline.addLast(appContext.getBean(MsgReadHandler.class));

        pipeline.addLast(new IMPacketEncoder());

        // pipeline.addLast(new WebSocketFrameDemoHandler());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
