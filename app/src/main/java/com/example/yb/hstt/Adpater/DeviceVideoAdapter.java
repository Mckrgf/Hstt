package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yb.hstt.Base.MyVideoThumbLoader;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.PlayVideoActivity;
import com.example.yb.hstt.Utils.DensityUtil;
import com.example.yb.hstt.Utils.FileUtils;
import com.example.yb.hstt.View.MyImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by TFHR02 on 2017/12/5.
 * 在线视频缩略图,本地视频缩略图，列表适配器。
 */
public class DeviceVideoAdapter extends RecyclerView.Adapter {
    private static final String TAG = "DeviceVideoAdapter";
    private List<String> video_paths;
    private Context context;
    private int width, height;
    private MyVideoThumbLoader mVideoThumbLoader;
    /**
     * 是否可编辑
     */
    private boolean is_delete;
    /**
     * 是否有两种类型的链接
     */
    private boolean is_hasTwoTypeUrl;
    private int recyclerViewId;

    public DeviceVideoAdapter(List<String> video_paths, Context context) {
        this(video_paths, context, false);
    }

    public DeviceVideoAdapter(List<String> video_paths, Context context, boolean is_delete) {
        this(video_paths, context, is_delete,false);
    }
    public DeviceVideoAdapter(List<String> video_paths, Context context, boolean is_delete,boolean is_hasTwoTypeUrl) {
        this.video_paths = video_paths;
        this.context = context;
        this.is_delete = is_delete;
        this.is_hasTwoTypeUrl=is_hasTwoTypeUrl;
        mVideoThumbLoader = new MyVideoThumbLoader();
        width = DensityUtil.getpxByDimensize(context, R.dimen.x315);
        height = DensityUtil.getpxByDimensize(context, R.dimen.y300);
    }
    public DeleteFileListener deleteFilelistener;
    public DeviceVideoAdapter setOnDeleteFileListener(DeleteFileListener listener){
        this.deleteFilelistener=listener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.recyclerViewId=parent.getId();
        View inflate = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
        return new TheViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String path = video_paths.get(position);
//        Log.i(TAG, "onBindViewHolder: "+path);
        if (holder instanceof TheViewHolder) {
            TheViewHolder theHolder = (TheViewHolder) holder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoPath = video_paths.get(position);
                    Intent intent = new Intent(context, PlayVideoActivity.class);
                    intent.putExtra("videoPath", formatPath(videoPath));
                    context.startActivity(intent);
                }
            });

            theHolder.iv_video_list_item.setBackgroundResource(R.drawable.background_gray_v_shape);
            String new_path = formatPath(path);

            theHolder.iv_video_list_item.setTag(new_path);
            if (path.startsWith("file")) {//本地视频
                String path2 = path.substring(0, path.length() - 4);
                Picasso.with(context).load(path2 + ".jpg").error(R.drawable.ic_photo_error).resize(width, height).centerCrop().into(theHolder.iv_video_list_item);
            } else {
                mVideoThumbLoader.showThumbByAsynctack(context, new_path, theHolder.iv_video_list_item);
            }
            theHolder.iv_video_deleteTag.setVisibility(is_delete ? View.VISIBLE : View.GONE);
            theHolder.iv_video_deleteTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (path.startsWith("file")) {
                        deleteFile(video_paths, position);
                        video_paths.remove(position);
                        notifyDataSetChanged();
                    } else {
                        //调用接口删除。。。
                        if (deleteFilelistener!=null){
                            deleteFilelistener.deleteFile(recyclerViewId,position);
                        }
//                        video_paths.remove(position);
//                        notifyDataSetChanged();
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
        return video_paths.size();
    }

    class TheViewHolder extends RecyclerView.ViewHolder {
        public MyImageView iv_video_list_item;
        public ImageView iv_video_deleteTag;

        public TheViewHolder(View itemView) {
            super(itemView);
            iv_video_list_item = ButterKnife.findById(itemView, R.id.iv_video_list_item);
            iv_video_deleteTag = ButterKnife.findById(itemView, R.id.iv_video_deleteTag);
        }
    }
}
