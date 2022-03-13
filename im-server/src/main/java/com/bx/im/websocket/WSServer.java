package com.bx.im.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class WSServer {

    @Autowired
    private WSChannelInitializer channelInitializer;

    @PostConstruct
    public void startServer() {
        WSServer server = this;

        new Thread(() -> {
            try {
                server.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(channelInitializer);

        // bootstrap.bind("127.0.0.1", 9988).sync().channel()
        //         .closeFuture().sync();
        ChannelFuture bindFuture = bootstrap.bind("127.0.0.1", 9988).sync();
        if (bindFuture.isSuccess())
            log.info("Netty WebSocket 服务器已启动：127.0.0.1:9988");

        bindFuture.channel().closeFuture().addListener(future -> {
            if (future.isSuccess())
                log.info("服务器已关闭");
        });
    }

}
