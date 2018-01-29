package com.example.yb.hstt.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by tfhr on 2017/11/14.
 */

@Entity
public class DeviceInfos {

    @Unique
    @Id(autoincrement = true)
    private Long ID;
    private String dev_id;//设备或终端id
    private String img_url;//图片url
    private int state;//上传状态,0未上传,1正在上传,2已上传,3上传失败
    public String getImg_url() {
        return this.img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public String getDev_id() {
        return this.dev_id;
    }
    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    @Generated(hash = 794596146)
    public DeviceInfos(Long ID, String dev_id, String img_url, int state) {
        this.ID = ID;
        this.dev_id = dev_id;
        this.img_url = img_url;
        this.state = state;
    }
    @Generated(hash = 379514798)
    public DeviceInfos() {
    }

}
