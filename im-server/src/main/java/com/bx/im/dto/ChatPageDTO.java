package com.bx.im.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatPageDTO {
    private List<ChatSessionDTO> sessionList;
    private UserDTO userInfo;
}
