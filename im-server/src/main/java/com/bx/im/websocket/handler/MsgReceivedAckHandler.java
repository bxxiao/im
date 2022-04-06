package com.bx.im.websocket.handler;

import com.bx.im.cache.RedisService;
import com.bx.im.proto.MsgReceivedAckProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsgReceivedAckHandler extends SimpleChannelInboundHandler<MsgReceivedAckProto.MsgReceivedAck> {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgReceivedAckProto.MsgReceivedAck msg) throws Exception {
        System.out.println("收到消息【" + msg.getMsgId() + "】的收到确认");
        String msgId = msg.getMsgId();
        String recordsKey = RedisService.RESEND_MSG_RECORDS_PRE + msgId;
        // 移除掉记录
        redisTemplate.opsForHash().delete(recordsKey, msg.getUid());
        // 若该消息已对全部目标用户完成投递，删除消息缓存
        Long size = redisTemplate.opsForHash().size(recordsKey);
        if (size == 0)
            redisTemplate.opsForHash().delete(RedisService.SENDING_CACHE_MSGS_KEY, msgId);
    }
}
