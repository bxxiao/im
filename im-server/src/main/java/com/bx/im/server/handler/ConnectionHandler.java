package com.bx.im.server.handler;

import com.bx.im.cache.RedisService;
import com.bx.im.entity.Session;
import com.bx.im.server.ChannelContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 连接管理器，对Channel不可用、发生异常等情况进行处理
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private RedisService redisService;

    /*
     * 连接关闭，则离线处理
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffline(ctx);
    }

    /*
    * 客户端在指定时间内没有往服务器发送数据（即服务端没有发送读超过指定时间）
    * 视该连接断线，进行下线处理
    * */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (IdleState.READER_IDLE == state) {
                Session session = userOffline(ctx);
                System.out.println(session.toString() + " lose connection");
                ctx.close();
            }
        } else
            super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Session session = userOffline(ctx);
        cause.printStackTrace();
        System.out.println(session.toString() + " has error in connection");
        ctx.close();
    }

    /**
     * 用户下线，移除绑定在 Channel 的 Session ，并移除 ChannelContext 中的登记信息和 Redis 中的信息
     * 返回绑定的 Session
     * @param ctx
     * @return
     */
    private Session userOffline(ChannelHandlerContext ctx) {
        Session session = ctx.channel().attr(ChannelContext.SESSION_KEY).get();

        if (session != null) {
            Long uid = session.getId();
            ctx.channel().attr(ChannelContext.SESSION_KEY).set(null);
            ChannelContext.offLine(uid);
            redisService.userOffline(uid);
        }

        return session;
    }
}
