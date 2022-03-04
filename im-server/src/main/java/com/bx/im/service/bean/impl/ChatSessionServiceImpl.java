package com.bx.im.service.bean.impl;

import com.bx.im.entity.ChatSession;
import com.bx.im.mapper.ChatSessionMapper;
import com.bx.im.service.bean.IChatSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bx
 * @since 2022-02-22
 */
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements IChatSessionService {

}
