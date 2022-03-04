package com.bx.im.service.bean.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.im.entity.GroupInfo;
import com.bx.im.mapper.GroupInfoMapper;
import com.bx.im.service.bean.IGroupInfoService;
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
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements IGroupInfoService {

}
