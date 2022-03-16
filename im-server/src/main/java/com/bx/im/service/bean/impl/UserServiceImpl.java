package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.im.dto.ItemDTO;
import com.bx.im.dto.UserDTO;
import com.bx.im.entity.GroupInfo;
import com.bx.im.entity.User;
import com.bx.im.entity.UserFriend;
import com.bx.im.mapper.UserMapper;
import com.bx.im.service.bean.IUserFriendService;
import com.bx.im.service.bean.IUserService;
import com.bx.im.util.IMConstant;
import com.bx.im.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bx
 * @since 2022-01-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IUserFriendService userFriendService;

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

    @Override
    public UserDTO getUserInfo(Long uid) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", uid).select("id", "name", "phone", "avatar");
        User user = this.getOne(wrapper);
        if (user == null)
            return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());

        return dto;
    }
}
