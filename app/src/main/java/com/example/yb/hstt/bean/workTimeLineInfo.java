package com.example.yb.hstt.bean;

import java.util.List;

/**
 * Created by TFHR02 on 2017/12/21.
 */
public class workTimeLineInfo {
    private int outworkerId;
    private String chooseSolutionTypeNo;
    private String currentLat;
    private String outworkerName;
    private String owwoId;
    private String owrdpId;
    private List<TimeLineFileInfo> newFiles;
    private List<TimeLineFileInfo> oldFiles;
    private List<TimeLineFileInfo> files;

    private List<TimeLineDeviceInfo> devInfo;
    private String comment;
    private String owrdpTypeNo;
    private String currentDt;
    private String currentLng;

    public int getOutworkerId() {
        return outworkerId;
    }

    public void setOutworkerId(int outworkerId) {
        this.outworkerId = outworkerId;
    }

    public String getChooseSolutionTypeNo() {
        return chooseSolutionTypeNo;
    }

    public void setChooseSolutionTypeNo(String chooseSolutionTypeNo) {
        this.chooseSolutionTypeNo = chooseSolutionTypeNo;
    }

    public String getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(String currentLat) {
        this.currentLat = currentLat;
    }

    public String getOwwoId() {
        return owwoId;
    }

    public void setOwwoId(String owwoId) {
        this.owwoId = owwoId;
    }

    public String getOutworkerName() {
        return outworkerName;
    }

    public void setOutworkerName(String outworkerName) {
        this.outworkerName = outworkerName;
    }

    public String getOwrdpId() {
        return owrdpId;
    }

    public void setOwrdpId(String owrdpId) {
        this.owrdpId = owrdpId;
    }

    public List<TimeLineFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<TimeLineFileInfo> files) {
        this.files = files;
    }

    public List<TimeLineFileInfo> getNewFiles() {
        return newFiles;
    }

    public void setNewFiles(List<TimeLineFileInfo> newFiles) {
        this.newFiles = newFiles;
    }

    public List<TimeLineFileInfo> getOldFiles() {
        return oldFiles;
    }

    public void setOldFiles(List<TimeLineFileInfo> oldFiles) {
        this.oldFiles = oldFiles;
    }

    public List<TimeLineDeviceInfo> getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(List<TimeLineDeviceInfo> devInfo) {
        this.devInfo = devInfo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwrdpTypeNo() {
        return owrdpTypeNo;
    }

    public void setOwrdpTypeNo(String owrdpTypeNo) {
        this.owrdpTypeNo = owrdpTypeNo;
    }

    public String getCurrentDt() {
        return currentDt;
    }

    public void setCurrentDt(String currentDt) {
        this.currentDt = currentDt;
    }

    public String getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(String currentLng) {
        this.currentLng = currentLng;
    }
}
