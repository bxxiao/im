package com.bx.im.service.bean;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.im.dto.ItemDTO;
import com.bx.im.dto.UserDTO;
import com.bx.im.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bx
 * @since 2022-01-31
 */
public interface IUserService extends IService<User> {

    UserDTO login(String phone, String password);

    UserDTO getUserInfo(Long uid);
}
