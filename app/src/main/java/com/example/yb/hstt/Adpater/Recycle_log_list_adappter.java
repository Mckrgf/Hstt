package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.UserDeviceDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by tfhr on 2017/10/27.
 */

public class Recycle_log_list_adappter extends RecyclerView.Adapter<Recycle_log_list_adappter.MyViewHolder> {
    private static final String TAG = "Recycle_log_list_adappt";

    private Context context;
    private ArrayList<HashMap<String, Object>> data;

    //列表监听器接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //列表监听器接口
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public Recycle_log_list_adappter(Context context) {
        this.context = context;
    }

    public Recycle_log_list_adappter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
    }

    /*设置界面*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_log_list, parent,
                false));
    }

    /*设置数据*/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        HashMap<String, Object> map = data.get(position);

        //判断是否设置了监听器
        if (mOnItemClickListener != null) {
            //为ItemView设置监听器
            holder.ll_log_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position1 = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position1); // 2
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.ll_log_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }

        HashMap map = data.get(position);
        Iterator iter = map.entrySet().iterator();//获取key和value的set
        while (iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();//把hashmap转成Iterator再迭代到entry
            String key = (String) entry.getValue();//从entry获取key
            if (!TextUtils.isEmpty(key)) {
                String content = "请求内容" + position + ": " + key;
                holder.tv_log_title.setText(content);
            }
        }
    }

    /*设置行列数*/
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_log_content;
        TextView tv_log_title;
        TextView tv_log_time;
        LinearLayout ll_log_item;

        MyViewHolder(View view) {
            super(view);
            tv_log_content = (TextView) view.findViewById(R.id.tv_log_content);
            tv_log_title = (TextView) view.findViewById(R.id.tv_log_title);
            tv_log_time = (TextView) view.findViewById(R.id.tv_log_time);
            ll_log_item = (LinearLayout) view.findViewById(R.id.ll_log_item);

        }
    }
}
