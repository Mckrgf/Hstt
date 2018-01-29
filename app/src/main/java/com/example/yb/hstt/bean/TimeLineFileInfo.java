package com.example.yb.hstt.bean;

/**
 * Created by TFHR02 on 2017/12/21.
 */
public class TimeLineFileInfo {
    private String owwoAttaId;
    private String attaFileName;
    private String attaFileSuffix;
    private String attaFilePath;
    private int owrdpId;

    public TimeLineFileInfo(String owwoAttaId, String attaFileName, String attaFileSuffix, String attaFilePath, int owrdpId) {
        this.owwoAttaId = owwoAttaId;
        this.attaFileName = attaFileName;
        this.attaFileSuffix = attaFileSuffix;
        this.attaFilePath = attaFilePath;
        this.owrdpId = owrdpId;
    }

    public String getOwwoAttaId() {
        return owwoAttaId;
    }

    public void setOwwoAttaId(String owwoAttaId) {
        this.owwoAttaId = owwoAttaId;
    }

    public String getAttaFileName() {
        return attaFileName;
    }

    public void setAttaFileName(String attaFileName) {
        this.attaFileName = attaFileName;
    }

    public String getAttaFileSuffix() {
        return attaFileSuffix;
    }

    public void setAttaFileSuffix(String attaFileSuffix) {
        this.attaFileSuffix = attaFileSuffix;
    }

    public String getAttaFilePath() {
        return attaFilePath;
    }

    public void setAttaFilePath(String attaFilePath) {
        this.attaFilePath = attaFilePath;
    }

    public int getOwrdpId() {
        return owrdpId;
    }

    public void setOwrdpId(int owrdpId) {
        this.owrdpId = owrdpId;
    }
}
