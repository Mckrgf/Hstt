package com.example.yb.hstt.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created by huangsf on 2017-07-21.
 */
@Entity
public class LoginInfo implements Serializable {
    @Unique
    @Id(autoincrement = true)
    private Long ID;

    private String id;//用戶编号
    private String job_number;//用户工号
    private String user_name;//用户名称
    private String user_phone;//用户电话

    public String getUser_org_name() {
        return user_org_name;
    }

    public void setUser_org_name(String user_org_name) {
        this.user_org_name = user_org_name;
    }

    private String user_org_name;//用户所在部门
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
    public String getJob_number() {
        return this.job_number;
    }
    public void setJob_number(String job_number) {
        this.job_number = job_number;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 1403313294)
    public LoginInfo(Long ID, String id, String job_number, String user_name,
            String user_phone, String user_org_name) {
        this.ID = ID;
        this.id = id;
        this.job_number = job_number;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_org_name = user_org_name;
    }

    @Generated(hash = 1911824992)
    public LoginInfo() {
    }


}
