package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import com.bx.im.util.SpringUtils;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IExceptionCode;
import com.bx.im.util.exception.IMException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
        wrapper.eq("id", uid).select("id", "name", "phone", "avatar", "intro");
        User user = this.getOne(wrapper);
        if (user == null)
            return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());
        dto.setIntro(user.getIntro());

        return dto;
    }

    @Override
    public void doRegistry(String username, String phone, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", username);
        if (this.count(wrapper) > 0)
            throw new IMException(ExceptionCodeEnum.USERNAME_DUPLICATE);

        wrapper.clear();
        wrapper.eq("phone", phone);
        if (this.count(wrapper) > 0)
            throw new IMException(ExceptionCodeEnum.PHONE_DUPLICATE);

        User user = new User();
        user.setName(username);
        user.setPhone(phone);
        user.setPassword(password);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setModifiedTime(now);


        this.save(user);
    }

    @Override
    public void editInfo(Long uid, String name, String phone, String intro) {
        SpringUtils.checkUser(uid);
        if (!StringUtils.hasText(name) || !StringUtils.hasText(phone) || !StringUtils.hasText(intro))
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", uid).set("name", name).set("phone", phone).set("intro", intro);
        this.update(wrapper);
    }

    @Override
    public void editPwd(Long uid, String oldPwd, String newPwd) {
        SpringUtils.checkUser(uid);
        if (!StringUtils.hasText(oldPwd) || !StringUtils.hasText(newPwd))
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", uid).eq("password", oldPwd);

        if (this.count(wrapper) <= 0)
            throw new IMException(ExceptionCodeEnum.OLD_PASSWORD_ERROR);

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", uid).set("password", newPwd);
        this.update(updateWrapper);
    }
}
