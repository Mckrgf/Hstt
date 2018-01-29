package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.ImageBrowseActivity;
import com.example.yb.hstt.Utils.DensityUtil;
import com.example.yb.hstt.Utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by TFHR02 on 2017/12/5.
 * 图片列表适配器
 */
public class DevicePhotoAdapter extends RecyclerView.Adapter {
    private static final String TAG = "DevicePhotoAdapter";
    private List<String> photo_files;
    private Context context;
    /**
     * 是否可编辑
     */
    private boolean is_delete;
    private int width, height;
    /**
     * 是否有两种类型的链接
     */
    private boolean is_hasTwoTypeUrl;
    private int recyclerViewId;


    public DevicePhotoAdapter(List<String> photo_files, Context context) {
        this(photo_files, context, false);
    }

    public DevicePhotoAdapter(List<String> photo_files, Context context, boolean is_delete) {
       this(photo_files,context,is_delete,false);
    }
    public DevicePhotoAdapter(List<String> photo_files, Context context, boolean is_delete,boolean is_hasTwoTypeUrl) {
        this.photo_files = photo_files;
        this.context = context;
        this.is_delete = is_delete;
        width = DensityUtil.getpxByDimensize(context, R.dimen.x315);
        height = DensityUtil.getpxByDimensize(context, R.dimen.y300);
        this.is_hasTwoTypeUrl=is_hasTwoTypeUrl;
    }

    public DeleteFileListener deleteFilelistener;
    public DevicePhotoAdapter setOnDeleteFileListener(DeleteFileListener listener){
        this.deleteFilelistener=listener;
        return this;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.recyclerViewId=parent.getId();
        View inflate = LayoutInflater.from(context).inflate(R.layout.photo_list_item, null);
        return new TheViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String path = photo_files.get(position);
        if (holder instanceof TheViewHolder) {
            TheViewHolder theHolder = (TheViewHolder) holder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //格式化一下数据
                    ArrayList<String> vp_dats = new ArrayList();
                    for (String path : photo_files) {
                        vp_dats.add(formatPath(path));
                    }
                    ImageBrowseActivity.startActivity(context, vp_dats, position);
                }
            });

            theHolder.iv_photo_list_item.setBackgroundResource(R.drawable.background_gray_v_shape);

            String new_path = formatPath(path);
//            Log.i(TAG, "new_path: "+new_path);

            Picasso.with(context).load(new_path).error(R.drawable.ic_photo_error).resize(width, height).centerCrop().into(theHolder.iv_photo_list_item);

            theHolder.iv_photo_deleteTag.setVisibility(is_delete ? View.VISIBLE : View.GONE);
            theHolder.iv_photo_deleteTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (path.startsWith("file")) {
                        deleteFile(photo_files, position);
                        photo_files.remove(position);
                        notifyDataSetChanged();
                    } else {
                        //调用接口删除。。。
                        if (deleteFilelistener!=null){
                            deleteFilelistener.deleteFile(recyclerViewId,position);
                        }
                    }
                }
            });
        }
    }

    /**
     * 格式化图片路径
     * @param path
     * @return
     */
    private String formatPath(String path){
        String owwoAttaId="",new_path="";
        if (is_hasTwoTypeUrl&&!path.startsWith("file")){
            owwoAttaId = ("@@"+path.split("@@")[1]);
        }
        new_path = path.substring(0, path.length() - owwoAttaId.length());
        return new_path;
    }

    /**
     * 删除文件
     *
     * @param files
     * @param position
     */
    private void deleteFile(List<String> files, int position) {
        String path = files.get(position);
        String head = "file://";
        String substring = path.substring(head.length(), path.length());
        FileUtils.deleteFile(substring);
    }

    @Override
    public int getItemCount() {
        return photo_files.size();
    }

    class TheViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_photo_list_item, iv_photo_deleteTag;

        public TheViewHolder(View itemView) {
            super(itemView);
            iv_photo_list_item = ButterKnife.findById(itemView, R.id.iv_photo_list_item);
            iv_photo_deleteTag = ButterKnife.findById(itemView, R.id.iv_photo_deleteTag);
        }
    }
}
