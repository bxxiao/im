package com.bx.im.controller;

import com.bx.im.dto.ApplyDTO;
import com.bx.im.dto.FriendDTO;
import com.bx.im.dto.GroupDTO;
import com.bx.im.dto.ItemDTO;
import com.bx.im.service.FriendHandleService;
import com.bx.im.util.CommonResult;
import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friend/")
public class FriendController {

    @Autowired
    private FriendHandleService friendHandleService;

    /**
     * 获取好友申请列表（好友发出的申请和群邀请统称为好友申请）
     * @param uid
     * @return
     */
    @GetMapping("/listApply")
    public CommonResult<List<ApplyDTO>> listApplys(Long uid) {
        List<ApplyDTO> applyDTOS = friendHandleService.listApplys(uid);
        if (applyDTOS == null)
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
        else
            return CommonResult.success(applyDTOS);
    }

    /**
     * 处理好友申请
     * @param applyId
     * @param dealResult
     * @return
     */
    @PostMapping("/dealApply")
    public CommonResult dealApply(Integer applyId, Integer dealResult) {
        if (friendHandleService.dealApply(applyId, dealResult))
            return CommonResult.success(null);
        else
            return CommonResult.error(ExceptionCodeEnum.REQUEST_ERROR);
    }

    @GetMapping("/listFriends")
    public CommonResult<List<FriendDTO>> listFriends(Long uid) {
        List<FriendDTO> dtos = friendHandleService.listFriends(uid);
        return CommonResult.success(dtos);
    }

    @GetMapping("/listGroups")
    public CommonResult<List<GroupDTO>> listGroups(Long uid) {
        List<GroupDTO> dtos = friendHandleService.listGroups(uid);
        return CommonResult.success(dtos);
    }

    /**
     * 删除群成员
     * @param uid 发出操作的用户
     * @param groupId
     * @param deleted
     * @return
     */
    @PostMapping("/delete/groupMember")
    public CommonResult deleteGroupMember(Long uid, Long groupId, Long deleted) {
        if (uid == null || groupId == null || deleted == null)
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);
        friendHandleService.deleteGroupMember(uid, groupId, deleted);
        return CommonResult.success();
    }

    /**
     * 删除好友
     * @param uid
     * @param friendUid
     * @return
     */
    @PostMapping("/delete/friend")
    public CommonResult deleteFriend(Long uid, Long friendUid) {
        friendHandleService.deleteFriend(uid, friendUid);
        return CommonResult.success();
    }

    @PostMapping("/quitGroup")
    public CommonResult quitGroup(Long uid, Long groupId) {
        if (uid == null || groupId == null)
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        friendHandleService.quitGroup(uid, groupId);
        return CommonResult.success();
    }

    @PostMapping("/apply")
    public CommonResult sendApply(Long targetId, Integer type) {
        if (targetId == null || type == null)
            throw new IMException(ExceptionCodeEnum.PARAM_ERROR);

        friendHandleService.sendApply(targetId, type);
        return CommonResult.success();
    }


    @GetMapping("/search")
    public CommonResult<List<ItemDTO>> search(String keyword) {
        List<ItemDTO> userDTOS = friendHandleService.searchUserAndGroup(keyword);
        return CommonResult.success(userDTOS);
    }
}
