package com.bx.im;

// import com.bx.im.proto.DemoProto;
// import com.google.protobuf.InvalidProtocolBufferException;
// import com.google.protobuf.util.JsonFormat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bx.im.cache.RedisService;
import com.bx.im.dto.*;
import com.bx.im.entity.ChatSession;
import com.bx.im.entity.FriendMsg;
import com.bx.im.entity.UserFriend;
import com.bx.im.service.application.ChatService;
import com.bx.im.service.application.FriendHandleService;
import com.bx.im.service.bean.IChatSessionService;
import com.bx.im.service.bean.IFriendMsgService;
import com.bx.im.service.bean.IUserFriendService;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.IMConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class ImServerApplicationTests {

    @Autowired
    private IChatSessionService service;

    @Autowired
    private IFriendMsgService msgService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IUserFriendService userFriendService;

    @Autowired
    private IFriendMsgService friendMsgService;

    @Test
    public void testTemp(){
        List<FriendMsg> msgs = friendMsgService.list();
        for (FriendMsg msg : msgs) {
            Long senderUid = msg.getSenderUid();
            Long toUid = msg.getToUid();
            Long from = senderUid.compareTo(toUid) < 0 ? senderUid : toUid;
            Long to = from.equals(senderUid) ? toUid : senderUid;
            UpdateWrapper<FriendMsg> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", msg.getId()).set("query_from", from).set("query_to", to);
            friendMsgService.update(wrapper);
        }
    }

    /*
     * listObjs??????????????????List???????????????????????????????????????
     * */
    @Test
    public void testMybatisListObjs() {
        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", 2L).select("uid", "friend_uid");
        // List<Long> list = userFriendService.listObjs(wrapper, userFriend -> ((UserFriend) userFriend).getFriendUid());
        List<Object> objects = userFriendService.listObjs(wrapper);
        System.out.println(objects);
    }

    /*
     * ??????entity???????????????????????????entity????????????null???????????????where??????and??????
     * */
    @Test
    void testQueryWrapper() {
        ChatSession chatSession = new ChatSession();
        chatSession.setUserId(2L);
        chatSession.setToName("ccc");
        QueryWrapper<ChatSession> wrapper = Wrappers.query(chatSession);

        List<ChatSession> list = service.list(wrapper);
        System.out.println(list);
    }

    @Test
    public void testOrAnd() {
        QueryWrapper<FriendMsg> msgWrapper = new QueryWrapper<>();
        msgWrapper.or(i -> i.eq("sender_uid", 1).eq("to_uid", 2))
                .or(i -> i.eq("sender_uid", 2).eq("to_uid", 1))
                .orderByDesc("msg_seq").last("limit 1").select("msg_content", "time");
        System.out.println(msgWrapper.getTargetSql());
        List<FriendMsg> list = msgService.list(msgWrapper);
        for (FriendMsg msg : list) {
            System.out.println(msg);
        }
    }

    /*
     * ??????List<Map<String, Object>>???????????????????????? ?????????-??? ???????????????Map???????????????????????????null?????????
     * */
    @Test
    public void testListMaps() {
        QueryWrapper<FriendMsg> wrapper = Wrappers.query();
        wrapper.eq("to_uid", 2).eq("has_read", 0).groupBy("sender_uid").select("sender_uid", "count(*) as num");
        List<Map<String, Object>> maps = msgService.listMaps(wrapper);
        System.out.println(maps.getClass());
        for (Map<String, Object> item : maps)
            System.out.println(item);
    }

    // @Test
    // public void testProto() throws InvalidProtocolBufferException {
    //     DemoProto.Demo demo = DemoProto.Demo.newBuilder()
    //             .setId(1)
    //             .setCode("66")
    //             .setName("??????").build();
    //     byte[] bytes = demo.toByteArray();
    //     System.out.println(bytes.length);
    //
    //     DemoProto.Demo demo1 = DemoProto.Demo.parseFrom(bytes);
    //     String json = JsonFormat.printer().print(demo1);
    //     System.out.println(demo1.toString());
    //     System.out.println(json);
    // }

    @Test
    public void testRedisTemplateAPI() {
        // ??????hash?????????
        Map<Integer, String> map = redisTemplate.opsForHash().entries("hash1");
        System.out.println(map);
        List list = redisTemplate.opsForHash().values("hash1");
        System.out.println(list);

        // ????????? []
        Long count = redisTemplate.opsForZSet().count("set11", 2, Double.MAX_VALUE);
        System.out.println(count);

        // ??????????????????
        Set<String> set = redisTemplate.opsForZSet().range("set11", 2, 3);
        System.out.println(set);

    }

    @Test
    public void testRedisTemplateZSetAPI() {
        RedisZSetCommands.Range range = RedisZSetCommands.Range.range();
        range.gt("\"one\"");
        // limit?????????0??????
        RedisZSetCommands.Limit limit = RedisZSetCommands.Limit.limit();
        limit.offset(0).count(1);
        // reverseRangeByLex??????range???????????????score?????????????????????
        Set set11 = redisTemplate.opsForZSet().reverseRangeByLex("set11", range, limit);
        System.out.println(set11);
        // ??????????????????
        Set set111 = redisTemplate.opsForZSet().reverseRangeByScore("set11", 0, Double.MAX_VALUE, 0, 1);
        System.out.println(set111);
    }

    @Test
    public void testSetGroupMsgTestData() {
        GroupMsgDTO dto = new GroupMsgDTO();
        dto.setContent("hahaha");
        dto.setTime(LocalDateTime.now().toString());
        dto.setGroupId(1L);
        dto.setFromUid(2L);
        dto.setMsgId("99090mskdfsdkfjdsf");
        dto.setMsgSeq(1L);
        redisTemplate.opsForZSet().add(RedisService.GROUP_MSGS_PRE + 1, dto, 1);
        // Long key = 1L;
        // Long value = 0L;
        // redisTemplate.opsForHash().put(RedisService.USER_LAST_MSG_SEQ_PRE + 2, key.toString(), value.toString());
    }

    @Test
    public void testRedisSetAPI() {
        Set intersect = redisTemplate.opsForSet().intersect("setA", "setB");
        System.out.println(intersect);
        Set intersect2 = redisTemplate.opsForSet().intersect("setA", "setC");
        System.out.println(intersect2);

    }

    // =======================Test Project API=========================

    @Test
    public void testGetGroupInfo() {
        GroupDataDTO dto = chatService.getGroupInfo(0L, 1L);
        System.out.println(dto);
    }

    @Autowired
    private FriendHandleService friendHandleService;

    @Test
    public void testGetHistoryMsgs() {
        Set<GroupMsgDTO> historyMsgs = redisService.getHistoryMsgs(1L, 11L);
        Iterator<GroupMsgDTO> iterator = historyMsgs.iterator();
        while (iterator.hasNext()) {
            GroupMsgDTO msg = iterator.next();
            System.out.println(msg.getMsgSeq() + " " + msg.getContent());
        }
        System.out.println("\n\n\n");

        List<ChatMsgDTO> collect = historyMsgs.stream().map(msg -> {
            ChatMsgDTO dto = new ChatMsgDTO();
            dto.setMsgSeq(msg.getMsgSeq());
            dto.setContent(msg.getContent());
            return dto;
        }).collect(Collectors.toList());
        Collections.reverse(collect);
        collect.forEach(i -> System.out.println(i.getMsgSeq() + " " + i.getContent()));
    }

    @Test
    public void testGetGroupOnlineUsers() {
        Set<Long> set = redisService.getGroupOnlineUsers(1);
        System.out.println(set);
    }

    @Test
    public void testgetSessionList() {
        ChatPageDTO dto = chatService.getChatPageData(1L);
        System.out.println(dto);
    }

    @Test
    public void testgetDialogueData() {
        DialogueDataDTO dialogueData = chatService.getDialogueData(1L, 2L, IMConstant.SINGLE_CHAT_TYPE);
        System.out.println(dialogueData.getIsOnline());
        for (ChatMsgDTO msg : dialogueData.getMsgs()) {
            System.out.println(msg);
        }
    }

    @Test
    public void testLogin() {
        UserDTO dto = userService.login("15895847456", "69999");
        System.out.println(dto);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisAPI() {
        Long size = redisTemplate.opsForHash().size("xxx");
        System.out.println(size);
    }

}
