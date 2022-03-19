package com.bx.im.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupCreateParam {
    private Long masterUid;
    private List<Long> members;
    private String groupName;
}
