package com.bx.im.controller;

import com.bx.im.dto.*;
import com.bx.im.util.CommonResult;
import com.bx.im.service.ChatService;
import com.bx.im.util.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    /**
     * 获取会话列表
     * @param uid
     * @return
     */
    @GetMapping("/getSessionList")
    public CommonResult<ChatPageDTO> getSessionList(Long uid) {
        ChatPageDTO dto = chatService.getChatPageData(uid);
        return CommonResult.success(dto);
    }

    /**
     * 点击会话项进入聊天面板时，获取初始化信息（包括是否在线、聊天记录等）
     *
     * @param uid
     * @param toId 对方的id（用户或群聊）
     * @return
     */
    @GetMapping("/getDialogueData")
    public CommonResult<DialogueDataDTO> getDialogueData(Long uid, Long toId, Integer type) {
        DialogueDataDTO dto = chatService.getDialogueData(uid, toId, type);
        if (dto != null)
            return CommonResult.success(dto);
        else
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @PostMapping("/createSession")
    public CommonResult<ChatSessionDTO> createSession(Long uid, Long toId, int type) {
        ChatSessionDTO dto = chatService.createSession(uid, toId, type);
        if (dto != null)
            return CommonResult.success(dto);
        else
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
    }

    /**
     *
     * @param lastSeq
     * @param groupId
     * @param uid
     * @return
     */
    @PostMapping("/updateLastSeq")
    public CommonResult updateLastSeq(Long lastSeq, Long groupId, Long uid) {
        chatService.updateLastSeq(lastSeq, groupId, uid);
        return CommonResult.success(null);
    }

    @GetMapping("/loadMsgs")
    public CommonResult<List<ChatMsgDTO>> loadMsgs(Long uid, Long toId, int type, Long msgSeq) {
        List<ChatMsgDTO> msgs =  chatService.loadMsgs(uid, toId, type, msgSeq);
        return CommonResult.success(msgs);
    }

    @GetMapping("/groupInfo")
    public CommonResult<GroupDataDTO> getGroupInfo(Long uid, Long groupId) {
        GroupDataDTO dto = chatService.getGroupInfo(uid, groupId);
        return CommonResult.success(dto);
    }
}
