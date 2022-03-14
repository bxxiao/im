package com.bx.im.dto;

import lombok.Data;

@Data
public class FriendDTO {
    private Long id;

    private String name;

    // private String phone;

    private String avatar;

    /**
     * 个性签名/自我简介
     */
    private String intro;

    private Boolean isOnline;
}
