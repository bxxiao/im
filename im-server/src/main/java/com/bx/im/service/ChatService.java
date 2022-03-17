package com.bx.im.service;

import com.bx.im.dto.*;

import java.util.List;

/**
 * 封装聊天面板相关业务处理
 */
public interface ChatService {
    /**
     * 获取指定用户的会话列表（包含未读消息数，最后一条消息）以及用户信息
     * @param uid
     * @return
     */
    ChatPageDTO getChatPageData(Long uid);

    /**
     * 点击会话项进入聊天面板时，获取初始化信息（包括是否在线、最后20条聊天记录等）
     * @param uid
     * @param toId
     * @param type
     * @return
     */
    DialogueDataDTO getDialogueData(Long uid, Long toId, Integer type);

    /**
     * 创建新会话项，并返回会话对应的头像和名字
     * @param uid
     * @param toId
     * @param type
     * @return
     */
    ChatSessionDTO createSession(Long uid, Long toId, int type);

    /**
     * 更新用户在指定群的last_msgSeq
     * @param seq
     * @param groupId
     * @param uid
     */
    void updateLastSeq(Long seq, Long groupId, Long uid);

    /**
     * 上滑加载消息，根据消息序号往前拉若干消息返回，若没有则返回null
     * @param uid
     * @param toId
     * @param type
     * @param msgSeq
     * @return
     */
    List<ChatMsgDTO> loadMsgs(Long uid, Long toId, int type, Long msgSeq);

    /**
     * uid预留；后续可能用到
     * @param uid
     * @param groupId
     * @return
     */
    GroupDataDTO getGroupInfo(Long uid, Long groupId);

    void deleteSession(Long toId, int type);
}
