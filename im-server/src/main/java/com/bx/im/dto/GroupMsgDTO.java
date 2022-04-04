package com.bx.im.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupMsgDTO implements Serializable {
    private Long groupId;
    private String msgId;
    private Long msgSeq;
    private Long fromUid;
    private String content;
    private String time;

    private String username;

    // 预留，表示消息类型
    private Integer type;

    private Boolean hasCancel;
}
