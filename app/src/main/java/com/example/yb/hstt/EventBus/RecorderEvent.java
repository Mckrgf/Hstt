package com.example.yb.hstt.EventBus;

public class RecorderEvent {

    public static final String MEDIA_SAVE_FLAG = "SAVE_VIDEO_OK";
    private String mMsg;
    private String mId;
    private String functionLabel;

    public String getEventId() {
        return mId;
    }

    public void setEventId(String mId) {
        this.mId = mId;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }

    public String getFunctionLabel() {
        return functionLabel;
    }

    public void setFunctionLabel(String functionLabel) {
        this.functionLabel = functionLabel;
    }
}
