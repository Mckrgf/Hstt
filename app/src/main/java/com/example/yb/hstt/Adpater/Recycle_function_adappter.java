package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yb.hstt.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tfhr on 2017/10/27.
 */

public class Recycle_function_adappter extends RecyclerView.Adapter<Recycle_function_adappter.MyViewHolder> {

    private static final String TAG = "Recycle_function_adappt";

    private Context context;
    private ArrayList<HashMap<String,Object>> data;

    //列表监听器接口
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    //列表监听器接口
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public Recycle_function_adappter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
    }

    /*设置界面*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_function, parent,
                false));
    }

    /*设置数据*/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        HashMap<String,Object> map = data.get(position);
        holder.iv_function.setImageResource((Integer) map.get("title_image"));
        holder.tv_function.setText((String) map.get("title_text"));

        //判断是否设置了监听器
        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.ll_function_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position1 = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView,position1); // 2
                }
            });
        }
        if(mOnItemLongClickListener != null){
            holder.ll_function_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

    }

    /*设置行列数*/
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_function;
        TextView tv_function;
        LinearLayout ll_function_item;

        MyViewHolder(View view) {
            super(view);
            iv_function = (ImageView) view.findViewById(R.id.iv_function);
            tv_function = (TextView) view.findViewById(R.id.tv_function);
            ll_function_item = (LinearLayout) view.findViewById(R.id.ll_function_item);
        }
    }
}
