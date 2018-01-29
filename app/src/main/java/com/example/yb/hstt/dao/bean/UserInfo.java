package com.example.yb.hstt.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created by tfhr on 2017/11/9.
 */

@Entity
public class UserInfo implements Serializable{
    @Unique
    @Id(autoincrement = true)
    private Long ID;
    private String user_id;//用户id
    private String user_addr;//用户地址
    private String user_name;//用户姓名
    private String user_phone;//用户电话
    private boolean is_upload;//是否上报
    public String getUser_phone() {
        return this.user_phone;
    }
    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }
    public String getUser_name() {
        return this.user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_addr() {
        return this.user_addr;
    }
    public void setUser_addr(String user_addr) {
        this.user_addr = user_addr;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public boolean getIs_upload() {
        return this.is_upload;
    }
    public void setIs_upload(boolean is_upload) {
        this.is_upload = is_upload;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 519747344)
    public UserInfo(Long ID, String user_id, String user_addr, String user_name,
            String user_phone, boolean is_upload) {
        this.ID = ID;
        this.user_id = user_id;
        this.user_addr = user_addr;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.is_upload = is_upload;
    }
    @Generated(hash = 1279772520)
    public UserInfo() {
    }
}
