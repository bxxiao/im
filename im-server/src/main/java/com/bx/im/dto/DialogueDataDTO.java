package com.bx.im.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DialogueDataDTO {
    // 仅对单聊
    private Boolean isOnline;
    // 仅对群聊
    private Map<Long, String> avatarMap;
    private List<ChatMsgDTO> msgs;
}
