package com.example.yb.hstt.UI.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.yb.hstt.Utils.MyDateUtils;
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

public class ChangeDeviceDetailActivity extends HsttBaseActivity implements View.OnClickListener {

    public static final String TAG = "ChangeDeviceActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_changeDeviceDetail)
    Toolbar tb_changeDeviceDetail;
    //旧设备布局show-hide
    @BindView(R.id.ll_changeDeviceDetail_oldLayoutDealWith)
    LinearLayout ll_changeDeviceDetail_oldLayoutDealWith;
    @BindView(R.id.ll_changeDeviceDetail_oldLayoutInfo)
    LinearLayout llChangeDeviceOldLayoutInfo;

    //新设备布局show-hide
    @BindView(R.id.ll_changeDeviceDetail_newLayoutDealWith)
    LinearLayout ll_changeDeviceDetail_newLayoutDealWith;
    @BindView(R.id.ll_changeDeviceDetail_newLayoutInfo)
    LinearLayout ll_changeDeviceDetail_newLayoutInfo;
    //旧设备拍照列表
    @BindView(R.id.rl_changeDeviceDetail_oldDevicePhotoTag)
    RelativeLayout rl_changeDeviceDetail_oldDevicePhotoTag;
    //旧设备录制视频列表
    @BindView(R.id.rl_changeDeviceDetail_oldDeviceVideoTag)
    RelativeLayout rl_changeDeviceDetail_oldDeviceVideoTag;
    //新设备拍照列表
    @BindView(R.id.rl_changeDeviceDetail_newDevicePhotoTag)
    RelativeLayout rl_changeDeviceDetail_newDevicePhotoTag;
    //新设备录制视频列表
    @BindView(R.id.rl_changeDeviceDetail_newDeviceVideoTag)
    RelativeLayout rl_changeDeviceDetail_newDeviceVideoTag;

    @BindView(R.id.tv_changeDeviceDetail_oldDeviceType)
    TextView tv_changeDeviceDetail_oldDeviceType;
    @BindView(R.id.tv_changeDeviceDetail_oldDeviceFactory)
    TextView tv_changeDeviceDetail_oldDeviceFactory;
    @BindView(R.id.tv_changeDeviceDetail_newDeviceType)
    TextView tv_changeDeviceDetail_newDeviceType;
    @BindView(R.id.tv_changeDeviceDetail_newDeviceFactory)
    TextView tv_changeDeviceDetail_newDeviceFactory;

    //扫码
    @BindView(R.id.tv_changeDeviceDetail_oldDeviceid)
    TextView tv_changeDeviceDetail_oldDeviceid;
    @BindView(R.id.tv_changeDeviceDetail_newDeviceid)
    TextView tv_changeDeviceDetail_newDeviceid;
    /**
     * 旧设备拍照rv
     */
    @BindView(R.id.rv_changeDeviceDetail_oldphotos)
    RecyclerView rv_changeDeviceDetail_oldphotos;
    /**
     * 旧设备录制视频rv
     */
    @BindView(R.id.rv_changeDeviceDetail_oldvideos)
    RecyclerView rv_changeDeviceDetail_oldvideos;
    /**
     * 新设备拍照rv
     */
    @BindView(R.id.rv_changeDeviceDetail_newphotos)
    RecyclerView rv_changeDeviceDetail_newphotos;
    /**
     * 新设备录制视频rv
     */
    @BindView(R.id.rv_changeDeviceDetail_newvideos)
    RecyclerView rv_changeDeviceDetail_newvideos;

    @BindView(R.id.tv_changeDeviceDetail_oldDevicePhotoTimeTag)
    TextView tv_changeDeviceDetail_oldDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDeviceDetail_oldDeviceVideoTimeTag)
    TextView tv_changeDeviceDetail_oldDeviceVideoTimeTag;
    @BindView(R.id.tv_changeDeviceDetail_newDevicePhotoTimeTag)
    TextView tv_changeDeviceDetail_newDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDeviceDetail_newDeviceVideoTimeTag)
    TextView tv_changeDeviceDetail_newDeviceVideoTimeTag;
    @BindView(R.id.tv_changeDeviceDetail_OldWorkResult)
    TextView tv_changeDeviceDetail_OldWorkResult;
    @BindView(R.id.tv_changeDeviceDetail_NewWorkResult)
    TextView tv_changeDeviceDetail_NewWorkResult;
    /**
     * 照片文件名
     */
    private String filename;
    private List<String> oldPhoto_paths = new ArrayList<>();
    private List<String> newPhoto_paths = new ArrayList<>();
    //    private List<String> oldVideo_paths = new ArrayList<>();
//    private List<String> newVideo_paths = new ArrayList<>();
    private List<String> oldVideo_Mp4paths = new ArrayList<>();
    private List<String> newVideo_Mp4paths = new ArrayList<>();
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    /**
     * 更换（编辑）用的设备表ID
     */
    private String edit_cdcid;
    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_device_detail);
        ButterKnife.bind(this);
        setSupportActionBar(tb_changeDeviceDetail);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        edit_cdcid = getIntent().getStringExtra("cd_id");

        tb_changeDeviceDetail.setNavigationOnClickListener(this);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);

        //初始化文件的标记时间
        String curTime = MyDateUtils.getCurTime("yyyy.MM.dd");
        tv_changeDeviceDetail_oldDevicePhotoTimeTag.setText(curTime);
        tv_changeDeviceDetail_oldDeviceVideoTimeTag.setText(curTime);
        tv_changeDeviceDetail_newDevicePhotoTimeTag.setText(curTime);
        tv_changeDeviceDetail_newDeviceVideoTimeTag.setText(curTime);

        //初始化旧设备录制图片的列表
        initRecycleview(rv_changeDeviceDetail_oldphotos, new DevicePhotoAdapter(oldPhoto_paths, getMe(), false, true));
        //初始化旧设备录制视频的列表
        initRecycleview(rv_changeDeviceDetail_oldvideos, new DeviceVideoAdapter(oldVideo_Mp4paths, getMe(), false, true));
        //初始化新设备拍照的列表
        initRecycleview(rv_changeDeviceDetail_newphotos, new DevicePhotoAdapter(newPhoto_paths, getMe(), false, true));
        //初始化新设备录制视频的列表
        initRecycleview(rv_changeDeviceDetail_newvideos, new DeviceVideoAdapter(newVideo_Mp4paths, getMe(), false, true));
            rl_changeDeviceDetail_oldDevicePhotoTag.setVisibility(View.VISIBLE);
            rl_changeDeviceDetail_oldDeviceVideoTag.setVisibility(View.VISIBLE);
            rl_changeDeviceDetail_newDevicePhotoTag.setVisibility(View.VISIBLE);
            rl_changeDeviceDetail_newDeviceVideoTag.setVisibility(View.VISIBLE);
            //查询更换的信息
            searchChangeDeviceInfo(edit_cdcid);
    }
    private void initRecycleview(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(photo_list_decoration);
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    @OnClick({ R.id.tv_changeDeviceDetail_oldLayoutDealWith, R.id.tv_changeDeviceDetail_newLayoutDealWith,
            R.id.tv_changeDeviceDetail_oldLayoutInfo, R.id.tv_changeDeviceDetail_newLayoutInfo})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_changeDeviceDetail_oldLayoutDealWith:
                LayoutShowHide(ll_changeDeviceDetail_oldLayoutDealWith, (TextView) view);
                break;
            case R.id.tv_changeDeviceDetail_newLayoutDealWith:
                LayoutShowHide(ll_changeDeviceDetail_newLayoutDealWith, (TextView) view);
                break;
            case R.id.tv_changeDeviceDetail_oldLayoutInfo:
                LayoutShowHide(llChangeDeviceOldLayoutInfo, (TextView) view);
                break;
            case R.id.tv_changeDeviceDetail_newLayoutInfo:
                LayoutShowHide(ll_changeDeviceDetail_newLayoutInfo, (TextView) view);
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
     * 查询更换设备信息
     */
    private void searchChangeDeviceInfo(String cdcId) {
        HttpParams params = new HttpParams();
        params.put("cdcId", cdcId);
        OkHttpUtils.get(GlobalUrl.Change_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        Log.i(TAG, "Change_DeviceInfo: " + map);
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> oldInfo = (List<Map>) data.get("oldInfo");
                            setDeviceInfo(oldInfo, tv_changeDeviceDetail_oldDeviceType, tv_changeDeviceDetail_oldDeviceid,
                                    tv_changeDeviceDetail_oldDeviceFactory, tv_changeDeviceDetail_OldWorkResult);
                            List<Map> select_infos = (List<Map>) data.get("newInfo");
                            setDeviceInfo(select_infos, tv_changeDeviceDetail_newDeviceType, tv_changeDeviceDetail_newDeviceid,
                                    tv_changeDeviceDetail_newDeviceFactory, tv_changeDeviceDetail_NewWorkResult);

                            List<Map> oldFiles = (List<Map>) data.get("oldFiles");
                            setDeviceFileInfo(oldFiles, true);
                            List<Map> select_files = (List<Map>) data.get("newFiles");
                            setDeviceFileInfo(select_files, false);
                        }
                    }
                });
    }
    /**
     * 设置设备类型
     *
     * @param deviceInfo
     * @param tv_deviceid
     */
    private void setDeviceInfo(List<Map> deviceInfo, TextView tv_deviceType, TextView tv_deviceid, TextView tv_factory, TextView tv_workResult) {
        if (deviceInfo != null && deviceInfo.size() > 0) {
            String factory = deviceInfo.get(0).get("devManufacturerNo").toString();
            String deviceType = deviceInfo.get(0).get("devTypeNo").toString();
            String devId = deviceInfo.get(0).get("devId").toString();
            String comment = deviceInfo.get(0).get("comment").toString();
            tv_deviceType.setText(deviceType);
            tv_factory.setText(factory);
            tv_deviceid.setText(devId);
            tv_workResult.setText(comment);
        }
    }
    /**
     * 设置设备的文件信息
     *
     * @param Files
     * @param is_old
     */
    private void setDeviceFileInfo(List<Map> Files, boolean is_old) {
        for (int i = 0; i < Files.size(); i++) {
            Map files = Files.get(i);
            String attaFilePath = (String) files.get("attaFilePath");
            String owwoAttaId = files.get("owwoAttaId").toString();
            if (is_old) {
                if (attaFilePath.contains("jpg")) {
                    oldPhoto_paths.add(attaFilePath + "@@" + owwoAttaId);
                } else {
                    oldVideo_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
                }
            } else {
                if (attaFilePath.contains("jpg")) {
                    newPhoto_paths.add(attaFilePath + "@@" + owwoAttaId);
                } else {
                    newVideo_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
                }
            }
        }

        if (is_old) {
            rv_changeDeviceDetail_oldphotos.getAdapter().notifyDataSetChanged();
            rv_changeDeviceDetail_oldvideos.getAdapter().notifyDataSetChanged();
        } else {
            rv_changeDeviceDetail_newphotos.getAdapter().notifyDataSetChanged();
            rv_changeDeviceDetail_newvideos.getAdapter().notifyDataSetChanged();
        }
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
