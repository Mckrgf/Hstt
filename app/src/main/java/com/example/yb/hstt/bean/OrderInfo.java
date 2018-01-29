package com.example.yb.hstt.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TFHR02 on 2017/12/12.
 * 工单
 */
public class OrderInfo implements Serializable {
    private String order_status;
    private String device_type;
    private String factory;
    private String crDescribe;
    private String address;
    private List<OrderFileInfo> files;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCrDescribe() {
        return crDescribe;
    }

    public void setCrDescribe(String crDescribe) {
        this.crDescribe = crDescribe;
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

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public List<OrderFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<OrderFileInfo> files) {
        this.files = files;
    }
}
