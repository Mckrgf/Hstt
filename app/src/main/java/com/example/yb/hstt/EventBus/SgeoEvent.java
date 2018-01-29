package com.example.yb.hstt.EventBus;

import java.util.HashMap;
import java.util.List;

public class SgeoEvent {


    public static final String MEDIA_TALK_REC = "TALK_REC_MESSAGE";
    public static final String MEDIA_PUSH_REC = "PUSH_REC_MESSAGE";
    public static final String IM_LOGIN_OK = "IM_LOGIN_OK";
    public static final String IM_CHAT_REC = "IM_CHAT_REC";

    private String mMsg;
    private String mId;
    private String fromId;
    private String toId;
    private String createUser;
    private HashMap<String, String> mMap;
    private List mList;
    private String msgtype;
    private int typeId;


    public List getmList() {
        return mList;
    }

    public void setmList(List mList) {
        this.mList = mList;
    }
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public HashMap<String, String> getmMap() {
        return mMap;
    }

    public void setmMap(HashMap<String, String> mMap) {
        this.mMap = mMap;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getEventId() {
        return mId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
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

}
