package com.bx.im.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupInviteParam {
    private Long groupId;
    private List<Long> friendIds;
}
