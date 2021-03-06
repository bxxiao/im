package com.bx.im.service.application;

import com.bx.im.dto.ApplyDTO;
import com.bx.im.dto.FriendDTO;
import com.bx.im.dto.GroupDTO;
import com.bx.im.dto.ItemDTO;

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

    /*
    * online表示是否查询好友是否在线
    * */
    List<FriendDTO> listFriends(Long uid, Boolean online);

    List<GroupDTO> listGroups(Long uid);

    /**
     * 将指定用户踢出群聊
     * @param uid 发起踢出操作的群主或管理员（to be...）
     * @param groupId
     * @param deleted
     */
    void deleteGroupMember(Long uid, Long groupId, Long deleted);

    void deleteFriend(Long uid, Long friendUid);

    /**
     * 退出群聊
     * @param uid
     * @param groupId
     */
    void quitGroup(Long uid, Long groupId);

    /**
     * 申请添加好友或申请入群
     * @param targetId 目标用户id；或目标群id
     * @param type 申请类型
     */
    void sendApply(Long targetId, Integer type);

    /**
     * 根据关键词在name和phone字段中搜索用户和群
     * @param keyword
     * @return
     */
    List<ItemDTO> searchUserAndGroup(String keyword);
}
