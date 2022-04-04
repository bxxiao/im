package com.bx.im.websocket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.GroupMsgDTO;
import com.bx.im.entity.FriendMsg;
import com.bx.im.proto.ChatMsgProto;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.proto.MsgAckProto;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.util.IMConstant;
import com.bx.im.util.WSUtils;
import com.bx.im.websocket.ChannelContext;
import com.bx.im.service.bean.IUserFriendService;
import com.bx.im.service.bean.IUserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChatMsgHandler extends SimpleChannelInboundHandler<ChatMsgProto.ChatMsg> {

    @Autowired
    private IFriendMsgService msgService;

    @Autowired
    private IUserFriendService friendService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMsgProto.ChatMsg msg) throws Exception {
        int msgType = msg.getType();
        switch (msgType) {
            // 单聊
            case 0:
                handleSingChatMsg(msg, ctx);
                break;
            // 群聊
            case 1:
                handleGroupChatMsg(msg, ctx);
                break;
        }
    }


    /*
     *
     * */
    private void handleSingChatMsg(ChatMsgProto.ChatMsg chatMsg, ChannelHandlerContext ctx) {
        QueryWrapper<FriendMsg> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id", chatMsg.getMsgId());
        if (msgService.count(wrapper) > 0L) {
            sendMsgAckPacket(ctx, chatMsg);
            return;
        }

        FriendMsg msg = toFriendMsg(chatMsg);
        // 获取序列号
        Long msgSeq = redisService.getSingleMsgSeq();
        msg.setMsgSeq(msgSeq);
        // 入库成功，发送MsgAck包
        if (msgService.save(msg))
            sendMsgAckPacket(ctx, chatMsg);

        // 若在线，则进行转发
        Long toUid = msg.getToUid();
        Channel targetChannel = ChannelContext.getOnlineChannel(toUid);
        if (targetChannel != null) {
            ChatMsgProto.ChatMsg newChatMsg = chatMsg.toBuilder().setMsgSeq(msgSeq).build();
            IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.CHATMSG_PROTOBUF_TYPE, null, newChatMsg);
            targetChannel.writeAndFlush(packet);
        }
    }

    private void handleGroupChatMsg(ChatMsgProto.ChatMsg chatMsg, ChannelHandlerContext ctx) {
        // 1. 根据消息id检查是否已保存，防止重复保存同一消息，已保存则返回
        long groupId = chatMsg.getToId();
        if (redisService.msgExist(groupId, chatMsg.getMsgId())) {
            sendMsgAckPacket(ctx, chatMsg);
            return;
        }
        // 2. 使用redis事务同时保存消息id和消息记录
        // 设置消息序列号
        ChatMsgProto.ChatMsg msgToSend = chatMsg.toBuilder().setMsgSeq(redisService.getGroupMsgSeq(groupId)).build();
        GroupMsgDTO groupMsgDTO = toGroupMsgDTO(msgToSend);
        groupMsgDTO.setUsername(chatMsg.getUsername());
        /*
        * 该方法的操作包括：存消息，存消息id，更新last_msgSeq（一个事务中执行）
        * */
        redisService.saveGroupMsg(groupMsgDTO);
        // 入库后发送MsgAck包
        sendMsgAckPacket(ctx, chatMsg);

        /*
        * 3. 转发消息到在线群成员
        * 从redis中的群所有成员的set和当前在线用户set两者取交集，获取群在线用户，逐个转发
        * */
        Set<Long> groupOnlineUsers = redisService.getGroupOnlineUsers(groupId);
        // 移除掉当前用户
        groupOnlineUsers.remove(WSUtils.getOnlineUserId(ctx.channel()));

        Iterator<Long> iterator = groupOnlineUsers.iterator();
        IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.CHATMSG_PROTOBUF_TYPE, null, msgToSend);
        while (iterator.hasNext()) {
            Long uid = iterator.next();
            Channel channel = ChannelContext.getOnlineChannel(uid);
            if (channel != null)
                channel.writeAndFlush(packet);
        }
    }

    /**
     * 发送MsgAck包；
     * 在消息入库（单群聊）后发送给客户端
     * @param ctx
     * @param msg
     */
    private void sendMsgAckPacket(ChannelHandlerContext ctx, ChatMsgProto.ChatMsg msg) {
        MsgAckProto.MsgAck msgAck = MsgAckProto.MsgAck.newBuilder()
                .setSenderUid(WSUtils.getOnlineUserId(ctx.channel()))
                .setToId(msg.getToId())
                .setMsgId(msg.getMsgId())
                .build();
        IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.MSGACK_PROTOBUF_TYPE, null, msgAck);
        ctx.channel().writeAndFlush(packet);
    }

    private FriendMsg toFriendMsg(ChatMsgProto.ChatMsg chatMsg) {
        FriendMsg msg = new FriendMsg();
        msg.setMsgId(chatMsg.getMsgId());
        msg.setSenderUid(chatMsg.getFromUid());
        msg.setToUid(chatMsg.getToId());
        msg.setMsgType(0);
        msg.setMsgContent(chatMsg.getContent());
        msg.setTime(LocalDateTime.parse(chatMsg.getTime(), DateTimeFormatter.ISO_DATE_TIME));
        msg.setHasRead(false);
        msg.setMsgType(chatMsg.getType());

        return msg;
    }

    private GroupMsgDTO toGroupMsgDTO(ChatMsgProto.ChatMsg chatMsg) {
        GroupMsgDTO dto = new GroupMsgDTO();
        dto.setGroupId(chatMsg.getToId());
        dto.setMsgId(chatMsg.getMsgId());
        dto.setMsgSeq(chatMsg.getMsgSeq());
        dto.setFromUid(chatMsg.getFromUid());
        dto.setContent(chatMsg.getContent());
        dto.setTime(chatMsg.getTime());
        dto.setType(chatMsg.getContentType());
        dto.setHasCancel(false);

        return dto;
    }

}
