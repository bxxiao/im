package com.bx.im.dto;

import lombok.Data;

@Data
public class ChatMsgCache {
    private Integer type;
    private String msgId;
    private Long msgSeq;
    private Long fromUid;
    private Long toId;
    private String content;
    private String time;
    private Integer contentType;
    private String username;
}
