package com.bx.im.server.handler;

import com.bx.im.cache.RedisService;
import com.bx.im.entity.Session;
import com.bx.im.entity.User;
import com.bx.im.proto.ChatMsgProto;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.proto.LoginProto;
import com.bx.im.server.ChannelContext;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.IMUtil;
import com.bx.im.util.JwtUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/*
* TODO:用户在线状态的登记改为在Redis中存储
* */
@ChannelHandler.Sharable
@Component
public class LoginHandler extends SimpleChannelInboundHandler<LoginProto.Login> {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginProto.Login login) throws Exception {
        long uid = login.getUid();
        String token = login.getToken();
        User user;
        if (JwtUtil.verifyJWT(token) && (user = userService.getById(uid)) != null) {
            Session session = new Session();
            session.setId(uid);
            session.setName(user.getName());
            session.setAvatar(user.getAvatar());
            session.setPhone(user.getPhone());

            /*
            * 在ChannelContext和Redis中同时保存在线用户信息
            * */
            ChannelContext.login(session, ctx.channel());
            redisService.userOnline(uid);
            ctx.pipeline().remove(this);
        } else {
            /*
            * 发送登录失败的回应包，关闭连接
            * */
            IMPacketProto.IMPacket packet = IMUtil.createLoginFailedPacket();
            ctx.channel().writeAndFlush(packet).addListener(future -> {
                ChannelFuture channelFuture = (ChannelFuture) future;
                if (channelFuture.isSuccess()) {
                    channelFuture.channel().close();
                }
            });
        }

    }

}
