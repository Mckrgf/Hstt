package com.example.yb.hstt.EventBus;

/**
 * Created by tfhr on 2017/10/31.
 */

public class HsttEvent {
    private int operate_type;//新建终端的操作类型

    private int tag_device_type;//消息类型

    private String TAG;//1为通知工单状态为1的页面打开"更多"弹窗,2同理

    private boolean select_by_brand;//新建用户下设备,区别dialog是选择品牌还是型号,true为品牌

    public boolean isSelect_by_brand() {
        return select_by_brand;
    }

    public void setSelect_by_brand(boolean select_by_brand) {
        this.select_by_brand = select_by_brand;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getREFRESH_POPUPWINDOW() {
        return REFRESH_POPUPWINDOW;
    }

    public void setREFRESH_POPUPWINDOW(String REFRESH_POPUPWINDOW) {
        this.REFRESH_POPUPWINDOW = REFRESH_POPUPWINDOW;
    }

    private String REFRESH_POPUPWINDOW = "REFRESH_POPUPWINDOW";

    private Object msg_body;

    public Object getMsg_body() {
        return msg_body;
    }

    public void setMsg_body(Object msg_body) {
        this.msg_body = msg_body;
    }

    public int getTag_device_type() {
        return tag_device_type;
    }

    public void setTag_device_type(int tag_device_type) {
        this.tag_device_type = tag_device_type;
    }

    public String getREFRESH_USERS() {
        return REFRESH_USERS;
    }

    public void setREFRESH_USERS(String REFRESH_USERS) {
        this.REFRESH_USERS = REFRESH_USERS;
    }

    private String REFRESH_USERS = "refresh_users";//通知用户列表刷新

    public int getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(int operate_type) {
        this.operate_type = operate_type;
    }
}
