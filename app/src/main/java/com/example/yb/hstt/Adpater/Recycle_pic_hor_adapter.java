package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.ImageBrowseActivity;
import com.example.yb.hstt.UI.Activities.TerminalDetailActivity;
import com.example.yb.hstt.dao.bean.DeviceInfos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tfhr on 2017/10/27.
 */

public class Recycle_pic_hor_adapter extends RecyclerView.Adapter<Recycle_pic_hor_adapter.MyViewHolder> {
    private static final String TAG = "Recycle_pic_hor_adapter";

    private Context context;
    private ArrayList data;

    public void setPicData (ArrayList data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setData(Intent data) {
    }

    //列表监听器接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //列表监听器接口
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    //监听器
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    //点击实现
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //长按实现
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    //构造方法
    public Recycle_pic_hor_adapter(Context context) {
        this.context = context;
    }

    //构造方法
    public Recycle_pic_hor_adapter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
    }

    /*设置界面*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_pic_hor_list, parent,
                false));
    }

    /*设置数据*/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String img_url= (String) data.get(position);
        Picasso.with(context).load(img_url).error(R.mipmap.login_user).into(holder.iv_item);
        holder.iv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageBrowseActivity.startActivity(context, data, position);
            }
        });
    }

    /*设置行列数*/
    @Override
    public int getItemCount() {
        if (null!=data){
            return data.size();
        }else {
            return 0;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item;

        MyViewHolder(View view) {
            super(view);
            iv_item = (ImageView) view.findViewById(R.id.iv_item);
        }
    }
}
