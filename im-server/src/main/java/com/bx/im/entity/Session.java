package com.bx.im.entity;

import lombok.Data;

/**
 * 保存ws服务器中用户的会话信息
 */
@Data
public class Session {
    private Long id;

    private String name;

    private String phone;

    private String avatar;

    /**
     * 个性签名/自我简介
     */
    private String intro;

    @Override
    public String toString() {
        return "User{" + id + "-" + name + "}";
    }
}
