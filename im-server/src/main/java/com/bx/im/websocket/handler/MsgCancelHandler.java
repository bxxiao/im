package com.bx.im.websocket.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.entity.FriendMsg;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.proto.MsgCancelProto;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.util.IMConstant;
import com.bx.im.util.WSUtils;
import com.bx.im.websocket.ChannelContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsgCancelHandler extends SimpleChannelInboundHandler<MsgCancelProto.MsgCancel> {

    @Autowired
    private IFriendMsgService friendMsgService;

    @Autowired
    private RedisService redisService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgCancelProto.MsgCancel msg) throws Exception {
        if (msg.getType() == IMConstant.SINGLE_CHAT_TYPE) {
            UpdateWrapper<FriendMsg> wrapper = new UpdateWrapper<>();
            wrapper.eq("msg_id", msg.getMsgId()).set("has_cancel", 1);
            if (friendMsgService.update(wrapper)) {
                Channel targetChannel = ChannelContext.getOnlineChannel(msg.getToId());
                if (targetChannel != null) {
                    IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.MSGCANCEL_PROTOBUF_TYPE, null, msg);
                    targetChannel.writeAndFlush(packet);
                }
            }
        } else if (msg.getType() == IMConstant.GROUP_CHAT_TYPE) {
            redisService.setMsgCanceled(msg.getToId(), msg.getMsgId());
            /*
             * 转发
             * */
            Set<Long> groupOnlineUsers = redisService.getGroupOnlineUsers(msg.getToId());
            groupOnlineUsers.remove(WSUtils.getOnlineUserId(ctx.channel()));
            Iterator<Long> iterator = groupOnlineUsers.iterator();

            IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.MSGCANCEL_PROTOBUF_TYPE, null, msg);
            while (iterator.hasNext()) {
                Long uid = iterator.next();
                Channel channel = ChannelContext.getOnlineChannel(uid);
                if (channel != null)
                    channel.writeAndFlush(packet);
            }
        }
    }
}
