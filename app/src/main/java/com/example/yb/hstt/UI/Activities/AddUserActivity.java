package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.yb.hstt.Adpater.Recycle_user_device_adapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.HsttEvent;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.ListViewHeaderView;
import com.example.yb.hstt.dao.DbHelper.UserInfoDbHelper;
import com.example.yb.hstt.dao.bean.UserInfo;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 兼--增加用户的页面和--用户信息的页面
 */
public class AddUserActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.add_terminal_toolbar)
    Toolbar addTerminalToolbar;//标题栏
    @BindView(R.id.bt_save)
    Button btSave;//保存按钮
    @BindView(R.id.tv_asset_no)
    TextView tvAssetNo;//资产号
    @BindView(R.id.tv_location)
    EditText tvLocation;//位置
    @BindView(R.id.rc_pic)
    RecyclerView rcPic;//现场拍照
    @BindView(R.id.ll_add_user_device)
    LinearLayout llAddUserDevice;
    @BindView(R.id.activity_add_terminal)
    LinearLayout activityAddTerminal;
    @BindView(R.id.tv_add_device_title)
    TextView tvAddDeviceTitle;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @BindView(R.id.device_list_header)
    XRefreshView deviceListHeader;
    @BindView(R.id.tv_title_add_device)
    TextView tvTitleAddDevice;
    private boolean isAddDevice;//页面作用是添加用户还是编辑用户
    private boolean isEditting = false;
    private String title;
    private int i = 0;
    private double lat;//获取的经纬度
    private double lon;//获取的经纬度
    /**
     * 定位监听
     */
    public BDLocationListener myListener = new MyLocationListenner();
    private LocationClient mLocationClient;
    private UserInfo userInfo;//用户信息
    private Recycle_user_device_adapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_devicel);
        ButterKnife.bind(this);
        setLocationOption();

        Intent intent = getIntent();
        isAddDevice = intent.getBooleanExtra("AddDevice", false);

        //标题栏初始化
        addTerminalToolbar.setNavigationIcon(R.mipmap.ic_return);
        addTerminalToolbar.setNavigationOnClickListener(this);
        //点击事件初始化
        btSave.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        llAddUserDevice.setOnClickListener(this);

        if (!isAddDevice) {
            //编辑原有用户
            //网络获取用户信息
            btSave.setText("编辑");
            userInfo = (UserInfo) intent.getSerializableExtra("user_info");
            llAddUserDevice.setVisibility(View.VISIBLE);//编辑用户时可以新增设备
            initData();
            initUserDevice();
            ivScan.setClickable(false);
            ivLocation.setClickable(false);

        } else {
            //新建用户
            //所有数据置空
            title = "添加用户";
            tvAddDeviceTitle.setText(title);
            btSave.setText("保存");
            llAddUserDevice.setVisibility(View.GONE);//新增用户是无法新增设备,必须等用户添加完成后
            tvTitleAddDevice.setVisibility(View.GONE);

            ivLocation.setClickable(true);
            ivScan.setClickable(true);

            tvLocation.setEnabled(true);
        }


        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.VERTICAL, false);
        rcPic.setLayoutManager(manager);
        //设置按钮列表数据

        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        deviceListHeader.setPullLoadEnable(false);
        deviceListHeader.setCustomHeaderView(new ListViewHeaderView(this));
        deviceListHeader.setDampingRatio(3.6f);
        deviceListHeader.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                initUserDevice();
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //从别的页面返回,刷新用户下设备数据
        if (null != userInfo) initUserDevice();
    }

    /**
     * 获取用户下关联设备
     */
    private void initUserDevice() {
        Map<String, Object> para = new HashMap<>();
        para.put("cust_no", userInfo.getUser_id());              //用户编号,必选                          //位置,必选
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010104", para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        final ArrayList user_device = (ArrayList) result.get("list");
                        adapter = new Recycle_user_device_adapter(getMe());
                        adapter.setOnItemClickListener(new Recycle_user_device_adapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //编辑用户下的设备
                                Intent intent = new Intent(getMe(), UserDeviceDetailActivity.class);


                                HashMap map = (HashMap) user_device.get(position);
                                Iterator iter = map.entrySet().iterator();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    String key = String.valueOf(entry.getKey());
                                    Object val = entry.getValue();
                                    String val_a = String.valueOf(val);
                                    if (null == val_a) {
                                        String val_s = String.valueOf("数据获取失败");
                                        map.put(key, val_s);
                                    }

                                    if (val_a.equals("null")) {
                                        String val_s = String.valueOf("数据获取失败");
                                        map.put(key, val_s);
                                    }
                                }
                                intent.putExtra("user_device", map);

                                startActivity(intent);
                            }
                        });
                        adapter.setData(user_device);
                        rcPic.setAdapter(adapter);
                    } else {
                        ToastUtil.showToast(getMe(), result.get("msg").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, s, call, response, e);
                deviceListHeader.stopRefresh();
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                if (null != e) Log.e(TAG, "获取终端列表失败" + e.toString());
            }
        });
    }

    /**
     * 初始化用户信息
     */
    private void initData() {
        tvLocation.setText(userInfo.getUser_addr());
        tvAssetNo.setText(userInfo.getUser_id());
    }


    private static final String TAG = "AddTerminalActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                Log.i(TAG, "返回");
                finish();
                break;
            case R.id.bt_save:
                Log.i(TAG, "保存");
                if (isAddDevice) {
                    //添加用户,保存
                    String a = tvAssetNo.getText().toString();
                    String b = tvLocation.getText().toString();

                    if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b)) {
                        UserInfoDbHelper helper = UserInfoDbHelper.getInstance(getMe());
                        UserInfo info = new UserInfo();
                        info.setUser_addr(tvLocation.getText().toString());
                        info.setUser_id(tvAssetNo.getText().toString());
                        info.setUser_name("");
                        info.setUser_phone("");
                        helper.insert(info);
                        List<UserInfo> infos = helper.getAllUser();
                        Log.i(TAG, infos.size() + "个用户");

                        HsttEvent event = new HsttEvent();
                        event.setREFRESH_USERS(event.getREFRESH_USERS());
                        EventBus.getDefault().post(event);
                        finish();

                    } else {
                        ToastUtil.showToast(getMe(), "用户编号和位置信息不能为空");
                    }

                } else {
                    //编辑用户

                    //判断是否处于编辑状态 isEditting
                    if (i == 0) {
                        isEditting = true;
                        i++;
                    }
                    if (isEditting) {
                        //正在编辑

                        //所有编辑类按钮设置为可点击
                        ivLocation.setClickable(true);
                        ivScan.setClickable(true);

                        btSave.setText("保存");
                        isEditting = false;

                        tvLocation.setEnabled(true);
                    } else {
                        //编辑完成

                        //所有编辑类按钮设置为不可点击
                        ivLocation.setClickable(false);
                        ivScan.setClickable(false);

                        btSave.setText("编辑");
                        isEditting = true;
                        userInfo.setUser_id(tvAssetNo.getText().toString());
                        userInfo.setUser_addr(tvLocation.getText().toString());
                        UserInfoDbHelper.getInstance(getMe()).updateInfo(userInfo);
                        Log.i(TAG, "用户信息修改成功");
                        ToastUtil.showToast(getMe(), "用户信息修改成功");

                        tvLocation.setEnabled(false);
                    }

                }
                break;
            case R.id.iv_location:
                Intent intent0 = new Intent(this, SelectLocationActivity.class);
                intent0.putExtra("xdata", lat);
                intent0.putExtra("ydata", lon);
                startActivityForResult(intent0, 103);
                break;
            case R.id.iv_scan:
                Log.i(TAG, "扫描资产号");
                Intent intent1 = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent1, 101);
                break;
            case R.id.ll_add_user_device:
                Log.i(TAG, "添加用户界面--添加设备");
                Intent intent = new Intent(getMe(), AddUserDeviceActivity.class);
                intent.putExtra("user_info", userInfo);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101://资产号
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        tvAssetNo.setText(result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        showMesage("解析二维码失败!");
                    }
                }
                break;

            case 103:
                if (GlobalManager.position != null) {
                    mLocationClient.stop();
                    LatLng latLng = GlobalManager.position;
                    GeoCoder coder = GeoCoder.newInstance();
                    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                        public void onGetGeoCodeResult(GeoCodeResult result) {
                            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                //没有检索到结果
                            }
                            //获取地理编码结果
                        }

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                //没有找到检索结果
                                Log.i(TAG, "反向地理编码无结果");
                            }
                            //获取反向地理编码结果
                            String s = result.getAddress();
                            tvLocation.setText(s);
                        }
                    };
                    coder.setOnGetGeoCodeResultListener(listener);
                    coder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                    coder.destroy();
                }
                break;

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddUser Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String cityname = location.getCity();
            try {
                lat = location.getLatitude();
                lon = location.getLongitude();
                cityname = cityname.substring(0, cityname.length() - 1);
                Log.i(TAG, cityname);
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }

        }

    }

    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getMe());

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();

    }
}
