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
import android.util.Log;
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

public class ChangeDeviceSuccessActivity extends HsttBaseActivity implements View.OnClickListener {
    private static final String TAG = "ChangeDeviceSuccessActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_changeDeviceSuccess)
    Toolbar tb_changeDeviceSuccess;
    @BindView(R.id.ll_changeDeviceSuccess_oldLayoutDealWith)
    LinearLayout ll_changeDeviceSuccess_oldLayoutDealWith;
    @BindView(R.id.ll_changeDeviceSuccess_newLayoutDealWith)
    LinearLayout ll_changeDeviceSuccess_newLayoutDealWith;
    //更换时的传图片，视频时刻
    @BindView(R.id.tv_changeDeviceSuccess_oldDevicePhotoTimeTag)
    TextView tv_changeDeviceSuccess_oldDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDeviceSuccess_oldDeviceVideoTimeTag)
    TextView tv_changeDeviceSuccess_oldDeviceVideoTimeTag;
    @BindView(R.id.tv_changeDeviceSuccess_newDevicePhotoTimeTag)
    TextView tv_changeDeviceSuccess_newDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDeviceSuccess_newDeviceVideoTimeTag)
    TextView tv_changeDeviceSuccess_newDeviceVideoTimeTag;

    /**
     * 旧设备-类型-生产厂家
     */
    @BindView(R.id.tv_changeDeviceSuccess_OldTypeWithFactory)
    TextView tvChangeDeviceSuccessOldTypeWithFactory;
    /**
     * 旧设备-设备编号
     */
    @BindView(R.id.tv_changeDeviceSuccess_OldDeviceId)
    TextView tvChangeDeviceSuccessOldDeviceId;
    /**
     * 旧设备-备注
     */
    @BindView(R.id.tv_changeDeviceSuccess_OldWorkResult)
    TextView tv_changeDeviceSuccess_OldWorkResult;
    /**
     * 新设备-类型-生产厂家
     */
    @BindView(R.id.tv_changeDeviceSuccess_NewTypeWithFactory)
    TextView tvChangeDeviceSuccessNewTypeWithFactory;
    /**
     * 新设备-设备编号
     */
    @BindView(R.id.tv_changeDeviceSuccess_NewDeviceId)
    TextView tvChangeDeviceSuccessNewDeviceId;
    /**
     * 新设备-备注
     */
    @BindView(R.id.tv_changeDeviceSuccess_NewWorkResult)
    TextView tv_changeDeviceSuccess_NewWorkResult;

    @BindView(R.id.rv_changeDeviceSuccess_oldphotos)
    RecyclerView rv_changeDeviceSuccess_oldphotos;
    @BindView(R.id.rv_changeDeviceSuccess_oldvideos)
    RecyclerView rv_changeDeviceSuccess_oldvideos;
    @BindView(R.id.rv_changeDeviceSuccess_newphotos)
    RecyclerView rv_changeDeviceSuccess_newphotos;
    @BindView(R.id.rv_changeDeviceSuccess_newvideos)
    RecyclerView rv_changeDeviceSuccess_newvideos;
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;
    private Dialog bottom_menu_dialog;
    /**
     * 更换设备表ID
     */
    private String cdcId;
    private List<String> old_photos = new ArrayList<>();
    private List<String> old_videos = new ArrayList<>();
    private List<String> new_photos = new ArrayList<>();
    private List<String> new_videos = new ArrayList<>();
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
        setContentView(R.layout.activity_change_device_success);
        ButterKnife.bind(this);
        cdcId = getIntent().getStringExtra("cdcId");
        searchChangeDeviceInfo();
        setLocationOption();
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        setSupportActionBar(tb_changeDeviceSuccess);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String curTime = MyDateUtils.getCurTime("yyyy.MM.dd");
        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);


        tv_changeDeviceSuccess_oldDevicePhotoTimeTag.setText(curTime);
        tv_changeDeviceSuccess_oldDeviceVideoTimeTag.setText(curTime);
        tv_changeDeviceSuccess_newDevicePhotoTimeTag.setText(curTime);
        tv_changeDeviceSuccess_newDeviceVideoTimeTag.setText(curTime);
        initRecycleview(rv_changeDeviceSuccess_oldphotos, new DevicePhotoAdapter(old_photos, getMe()));
        initRecycleview(rv_changeDeviceSuccess_oldvideos, new DeviceVideoAdapter(old_videos, getMe()));
        initRecycleview(rv_changeDeviceSuccess_newphotos, new DevicePhotoAdapter(new_photos, getMe()));
        initRecycleview(rv_changeDeviceSuccess_newvideos, new DeviceVideoAdapter(new_videos, getMe()));
    }

    private void initRecycleview(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(photo_list_decoration);
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    //更换的用户信息，文件信息。
    private List<Map> select_infos, select_files;

    /**
     * 查询更换设备信息
     */
    private void searchChangeDeviceInfo() {
        HttpParams params = new HttpParams();
        params.put("cdcId", cdcId);
        OkHttpUtils.get(GlobalUrl.Change_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> oldInfo = (List<Map>) data.get("oldInfo");
                            setDeviceInfo(oldInfo, tvChangeDeviceSuccessOldTypeWithFactory,
                                    tvChangeDeviceSuccessOldDeviceId, tv_changeDeviceSuccess_OldWorkResult);
                            select_infos = (List<Map>) data.get("newInfo");
                            setDeviceInfo(select_infos, tvChangeDeviceSuccessNewTypeWithFactory,
                                    tvChangeDeviceSuccessNewDeviceId, tv_changeDeviceSuccess_NewWorkResult);

                            List<Map> oldFiles = (List<Map>) data.get("oldFiles");
                            setDeviceFileInfo(oldFiles, true);
                            select_files = (List<Map>) data.get("newFiles");
                            setDeviceFileInfo(select_files, false);
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
     * @param tv_TypeWithFactory
     * @param tv_deviceid
     */
    private void setDeviceInfo(List<Map> deviceInfo, TextView tv_TypeWithFactory, TextView tv_deviceid, TextView tv_workResult) {
        if (deviceInfo != null && deviceInfo.size() > 0) {
            String factory = deviceInfo.get(0).get("devManufacturerNo").toString();
            String deviceType = deviceInfo.get(0).get("devTypeNo").toString();
            String devId = deviceInfo.get(0).get("devId").toString();
            String comment = deviceInfo.get(0).get("comment").toString();
            owwoId = deviceInfo.get(0).get("owwoId").toString();
            tv_TypeWithFactory.setText(deviceType + "-" + factory);
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
            if (is_old) {
                if (attaFilePath.contains("jpg")) {
                    old_photos.add(attaFilePath);
                } else {
                    old_videos.add(attaFilePath);
                }
            } else {
                if (attaFilePath.contains("jpg")) {
                    new_photos.add(attaFilePath);
                } else {
                    new_videos.add(attaFilePath);
                }
            }
        }
        rv_changeDeviceSuccess_oldphotos.getAdapter().notifyDataSetChanged();
        rv_changeDeviceSuccess_oldvideos.getAdapter().notifyDataSetChanged();
        rv_changeDeviceSuccess_newphotos.getAdapter().notifyDataSetChanged();
        rv_changeDeviceSuccess_newvideos.getAdapter().notifyDataSetChanged();
    }

    /**
     * 黄油刀的点击回调
     *
     * @param view
     */
    @OnClick({R.id.bt_changeDeviceSuccess_viewlist, R.id.bt_changeDeviceSuccess_finishOrder, R.id.tv_changeDeviceSuccess_oldLayoutDealWith,
            R.id.tv_changeDeviceSuccess_newLayoutDealWith, R.id.bt_changeDeviceSuccess_continueDealWith})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_changeDeviceSuccess_viewlist:
                Intent intent = new Intent(this, TheOrderFinishedListActivity.class);
                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent.putExtra("owwoId", owwoId);
                startActivity(intent);
                break;
            case R.id.bt_changeDeviceSuccess_finishOrder:
                showMyDialog();
                break;
            case R.id.tv_changeDeviceSuccess_oldLayoutDealWith:
                LayoutShowHide(ll_changeDeviceSuccess_oldLayoutDealWith, (TextView) view);
                break;
            case R.id.tv_changeDeviceSuccess_newLayoutDealWith:
                LayoutShowHide(ll_changeDeviceSuccess_newLayoutDealWith, (TextView) view);
                break;
            case R.id.bt_changeDeviceSuccess_continueDealWith:
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
//                        startActivity(new Intent(getMe(), FinalFinishedOrderActivity.class));
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
                Log.i(TAG, "Task_Finish: " + map);
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
        if (bottom_menu_dialog == null) {
            bottom_menu_dialog = new Dialog(this, R.style.dialog_custom);
            View v = LayoutInflater.from(this).inflate(R.layout.continue_dealwith_layout, null);
            Button bt_continue_dealwith_change = ButterKnife.findById(v, R.id.bt_continue_dealwith_change);
            Button bt_continue_dealwith_repair = ButterKnife.findById(v, R.id.bt_continue_dealwith_repair);
            Button bt_continue_dealwith_cancel = ButterKnife.findById(v, R.id.bt_continue_dealwith_cancel);
            bt_continue_dealwith_change.setOnClickListener(this);
            bt_continue_dealwith_repair.setOnClickListener(this);
            bt_continue_dealwith_cancel.setOnClickListener(this);
            bottom_menu_dialog.setContentView(v);
            WindowManager.LayoutParams attributes = bottom_menu_dialog.getWindow().getAttributes();
            attributes.gravity = Gravity.BOTTOM;
            attributes.width = DisplayUtil.Width(getMe());
            bottom_menu_dialog.getWindow().setAttributes(attributes);
        }
        bottom_menu_dialog.show();
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
                intent1.putExtra("owwoId", owwoId + "");
                intent1.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                startActivity(intent1);
                break;
            case R.id.bt_continue_dealwith_cancel://取消
                if (bottom_menu_dialog.isShowing())
                    bottom_menu_dialog.dismiss();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bottom_menu_dialog!=null&&bottom_menu_dialog.isShowing()) {
            bottom_menu_dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //禁止返回按键
    }
}
