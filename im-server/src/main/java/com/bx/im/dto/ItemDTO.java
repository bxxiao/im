package com.bx.im.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 搜索用户或群时返回的搜索项
 * type指定类型: 1-用户;2-群
 */
@Data
@Builder
public class ItemDTO {
    private int type;

    // 相同的字段
    private Long id;
    private String name;
    private String avatar;

    // 仅对用户(type为1)
    private String phone;

    // 仅对群(type为2)
    private String groupNumber;
}
