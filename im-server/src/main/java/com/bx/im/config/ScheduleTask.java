package com.bx.im.config;

import com.bx.im.cache.RedisService;
import com.bx.im.dto.ChatMsgCache;
import com.bx.im.proto.ChatMsgProto;
import com.bx.im.proto.IMPacketProto;
import com.bx.im.util.IMConstant;
import com.bx.im.util.WSUtils;
import com.bx.im.websocket.ChannelContext;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTask {

    public static final int MAX_RESEND_COUNT = 5;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    *
    * */
    @Scheduled(fixedRate=4000)
    private void configureTasks() {
        Map<String, ChatMsgCache> entries = redisTemplate.opsForHash().entries(RedisService.SENDING_CACHE_MSGS_KEY);
        if (entries == null || entries.isEmpty())
            return;

        Set<Map.Entry<String, ChatMsgCache>> iteratorSet = entries.entrySet();
        iteratorSet.forEach(entry -> {
            String msgId = entry.getKey();
            String recordsKey = RedisService.RESEND_MSG_RECORDS_PRE + msgId;
            Map<Integer, Integer> records = redisTemplate.opsForHash().entries(recordsKey);
            AtomicInteger removed = new AtomicInteger();
            records.forEach((uid, resendCount) -> {
                // 消息投递次数达到上限
                if (resendCount == MAX_RESEND_COUNT) {
                    // 删掉记录
                    redisTemplate.opsForHash().delete(recordsKey, uid);
                    removed.getAndIncrement();
                    log.info("消息[" + msgId + "] 投递失败 -【用户-" + uid + "】");
                    // 所有目标用户全部都投递失败了，移除掉消息
                    if (records.size() == removed.get())
                        redisTemplate.opsForHash().delete(RedisService.SENDING_CACHE_MSGS_KEY, msgId);
                } else {
                    Channel target = ChannelContext.getOnlineChannel(uid.longValue());
                    // 为空，表示用户已下线或断开连接，则没必要投递，删除掉记录
                    if (target == null) {
                        redisTemplate.opsForHash().delete(recordsKey, uid);
                        removed.getAndIncrement();
                        log.info("[用户-" + uid + "】已离线");
                        if (records.size() == removed.get())
                            redisTemplate.opsForHash().delete(RedisService.SENDING_CACHE_MSGS_KEY, msgId);
                    } else {
                        // 增加重发计数，并发送消息
                        IMPacketProto.IMPacket packet = getChatMsgIMPacket(entry.getValue());
                        redisTemplate.opsForHash().increment(recordsKey, uid, 1);
                        target.writeAndFlush(packet);
                        log.info("消息[" + msgId + "] 已重发 [" + (resendCount + 1) + "] 次 - 【用户-" + uid + "】");
                    }
                }
            });
        });
    }

    private IMPacketProto.IMPacket getChatMsgIMPacket(ChatMsgCache msgCache) {
        ChatMsgProto.ChatMsg chatMsg = ChatMsgProto.ChatMsg.newBuilder()
                .setType(msgCache.getType())
                .setMsgId(msgCache.getMsgId())
                .setMsgSeq(msgCache.getMsgSeq())
                .setFromUid(msgCache.getFromUid())
                .setToId(msgCache.getToId())
                .setContent(msgCache.getContent())
                .setTime(msgCache.getTime())
                .setContentType(msgCache.getContentType())
                .setUsername(msgCache.getUsername())
                .build();
        IMPacketProto.IMPacket packet = WSUtils.createIMPacket(IMConstant.CHATMSG_PROTOBUF_TYPE, null, chatMsg);
        return packet;
    }
}
