package com.example.yb.hstt.UI.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.View.MyDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class RepairDeviceDetailActivity extends HsttBaseActivity implements View.OnClickListener {

    public static final String TAG = "RepairDeviceActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_repairDeviceDetail)
    Toolbar tb_repairDeviceDetail;
    //旧设备布局show-hide
    @BindView(R.id.ll_repairDeviceDetail_LayoutDealWith)
    LinearLayout ll_repairDeviceDetail_LayoutDealWith;
    @BindView(R.id.ll_repairDeviceDetail_LayoutInfo)
    LinearLayout ll_repairDeviceDetail_LayoutInfo;

    //新设备拍照列表
    @BindView(R.id.rl_repairDeviceDetail_DevicePhotoTag)
    RelativeLayout rl_repairDeviceDetail_DevicePhotoTag;

    //旧设备录制视频列表
    @BindView(R.id.rl_repairDeviceDetail_DeviceVideoTag)
    RelativeLayout rl_repairDeviceDetail_DeviceVideoTag;
    /**
     * 旧设备拍照rv
     */
    @BindView(R.id.rv_repairDeviceDetail_photos)
    RecyclerView rv_repairDeviceDetail_photos;
    /**
     * 旧设备录制视频rv
     */
    @BindView(R.id.rv_repairDeviceDetail_videos)
    RecyclerView rv_repairDeviceDetail_videos;

    @BindView(R.id.tv_repairDeviceDetail_Deviceid)
    TextView tv_repairDeviceDetail_Deviceid;
    @BindView(R.id.tv_repairDeviceDetail_DeviceType)
    TextView tv_repairDeviceDetail_DeviceType;
    @BindView(R.id.tv_repairDeviceDetail_DeviceFactory)
    TextView tv_repairDeviceDetail_DeviceFactory;
    @BindView(R.id.tv_repairDeviceDetail_OldWorkResult)
    TextView tv_repairDeviceDetail_OldWorkResult;
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;

    /**
     * 维修(编辑)设备表ID
     */
    private String edit_cdmId;
    private List<String> Photo_paths = new ArrayList<>();
    private List<String> Video_Mp4paths = new ArrayList<>();
    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_device_detail);
        ButterKnife.bind(this);
        setSupportActionBar(tb_repairDeviceDetail);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        edit_cdmId = getIntent().getStringExtra("cd_id");

        tb_repairDeviceDetail.setNavigationOnClickListener(this);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);

        //初始化旧设备拍照的列表
        initRecycleview(rv_repairDeviceDetail_photos, new DevicePhotoAdapter(Photo_paths, getMe(), false, true));

        //初始化旧设备录制视频的列表
        initRecycleview(rv_repairDeviceDetail_videos, new DeviceVideoAdapter(Video_Mp4paths, getMe(), false, true));
        rl_repairDeviceDetail_DevicePhotoTag.setVisibility(View.VISIBLE);
        rl_repairDeviceDetail_DeviceVideoTag.setVisibility(View.VISIBLE);
        //查询更换的信息
        searchRepairDeviceInfo(edit_cdmId);
    }

    @OnClick({R.id.tv_repairDeviceDetail_LayoutInfo, R.id.tv_repairDeviceDetail_LayoutDealWith})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_repairDeviceDetail_LayoutInfo:
                LayoutShowHide(ll_repairDeviceDetail_LayoutInfo, (TextView) view);
                break;
            case R.id.tv_repairDeviceDetail_LayoutDealWith:
                LayoutShowHide(ll_repairDeviceDetail_LayoutDealWith, (TextView) view);
                break;
        }
    }

    /**
     * 布局先是隐藏
     *
     * @param v2 要显示或隐藏的布局
     * @param v1 点击的布局
     */
    private void LayoutShowHide(LinearLayout v2, TextView v1) {
        boolean shown = v2.isShown();
        v2.setVisibility(shown ? View.GONE : View.VISIBLE);
        v1.setCompoundDrawablesWithIntrinsicBounds(null, null, shown ? drawable_up : drawable_down, null);
    }


    /**
     * 查询维修设备信息
     */
    private void searchRepairDeviceInfo(String search_cdmId) {
        HttpParams params = new HttpParams();
        params.put("cdmId", search_cdmId);
        OkHttpUtils.get(GlobalUrl.Repair_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> select_infos = (List<Map>) data.get("info");
                            setDeviceInfo(select_infos);

                            List<Map> select_files = (List<Map>) data.get("files");
                            setDeviceFileInfo(select_files);
                        }
                    }
                });
    }

    /**
     * 设置设备类型
     *
     * @param deviceInfo
     */
    private void setDeviceInfo(List<Map> deviceInfo) {
        if (deviceInfo != null && deviceInfo.size() > 0) {
            String factory = deviceInfo.get(0).get("devManufacturerNo").toString();
            String deviceType = deviceInfo.get(0).get("devTypeNo").toString();
            String devId = deviceInfo.get(0).get("devId").toString();
            String comment = deviceInfo.get(0).get("comment").toString();
            tv_repairDeviceDetail_DeviceType.setText(deviceType);
            tv_repairDeviceDetail_DeviceFactory.setText(factory);
            tv_repairDeviceDetail_Deviceid.setText(devId);
            tv_repairDeviceDetail_OldWorkResult.setText(comment);
        }
    }

    /**
     * 设置设备的文件信息
     *
     * @param Files
     */
    private void setDeviceFileInfo(List<Map> Files) {
        for (int i = 0; i < Files.size(); i++) {
            Map files = Files.get(i);
            String attaFilePath = (String) files.get("attaFilePath");
            String owwoAttaId = files.get("owwoAttaId").toString();
            if (attaFilePath.contains("jpg")) {
                Photo_paths.add(attaFilePath + "@@" + owwoAttaId);
            } else {
                Video_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
            }
        }
        rv_repairDeviceDetail_photos.getAdapter().notifyDataSetChanged();
        rv_repairDeviceDetail_videos.getAdapter().notifyDataSetChanged();
    }

    private void initRecycleview(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(photo_list_decoration);
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
