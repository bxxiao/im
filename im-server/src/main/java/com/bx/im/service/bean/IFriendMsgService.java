package com.bx.im.service.bean;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.im.entity.FriendMsg;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bx
 * @since 2022-02-23
 */
public interface IFriendMsgService extends IService<FriendMsg> {
    /**
     * 获取当前用户的所有未读消息数
     * Map<friend_uid —— unread num>
     * @param uid
     * @return
     */
    Map<Long, Long> getAllUnreadNum(long uid);
}
