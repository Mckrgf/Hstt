package com.example.yb.hstt.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by tfhr on 2017/11/14.
 */

@Entity
public class TaskInfo {

    @Unique
    @Id(autoincrement = true)
    private Long ID;
    private String task_id;//任务编号
    private boolean has_chpsen;//是否被点开过,默认为fasle未点开过
    public boolean getHas_chpsen() {
        return this.has_chpsen;
    }
    public void setHas_chpsen(boolean has_chpsen) {
        this.has_chpsen = has_chpsen;
    }
    public String getTask_id() {
        return this.task_id;
    }
    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    @Generated(hash = 933207386)
    public TaskInfo(Long ID, String task_id, boolean has_chpsen) {
        this.ID = ID;
        this.task_id = task_id;
        this.has_chpsen = has_chpsen;
    }
    @Generated(hash = 2022720704)
    public TaskInfo() {
    }


}
