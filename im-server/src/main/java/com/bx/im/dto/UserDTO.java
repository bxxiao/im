package com.bx.im.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private String token;
}
