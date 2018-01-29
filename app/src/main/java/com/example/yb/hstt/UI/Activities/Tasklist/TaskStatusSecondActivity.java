package com.example.yb.hstt.UI.Activities.Tasklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.yb.hstt.Adpater.Recycle_pic_hor_adapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.MainActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.DialogUtil;
import com.example.yb.hstt.Utils.Gps;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.PositionUtils;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.lzy.okhttputils.OkHttpUtils;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.yb.hstt.Utils.MyDateUtils.date_Format;

/**
 * 状态2:任务未接单,最下面的按钮是"到达现场"
 */
public class TaskStatusSecondActivity extends HsttBaseActivity implements View.OnClickListener , SensorEventListener {
    private static final String TAG = "TaskStatusSecondActivit";
    //定位地图相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    @BindView(R.id.task_status_second_toolbar)
    Toolbar taskStatusSecondToolbar;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_task_info)
    ImageView ivTaskInfo;
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;
    @BindView(R.id.tv_start_deal)
    Button tvStartDeal;
    @BindView(R.id.bt_more)
    Button btMore;
    @BindView(R.id.v_trans)
    View vTrans;
    @BindView(R.id.activity_task_status_second)
    RelativeLayout activityTaskStatusSecond;
    @BindView(R.id.check_location)
    TextView checkLocation;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private BaiduMap mBaiduMap = null;

    private String pop_title;

    //定位相关参数
    private double mCurrentLat = GlobalManager.lat_for_task;
    private double mCurrentLon = GlobalManager.lng_for_task;
    private float mCurrentAccracy;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private Double lastX = 0.0;
    private Marker mMarkerA;//做成全局的是因为退出界面时候要清除掉

    private PopupWindow pop_call;
    private PopupWindow more_menu;
    private HashMap task_info;
    private double addr_lat;
    private double addr_lng;
    private Polyline mPolyline;
    private HashMap data;
    private String dev_info;
    private long count_time;
    private CenterDialog dialog;
    private String addr_lat_s = "";
    private String addr_lng_s = "";
    private boolean location_confirm = false;
    private AlertDialog alertDialog;

    private  int a = 0;
    private boolean is_first_loc = false;
    private LatLng point;
    private LatLng des_latlng;
    private double mCurrentLat_after;
    private double mCurrentLon_after;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status_second);
        ButterKnife.bind(this);

        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");

        addr_lat_s = String.valueOf(task_info.get("crAddressLat"));
        addr_lng_s = String.valueOf(task_info.get("crAddressLng"));

        if (!TextUtils.isEmpty(addr_lat_s)) {
            addr_lat = Double.parseDouble(addr_lat_s);
        } else {
            addr_lat = Double.parseDouble(GlobalManager.Globel_lat);
        }

        if (!TextUtils.isEmpty(addr_lng_s)) {
            addr_lng = Double.parseDouble(addr_lng_s);
        } else {
            addr_lng = Double.parseDouble(GlobalManager.Globle_lng);
        }

        mapView.removeViewAt(2);

        point = new LatLng(addr_lat, addr_lng);

        initView();

        initData(GlobalUrl.Get_TaskInfo);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initMapAndLocation();
            }
        });



        initCallPop();

        initMoreMenu();

        initOverLay();

        initMapClick();

        initCountTim();


    }

    /**
     * 倒计时处理
     */
    private void count_time() {
        String dispatch_time = (String) task_info.get("woDispatchDt");
        try {
            long time = MyDateUtils.getLongDateFromString(dispatch_time, date_Format);
            time = time + count_time;//添加两小时,为截止日期
            long timeGetTime = new Date().getTime();//当前日期
            final long count = time - timeGetTime;
            final String[] count_s = {MyDateUtils.formatDuring(count)};
            tvTime.setText(count_s[0]);
            CountDownTimer timer = new CountDownTimer(count, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    count_s[0] = MyDateUtils.formatDuring(millisUntilFinished);
                    if (count_s[0].equals("已超时")) {
                        tvTime.setTextColor(Color.RED);
                    }
                    tvTime.setText(count_s[0]);
                }

                @Override
                public void onFinish() {
                    tvTime.setTextColor(Color.RED);
                    tvTime.setText("已超时");
                }
            };
            timer.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * 后台查询倒计时时间
     */
    private void initCountTim() {
        OkHttpUtils.get(GlobalUrl.CountTime)
                .params("owwoLevelTypeNo", String.valueOf(task_info.get("owwoLevelTypeNo")))
                .params("ow", "outwork")
                .params("owItemName", "WO_ARRIVE")
                .execute(new CommonCallback<Map>(getMe()) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                Log.i(TAG, "获取数据成功,弹窗");
                                ArrayList datas = (ArrayList) map.get("data");
                                if (null != datas) {
                                    HashMap data = (HashMap) datas.get(0);
                                    int count_time_int = (int) data.get("dealTimeLimit");
                                    String count_unit = (String) data.get("dtlUnitTypeNo");
                                    if (count_unit.equals("12")) {//minute
                                        count_time = count_time_int * 60000;
                                    } else if (count_unit.equals("13")) {//second
                                        count_time = count_time_int * 1000;
                                    } else if (count_unit.equals("10")) {//hour
                                        count_time = count_time_int * 60000 * 60;
                                    }
                                    count_time();
                                }

                            } else {
                                ToastUtil.showToast(getMe(), "请求任务信息失败,错误码: " + res_code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (null != e) {
                            ToastUtil.showToast(getMe(), "请求任务信息失败" + e.toString());
                        } else {
                            ToastUtil.showToast(getMe(), "请求任务信息失败");
                        }
                    }
                });

    }

    /**
     * 地图点击事件以及覆盖物点击事件
     */
    private void initMapClick() {

        //地图点击后取消所有InfoWindow显示
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //点击Marker显示InfoWindow
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                LinearLayout layout = new LinearLayout(getApplicationContext());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.x540), ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(params);

                layout.setBackgroundResource(R.drawable.infopatch);
                layout.setOrientation(LinearLayout.VERTICAL);
                InfoWindow.OnInfoWindowClickListener listener = null;

                //第一层布局
                LinearLayout layout_in = new LinearLayout(getApplicationContext());
                layout_in.setLayoutParams(params);
                layout_in.setOrientation(LinearLayout.HORIZONTAL);
                layout_in.setGravity(Gravity.CENTER_VERTICAL);


                //第一层布局里的文字控件
                TextView textView1 = new TextView(getMyApplication());
                textView1.setTextColor(getResources().getColor(R.color.black));
                textView1.setText("联系人以及电话");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1.0f;
                textView1.setLayoutParams(lp);

                //第一层布局添加文字I


                ImageView imageView = new ImageView(getMe());
                imageView.setImageResource(R.drawable.takephone);
                LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(lp_iv);


                //第二层布局文字控件
                TextView textView2 = new TextView(getMyApplication());
                textView2.setText("联系人地址联系人地址联系人地址联系人地址联系人地址联系人地址联系人地址联系人地址");
                textView2.setTextColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams lp_tv2 = new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.x540),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView2.setLayoutParams(lp_tv2);

                layout_in.addView(textView1);
                layout_in.addView(imageView);
                layout.addView(layout_in);
                layout.addView(textView2);

                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        LatLng ll = marker.getPosition();
                        LatLng llNew = new LatLng(ll.latitude + 0.005,
                                ll.longitude + 0.005);
                        marker.setPosition(llNew);
                        mBaiduMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(layout), ll, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);

                return true;
            }
        });
    }

    /**
     * 画目的地的坐标
     */
    private void initOverLay() {
        // 将所用坐标转换成百度坐标
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        if (TextUtils.isEmpty(addr_lat_s)||TextUtils.isEmpty(addr_lng_s)) {
            converter.coord(new LatLng(Double.parseDouble(GlobalManager.Globel_lat), Double.parseDouble(GlobalManager.Globle_lng)));
        } else {
            converter.coord(point);
        }
        des_latlng = converter.convert();
        addr_lat = des_latlng.latitude;
        addr_lng = des_latlng.longitude;


        BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

        LatLng llA = new LatLng(addr_lat, addr_lng);
        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
    }

    private void initView() {

        taskStatusSecondToolbar.setNavigationIcon(R.mipmap.ic_return);
        taskStatusSecondToolbar.setNavigationOnClickListener(this);

        ivTaskInfo.setOnClickListener(this);//右上角工单信息
        ivNavigation.setOnClickListener(this);//导航按钮
        tvStartDeal.setOnClickListener(this);//到达现场按钮
        btMore.setOnClickListener(this);//更多(退回/结束工单)按钮

        if (mapView != null) {
            mBaiduMap = mapView.getMap();
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
        }
    }


    /**
     * 初始化定位以及地图信息
     */
    private void initMapAndLocation() {
        //定位相关
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
        // 地图初始化
        mBaiduMap = mapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位间隔
        mLocClient.setLocOption(option);
        mLocClient.start();
        Log.i(TAG, "开始定位");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;
            case R.id.iv_task_info:
                int[] views = new int[]{R.id.iv_call};

                CenterDialog dialog = new CenterDialog(getMe(), R.layout.task_detail, views);

                dialog.show();

                //隐藏不必要的控件
                TextView tv1 = (TextView) dialog.findViewById(R.id.tv_last_time);
                TextView tv2 = (TextView) dialog.findViewById(R.id.tv_left_time);
                RelativeLayout rl_deal = (RelativeLayout) dialog.findViewById(R.id.rl_deal);
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                rl_deal.setVisibility(View.GONE);

                TextView device_info = (TextView) dialog.findViewById(R.id.tv_equipment_type);//设备品牌+型号
                TextView tv_equipment_content = (TextView) dialog.findViewById(R.id.tv_equipment_content);//设备故障信息
                TextView tv_contact = (TextView) dialog.findViewById(R.id.tv_contact);//联系人
                TextView tv_contact_phone = (TextView) dialog.findViewById(R.id.tv_contact_phone);//联系人电话
                TextView tv_contact_addr = (TextView) dialog.findViewById(R.id.tv_contact_addr);//联系人地址
                TextView tv_left_time = (TextView) dialog.findViewById(R.id.tv_left_time);//联系人地址
                RecyclerView rv_info_pic = (RecyclerView) dialog.findViewById(R.id.rv_info_pic);//图片列表
                LinearLayout ll_title_pop = (LinearLayout) dialog.findViewById(R.id.ll_title_pop);

                String s_dev_type = "";//设备类型
                String s_dev_factory = "";//设备厂家
                String s_equipment_content = "";//设备故障表述
                String s_contact = "";//联系人
                String s_contact_phone = "";//电话
                String s_contact_addr = "";//地址


                //列表初始化
                LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
                rv_info_pic.setLayoutManager(manager);

                dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
                    @Override
                    public void OnCenterItemClick(CenterDialog dialog, View view) {
                        switch (view.getId()) {
                            case R.id.iv_call:
                                //打电话的弹窗
                                dialog.dismiss();
                                pop_call.setAnimationStyle(R.style.popWindow_animation);
                                pop_call.showAtLocation(activityTaskStatusSecond, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));//parent为泡泡窗体所在的父容器view
                                setScreenDark();
                                break;
                        }
                    }
                });

                dev_info = "";
                if (null != data) {
                    ArrayList<HashMap> devs = (ArrayList) data.get("devs");//工单设备信息
                    ArrayList<HashMap> task_infos = (ArrayList) data.get("info");//工单信息
                    task_info = task_infos.get(0);//工单信息是唯一的

                    //设备信息不唯一,需要遍历

                    Iterator ite = devs.iterator();
                    while (ite.hasNext()) {
                        HashMap dev = (HashMap) ite.next();
                        s_dev_type = s_dev_type + String.valueOf(dev.get("devTypeNo"));
                        s_dev_factory = s_dev_factory + String.valueOf(dev.get("devManufacturerNo"));
                        dev_info = s_dev_type + "-" + s_dev_factory;
                        if (ite.hasNext()) {
                            dev_info = dev_info + "\n";
                        }
                    }

                    s_equipment_content = String.valueOf(task_info.get("crDescribe"));
                    s_contact = String.valueOf(task_info.get("crContactName"));
                    s_contact_phone = String.valueOf(task_info.get("crContactPhoneNum"));
                    s_contact_addr = String.valueOf(task_info.get("crAddress"));

                    ArrayList pics = (ArrayList) data.get("files");
                    ArrayList<String> pic_urls = new ArrayList<>();
                    for (int i = 0; i < pics.size(); i++) {
                        HashMap map = (HashMap) pics.get(i);
                        String pic_url = (String) map.get("attaFilePath");
                        pic_urls.add(pic_url);
                    }
                    Recycle_pic_hor_adapter adapter = new Recycle_pic_hor_adapter(getMe(), pic_urls);
                    rv_info_pic.setAdapter(adapter);


                } else {
                    s_dev_type = "网络异常";//设备类型
                    s_dev_factory = "网络异常";//设备厂家
                    s_equipment_content = "网络异常";//设备故障表述
                    s_contact = "网络异常";//联系人
                    s_contact_phone = "网络异常";//电话
                    s_contact_addr = "网络异常";//地址
                    dev_info = "网络异常";//组合设备信息
                }
                if (!TextUtils.isEmpty(dev_info)) {
                    device_info.setText(dev_info);
                } else {
                    device_info.setText("无设备信息");
                }
                tv_equipment_content.setMovementMethod(new ScrollingMovementMethod());
                tv_equipment_content.setText(s_equipment_content);
                tv_contact.setText(s_contact);
                tv_contact_phone.setText(s_contact_phone);
                tv_contact_addr.setText(s_contact_addr);

                tv_equipment_content.setOnClickListener(this);
                break;
            case R.id.iv_navigation:

                break;

            case R.id.rl_call_a:
                Log.i(TAG, "打电话");
                pop_call.dismiss();
                //用intent启动拨打电话
                AppUtils.callPhone(String.valueOf(task_info.get("crContactPhoneNum")), getMe());
                break;
            case R.id.rl_call_cancel:
                Log.i(TAG, "取消打电话");
                if (pop_call.isShowing()) {
                    pop_call.dismiss();
                }
                break;
            case R.id.rl_task_return:
                Log.i(TAG, "回退工单");
                pop_title = "回退原因";
                showMyDialog();
                break;
            case R.id.rl_task_end:
                Log.i(TAG, "结束工单");
                pop_title = "回退原因";
                showMyDialog();
                break;
            case R.id.rl_do_together://协作完成
                pop_title = "协作完成";
                showMyDialog();
                break;
            case R.id.rl_return_main://返回首页
                Intent intent = new Intent(getMe(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_start_deal:
                if (location_confirm) {
                    arrive_click();
                }else {
                    show_confirm_dialog("","",1);
                }
                break;
            case R.id.bt_more:
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(activityTaskStatusSecond, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                initListener();
                setScreenDark();
                break;
            case R.id.tv_equipment_content:// 开始接单
                DialogUtil.showNormalDialog(String.valueOf(task_info.get("crDescribe")),getMe());
                break;
        }
    }

    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(TaskStatusSecondActivity.this, R.color.black_trans);
    }

    /**
     * 当位置信息未获取到的时候,需要提醒用户
     * @param url
     * @param comment
     * @param type 上报类型,1为下一步,2为退回/协作/结束
     */
    public void show_confirm_dialog(final String url, final String comment, final int type) {
        alertDialog = new  AlertDialog.Builder(this)
                .setTitle("位置信息确认" )
                .setMessage("还未获得当前位置,是否要继续操作?" )
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"开始操作");
                        if (type==1) {
                            arrive_click();
                        }else if (type == 2) {
                            task_opreate(url, comment);
                        }

                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * 展示回退/结束工单按钮之后的布局
     */
    private void showMyDialog() {
        int[] views = new int[]{R.id.cb_reason_a,
                R.id.cb_reason_b,
                R.id.cb_reason_c,
                R.id.bt_confirm,
                R.id.bt_cnacel};
        dialog = new CenterDialog(getMe(), R.layout.dialog_return_end, views);

        dialog.show();
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_reason);
        final TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_dialog_title.setText(pop_title);

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
                        String comment = et_content.getText().toString();
                        if (!TextUtils.isEmpty(comment)) {
                            if (pop_title.contains("回退")&&location_confirm) {
                                // 走退回接口
                                task_opreate(GlobalUrl.Task_Back, comment);
                            } else if (pop_title.contains("结束")&&location_confirm) {
                                // 走结束接口
                                task_opreate(GlobalUrl.Task_Finish, comment);
                            } else if (pop_title.contains("协作")&&location_confirm) {
                                // 走协作接口
                                task_opreate(GlobalUrl.Finish_Together, comment);
                            } else {
                                //位置信息获取失败,给用户弹窗
                                show_confirm_dialog(getDealUrl(),comment,2);
                            }
                        } else {
                            ToastUtil.showToast(getMe(), "原因不能为空");
                        }

                        break;
                    case R.id.bt_cnacel:
                        Log.i(TAG, "关闭弹窗");
                        dialog.dismiss();
                        break;
                    case R.id.iv_close:
                        Log.i(TAG, "关闭弹窗");
                        dialog.dismiss();
                        break;
                }
            }
        });

    }

    /**
     * 根据不同的选项,设置不同的url
     * @return
     */
    private String getDealUrl() {
        String url = "";
        if (pop_title.contains("回退")) {
            // 走退回接口
            url = GlobalUrl.Task_Back;
        } else if (pop_title.contains("结束")) {
            // 走结束接口
            url = GlobalUrl.Task_Finish;
        } else if (pop_title.contains("协作")) {
            // 走协作接口
            url = GlobalUrl.Finish_Together;
        }
        return url;
    }

    /**
     * 对工单的操作,包括退回和结束工单
     *
     * @param task_back
     */
    private void task_opreate(final String task_back, String comment) {
        OkHttpUtils.post(task_back)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .params("comment", comment)
                .params("currentLng", String.valueOf(mCurrentLon_after))
                .params("currentLat", String.valueOf(mCurrentLon_after))
                .execute(new CommonCallback<Map>(getMe(),true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        int code = (int) map.get("code");
                        if (code == 10000) {

                            try {
                                //获取步骤id,赋值给全局变量以供上传位置服务使用
                                HashMap map1 = (HashMap) map.get("data");
                                String owrdpId = String.valueOf(map1.get("owrdpId"));
                                AppPreferences preferences = new AppPreferences(getMe());
                                preferences.put("owrdpId", owrdpId);
                            } catch (Exception e) {
                                Log.i(TAG, "类型转换异常");
                            }

                            if (task_back.contains("302")) {
                                //回退接口,进入主页面
                                Intent intent = new Intent(getMe(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (task_back.contains("303")) {
                                //结束接口,也进入结束工单页面
                                Intent intent = new Intent(getMe(), TaskFinishActivity.class);
                                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                                intent.putExtra("DEVICE_INFO", dev_info);//问题设备信息
                                intent.putExtra("PICS", dev_info);//问题设备信息
                                startActivity(intent);
                                finish();
                            } else if (task_back.contains("305")) {
                                //协作接口
                                Intent intent = new Intent(getMe(), TaskFinishActivity.class);
                                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                                intent.putExtra("DEVICE_INFO", dev_info);//问题设备信息
                                intent.putExtra("PICS", dev_info);//问题设备信息
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            ToastUtil.showToast(getMe(), "数据异常！");
                        }

                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                    }
                });
    }

    private void initData(String url) {

        OkHttpUtils.get(url)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .execute(new CommonCallback<Map>(getMe()) {


                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                Log.i(TAG, "获取数据成功,弹窗");
                                data = (HashMap) map.get("data");

                            } else {
                                ToastUtil.showToast(getMe(), "请求任务信息失败,错误码: " + res_code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (null != e) {
                            ToastUtil.showToast(getMe(), "请求任务信息失败" + e.toString());
                        } else {
                            ToastUtil.showToast(getMe(), "请求任务信息失败");
                        }
                    }
                });


    }

    /**
     * 开始处理的接口
     */
    private void arrive_click() {
        String url = GlobalUrl.Arrive_Place;
        OkHttpUtils.post(url)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .params("currentLng", String.valueOf(mCurrentLon_after))
                .params("currentLat", String.valueOf(mCurrentLat_after))
                .execute(new CommonCallback<Map>(getMe(),true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");

                            try {
                                //获取步骤id,赋值给全局变量以供上传位置服务使用
                                HashMap map1 = (HashMap) map.get("data");
                                String owrdpId = String.valueOf(map1.get("owrdpId"));
                                AppPreferences preferences = new AppPreferences(getMe());
                                preferences.put("owrdpId", owrdpId);
                            } catch (Exception e) {
                                Log.i(TAG, "类型转换异常");
                            }

                            if (res_code == 10000) {
                                Log.i(TAG, "开始处理接口调用成功");
                                Intent intent = new Intent(getMe(), TaskStatusThirdActivity.class);
                                intent.putExtra("TASK_INFO", task_info);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showToast(getMe(), "开始处理失败,错误码: " + res_code + " 错误信息: " + String.valueOf(map.get("msg")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (null != e) {
                            ToastUtil.showToast(getMe(), "请求任务信息失败" + e.toString());
                        } else {
                            ToastUtil.showToast(getMe(), "请求任务信息失败");
                        }
                    }
                });
    }

    /**
     * 初始化弹窗的点击事件
     */
    private void initListener() {
        View view = more_menu.getContentView();
        RelativeLayout rl_task_return = (RelativeLayout) view.findViewById(R.id.rl_task_return);
        RelativeLayout rl_task_end = (RelativeLayout) view.findViewById(R.id.rl_task_end);
        rl_task_return.setOnClickListener(this);
        rl_task_end.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            String x_s = location.getLatitude() + "";
            //坐标正常,用户才可以进行下一步操作
            if (!x_s.contains("4.9E")) {
                Log.i(TAG, "得到定位_成功");

                a++;//默认为零,第一次为1,仅为1的时候设置缩放级别

                mCurrentAccracy = location.getRadius();
                locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

                //第一次定位,定位至自己的位置
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }

                //清除原有的线段
                mapView.getMap().clear();
                //画坐标
                addAddressPoint();

                location_confirm = true;
                checkLocation.setText(R.string.get_location_success);

                mCurrentLat = location.getLatitude();
                mCurrentLon = location.getLongitude();

                if (a == 1) {
                    setMapRange(new LatLng(mCurrentLat, mCurrentLon), des_latlng);
                }

                // 添加普通折线绘制
                LatLng p1 = new LatLng(mCurrentLat, mCurrentLon);
                LatLng p2 = new LatLng(des_latlng.latitude, des_latlng.longitude);
                List<LatLng> points = new ArrayList<>();
                points.add(p1);
                points.add(p2);
                OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                mBaiduMap.addOverlay(ooPolyline);
                Log.i(TAG, "重新绘制坐标于目的地之间的线段");
                //画完线段以后,把坐标转换为火星坐标发送给后台
                Gps gps = PositionUtils.bd09_To_Gcj02(mCurrentLat, mCurrentLon);
                mCurrentLat_after = gps.getWgLat();
                mCurrentLon_after = gps.getWgLon();
            } else {
                Log.i(TAG, "得到定位_失败");
                location_confirm = false;
                checkLocation.setText(R.string.get_location_error);
            }
        }
    }


    /**
     * 根据坐标位置设置范围
     * @param point1
     * @param point2
     */
    private void setMapRange(LatLng point1, LatLng point2) {
        Log.i(TAG,"设置范围开始");
        List<LatLng> points = new ArrayList<>();
        points.add(point1);//起点坐标
        points.add(point2);//终点坐标
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0x00FFFFFF).points(points);
        mBaiduMap.addOverlay(ooPolyline);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng p : points) {
            builder = builder.include(p);
        }
        LatLngBounds latlngBounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, mapView.getWidth(), mapView.getHeight());
        mBaiduMap.animateMapStatus(u);
        Log.i(TAG,"设置范围结束");
        points.clear();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null!=mapView)mapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        if (null!=mSensorManager)mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
    }




    private void addAddressPoint() {
        //定义Maker坐标点
        LatLng point = new LatLng(addr_lat, addr_lng);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 初始化打电话弹窗
     */
    private void initCallPop() {
        View view = View.inflate(getMe(), R.layout.task_list_call, null);
        pop_call = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_call.setBackgroundDrawable(new BitmapDrawable());    //设置背景，否则setOutsideTouchable无效
        pop_call.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        pop_call.setFocusable(true);
        pop_call.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setScreenLight();
            }
        });

        RelativeLayout rl_call_a = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_a);
        RelativeLayout rl_call_cancel = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_cancel);
        TextView tv_contact_phone = (TextView) pop_call.getContentView().findViewById(R.id.tv_contact_phone);
        tv_contact_phone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        rl_call_a.setOnClickListener(this);
        rl_call_cancel.setOnClickListener(this);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(TaskStatusSecondActivity.this, R.color.transparent);
    }

    /**
     * 初始化"更多"弹窗
     */
    private void initMoreMenu() {
        View view = View.inflate(getMe(), R.layout.more_menu, null);
        more_menu = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        more_menu.setBackgroundDrawable(new BitmapDrawable());    //设置背景，否则setOutsideTouchable无效
        more_menu.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        more_menu.setFocusable(true);
        more_menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setScreenLight();
            }
        });

        RelativeLayout rl_task_return = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_task_return);
        RelativeLayout rl_task_end = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_task_end);
        rl_task_return.setOnClickListener(this);
        rl_task_end.setOnClickListener(this);
        RelativeLayout rl_do_together = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_do_together);
        RelativeLayout rl_return_main = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_return_main);
        rl_do_together.setOnClickListener(this);
        rl_return_main.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        mLocClient.unRegisterLocationListener(myListener);
        mLocClient.stop();
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
