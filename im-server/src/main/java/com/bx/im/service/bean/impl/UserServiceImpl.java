package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.im.dto.UserDTO;
import com.bx.im.entity.User;
import com.bx.im.mapper.UserMapper;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.JwtUtil;
import org.springframework.beans.BeanUtils;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public UserDTO login(String phone, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User entity = new User();
        entity.setPhone(phone);
        entity.setPassword(password);
        wrapper.setEntity(entity);
        User user = this.getOne(wrapper);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            String token = JwtUtil.generateJWT(user.getId());
            userDTO.setToken(token);
            return userDTO;
        }
        return null;
    }
}
