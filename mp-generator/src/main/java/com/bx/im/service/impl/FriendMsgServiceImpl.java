package com.bx.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bx.im.entity.FriendMsg;
import com.bx.im.mapper.FriendMsgMapper;
import com.bx.im.service.IFriendMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bx
 * @since 2022-02-23
 */
@Service
public class FriendMsgServiceImpl extends ServiceImpl<FriendMsgMapper, FriendMsg> implements IFriendMsgService {
    @Override
    public Map<Long, Integer> getAllUnreadNum(long uid) {
        Map<Long, Integer> result = new HashMap<>();

        QueryWrapper<FriendMsg> wrapper = new QueryWrapper<>();
        // 发给当前用户的所有未读消息，按发送者进行分组，计数
        wrapper.eq("to_uid", uid).eq("has_read", 0).groupBy("sender_uid")
                // 指定查询列表
                .select("sender_uid", "count(*) as num");
        List<Map<String, Object>> mapList = this.listMaps(wrapper);

        mapList.forEach(map -> {
            result.put((Long) map.get("sender_uid"), (Integer) map.get("num"));
        });

        return result;
    }
}
