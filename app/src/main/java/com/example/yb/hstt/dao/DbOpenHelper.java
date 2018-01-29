package com.example.yb.hstt.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * Created by TFHR02 on 2017/7/19.
 */
public class DbOpenHelper extends DaoMaster.OpenHelper {
    private static SQLiteDatabase DB;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static DbOpenHelper openhelper;

    public static DbOpenHelper getInstance(Context context) {
        if (openhelper == null) {
            openhelper = new DbOpenHelper(context);
        }
        return openhelper;
    }
    public DbOpenHelper(Context context) {
        super(context.getApplicationContext(), "xsas-db", null);
        initDB();
    }
    public void init() {
        Log.i("DbOpenHelper", "DbOpenHelper init Success");
    }
    private void initDB() {
        DB = getWritableDatabase();
        daoMaster = new DaoMaster(DB);
        daoSession=daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SQLiteDatabase getDB() {
        return DB;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        //只限于更改过的实体类(新增的不用加), 更新LoginInfoDao文件,可以添加多个  XXDao.class 文件
        if (oldVersion<newVersion){
            MigrationHelper.getInstance().migrate(db,TaskInfoDao.class);
        }
    }
}
