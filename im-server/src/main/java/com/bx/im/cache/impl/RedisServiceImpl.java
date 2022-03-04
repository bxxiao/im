package com.bx.im.cache.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.GroupMsgDTO;
import com.bx.im.entity.GroupUsers;
import com.bx.im.service.bean.IGroupUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IGroupUsersService groupUsersService;

    @Override
    public void userOnline(Long uid) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, uid);
    }

    @Override
    public void userOffline(Long uid) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, uid);
    }

    @Override
    public boolean expire(String key, long expire) {
        return false;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Long getSingleMsgSeq() {
        Long increment = redisTemplate.opsForValue().increment(SINGE_MSG_SEQ_KEY);
        return increment;
    }

    @Override
    public void saveGroupMsg(GroupMsgDTO msg) {

        SessionCallback transaction = new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                // 添加消息到zset
                operations.opsForZSet().add(GROUP_MSGS_PRE + msg.getGroupId(), msg, msg.getMsgSeq());
                // 添加消息的id到对应set
                operations.opsForSet().add(GROUP_MSGS_ID_SET_PRE + msg.getGroupId(), msg.getMsgId());
                // 更新last_msgSeq
                operations.opsForHash().put(USER_LAST_MSG_SEQ_PRE + msg.getFromUid(), msg.getGroupId().toString(), msg.getMsgSeq().toString());
                return operations.exec();
            }
        };

        redisTemplate.execute(transaction);
    }

    @Override
    public Long getGroupMsgSeq(Long groupId) {
        Long msgSeq = redisTemplate.opsForValue().increment(GROUP_MSG_SEQ_PRE + groupId);
        return msgSeq;
    }

    @Override
    public Map<Long, Long> getUserLastSeqMap(Long uid) {
        /*
        * 以String形式取出，再转换为Long类型
        * */
        Map<Long, Long> result = new HashMap<>();
        Map<String, String> entries = redisTemplate.opsForHash().entries(USER_LAST_MSG_SEQ_PRE + uid);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Long key = Long.parseLong(entry.getKey());
            Long value = Long.parseLong(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Long getGroupUnReadCount(Long groupId, Long seq) {
        Long count = redisTemplate.opsForZSet().count(GROUP_MSGS_PRE + groupId, seq + 1, Double.MAX_VALUE);
        return count;
    }

    @Override
    public GroupMsgDTO getLastGroupMsg(Long groupId) {
        // 以score反序排序，取出第一个，就是最后一条消息
        Set<GroupMsgDTO> set = redisTemplate.opsForZSet().reverseRangeByScore(GROUP_MSGS_PRE + groupId, 0, Double.MAX_VALUE, 0, 1);

        if (set.size() > 0) {
            Iterator<GroupMsgDTO> iterator = set.iterator();
            while (iterator.hasNext())
                return iterator.next();
        }

        return null;
    }

    @Override
    public Long getGroupLastMsgSeq(Long uid, Long groupId) {
        String seqStr = (String) redisTemplate.opsForHash().get(USER_LAST_MSG_SEQ_PRE + uid, groupId.toString());
        return Long.parseLong(seqStr);
    }

    @Override
    public List<GroupMsgDTO> getNewGroupMsgs(Long lastMsgSeq, Long groupId) {
        List<GroupMsgDTO> list = new ArrayList<>();
        Set<GroupMsgDTO> set = redisTemplate.opsForZSet().rangeByScore(GROUP_MSGS_PRE + groupId, lastMsgSeq + 1, Double.MAX_VALUE);
        /*
        * 返回的set类型是LinkedHashSet，所以遍历速度很快
        * */
        // System.out.println(set.getClass().getName());
        Iterator<GroupMsgDTO> iterator = set.iterator();
        while (iterator.hasNext()) {
            GroupMsgDTO next = iterator.next();
            list.add(next);
        }

        return list;
    }

    @Override
    public Boolean msgExist(Long groupId, String msgId) {
        return redisTemplate.opsForSet().isMember(GROUP_MSGS_ID_SET_PRE + groupId, msgId);
    }

    @Override
    public Set<Long> getGroupOnlineUsers(long groupId) {
        String allMembersKey = GROUP_ALL_MEMBERS_PRE + groupId;
        if (!redisTemplate.hasKey(allMembersKey)) {
            QueryWrapper<GroupUsers> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id", groupId).select("user_id");
            List<Map<String, Object>> result = groupUsersService.listMaps(wrapper);
            List<Long> userIds = result.stream().map(map -> {
                Long uid = (Long) map.get("user_id");
                return uid;
            }).collect(Collectors.toList());
            System.out.println(userIds);
            // TODO：加上过期时间
            redisTemplate.opsForSet().add(allMembersKey, userIds.toArray());
        }
        // 群成员id set跟在线用户set取交集，得到在线群成员
        Set<Integer> intersect = redisTemplate.opsForSet().intersect(allMembersKey, ONLINE_USERS_KEY);
        Set<Long> set = new LinkedHashSet<>();
        Iterator<Integer> iterator = intersect.iterator();
        while (iterator.hasNext())
            set.add(iterator.next().longValue());
        return set;
    }


}
