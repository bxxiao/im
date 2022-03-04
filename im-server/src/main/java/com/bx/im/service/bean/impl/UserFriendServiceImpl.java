package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.im.entity.UserFriend;
import com.bx.im.mapper.UserFriendMapper;
import com.bx.im.service.bean.IUserFriendService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bx
 * @since 2022-01-31
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {

}
