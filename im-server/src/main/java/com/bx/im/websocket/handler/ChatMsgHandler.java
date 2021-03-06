package com.bx.im.websocket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.ChatMsgCache;
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
import java.util.LinkedHashSet;
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
        // System.out.println(msg.getUsername() + " " + msg.getContent());
        int msgType = msg.getType();
        switch (msgType) {
            // åč
            case 0:
                handleSingChatMsg(msg, ctx);
                break;
            // įž¤č
            case 1:
                handleGroupChatMsg(msg, ctx);
                break;
        }
    }


    /*
     *
     * */
    private void handleSingChatMsg(ChatMsgProto.ChatMsg chatMsg, ChannelHandlerContext ctx) {
        // å¤æ­æļæ¯æ¯åĻåˇ˛åĨåē
        QueryWrapper<FriendMsg> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id", chatMsg.getMsgId());
        if (msgService.count(wrapper) > 0L) {
            sendMsgAckPacket(ctx, chatMsg);
            return;
        }

        FriendMsg msg = toFriendMsg(chatMsg);
        // čˇååēååˇ
        Long msgSeq = redisService.getSingleMsgSeq();
        msg.setMsgSeq(msgSeq);
        // åĨåēæåīŧåéMsgAckååšļææļæ¯čŋčĄå¯čžžæ§æé
        if (msgService.save(msg)) {
            sendMsgAckPacket(ctx, chatMsg);
            Set<Long> uid = new LinkedHashSet<>();
            uid.add(chatMsg.getToId());
            putMsgInSendingCache(chatMsg, uid);
        }

        // čĨå¨įēŋīŧåčŋčĄčŊŦå
        Long toUid = msg.getToUid();
        Channel targetChannel = ChannelContext.getOnlineChannel(toUid);
        if (targetChannel != null) {
            ChatMsgProto.ChatMsg newChatMsg = chatMsg.toBuilder().setMsgSeq(msgSeq).build();
            IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.CHATMSG_PROTOBUF_TYPE, null, newChatMsg);
            targetChannel.writeAndFlush(packet);
        }
    }

    private void handleGroupChatMsg(ChatMsgProto.ChatMsg chatMsg, ChannelHandlerContext ctx) {
        // 1. æ šæŽæļæ¯idæŖæĨæ¯åĻåˇ˛äŋå­īŧé˛æ­ĸéå¤äŋå­åä¸æļæ¯īŧåˇ˛äŋå­åčŋå
        long groupId = chatMsg.getToId();
        if (redisService.msgExist(groupId, chatMsg.getMsgId())) {
            sendMsgAckPacket(ctx, chatMsg);
            return;
        }
        // 2. äŊŋį¨redisäēåĄåæļäŋå­æļæ¯idåæļæ¯čŽ°åŊ
        // čŽžįŊŽæļæ¯åēååˇ
        ChatMsgProto.ChatMsg msgToSend = chatMsg.toBuilder().setMsgSeq(redisService.getGroupMsgSeq(groupId)).build();
        GroupMsgDTO groupMsgDTO = toGroupMsgDTO(msgToSend);
        groupMsgDTO.setUsername(chatMsg.getUsername());
        /*
        * č¯ĨæšæŗįæäŊåæŦīŧå­æļæ¯īŧå­æļæ¯idīŧæ´æ°last_msgSeqīŧä¸ä¸ĒäēåĄä¸­æ§čĄīŧ
        * */
        redisService.saveGroupMsg(groupMsgDTO);
        // åĨåēååéMsgAckå
        sendMsgAckPacket(ctx, chatMsg);


        /*
        * 3. čŊŦåæļæ¯å°å¨įēŋįž¤æå
        * äģredisä¸­įįž¤æææåįsetååŊåå¨įēŋį¨æˇsetä¸¤čåäē¤éīŧčˇåįž¤å¨įēŋį¨æˇīŧéä¸ĒčŊŦå
        * */
        Set<Long> groupOnlineUsers = redisService.getGroupOnlineUsers(groupId);
        // į§ģé¤æåŊåį¨æˇ
        groupOnlineUsers.remove(WSUtils.getOnlineUserId(ctx.channel()));

        // æļæ¯čŋčĄå¯čžžæ§æéæēåļ
        putMsgInSendingCache(chatMsg, groupOnlineUsers);

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
     * ææļæ¯æžåĨSENDING_CACHE_MSGSä¸­åšļčŽ°åŊį¸åŗæ°æŽīŧčŋčĄå¯čžžæ§æé
     * @param chatMsg
     * @param targetUids
     */
    private void putMsgInSendingCache(ChatMsgProto.ChatMsg chatMsg, Set<Long> targetUids) {
        ChatMsgCache msgCache = new ChatMsgCache();
        msgCache.setType(chatMsg.getType());
        msgCache.setMsgId(chatMsg.getMsgId());
        msgCache.setMsgSeq(chatMsg.getMsgSeq());
        msgCache.setFromUid(chatMsg.getFromUid());
        msgCache.setToId(chatMsg.getToId());
        msgCache.setContent(chatMsg.getContent());
        msgCache.setTime(chatMsg.getTime());
        msgCache.setContentType(chatMsg.getContentType());
        msgCache.setUsername(chatMsg.getUsername());

        redisService.setChatMsgInCheck(msgCache, targetUids);
    }

    /**
     * åéMsgAckåīŧ
     * å¨æļæ¯åĨåēīŧåįž¤čīŧååéįģåŽĸæˇįĢ¯
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
        //
        msg.setMsgType(chatMsg.getContentType());

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
