package com.bx.im.dto;

import lombok.Data;

import java.util.List;

@Data
public class DialogueDataDTO {
    private Boolean isOnline;
    private List<ChatMsgDTO> msgs;
}
