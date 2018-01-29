package com.example.yb.hstt.Adpater;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by tfhr on 2017/11/30.
 */

public class TaskListAdapter extends CommonAdapter {
    public TaskListAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {

    }

}
