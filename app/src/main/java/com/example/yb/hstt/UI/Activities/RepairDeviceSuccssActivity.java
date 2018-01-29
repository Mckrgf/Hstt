package com.example.yb.hstt.UI.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.DisplayUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class RepairDeviceSuccssActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "repairDeviceSuccessActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_repairDeviceSuccess)
    Toolbar tb_repairDeviceSuccess;
    @BindView(R.id.ll_repairDeviceSuccess_oldLayoutDealWith)
    LinearLayout ll_repairDeviceSuccess_oldLayoutDealWith;

    @BindView(R.id.tv_repairDeviceSuccess_PhotoTimeTag)
    TextView tv_repairDeviceSuccess_PhotoTimeTag;
    @BindView(R.id.tv_repairDeviceSuccess_VideoTimeTag)
    TextView tv_repairDeviceSuccess_VideoTimeTag;


    @BindView(R.id.rv_repairDeviceSuccess_photos)
    RecyclerView rv_repairDeviceSuccess_photos;
    @BindView(R.id.rv_repairDeviceSuccess_videos)
    RecyclerView rv_repairDeviceSuccess_videos;

    @BindView(R.id.tv_repairDeviceSuccess_TypeWithFactory)
    TextView tv_repairDeviceSuccess_TypeWithFactory;
    @BindView(R.id.tv_repairDeviceSuccess_DeviceId)
    TextView tv_repairDeviceSuccess_DeviceId;
    @BindView(R.id.tv_repairDeviceSuccess_WorkResult)
    TextView tv_repairDeviceSuccess_WorkResult;

    private Dialog Repair_menu_dialog;

    /**
     * 维修设备表ID
     */
    private String cdmId;
    private List<String> repair_photos = new ArrayList<>();
    private List<String> repair_videos = new ArrayList<>();
    /**
     * 图片列表的分割线
     */
    private MyDecoration myDecoration = null;
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    /**
     * 位置类
     */
    public LocationClient mLocationClient = null;
    private String latitude;
    private String longitude;
    /**
     * 定位监听
     */
    public BDAbstractLocationListener myListener = new MyLocationListenner();
    private HashMap task_info;

    public class MyLocationListenner extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            if (!TextUtils.isEmpty(latitude) || !TextUtils.isEmpty(longitude)) {
                mLocationClient.stop();
            }
        }
    }

    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(1500);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_device_succss);
        ButterKnife.bind(this);
        setLocationOption();
        setSupportActionBar(tb_repairDeviceSuccess);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String curTime = MyDateUtils.getCurTime("yyyy.MM.dd");
        cdmId = getIntent().getStringExtra("cdmId");
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        myDecoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        searchRepairDeviceInfo();
        tv_repairDeviceSuccess_PhotoTimeTag.setText(curTime);
        tv_repairDeviceSuccess_VideoTimeTag.setText(curTime);
        initRecyclerView(rv_repairDeviceSuccess_photos, new DevicePhotoAdapter(repair_photos, getMe()));
        initRecyclerView(rv_repairDeviceSuccess_videos, new DeviceVideoAdapter(repair_videos, getMe()));
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(myDecoration);
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    //查询维修的用户信息，文件信息。
    private List<Map> select_infos, select_files;

    /**
     * 查询维修设备信息
     */
    private void searchRepairDeviceInfo() {
        HttpParams params = new HttpParams();
        params.put("cdmId", cdmId);
        OkHttpUtils.get(GlobalUrl.Repair_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            select_infos = (List<Map>) data.get("info");
                            setDeviceInfo(select_infos);

                            select_files = (List<Map>) data.get("files");
                            setDeviceFileInfo(select_files);
                        }
                    }
                });
    }

    /**
     * 外勤工单ID
     */
    private String owwoId;

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
            owwoId = deviceInfo.get(0).get("owwoId").toString();
            tv_repairDeviceSuccess_TypeWithFactory.setText(deviceType + "-" + factory);
            tv_repairDeviceSuccess_DeviceId.setText(devId);
            tv_repairDeviceSuccess_WorkResult.setText(comment);
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
            if (attaFilePath.contains("jpg")) {
                repair_photos.add(attaFilePath);
            } else {
                repair_videos.add(attaFilePath);
            }
        }
        rv_repairDeviceSuccess_photos.getAdapter().notifyDataSetChanged();
        rv_repairDeviceSuccess_videos.getAdapter().notifyDataSetChanged();
    }

    /**
     * 黄油刀的点击回调
     *
     * @param view
     */
    @OnClick({R.id.bt_repairDeviceSuccess_viewlist, R.id.bt_repairDeviceSuccess_finishOrder,
            R.id.tv_repairDeviceSuccess_oldLayoutDealWith, R.id.bt_repairDeviceSuccess_continueDealWith})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_repairDeviceSuccess_viewlist://查看列表
                Intent intent = new Intent(this, TheOrderFinishedListActivity.class);
                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent.putExtra("owwoId", owwoId);
                startActivity(intent);
                break;
            case R.id.bt_repairDeviceSuccess_finishOrder://结束工单
                showMyDialog();
                break;
            case R.id.tv_repairDeviceSuccess_oldLayoutDealWith:
                LayoutShowHide(ll_repairDeviceSuccess_oldLayoutDealWith, (TextView) view);
                break;
            case R.id.bt_repairDeviceSuccess_continueDealWith:
                continue_dealwith();
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
     * 展示回退/结束工单按钮之后的布局
     */
    private void showMyDialog() {
        int[] views = new int[]{R.id.cb_reason_a,
                R.id.cb_reason_b,
                R.id.cb_reason_c,
                R.id.bt_confirm,
                R.id.bt_cnacel,
                R.id.iv_close};
        CenterDialog dialog = new CenterDialog(getMe(), R.layout.dialog_return_end, views);

        dialog.show();
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_reason);
        final TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_dialog_title.setText("结束工单");

        dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
            @Override
            public void OnCenterItemClick(CenterDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.cb_reason_a:
                        et_content.setText("无法处理");
                        break;
                    case R.id.cb_reason_b:
                        et_content.setText("其他人处理");
                        break;
                    case R.id.cb_reason_c:
                        et_content.setText("已完成");
                        break;
                    case R.id.bt_confirm:
                        //结束工单
                        FinishOrder(et_content.getText().toString());
                        break;
                    case R.id.iv_close:
                        dialog.dismiss();
                        break;
                    case R.id.bt_cnacel:
                        dialog.dismiss();
                        break;
                }
            }
        });
    }

    /**
     * 結束工单
     */

    private void FinishOrder(String comment) {
        if (TextUtils.isEmpty(comment)) {
            ToastUtil.showToast(getMe(), "备注不能为空！");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("owwoId", owwoId);
        params.put("comment", comment);
        params.put("currentLng", longitude);
        params.put("currentLat", latitude);
        OkHttpUtils.post(GlobalUrl.Task_Finish).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                if (map.get("code").toString().equals("10000")) {
                    Map data = (Map) map.get("data");
                    String owrdpId = data.get("owrdpId").toString();
                    Intent intent = new Intent(getMe(), FinalFinishedOrderActivity.class);
                    intent.putExtra("owwoId", owwoId);
                    intent.putExtra("owrdpId", owrdpId);
                    intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                    intent.putExtra("select_infos", (Serializable) select_infos);
                    intent.putExtra("select_files", (Serializable) select_files);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(getMe(), "结束工单失败！");
                }

            }
        });
    }

    /**
     * 继续处理
     */
    private void continue_dealwith() {
        if (Repair_menu_dialog == null) {
            Repair_menu_dialog = new Dialog(this, R.style.dialog_custom);
            View v = LayoutInflater.from(this).inflate(R.layout.continue_dealwith_layout, null);
            Button bt_continue_dealwith_change = ButterKnife.findById(v, R.id.bt_continue_dealwith_change);
            Button bt_continue_dealwith_repair = ButterKnife.findById(v, R.id.bt_continue_dealwith_repair);
            Button bt_continue_dealwith_cancel = ButterKnife.findById(v, R.id.bt_continue_dealwith_cancel);
            bt_continue_dealwith_change.setOnClickListener(this);
            bt_continue_dealwith_repair.setOnClickListener(this);
            bt_continue_dealwith_cancel.setOnClickListener(this);
            Repair_menu_dialog.setContentView(v);
            WindowManager.LayoutParams attributes = Repair_menu_dialog.getWindow().getAttributes();
            attributes.gravity = Gravity.BOTTOM;
            attributes.width = DisplayUtil.Width(getMe());
            Repair_menu_dialog.getWindow().setAttributes(attributes);
        }
        Repair_menu_dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_continue_dealwith_change://继续更换
                Intent intent2 = new Intent(getMe(), ChangeDeviceActivity.class);
                intent2.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent2.putExtra("owwoId", owwoId + "");
                startActivity(intent2);
                break;
            case R.id.bt_continue_dealwith_repair://继续维修
                Intent intent1 = new Intent(getMe(), RepairDeviceActivity.class);
                intent1.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent1.putExtra("owwoId", owwoId + "");
                startActivity(intent1);
                break;
            case R.id.bt_continue_dealwith_cancel://取消
                if (Repair_menu_dialog.isShowing())
                    Repair_menu_dialog.dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //禁止返回按键
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != Repair_menu_dialog && Repair_menu_dialog.isShowing()) {
            Repair_menu_dialog.dismiss();
        }
    }
}
