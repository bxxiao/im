package com.bx.im.service.application;

import java.util.List;

/*
* TODO：FriendHandleService中相关接口移到这里
* */
public interface GroupHandleService {

    /**
     * 创建群聊
     * 创建后先把群主入群，对于其它成员，先发送入群邀请，对方同意后才入群
     * @param masterUid
     * @param members
     * @param groupName
     */
    void createGroup(Long masterUid, List<Long> members, String groupName);
}
