package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.UserDeviceDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tfhr on 2017/10/27.
 */

public class Recycle_user_device_adapter extends RecyclerView.Adapter<Recycle_user_device_adapter.MyViewHolder> {
    private static final String TAG = "Recycle_pic_hor_adapter";

    private Context context;
    private ArrayList data;


    public void setData(ArrayList user_device) {
        this.data = user_device;
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
    public Recycle_user_device_adapter(Context context) {
        this.context = context;
    }

    //构造方法
    public Recycle_user_device_adapter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
    }

    /*设置界面*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_user_device_list, parent,
                false));
    }

    /*设置数据*/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑用户下的设备
                Intent intent = new Intent(context, UserDeviceDetailActivity.class);
                HashMap map = (HashMap) data.get(position);
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = String.valueOf(entry.getKey());
                    Object val = entry.getValue();
                    String val_null = null;
                    String val_a = String.valueOf(val);
                    if (null==val_a) {
                        String val_s = String.valueOf("数据获取失败");
                        map.put(key, val_s);
                    }

                    if (val_a.equals("null")) {
                        String val_s = String.valueOf("数据获取失败");
                        map.put(key, val_s);
                    }
                }
                intent.putExtra("user_device", map);intent.putExtra("user_device",(Serializable) data.get(position));
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
                    return true;
                }
            });
        }

        HashMap device = (HashMap) data.get(position);
        int device_id = (int) device.get("device_id");
        String asset_no = (String) device.get("asset_no");
        String dev_type_s = String.valueOf(device.get("dev_type"));
        String type;
        if (!dev_type_s.equals("null")) {
            int dev_type = Integer.parseInt((String) device.get("dev_type"));
            type = (String) GlobalManager.deivce_map.get(dev_type);
        }else {
            type = "未获取到设备类型";
        }

        holder.tv_device_id.setText(device_id+"");
        holder.tv_device_status_content.setText(asset_no);
        holder.tv_device_type_content.setText(type);


    }

    /*设置行列数*/
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_device_type;
        TextView tv_device_id;//设备编号
        TextView tv_device_status_content;//资产编号
        TextView tv_device_type_content;//设备类型
        Button bt_edit;
        Button bt_delete;
        LinearLayout ll_terminal_item;

        MyViewHolder(View view) {
            super(view);
            tv_device_type_content = (TextView) view.findViewById(R.id.tv_device_type_content);
            tv_device_type = (TextView) view.findViewById(R.id.tv_device_type);
            tv_device_id = (TextView) view.findViewById(R.id.tv_device_id);
            tv_device_status_content = (TextView) view.findViewById(R.id.tv_device_status_content);
            bt_edit = (Button) view.findViewById(R.id.bt_edit);
            bt_delete = (Button) view.findViewById(R.id.bt_delete);
            ll_terminal_item = (LinearLayout) view.findViewById(R.id.ll_terminal_item);
        }
    }
}
