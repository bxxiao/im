package com.bx.im.cache;

import com.bx.im.dto.ChatMsgCache;
import com.bx.im.dto.GroupMsgDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    String SINGE_MSG_SEQ_KEY = "SINGE_MSG_SEQ_KEY";

    /*
    * 类型：Set
    * 存储在线用户id
    * */
    String ONLINE_USERS_KEY = "ONLINE_USERS";

    /*
    * 类型：ZSet
    * 存储群聊消息的zset的键前缀
    * GROUP_MSGS_【groupId】 score值是消息序列号
    * */
    String GROUP_MSGS_PRE = "GROUP_MSGS_";

    /*
    * 类型：Set
    * 存放群消息的msgId，每个群聊对应一个Set
    * */
    String GROUP_MSGS_ID_SET_PRE = "GROUP_MSGS_ID_SET_";

    /*
    * 类型：string
    * 消息序列号键的前缀
    * GROUP_MSG_SEQ_【groupId】 每个群对应一个键
    * */
    String GROUP_MSG_SEQ_PRE = "GROUP_MSG_SEQ_";

    /*
    * 类型：Hash
    * 保存用户加入的所有群聊对应的last_msgSeq，
    * 类型是Hash【groupId - seq】 这里要注意groupId和seq都是以字符串的形式存入，使用long类型存入redis，在取出时会转换成Integer
    * USER_LAST_MSG_SEQ_【uid】
    * */
    String USER_LAST_MSG_SEQ_PRE = "USER_LAST_GMSG_SEQ_";

    /*
    * 类型：Set
    * 保存一个群聊的所有用户id，每个群对应一个
    * */
    String GROUP_ALL_MEMBERS_PRE = "GROUP_ALL_MEMBERS_";

    /*
    * Set
    * 群聊消息被撤回的消息id
    * */
    String GROUP_CANCELED_MSG_IDS_PRE = "GROUP_CANCELED_MSG_IDS_";

    /*
    * Hash
    * 存放被发送后的消息，进行可达性投递
    * msgId——msg
    * */
    String SENDING_CACHE_MSGS_KEY = "SENDING_CACHE_MSGS";

    /*
    * Hash
    * 记录SENDING_CACHE_MSGS的消息的投递用户id及其已重发次数
    * 映射关系：uid——重发次数 3次后删除
    * */
    String RESEND_MSG_RECORDS_PRE = "RESEND_MSG_RECORDS_";

    //======================通用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    void userOnline(Long uid);

    void userOffline(Long uid);

    /**
     * 设置超期时间
     */
    boolean expire(String key, long expire);

    /**
     * 删除数据
     */
    void remove(String key);

    Boolean isUserOnline(Long id);


    //======================通用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //===============单聊>>>>>>>>>>>>>>>>>>
    /**
     * 自增并获取单聊消息序列号
     * @return
     */
    Long getSingleMsgSeq();
    //====================单聊<<<<<<<<<<<<<<<<<<<<<

    //=====================================群聊>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 保存消息的同时保存消息的id到对应的set，两个操作在一个Redis事务中执行
     * 避免2个命令只执行了一个的情况
     * @param msg
     */
    void saveGroupMsg(GroupMsgDTO msg);

    /**
     * 自增并获取对应群的消息序列号
     * @param groupId
     * @return
     */
    Long getGroupMsgSeq(Long groupId);

    /**
     * 获取用户对应的USER_LAST_GMSG_SEQ的所有值（Map形式）
     * @param uid
     * @return
     */
    Map<Long, Long> getUserLastSeqMap(Long uid);

    /**
     * 根据序列号获取在指定群中未读消息数
     * @param groupId
     * @param seq
     * @return
     */
    Long getGroupUnReadCount(Long groupId, Long seq);

    /**
     * 从对应群的zset中获取最后一条消息
     * @param groupId
     * @param curUid
     * @return
     */
    GroupMsgDTO getLastGroupMsg(Long groupId, Long curUid);

    /**
     * 获取指定用户在指定群的最后消息序列号
     * @param uid
     * @param groupId
     * @return
     */
    Long getGroupLastMsgSeq(Long uid, Long groupId);


    /**
     * 拉取在指定群的所有新消息，并拉取新消息前的10条消息
     * @return
     */
    List<GroupMsgDTO> getNewGroupMsgs(Long uid, Long groupId);

    /**
     * 根据消息id判断消息是否已存在
     * @param groupId
     * @param msgId
     * @return
     */
    Boolean msgExist(Long groupId, String msgId);

    /**
     * 获取指定群在线用户的uid列表
     * @param groupId
     * @return
     */
    Set<Long> getGroupOnlineUsers(long groupId);

    /**
     * 更新用户在指定群的last_msgSeq，
     * 只有给定序号（seq）大于当前序号才更新
     * @param seq
     * @param groupId
     * @param uid
     */
    void updateLastSeq(Long seq, Long groupId, Long uid);

    /**
     * 根据消息序号在指定群记录的zset往前拉取若干消息返回
     * @param groupId
     * @param msgSeq
     * @return
     */
    Set<GroupMsgDTO> getHistoryMsgs(Long groupId, Long msgSeq);

    /**
     * 新成员入群时，设置在该群的last_msgSeq
     * @param uid
     * @param groupId
     */
    void initLastSeq(Long uid, Long groupId);

    /**
     * 新建群时，初始化群的序列号键
     * @param groupId
     */
    void initGroupSeqKey(Long groupId);

    /**
     * 用户退出或被踢出群聊后，移除在 USER_LAST_GMSG_SEQ_ 中对应的字段
     * @param uid
     * @param groupId
     */
    void removeLastSeqKey(Long uid, Long groupId);

    /**
     * 群解散后，在redis中使用事务删除相关数据：
     *      群消息记录
     *      群消息序列号key
     *      各群成员在该群的last_msgSeq
     * @param groupId
     * @param memberIds
     */
    void dissolveGroup(Long groupId, List<Long> memberIds);

    void setMsgCanceled(Long groupId, String msgId);

    /**
     * 消息被发送后进行可靠性投递
     * @param msg
     * @param targetUids
     */
    void setChatMsgInCheck(ChatMsgCache msg, Set<Long> targetUids);

    //=====================================群聊<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
