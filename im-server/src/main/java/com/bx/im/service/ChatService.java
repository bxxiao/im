package com.bx.im.service;

import com.bx.im.dto.ChatPageDTO;
import com.bx.im.dto.ChatSessionDTO;
import com.bx.im.dto.DialogueDataDTO;

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
}
