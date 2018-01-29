package com.example.yb.hstt.Adpater;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.DensityUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.bean.TimeLineDeviceInfo;
import com.example.yb.hstt.bean.TimeLineFileInfo;
import com.example.yb.hstt.bean.workTimeLineInfo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TFHR02 on 2017/12/21.
 */
public class WorkingTimeLineAdapter extends RecyclerView.Adapter {
    private Context mContent;
    private List<workTimeLineInfo> lists;
    public static Map<String, String> workingStatusMap = new ArrayMap<>();
    private LayoutInflater inflater;
    private View headView;

    static {
        workingStatusMap.put("02", "开始处理");
        workingStatusMap.put("03", "到达现场");
        workingStatusMap.put("04", "现场采证");
        workingStatusMap.put("05", "现场处理");
        workingStatusMap.put("06", "结束工单");
        workingStatusMap.put("07", "退回");
        workingStatusMap.put("08", "协作完成");
    }

    public WorkingTimeLineAdapter(Context mContent,View headView, List<workTimeLineInfo> lists) {
        this.mContent = mContent;
        this.headView=headView;
        this.lists = lists;
        this.inflater = LayoutInflater.from(mContent);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return -1;
        }
        int itemViewType = super.getItemViewType(position);
        workTimeLineInfo info = lists.get(position-1);
        String owrdpTypeNo = info.getOwrdpTypeNo();
        switch (owrdpTypeNo) {
            case "05":
                if (info.getChooseSolutionTypeNo().equals("01")) {
                    itemViewType = 1;
                } else {
                    itemViewType = 2;
                }
                break;
            case "04":
                itemViewType = 3;
                break;
        }
        return itemViewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==-1){
            return new TimeLineHolder_1(headView);
        }
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0://其他
                viewHolder = new TimeLineHolder(getView(R.layout.work_time_line_item));
                break;
            case 1://更换
                viewHolder = new TimeLineHolder1(getView(R.layout.work_time_line_item1));
                break;
            case 2://维修
                viewHolder = new TimeLineHolder2(getView(R.layout.work_time_line_item2));
                break;
            case 3://现场采证
                viewHolder = new TimeLineHolder3(getView(R.layout.work_time_line_item3));
                break;
        }
        return viewHolder;
    }

    /**
     * 获取view
     *
     * @param LayoutId
     * @return
     */
    private View getView(int LayoutId) {
        return this.inflater.inflate(LayoutId, null);
    }

    @Override
    public int getItemCount() {
        return lists.size()+(1);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position==0){//抬头，
            return;
        }
        workTimeLineInfo info = lists.get(position-1);
        if (holder instanceof TimeLineHolder) {//普通的item
            TimeLineHolder Holder = (TimeLineHolder) holder;
            setBaseData(holder,position, Holder.v_workTimeLine_ling,
                    Holder.iv_workTimeLine_lingLog, Holder.tv_workTimeLine_time,
                    Holder.tv_workTimeLine_status, Holder.tv_workTimeLine_Devcomment, info);
        } else if (holder instanceof TimeLineHolder1) {//更换
            TimeLineHolder1 Holder1 = (TimeLineHolder1) holder;
            setBaseData(holder,position, Holder1.v_workTimeLine1_ling,
                    Holder1.iv_workTimeLine1_lingLog, Holder1.tv_workTimeLine1_time,
                    Holder1.tv_workTimeLine1_status, null, info);
            List<TimeLineDeviceInfo> devInfo = info.getDevInfo();
            if (devInfo != null && devInfo.size() > 0) {
                Holder1.tv_workTimeLine1_oldDevId.setText(devInfo.get(0).getOldDevId());
                String devTypeWithFactory = devInfo.get(0).getDevTypeNo() + "-" + devInfo.get(0).getDevManufacturerNo();
                Holder1.tv_workTimeLine1_oldDevTypeWithFactory.setText(devTypeWithFactory);
                Holder1.tv_workTimeLine1_oldDevComment.setText(devInfo.get(0).getOldDevComment());

                Holder1.tv_workTimeLine1_newDevId.setText(devInfo.get(0).getNewDevId());
                String NewDevTypeWithFactory = devInfo.get(0).getNewDevTypeNo() + "-" + devInfo.get(0).getNewDevManufacturerNo();
                Holder1.tv_workTimeLine1_newDevTypeWithFactory.setText(NewDevTypeWithFactory);
                Holder1.tv_workTimeLine1_newDevComment.setText(devInfo.get(0).getNewDevComment());
            }

            List<String> oldVideoFiles = getPathsByEntities(info.getOldFiles(), "mp4");
            List<String> oldPhotoFiles = getPathsByEntities(info.getOldFiles(), "jpg");
            List<String> newVideoFiles = getPathsByEntities(info.getNewFiles(), "mp4");
            List<String> newPhotoFiles = getPathsByEntities(info.getNewFiles(), "jpg");
            initRecyclerView(Holder1.rv_workTimeLine1_oldDevVideoFile, new DeviceVideoAdapter(oldVideoFiles, mContent));
            initRecyclerView(Holder1.rv_workTimeLine1_oldDevPhotoFile, new DevicePhotoAdapter(oldPhotoFiles, mContent));
            initRecyclerView(Holder1.rv_workTimeLine1_newDevVideoFile, new DeviceVideoAdapter(newVideoFiles, mContent));
            initRecyclerView(Holder1.rv_workTimeLine1_newDevPhotoFile, new DevicePhotoAdapter(newPhotoFiles, mContent));

        } else if (holder instanceof TimeLineHolder2) {//维修
            TimeLineHolder2 Holder2 = (TimeLineHolder2) holder;
            setBaseData(holder,position, Holder2.v_workTimeLine2_ling,
                    Holder2.iv_workTimeLine2_lingLog, Holder2.tv_workTimeLine2_time,
                    Holder2.tv_workTimeLine2_status, null, info);
            List<TimeLineDeviceInfo> devInfo = info.getDevInfo();
            if (devInfo != null && devInfo.size() > 0) {
                Holder2.tv_workTimeLine2_DevId.setText(devInfo.get(0).getOldDevId());
                String devTypeWithFactory = devInfo.get(0).getDevTypeNo() + "-" + devInfo.get(0).getDevManufacturerNo();
                Holder2.tv_workTimeLine2_DevTypeWithFactory.setText(devTypeWithFactory);
                Holder2.tv_workTimeLine2_DevComment.setText(devInfo.get(0).getOldDevComment());
            }
            List<String> VideoFilePaths = getPathsByEntities(info.getFiles(), "mp4");
            List<String> PhotoFilePaths = getPathsByEntities(info.getFiles(), "jpg");
            initRecyclerView(Holder2.rv_workTimeLine2_DevVideoFile, new DeviceVideoAdapter(VideoFilePaths, mContent));
            initRecyclerView(Holder2.rv_workTimeLine2_DevPhotoFile, new DevicePhotoAdapter(PhotoFilePaths, mContent));

        } else if (holder instanceof TimeLineHolder3) {//现场采证
            TimeLineHolder3 Holder3 = (TimeLineHolder3) holder;
            setBaseData(holder,position, Holder3.v_workTimeLine3_ling,
                    Holder3.iv_workTimeLine3_lingLog, Holder3.tv_workTimeLine3_time,
                    Holder3.tv_workTimeLine3_status, Holder3.tv_workTimeLine3_DevComment, info);

            List<String> filePaths = getPathsByEntities(info.getFiles());
            initRecyclerView(Holder3.rv_workTimeLine3_DevVideoFile, new DevicePhotoAdapter(filePaths, mContent));
        }
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        MyDecoration myDecoration = new MyDecoration(mContent, MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContent, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(myDecoration);
            recyclerView.setAdapter(devicePhotoAdapter);
        }
    }

    /**
     * 根据文件对象获取 指定文件类型路径
     *
     * @param files
     * @param tag   jpg或者mp4
     * @return
     */
    private List<String> getPathsByEntities(List<TimeLineFileInfo> files, String tag) {
        List<String> paths = new ArrayList<>();
        for (TimeLineFileInfo info : files) {
            String attaFilePath = info.getAttaFilePath();
            if (!TextUtils.isEmpty(tag) && attaFilePath.endsWith(tag)) {
                paths.add("http://47.93.159.225/cte/" + attaFilePath);
            }
        }
        return paths;
    }

    private List<String> getPathsByEntities(List<TimeLineFileInfo> files) {
        return getPathsByEntities(files, null);
    }


    /**
     * 设置基础数据
     *
     * @param position
     * @param ling
     * @param lingLog
     * @param time
     * @param status
     * @param DevComment
     * @param info
     */
    private void setBaseData(RecyclerView.ViewHolder holder,int position, View ling, ImageView lingLog, TextView time, TextView status, TextView DevComment, workTimeLineInfo info) {
        int padding= DensityUtil.getpxByDimensize(mContent,R.dimen.y30);
        int p_bottom= DensityUtil.getpxByDimensize(mContent,R.dimen.y150);


        if (position == lists.size()) {//最后一项
            holder.itemView.setPadding(padding,0,padding,p_bottom);
            ling.setVisibility(View.GONE);
        } else {
            holder.itemView.setPadding(padding,0,padding,0);
            ling.setVisibility(View.VISIBLE);
        }
        if (position == 0+(1)) {//第一项
            holder.itemView.setPadding(padding,padding,padding,0);
            lingLog.setImageResource(R.drawable.ic_timeline_selected);
            status.setTextColor(mContent.getResources().getColor(R.color.logo_yellow));
        } else {
            lingLog.setImageResource(R.drawable.ic_timeline_arrow);
            status.setTextColor(mContent.getResources().getColor(R.color.tv_text_gray));
        }
        Long t = 0l;
        try {
            t = MyDateUtils.getLongDateFromString(info.getCurrentDt(), MyDateUtils.date_Format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        time.setText(MyDateUtils.getDateFromLong(t, "MM-dd HH:mm"));
        if (info.getOwrdpTypeNo().equals("05")) {
            status.setText(info.getComment());
        } else {
            status.setText(workingStatusMap.get(info.getOwrdpTypeNo()));
        }
        if (DevComment != null) {
            DevComment.setText(info.getComment());
            if (position==0+1){//第一项备注
                DevComment.setTextColor(mContent.getResources().getColor(R.color.logo_yellow));
            }else{
                DevComment.setTextColor(mContent.getResources().getColor(R.color.tv_text_gray));
            }
        }
    }

    class TimeLineHolder_1 extends RecyclerView.ViewHolder{

        public TimeLineHolder_1(View itemView) {
            super(itemView);
        }
    }
    class TimeLineHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_workTimeLine_time)
        TextView tv_workTimeLine_time;
        @BindView(R.id.tv_workTimeLine_status)
        TextView tv_workTimeLine_status;
        @BindView(R.id.tv_workTimeLine_Devcomment)
        TextView tv_workTimeLine_Devcomment;

        @BindView(R.id.iv_workTimeLine_lingLog)
        ImageView iv_workTimeLine_lingLog;
        @BindView(R.id.v_workTimeLine_ling)
        View v_workTimeLine_ling;

        public TimeLineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TimeLineHolder1 extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_workTimeLine1_time)
        TextView tv_workTimeLine1_time;
        @BindView(R.id.iv_workTimeLine1_lingLog)
        ImageView iv_workTimeLine1_lingLog;
        @BindView(R.id.v_workTimeLine1_ling)
        View v_workTimeLine1_ling;
        @BindView(R.id.tv_workTimeLine1_status)
        TextView tv_workTimeLine1_status;
        @BindView(R.id.tv_workTimeLine1_oldDevId)
        TextView tv_workTimeLine1_oldDevId;
        @BindView(R.id.tv_workTimeLine1_oldDevTypeWithFactory)
        TextView tv_workTimeLine1_oldDevTypeWithFactory;
        @BindView(R.id.rv_workTimeLine1_oldDevVideoFile)
        RecyclerView rv_workTimeLine1_oldDevVideoFile;
        @BindView(R.id.tv_workTimeLine1_oldDevComment)
        TextView tv_workTimeLine1_oldDevComment;
        @BindView(R.id.rv_workTimeLine1_oldDevPhotoFile)
        RecyclerView rv_workTimeLine1_oldDevPhotoFile;
        @BindView(R.id.tv_workTimeLine1_newDevId)
        TextView tv_workTimeLine1_newDevId;
        @BindView(R.id.tv_workTimeLine1_newDevTypeWithFactory)
        TextView tv_workTimeLine1_newDevTypeWithFactory;
        @BindView(R.id.rv_workTimeLine1_newDevVideoFile)
        RecyclerView rv_workTimeLine1_newDevVideoFile;
        @BindView(R.id.tv_workTimeLine1_newDevComment)
        TextView tv_workTimeLine1_newDevComment;
        @BindView(R.id.rv_workTimeLine1_newDevPhotoFile)
        RecyclerView rv_workTimeLine1_newDevPhotoFile;

        public TimeLineHolder1(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TimeLineHolder2 extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_workTimeLine2_time)
        TextView tv_workTimeLine2_time;
        @BindView(R.id.iv_workTimeLine2_lingLog)
        ImageView iv_workTimeLine2_lingLog;
        @BindView(R.id.v_workTimeLine2_ling)
        View v_workTimeLine2_ling;
        @BindView(R.id.tv_workTimeLine2_status)
        TextView tv_workTimeLine2_status;
        @BindView(R.id.tv_workTimeLine2_DevId)
        TextView tv_workTimeLine2_DevId;
        @BindView(R.id.tv_workTimeLine2_DevTypeWithFactory)
        TextView tv_workTimeLine2_DevTypeWithFactory;
        @BindView(R.id.rv_workTimeLine2_DevVideoFile)
        RecyclerView rv_workTimeLine2_DevVideoFile;
        @BindView(R.id.tv_workTimeLine2_DevComment)
        TextView tv_workTimeLine2_DevComment;
        @BindView(R.id.rv_workTimeLine2_DevPhotoFile)
        RecyclerView rv_workTimeLine2_DevPhotoFile;

        public TimeLineHolder2(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TimeLineHolder3 extends RecyclerView.ViewHolder {//现场采证
        @BindView(R.id.tv_workTimeLine3_time)
        TextView tv_workTimeLine3_time;
        @BindView(R.id.tv_workTimeLine3_status)
        TextView tv_workTimeLine3_status;
        @BindView(R.id.tv_workTimeLine3_DevComment)
        TextView tv_workTimeLine3_DevComment;

        @BindView(R.id.iv_workTimeLine3_lingLog)
        ImageView iv_workTimeLine3_lingLog;
        @BindView(R.id.v_workTimeLine3_ling)
        View v_workTimeLine3_ling;
        @BindView(R.id.rv_workTimeLine3_DevVideoFile)
        RecyclerView rv_workTimeLine3_DevVideoFile;

        public TimeLineHolder3(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
