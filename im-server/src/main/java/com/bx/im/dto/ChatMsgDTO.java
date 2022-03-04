package com.bx.im.dto;

import lombok.Data;

@Data
public class ChatMsgDTO {
    // * demo: {msgId, msgSeq, fromUid, toId, type, content, time}
    private String msgId;
    private Long msgSeq;
    private Long fromUid;
    private Long toId;
    private Integer type;
    private String content;
    // 时间的ISO 8601格式对应的字符串
    private String time;
    // 针对单聊
    private Boolean hasRead;
}
