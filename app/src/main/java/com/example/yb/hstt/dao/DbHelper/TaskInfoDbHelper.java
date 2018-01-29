package com.example.yb.hstt.dao.DbHelper;

import android.content.Context;

import com.example.yb.hstt.dao.DbOpenHelper;
import com.example.yb.hstt.dao.DeviceInfosDao;
import com.example.yb.hstt.dao.TaskInfoDao;
import com.example.yb.hstt.dao.bean.DeviceInfos;
import com.example.yb.hstt.dao.bean.TaskInfo;

import java.util.List;

/**
 * Created by tfhr on 2017/11/14.
 */

public class TaskInfoDbHelper extends DbOpenHelper {
    public TaskInfoDbHelper(Context context) {
        super(context);
    }

    private static TaskInfoDbHelper helper;
    private static TaskInfoDao taskinfoDao;

    public static synchronized TaskInfoDbHelper getInstance(Context context) {
        if (helper == null) {
            helper = new TaskInfoDbHelper(context);
        }
        taskinfoDao = getDao();
        return helper;
    }

    public static TaskInfoDao getDao() {
        if (taskinfoDao == null) {
            taskinfoDao =  getDaoSession().getTaskInfoDao();
        }
        return taskinfoDao;
    }

    //====================================数据库操作方法

    /**
     * 增加一条
     * @param infos
     */
    public void add(TaskInfo infos) {
        taskinfoDao.insert(infos);
    }

    /**
     * 根据设备id查找任务列表
     * @param task_id
     * @return
     */
    public TaskInfo select(String task_id) {
        TaskInfo info =  taskinfoDao.queryBuilder()
                .where(TaskInfoDao.Properties.Task_id.eq(task_id))
                .build().unique();
        return info;
    }

    /**
     * 查找所有数据
     * @return
     */
    public List<TaskInfo> selectAll() {
        List<TaskInfo> infos = (List) taskinfoDao.queryBuilder().list();
        return infos;
    }


    /**
     * 更新info
     * @param info
     */
    public void updateInfo(TaskInfo info) {
        if (info != null) {
            taskinfoDao.update(info);
        }
    }
}
