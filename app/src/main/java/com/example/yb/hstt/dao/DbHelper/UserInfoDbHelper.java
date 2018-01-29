package com.example.yb.hstt.dao.DbHelper;

import android.content.Context;

import com.example.yb.hstt.dao.DbOpenHelper;
import com.example.yb.hstt.dao.UserInfoDao;
import com.example.yb.hstt.dao.bean.UserInfo;

import java.util.List;

/**
 * Created by TFHR02 on 2017/8/4.
 */
public class UserInfoDbHelper extends DbOpenHelper {
    private static UserInfoDbHelper MDbHelper;
    private static UserInfoDao userInfoDao;

    public UserInfoDbHelper(Context context) {
        super(context);
    }

    public static synchronized UserInfoDbHelper getInstance(Context context) {
        if (MDbHelper == null) {
            MDbHelper = new UserInfoDbHelper(context);
        }
        userInfoDao = getMDao();
        return MDbHelper;
    }

    public static UserInfoDao getMDao() {
        if (userInfoDao == null) {
            userInfoDao = getDaoSession().getUserInfoDao();
        }
        return userInfoDao;
    }

    //===========================

    /**
     * 插入数据
     * @param info
     */
    public void insert(UserInfo info) {

        userInfoDao.insert(info);
    }

    /**
     * 清理数据
     */
    public void delete() {
        userInfoDao.deleteAll();
    }

    public UserInfo select() {
        UserInfo info = new UserInfo();
        try {
            List<UserInfo> list= userInfoDao.queryBuilder().list();
            if (null!=list && list.size()>0)
               info = list.get(0);
        } catch (Exception e) {
            return info;
        }
        return info;
    }

    public  List<UserInfo> getAllUser() {
        List<UserInfo> userInfos = userInfoDao.queryBuilder().list();
        return userInfos;
    }


    public void updateInfo(UserInfo info) {
        if (info != null) {
            userInfoDao.update(info);
        }
    }


}
