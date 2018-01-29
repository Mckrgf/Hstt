package com.example.yb.hstt.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.yb.hstt.dao.bean.TaskInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TASK_INFO".
*/
public class TaskInfoDao extends AbstractDao<TaskInfo, Long> {

    public static final String TABLENAME = "TASK_INFO";

    /**
     * Properties of entity TaskInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ID = new Property(0, Long.class, "ID", true, "_id");
        public final static Property Task_id = new Property(1, String.class, "task_id", false, "TASK_ID");
        public final static Property Has_chpsen = new Property(2, boolean.class, "has_chpsen", false, "HAS_CHPSEN");
    }


    public TaskInfoDao(DaoConfig config) {
        super(config);
    }
    
    public TaskInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TASK_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: ID
                "\"TASK_ID\" TEXT," + // 1: task_id
                "\"HAS_CHPSEN\" INTEGER NOT NULL );"); // 2: has_chpsen
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TASK_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TaskInfo entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String task_id = entity.getTask_id();
        if (task_id != null) {
            stmt.bindString(2, task_id);
        }
        stmt.bindLong(3, entity.getHas_chpsen() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TaskInfo entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String task_id = entity.getTask_id();
        if (task_id != null) {
            stmt.bindString(2, task_id);
        }
        stmt.bindLong(3, entity.getHas_chpsen() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TaskInfo readEntity(Cursor cursor, int offset) {
        TaskInfo entity = new TaskInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // ID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // task_id
            cursor.getShort(offset + 2) != 0 // has_chpsen
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TaskInfo entity, int offset) {
        entity.setID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTask_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHas_chpsen(cursor.getShort(offset + 2) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TaskInfo entity, long rowId) {
        entity.setID(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TaskInfo entity) {
        if(entity != null) {
            return entity.getID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TaskInfo entity) {
        return entity.getID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
