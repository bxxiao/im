package com.bx.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author bx
 * @since 2022-03-13
 */
public class Apply implements Serializable {

    /*
    * status对应的3种状态
    * */
    public static final int DEALING = 0;
    public static final int AGREED = 1;
    public static final int REJECTED = 2;

    /*
    * 3种类型：好友申请、群聊邀请、入群申请
    * */
    public static final int FRIEND_APPLY = 1;
    public static final int GROUP_INVITATION = 2;
    public static final int GROUP_APPLY = 3;

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long senderUid;

    private Long toUid;

    /**
     * 若类型是群聊邀请，表示对应的群id
     */
    private Long groupId;

    /**
     * 1-好友申请；2-群聊邀请
     */
    private Integer type;

    /**
     * 0-已发出；1-已同意；2-已拒绝
     */
    private Integer status;

    private LocalDateTime time;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(Long senderUid) {
        this.senderUid = senderUid;
    }

    public Long getToUid() {
        return toUid;
    }

    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Apply{" +
        "id=" + id +
        ", senderUid=" + senderUid +
        ", toUid=" + toUid +
        ", groupId=" + groupId +
        ", type=" + type +
        ", status=" + status +
        ", time=" + time +
        "}";
    }
}
