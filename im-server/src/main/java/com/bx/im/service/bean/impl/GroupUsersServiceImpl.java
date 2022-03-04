package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.im.entity.GroupUsers;
import com.bx.im.mapper.GroupUsersMapper;
import com.bx.im.service.bean.IGroupUsersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bx
 * @since 2022-03-04
 */
@Service
public class GroupUsersServiceImpl extends ServiceImpl<GroupUsersMapper, GroupUsers> implements IGroupUsersService {

}
