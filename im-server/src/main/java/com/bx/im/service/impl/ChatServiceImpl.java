package com.bx.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.*;
import com.bx.im.entity.ChatSession;
import com.bx.im.entity.FriendMsg;
import com.bx.im.entity.GroupInfo;
import com.bx.im.entity.User;
import com.bx.im.server.ChannelContext;
import com.bx.im.service.ChatService;
import com.bx.im.service.bean.IChatSessionService;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.service.bean.IGroupInfoService;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.IMConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private IFriendMsgService friendMsgService;

    @Autowired
    private IChatSessionService sessionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IGroupInfoService groupInfoService;

    /*
     * TODO：目前只针对单聊会话，群聊 to be continued
     * */
    @Override
    public ChatPageDTO getChatPageData(Long uid) {
        ChatPageDTO chatPageDTO = new ChatPageDTO();

        // 1.查当前用户所有会话的entity，转换为dto
        QueryWrapper<ChatSession> wrapper = Wrappers.query();
        List<ChatSession> sessionEntities = sessionService.list(wrapper.eq("user_id", uid));
        // 保存单聊类型会话
        List<ChatSessionDTO> sessionList1 = new ArrayList<>();
        // 保存群聊类型会话
        List<ChatSessionDTO> sessionList2 = new ArrayList<>();

        // 转换为dto，并根据类型放在2个list
        for (ChatSession entity : sessionEntities) {
            ChatSessionDTO dto = toChatSessionDTO(entity);
            if (dto.getType() == IMConstant.SINGLE_CHAT_TYPE)
                sessionList1.add(dto);
            else if (dto.getType() == IMConstant.GROUP_CHAT_TYPE)
                sessionList2.add(dto);
        }

        dealSingleTypeSessions(sessionList1, uid);
        dealGroupTypeSessions(sessionList2, uid);

        sessionList1.addAll(sessionList2);
        // 查询当前用户的信息
        chatPageDTO.setSessionList(sessionList1);
        User user = userService.getById(uid);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        chatPageDTO.setUserInfo(userDTO);

        return chatPageDTO;
    }

    @Override
    public DialogueDataDTO getDialogueData(Long uid, Long toId, Integer type) {
        if (uid == null || toId == null || type == null)
            return null;

        DialogueDataDTO result = new DialogueDataDTO();
        if (type == IMConstant.SINGLE_CHAT_TYPE) {
            result.setIsOnline(ChannelContext.getOnlineChannel(toId) != null);
            QueryWrapper<FriendMsg> wrapper = new QueryWrapper<>();
            /*
             * select * from friend_msg
             * where ((sender_uid = ? AND to_uid = ?) OR (sender_uid = ? AND to_uid = ?))
             * ORDER BY msg_seq DESC
             * limit 20
             * */
            wrapper.or(i -> i.eq("sender_uid", uid).eq("to_uid", toId))
                    .or(i -> i.eq("sender_uid", toId).eq("to_uid", uid))
                    .orderByDesc("msg_seq")
                    .last("limit 20");
            List<FriendMsg> msgList = friendMsgService.list(wrapper);
            List<ChatMsgDTO> msgDTOS = msgList.stream().map(msg -> {
                ChatMsgDTO msgDTO = toChatMsgDTO(msg);
                return msgDTO;
            }).collect(Collectors.toList());
            Collections.reverse(msgDTOS);
            result.setMsgs(msgDTOS);
        } else if (type == IMConstant.GROUP_CHAT_TYPE) {
            // 获取用户在该群的lastMsgSeq
            Long groupLastMsgSeq = redisService.getGroupLastMsgSeq(uid, toId);
            // 根据lastMsgSeq获取全部的新消息
            List<GroupMsgDTO> groupMsgs = redisService.getNewGroupMsgs(groupLastMsgSeq, toId);
            List<ChatMsgDTO> collect = groupMsgs.stream().map(item -> toChatMsgDTO(item)).collect(Collectors.toList());
            result.setMsgs(collect);
        } else
            return null;

        return result;
    }

    @Override
    public ChatSessionDTO createSession(Long uid, Long toId, int type) {
        ChatSession entity = new ChatSession();
        ChatSessionDTO dto = new ChatSessionDTO();

        entity.setUserId(uid);
        entity.setToId(toId);
        entity.setType(type);
        // 单聊
        if (type == IMConstant.SINGLE_CHAT_TYPE) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("id", toId).select("name", "avatar");
            User toUser = userService.getOne(wrapper);
            dto.setName(toUser.getName());
            dto.setAvatar(toUser.getAvatar());
            entity.setToName(toUser.getName());
            entity.setToAvatar(toUser.getAvatar());
            // TODO:群聊会话
        } else {

        }

        sessionService.save(entity);
        return dto;
    }

    /**
     * 处理单聊会话列表，为每个会话查询未读数和最后一条消息
     * TODO：dealSingleTypeSessions 和 dealGroupTypeSessions 的逻辑合为一个
     * @param sessionList
     * @param uid
     */
    private void dealSingleTypeSessions(List<ChatSessionDTO> sessionList, Long uid) {
        // 2. 查询未读消息数
        // <userId - unread num>
        Map<Long, Long> unreadMap = friendMsgService.getAllUnreadNum(uid);
        sessionList.forEach(session -> {
            Long toId = session.getToId();
            if (unreadMap.containsKey(toId)) {
                session.setUnread(unreadMap.get(toId));
                // 设置完移除掉
                unreadMap.remove(toId);
            }
        });

        // 有新消息但是没有对应会话，则创建一个新会话
        if (!unreadMap.isEmpty()) {
            Set<Map.Entry<Long, Long>> entries = unreadMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                ChatSession entity = new ChatSession();
                entity.setUserId(uid);
                entity.setToId(entry.getKey());
                entity.setType(IMConstant.SINGLE_CHAT_TYPE);

                // 查询好友的名称和头像
                User friend = userService.getOne(new QueryWrapper<User>().eq("id", entry.getKey()).select("name", "avatar"));
                entity.setToName(friend.getName());
                entity.setToAvatar(friend.getAvatar());

                // 入库
                sessionService.save(entity);
                // 创建对应的dto，加入sessionList
                ChatSessionDTO entityDto = toChatSessionDTO(entity);
                entityDto.setUnread(entry.getValue());
                sessionList.add(entityDto);
            }
        }

        // 3. 为每个会话（单聊）查询最后一条消息
        sessionList.forEach(dto -> {
            QueryWrapper<FriendMsg> msgWrapper = new QueryWrapper<>();
            /*
             * select "msg_content", "time" from friend_msg
             * where ((sender_uid = ? AND to_uid = ?) OR (sender_uid = ? AND to_uid = ?))
             * ORDER BY msg_seq DESC
             * limit 1
             * */
            msgWrapper.or(i -> i.eq("sender_uid", uid).eq("to_uid", dto.getToId()))
                    .or(i -> i.eq("sender_uid", dto.getToId()).eq("to_uid", uid))
                    .orderByDesc("msg_seq")
                    .last("limit 1")
                    .select("msg_content", "time");
            List<FriendMsg> list = friendMsgService.list(msgWrapper);
            if (!list.isEmpty()) {
                dto.setLastMsg(list.get(0).getMsgContent());
                dto.setTime(list.get(0).getTime().toString());
            }
        });
    }

    /**
     * 处理群聊会话列表，为每个会话查询未读数和最后一条消息
     * @param sessionList
     * @param uid
     */
    private void dealGroupTypeSessions(List<ChatSessionDTO> sessionList, Long uid) {
        // 1.查询未读数，若没有对应会话，则创建新会话项
        Map<Long, Long> unreadMap = getGroupMsgUnreadMap(uid);
        sessionList.forEach(session -> {
            Long toId = session.getToId();
            if (unreadMap.containsKey(toId)) {
                session.setUnread(unreadMap.get(toId));
                // 设置完移除掉
                unreadMap.remove(toId);
            }
        });

        // 有新消息但是没有对应会话，则创建一个新会话
        if (!unreadMap.isEmpty()) {
            Set<Map.Entry<Long, Long>> entries = unreadMap.entrySet();
            for (Map.Entry<Long, Long> entry : entries) {
                ChatSession entity = new ChatSession();
                entity.setUserId(uid);
                entity.setToId(entry.getKey());
                entity.setType(IMConstant.GROUP_CHAT_TYPE);

                // 查询好友的名称和头像
                GroupInfo info = groupInfoService.getOne(new QueryWrapper<GroupInfo>().eq("id", entry.getKey()).select("name", "avatar"));
                entity.setToName(info.getName());
                entity.setToAvatar(info.getAvatar());

                // 入库
                sessionService.save(entity);
                // 创建对应的dto，加入sessionList
                ChatSessionDTO entityDto = toChatSessionDTO(entity);
                entityDto.setUnread(entry.getValue());
                sessionList.add(entityDto);
            }
        }

        // 查询最后一条消息
        sessionList.forEach(session -> {
            long gid = session.getToId();
            GroupMsgDTO lastMsg = redisService.getLastGroupMsg(gid);
            /*
            * 若为null，表示当前没有消息；这时不用设置属性，前端有对null和undefined值的处理
            * */
            if (lastMsg != null) {
                session.setLastMsg(lastMsg.getContent());
                session.setTime(lastMsg.getTime());
            }
        });
    }

    /**
     * 获取用户的所有群聊的消息未读数（ >0 的）
     * @param uid
     * @return
     */
    private Map<Long, Long> getGroupMsgUnreadMap(Long uid) {
        Map<Long, Long> map = new HashMap<>();
        /*
        * 1 从USER_LAST_GMSG_SEQ_中获取当前用户加入的所有群对应的所有last_seq
        * 这里默认用户加入群聊时，会在USER_LAST_GMSG_SEQ中添加对应last_seq，即USER_LAST_GMSG_SEQ中保存了用户加入的所有群
        * [groupId - lastSeq]
        * */
        Map<Long, Long> userLastSeqMap = redisService.getUserLastSeqMap(uid);

        // 2 根据每个last_seq到对应群消息的zset中查询有多少消息未读

        for (Map.Entry<Long, Long> entry : userLastSeqMap.entrySet()) {
            Long gid = entry.getKey().longValue();
            Long lastSeq = entry.getValue().longValue();
            Long unReadCount = redisService.getGroupUnReadCount(gid, lastSeq);
            map.put(gid, unReadCount);
        }

        return map;
    }

    private ChatSessionDTO toChatSessionDTO(ChatSession entity) {
        ChatSessionDTO dto = new ChatSessionDTO();
        dto.setToId(entity.getToId());
        dto.setName(entity.getToName());
        dto.setAvatar(entity.getToAvatar());
        dto.setType(entity.getType());
        dto.setUnread(0L);
        return dto;
    }

    private ChatMsgDTO toChatMsgDTO(FriendMsg msg) {
        ChatMsgDTO msgDTO = new ChatMsgDTO();
        msgDTO.setMsgId(msg.getMsgId());
        msgDTO.setMsgSeq(msg.getMsgSeq());
        msgDTO.setFromUid(msg.getSenderUid());
        msgDTO.setToId(msg.getToUid());
        msgDTO.setType(IMConstant.SINGLE_CHAT_TYPE);
        msgDTO.setContent(msg.getMsgContent());
        msgDTO.setTime(msg.getTime().toString());
        msgDTO.setHasRead(msg.getHasRead());
        return msgDTO;
    }

    private ChatMsgDTO toChatMsgDTO(GroupMsgDTO msg) {
        ChatMsgDTO msgDTO = new ChatMsgDTO();
        msgDTO.setMsgId(msg.getMsgId());
        msgDTO.setMsgSeq(msg.getMsgSeq());
        msgDTO.setFromUid(msg.getFromUid());
        msgDTO.setToId(msg.getGroupId());
        msgDTO.setType(IMConstant.GROUP_CHAT_TYPE);
        msgDTO.setContent(msg.getContent());
        msgDTO.setTime(msg.getTime());
        return msgDTO;
    }
}



























