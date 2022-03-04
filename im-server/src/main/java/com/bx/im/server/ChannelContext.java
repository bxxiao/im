package com.bx.im.server;

import com.bx.im.entity.Session;
import com.bx.im.entity.User;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelContext {
    private static ConcurrentHashMap<Long, Channel> online = new ConcurrentHashMap<>();

    public static AttributeKey<Session> SESSION_KEY = AttributeKey.newInstance("session");

    public static boolean isLogin(Channel channel) {
        return channel.attr(SESSION_KEY).get() != null;
    }

    public static void login(Session session, Channel channel) {
        channel.attr(SESSION_KEY).set(session);
        online.put(session.getId(), channel);
        System.out.println("user[" + session.getId() + "-" + session.getName() + "] online");
    }

    public static Channel getOnlineChannel(Long uid) {
        return online.get(uid);
    }

    public static void offLine(Long uid) {
        online.remove(uid);
        System.out.println("user[" + uid + "] offline");
    }
}
