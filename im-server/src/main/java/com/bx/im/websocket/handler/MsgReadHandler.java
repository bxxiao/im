package com.bx.im.websocket.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bx.im.entity.FriendMsg;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.proto.MsgReadProto;
import com.bx.im.util.IMConstant;
import com.bx.im.websocket.ChannelContext;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.util.WSUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsgReadHandler extends SimpleChannelInboundHandler<MsgReadProto.MsgRead> {
    @Autowired
    private IFriendMsgService friendMsgService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgReadProto.MsgRead msg) throws Exception {
        List<String> list = msg.getMsgIdsList();
        UpdateWrapper<FriendMsg> wrapper = new UpdateWrapper<>();
        /*
        * 逐条更新已读字段，这里不用 IN 子句，可能会触发范围锁
        * */
        list.forEach(msgId -> {
            wrapper.clear();
            wrapper.eq("msg_id", msgId).set("has_read", 1);
            friendMsgService.update(wrapper);
        });
        /*
        * 若对端（即发出这些消息的用户）在线，通知其消息已被读
        * */
        long toId = msg.getToId();
        Channel toUser = ChannelContext.getOnlineChannel(toId);
        if (toUser != null) {
            IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.MSGREAD_PROTOBUF_TYPE, null, msg);
            toUser.writeAndFlush(packet);
        }
    }
}
