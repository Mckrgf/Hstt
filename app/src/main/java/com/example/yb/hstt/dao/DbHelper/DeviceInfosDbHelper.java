package com.example.yb.hstt.dao.DbHelper;

import android.content.Context;

import com.example.yb.hstt.dao.DbOpenHelper;
import com.example.yb.hstt.dao.DeviceInfosDao;
import com.example.yb.hstt.dao.bean.DeviceInfos;

import java.util.List;

/**
 * Created by tfhr on 2017/11/14.
 */

public class DeviceInfosDbHelper extends DbOpenHelper {
    public DeviceInfosDbHelper(Context context) {
        super(context);
    }

    private static DeviceInfosDbHelper helper;
    private static DeviceInfosDao deviceInfosDao;

    public static synchronized DeviceInfosDbHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DeviceInfosDbHelper(context);
        }
        deviceInfosDao = getDao();
        return helper;
    }

    public static DeviceInfosDao getDao() {
        if (deviceInfosDao == null) {
            deviceInfosDao =  getDaoSession().getDeviceInfosDao();
        }
        return deviceInfosDao;
    }

    //====================================数据库操作方法

    /**
     * 增加一条
     * @param infos
     */
    public void add(DeviceInfos infos) {
        deviceInfosDao.insert(infos);
    }

    /**
     * 根据设备id查找图片列表
     * @param dev_id
     * @return
     */
    public List<DeviceInfos> select(String dev_id) {
        List<DeviceInfos> infos = deviceInfosDao.queryBuilder()
                .where(DeviceInfosDao.Properties.Dev_id.eq(dev_id))
                .build().list();
        return infos;
    }

    /**
     * 筛选出未上传的文件
     * @return
     */
    public List<DeviceInfos> select_need_to_upload () {
        List<DeviceInfos> infoses = deviceInfosDao.queryBuilder()
                .where(DeviceInfosDao.Properties.State.eq(0))
                .build().list();
        return infoses;
    }

    /**
     * 更新info
     * @param info
     */
    public void updateInfo(DeviceInfos info) {
        if (info != null) {
            deviceInfosDao.update(info);
        }
    }
}
