package com.example.yb.hstt.dao.DbHelper;

import android.content.Context;


import com.example.yb.hstt.dao.DbOpenHelper;
import com.example.yb.hstt.dao.LoginInfoDao;
import com.example.yb.hstt.dao.bean.LoginInfo;

import java.util.List;

/**
 * Created by TFHR02 on 2017/8/4.
 */
public class LoginInfoDbHelper extends DbOpenHelper {
    private static LoginInfoDbHelper MDbHelper;
    private static LoginInfoDao loginInfoDao;

    public LoginInfoDbHelper(Context context) {
        super(context);
    }

    public static synchronized LoginInfoDbHelper getInstance(Context context) {
        if (MDbHelper == null) {
            MDbHelper = new LoginInfoDbHelper(context);
        }
        loginInfoDao = getMDao();
        return MDbHelper;
    }

    public static LoginInfoDao getMDao() {
        if (loginInfoDao == null) {
            loginInfoDao = getDaoSession().getLoginInfoDao();
        }
        return loginInfoDao;
    }

    //===========================
    public void insert(LoginInfo info) {
        LoginInfo oldinfo = select();
        if (oldinfo != null) {
            delete();
        }
        loginInfoDao.insert(info);
    }

    /**
     * 清理数据
     */
    public void delete() {
        loginInfoDao.deleteAll();
    }

    public LoginInfo select() {
        LoginInfo info = new LoginInfo();
        try {
            List<LoginInfo> list=loginInfoDao.queryBuilder().list();
            if (null!=list && list.size()>0)
               info = list.get(0);
        } catch (Exception e) {
            return info;
        }
        return info;
    }


    public void updateInfo(LoginInfo info) {
        if (info != null) {
            loginInfoDao.update(info);
        }
    }



}
