package com.example.yb.hstt.View.SlidingLib;

/**
 * Created by TFHR02 on 2017/10/11.
 */
public class OrderInfoList {
    private int resId;
    private String device_type;//设备类型
    private String factory;//生产厂家
    private String cd_id;//设备更换（维修）表ID
    private String status;//状态
    private int textcolor;
    private int is_haveFile;//是否有文件

    public OrderInfoList(int resId, String device_type, String factory, String cd_id, String status, int textcolor, int is_haveFile) {
        this.resId = resId;
        this.device_type = device_type;
        this.factory = factory;
        this.cd_id = cd_id;
        this.status = status;
        this.textcolor = textcolor;
        this.is_haveFile = is_haveFile;
    }

    public String getCd_id() {
        return cd_id;
    }

    public void setCd_id(String cd_id) {
        this.cd_id = cd_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public int getIs_haveFile() {
        return is_haveFile;
    }

    public void setIs_haveFile(int is_haveFile) {
        this.is_haveFile = is_haveFile;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTextcolor() {
        return textcolor;
    }

    public void setTextcolor(int textcolor) {
        this.textcolor = textcolor;
    }
}

