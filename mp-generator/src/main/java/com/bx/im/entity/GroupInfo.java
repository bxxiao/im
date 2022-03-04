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
 * @since 2022-03-04
 */
@TableName("group_info")
public class GroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String avatar;

    /**
     * 群主uid

     */
    private Long masterUid;

    /**
     * 群成员数
     */
    private Long memberNum;

    /**
     * 预留；管理员数
     */
    private Integer adminNum;

    private LocalDateTime createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getMasterUid() {
        return masterUid;
    }

    public void setMasterUid(Long masterUid) {
        this.masterUid = masterUid;
    }

    public Long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Long memberNum) {
        this.memberNum = memberNum;
    }

    public Integer getAdminNum() {
        return adminNum;
    }

    public void setAdminNum(Integer adminNum) {
        this.adminNum = adminNum;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
        "id=" + id +
        ", name=" + name +
        ", avatar=" + avatar +
        ", masterUid=" + masterUid +
        ", memberNum=" + memberNum +
        ", adminNum=" + adminNum +
        ", createTime=" + createTime +
        "}";
    }
}
