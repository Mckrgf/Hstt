package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.AddUserActivity;
import com.example.yb.hstt.UI.Activities.UserDeviceDetailActivity;
import com.example.yb.hstt.dao.bean.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfhr on 2017/10/27.
 */

public class Recycle_device_list_adappter extends RecyclerView.Adapter<Recycle_device_list_adappter.MyViewHolder> {
    private static final String TAG = "Recycle_device_list_ada";

    private Context context;
    private List<UserInfo> data;

    public void setData(List<UserInfo> infos) {
        this.data = infos;
        notifyDataSetChanged();
    }

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

    public Recycle_device_list_adappter(Context context) {
        this.context = context;
    }

    public Recycle_device_list_adappter(Context context, List data) {
        this.context = context;
        this.data = data;
    }

    public void setData(ArrayList devices) {
        this.data = devices;
        notifyDataSetChanged();
    }

    /*设置界面*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_device_list, parent,
                false));
    }

    /*设置数据*/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        HashMap<String, Object> map = data.get(position);

        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"编辑用户"+position);
                Intent intent = new Intent(context, AddUserActivity.class);
                intent.putExtra("user_info",(Serializable) data.get(position));
                intent.putExtra("AddDevice",false);//用户详情
                intent.putExtra("if_edit",true);//编辑按钮进入,需要编辑
                context.startActivity(intent);
            }
        });
        //判断是否设置了监听器
        if (mOnItemClickListener != null) {
            //为ItemView设置监听器
            holder.ll_terminal_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position1 = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position1); // 2
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.ll_terminal_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    holder.rl_gray.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }

        //点击空白处隐藏删除布局
        holder.rl_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_gray.setVisibility(View.GONE);
            }
        });

        //删除按钮
        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"删除"+position);
            }
        });

        UserInfo info = data.get(position);
        holder.tv_terminal_list_addr.setText(info.getUser_addr());
        holder.tv_device_status.setText(info.getUser_id());
        boolean b = info.getIs_upload();
        if (!b){
            holder.tv_if_uoload.setText("未绑定设备");
        }else {
            holder.tv_if_uoload.setText("已绑定设备");
        }
    }

    /*设置行列数*/
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_terminal_list_addr;//用户地址
        TextView tv_if_uoload;
        TextView tv_device_status;
        Button bt_edit;
        RelativeLayout ll_terminal_item;
        RelativeLayout rl_gray;
        Button bt_delete;

        MyViewHolder(View view) {
            super(view);
            tv_terminal_list_addr = (TextView) view.findViewById(R.id.tv_terminal_list_title);
            tv_if_uoload = (TextView) view.findViewById(R.id.tv_if_uoload_content);
            tv_device_status = (TextView) view.findViewById(R.id.tv_device_status_content);
            bt_edit = (Button) view.findViewById(R.id.bt_edit);
            ll_terminal_item = (RelativeLayout) view.findViewById(R.id.ll_terminal_item);
            rl_gray = (RelativeLayout) view.findViewById(R.id.rl_gray);
            bt_delete = (Button) view.findViewById(R.id.bt_delete);
        }
    }
}
