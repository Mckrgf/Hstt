package com.example.yb.hstt.bean;

import java.io.Serializable;

/**
 * Created by TFHR02 on 2017/12/12.
 * 工單信息
 */
public class OrderFileInfo implements Serializable {
    private String file_url;
    private String file_id;

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
