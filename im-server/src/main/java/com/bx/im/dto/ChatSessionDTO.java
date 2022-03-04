package com.bx.im.dto;

import lombok.Data;

@Data
public class ChatSessionDTO {
    private long toId;
    private String name;
    private String avatar;
    private Integer type;

    private String lastMsg;
    // ISO 8601格式对应的字符串
    private String time;
    private Long unread;
}
