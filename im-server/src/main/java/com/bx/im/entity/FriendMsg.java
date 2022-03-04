package com.bx.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author bx
 * @since 2022-02-23
 */
@TableName("friend_msg")
public class FriendMsg implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uuid
     */
    private String msgId;

    /**
     * 消息的序列号，用于实现顺序性
     */
    private Long msgSeq;

    /**
     * 发送者id
     */
    private Long senderUid;

    private Long toUid;

    /**
     * 消息类型，目前只有文本消息；后续可作扩展用
     */
    private Integer msgType;

    private String msgContent;

    private LocalDateTime time;

    /**
     * 1：已读；0：未读（默认）
     */
    private Boolean hasRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(Long msgSeq) {
        this.msgSeq = msgSeq;
    }

    public Long getToUid() {
        return toUid;
    }

    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Long getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(Long senderUid) {
        this.senderUid = senderUid;
    }

    @Override
    public String toString() {
        return "FriendMsg{" +
        "id=" + id +
        ", msgId=" + msgId +
        ", msgSeq=" + msgSeq +
        ", toUid=" + toUid +
        ", msgType=" + msgType +
        ", msgContent=" + msgContent +
        ", time=" + time +
        ", hasRead=" + hasRead +
        ", senderUid=" + senderUid +
        "}";
    }
}
