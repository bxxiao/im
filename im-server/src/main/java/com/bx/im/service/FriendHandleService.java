package com.bx.im.service;

import com.bx.im.dto.ApplyDTO;

import java.util.List;

public interface FriendHandleService {

    /**
     * 获取全部申请
     * @param uid
     * @return
     */
    List<ApplyDTO> listApplys(Long uid);

    /**
     * 处理好友申请
     * @param id 申请对应的数据库记录id
     * @param dealResult 处理结果，1-同意；2-拒绝
     * @return
     */
    boolean dealApply(Integer id, Integer dealResult);
}