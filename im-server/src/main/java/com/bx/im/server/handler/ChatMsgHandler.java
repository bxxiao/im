package com.bx.im.server.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.GroupMsgDTO;
import com.bx.im.entity.FriendMsg;
import com.bx.im.proto.ChatMsgProto;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.proto.MsgAckProto;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.util.IMUtil;
import com.bx.im.server.ChannelContext;
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
import java.util.List;
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
        // 不管单群聊，先发送个确认回去
        MsgAckProto.MsgAck msgAck = MsgAckProto.MsgAck.newBuilder()
                .setSenderUid(IMUtil.getOnlineUserId(ctx.channel()))
                .setToId(msg.getToId())
                .setMsgId(msg.getMsgId())
                .build();
        IMPacketProto.IMPacket packet = IMUtil.createIMPacket(IMUtil.MSGACK_TYPE, null, msgAck);
        ctx.channel().writeAndFlush(packet);

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
     * 连接关闭，则离线
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long uid = ctx.channel().attr(ChannelContext.SESSION_KEY).get().getId();
        ChannelContext.offLine(uid);
        redisService.userOffline(uid);
    }

    private void handleSingChatMsg(ChatMsgProto.ChatMsg chatMsg, ChannelHandlerContext ctx) {
        /*
        * 若消息已存在，则停止。
        * 前端接收不到MsgAck包，重新发送消息时会出现这种情况
        * 【这部分代码可以移到channelRead0中】
        * */
        QueryWrapper<FriendMsg> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id", chatMsg.getMsgId());
        if (msgService.count(wrapper) > 0L)
            return;

        FriendMsg msg = toFriendMsg(chatMsg);
        Long msgSeq = redisService.getSingleMsgSeq();
        msg.setMsgSeq(msgSeq);
        msgService.save(msg);

        ChatMsgProto.ChatMsg newChatMsg = chatMsg.toBuilder().setMsgSeq(msgSeq).build();

        Long toUid = msg.getToUid();
        Channel targetChannel = ChannelContext.getOnlineChannel(toUid);
        if (targetChannel != null) {
            IMPacketProto.IMPacket packet = IMUtil.createIMPacket(IMUtil.CHATMSG_TYPE, null, newChatMsg);
            targetChannel.writeAndFlush(packet);
        }
    }

    private void handleGroupChatMsg(ChatMsgProto.ChatMsg msg, ChannelHandlerContext ctx) {
        // 1. 根据消息id检查是否已保存，防止重复保存同一消息，已保存则返回
        long groupId = msg.getToId();
        if (redisService.msgExist(groupId, msg.getMsgId()))
            return;
        // 2. 使用redis事务同时保存消息id和消息记录
        // 设置消息序列号
        ChatMsgProto.ChatMsg msgToSend = msg.toBuilder().setMsgSeq(redisService.getGroupMsgSeq(groupId)).build();
        GroupMsgDTO groupMsgDTO = toGroupMsgDTO(msgToSend);
        redisService.saveGroupMsg(groupMsgDTO);


        /*
        * 3. 转发消息到在线群成员
        * 从redis中的群所有成员的set和当前在线用户set两者取交集，获取群在线用户，逐个转发
        * */
        Set<Long> groupOnlineUsers = redisService.getGroupOnlineUsers(groupId);
        // 移除掉当前用户
        groupOnlineUsers.remove(IMUtil.getOnlineUserId(ctx.channel()));

        Iterator<Long> iterator = groupOnlineUsers.iterator();
        while (iterator.hasNext()) {
            Long uid = iterator.next();
            Channel channel = ChannelContext.getOnlineChannel(uid);
            if (channel != null) {
                IMPacketProto.IMPacket packet = IMUtil.createIMPacket(IMUtil.CHATMSG_TYPE, null, msgToSend);
                channel.writeAndFlush(packet);
            }
        }
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

        return dto;
    }

}
