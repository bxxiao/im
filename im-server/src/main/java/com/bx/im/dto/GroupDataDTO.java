package com.bx.im.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDataDTO {
    private Long id;
    private String name;
    private Long masterId;
    private String masterName;
    private List<GroupMember> members;

    @Data
    public static class GroupMember {
        private Long uid;
        private String name;
        private String avatar;
    }
}
