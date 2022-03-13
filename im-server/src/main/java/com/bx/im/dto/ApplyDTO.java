package com.bx.im.dto;

import lombok.Data;

@Data
public class ApplyDTO {
    private Integer id;

    private Long senderUid;

    private String senderAvatar;

    private String senderName;

    private Long toUid;

    private Long groupId;

    private String groupName;

    /**
     * 1-好友申请；2-群聊邀请
     */
    private Integer type;

    /**
     * 0-已发出；1-已同意；2-已拒绝
     */
    private Integer status;

    private String time;
}
