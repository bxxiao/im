package com.bx.im.service;

import com.bx.im.entity.FriendMsg;
import com.baomidou.mybatisplus.extension.service.IService;

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
    Map<Long, Integer> getAllUnreadNum(long uid);
}
