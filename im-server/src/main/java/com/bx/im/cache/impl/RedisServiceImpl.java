package com.bx.im.cache.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.ChatMsgCache;
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
    public Boolean isUserOnline(Long id) {
        return redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, id);
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
    public GroupMsgDTO getLastGroupMsg(Long groupId, Long curUid) {
        // 以score反序排序，取出第一个，就是最后一条消息
        Set<GroupMsgDTO> set = redisTemplate.opsForZSet().reverseRangeByScore(GROUP_MSGS_PRE + groupId, 0, Double.MAX_VALUE, 0, 1);
        Set<String> canceled = redisTemplate.opsForSet().members(GROUP_CANCELED_MSG_IDS_PRE + groupId);

        if (set.size() > 0) {
            Iterator<GroupMsgDTO> iterator = set.iterator();
            while (iterator.hasNext()) {
                GroupMsgDTO next = iterator.next();
                if (canceled.contains(next.getMsgId())) {
                    String content = null;
                    if (curUid.equals(next.getFromUid()))
                        content = "你 撤回了消息";
                    else
                        content = next.getUsername() + " 撤回了消息";
                    next.setContent(content);
                }
                return next;
            }
        }

        return null;
    }

    @Override
    public Long getGroupLastMsgSeq(Long uid, Long groupId) {
        String seqStr = (String) redisTemplate.opsForHash().get(USER_LAST_MSG_SEQ_PRE + uid, groupId.toString());
        return Long.parseLong(seqStr);
    }

    @Override
    public List<GroupMsgDTO> getNewGroupMsgs(Long uid, Long groupId) {
        Long lastMsgSeq = this.getGroupLastMsgSeq(uid, groupId);
        List<GroupMsgDTO> list = new ArrayList<>();
        Set<String> canceledIdSet = redisTemplate.opsForSet().members(GROUP_CANCELED_MSG_IDS_PRE + groupId);
        // [...] （左右闭区间）
        /*
        * 若序号不是严格递增的，则使用 lastMsgSeq - 9 不一定能获取到10条消息
        * */
        Set<GroupMsgDTO> set = redisTemplate.opsForZSet().rangeByScore(GROUP_MSGS_PRE + groupId, lastMsgSeq - 9, Double.MAX_VALUE);
        /*
        * 返回的set类型是LinkedHashSet，所以遍历速度很快
        * 且会按从redis中取出的score顺序遍历，所以不用额外进行排序操作
        * */
        // System.out.println(set.getClass().getName());
        Iterator<GroupMsgDTO> iterator = set.iterator();
        while (iterator.hasNext()) {
            GroupMsgDTO next = iterator.next();
            if (canceledIdSet.contains(next.getMsgId()))
                next.setHasCancel(true);
            else
                next.setHasCancel(false);
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

    @Override
    public void updateLastSeq(Long seq, Long groupId, Long uid) {
        Long lastMsgSeq = this.getGroupLastMsgSeq(uid, groupId);
        if (seq > lastMsgSeq)
            redisTemplate.opsForHash().put(USER_LAST_MSG_SEQ_PRE + uid, groupId.toString(), seq.toString());
    }

    @Override
    public Set<GroupMsgDTO> getHistoryMsgs(Long groupId, Long msgSeq) {
        /*
        * 0 - msgSeq 反序排列，取前10条
        * min-max 是左右闭区间，msgSeq要减一
        * */
        Set<GroupMsgDTO> set = redisTemplate.opsForZSet().reverseRangeByScore(GROUP_MSGS_PRE + groupId, 0, msgSeq - 1, 0, 10);
        Set<String> canceled = redisTemplate.opsForSet().members(GROUP_CANCELED_MSG_IDS_PRE + groupId);
        Iterator<GroupMsgDTO> iterator = set.iterator();
        while (iterator.hasNext()) {
            GroupMsgDTO next = iterator.next();
            if (canceled.contains(next.getMsgId()))
                next.setHasCancel(true);
            else
                next.setHasCancel(false);
        }

        return set;
    }

    @Override
    public void initLastSeq(Long uid, Long groupId) {
        redisTemplate.opsForHash().put(USER_LAST_MSG_SEQ_PRE + uid, groupId.toString(), "0");
    }

    @Override
    public void initGroupSeqKey(Long groupId) {
        redisTemplate.opsForValue().set(GROUP_MSG_SEQ_PRE + groupId, 0L);
    }

    @Override
    public void removeLastSeqKey(Long uid, Long groupId) {
        redisTemplate.opsForHash().delete(USER_LAST_MSG_SEQ_PRE + uid, groupId.toString());
    }

    @Override
    public void dissolveGroup(Long groupId, List<Long> memberIds) {
        /*
        * *      群消息记录
         *      群消息序列号key
         *      各群成员在该群的last_msgSeq
        * */
        SessionCallback transaction = new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                // 删除群消息记录（zset和set）
                redisTemplate.delete(GROUP_MSGS_PRE + groupId);
                redisTemplate.delete(GROUP_MSGS_ID_SET_PRE + groupId);
                // 删除序列号生成键
                redisTemplate.delete(GROUP_MSG_SEQ_PRE + groupId);
                // 删除各群成员在该群的last_msgSeq
                for (int i = 0; i < memberIds.size(); i++)
                    redisTemplate.opsForHash().delete(USER_LAST_MSG_SEQ_PRE + memberIds.get(i), groupId.toString());

                return operations.exec();
            }
        };

        redisTemplate.execute(transaction);
    }

    @Override
    public void setMsgCanceled(Long groupId, String msgId) {
        redisTemplate.opsForSet().add(GROUP_CANCELED_MSG_IDS_PRE + groupId, msgId);
    }

    @Override
    public void setChatMsgInCheck(ChatMsgCache msg, Set<Long> targetUids) {

        Map<Long, Long> records = new HashMap<>();

        Iterator<Long> iterator = targetUids.iterator();
        while (iterator.hasNext()) {
            Long next = iterator.next();
            records.put(next, 0L);
        }

        // redis事务
        SessionCallback transaction = new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                // 放入消息缓存
                redisTemplate.opsForHash().put(SENDING_CACHE_MSGS_KEY, msg.getMsgId(), msg);
                // 放入该消息的目标用户及已重发次数，后者初始为0
                redisTemplate.opsForHash().putAll(RESEND_MSG_RECORDS_PRE + msg.getMsgId(), records);
                return operations.exec();
            }
        };

        redisTemplate.execute(transaction);
    }


}
