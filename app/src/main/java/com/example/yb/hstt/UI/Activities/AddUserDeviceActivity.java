package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.Recycle_pic_hor_adapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.HsttEvent;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Service.UpLoadService;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.ImageCompressUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.dao.DbHelper.DeviceInfosDbHelper;
import com.example.yb.hstt.dao.DbHelper.UserInfoDbHelper;
import com.example.yb.hstt.dao.bean.DeviceInfos;
import com.example.yb.hstt.dao.bean.UserInfo;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 新建设备
 */
public class AddUserDeviceActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.add_terminal_toolbar)
    Toolbar addTerminalToolbar;//标题栏
    @BindView(R.id.bt_save)
    Button btSave;//保存按钮
    @BindView(R.id.bt_upload)
    Button btUpload;//上报主站按钮
    @BindView(R.id.tv_asset_no)
    TextView tvAssetNo;//资产号
    @BindView(R.id.tv_location)
    TextView tvLocation;//位置
    @BindView(R.id.iv_location)
    ImageView ivLocation;//定位
    @BindView(R.id.tv_communication)
    TextView tvCommunication;//通讯地址
    @BindView(R.id.tv_id)
    TextView tvId;//系统ID
    @BindView(R.id.tv_operate_type)
    TextView tvOperateType;//操作类型
    @BindView(R.id.tv_status)
    TextView tvStatus;//调试状态
    @BindView(R.id.rc_pic)
    RecyclerView rcPic;//现场拍照
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @BindView(R.id.ll_switch_scan)
    LinearLayout llSwitchScan;//开关的隐藏扫描栏
    @BindView(R.id.tv_switch_id)
    TextView tvSwitchId;//开关编号的文字
    @BindView(R.id.tv_loc)
    TextView tvLoc;
    @BindView(R.id.iv_scan_1)
    ImageView ivScan1;
    private double lat = 0;
    private double lon = 0;
    private Recycle_pic_hor_adapter adapter;
    private UserInfo userInfo;
    private ArrayList<String> return_types;
    private String post_method;
    private boolean check_swit = false;//是否要判断开关编号为空(当添加设备的类型是电采暖的时候为true)
    private int model_param_pid;
    private ArrayList model_pids;
    private String model_pid;
    private CenterDialog dialog;
    private String device_type;
    private boolean select_by_brand;
    private int type;
    private HsttEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_device);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("user_info");

        //标题栏初始化
        addTerminalToolbar.setNavigationIcon(R.mipmap.ic_return);
        addTerminalToolbar.setNavigationOnClickListener(this);

        //获取位置
        setLocationOption();

        //点击事件初始化
        btSave.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivTakePic.setOnClickListener(this);
        tvOperateType.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
        tvAssetNo.setOnClickListener(this);
        tvSwitchId.setOnClickListener(this);
        ivScan1.setOnClickListener(this);


        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rcPic.setLayoutManager(manager);
        //设置按钮列表数据
        final ArrayList pics = new ArrayList();
        adapter = new Recycle_pic_hor_adapter(this, pics);
        rcPic.setAdapter(adapter);
    }

    private static final String TAG = "AddUserDeviceActivity";

    @Subscribe
    public void onEvent(HsttEvent event1) {
        event = event1;
        type = event.getTag_device_type();
        if (type == 101) {
            //选择设备类型的消息回调
            String device_type = (String) event.getMsg_body();
            tvAssetNo.setText(device_type);
            if (device_type.contains("电采暖")) {
                llSwitchScan.setVisibility(View.VISIBLE);
                post_method = "010202";//根据设备不同类型修改上传method
                check_swit = true;
            } else if (device_type.contains("智能开关")) {
                llSwitchScan.setVisibility(View.GONE);
                post_method = "010201";//根据设备不同类型修改上传method
                check_swit = false;
            } else if (device_type.contains("智能插座")) {
                llSwitchScan.setVisibility(View.GONE);
                post_method = "010203";//根据设备不同类型修改上传method
                check_swit = false;
            } else if (device_type.contains("智能电表")) {
                llSwitchScan.setVisibility(View.GONE);
                post_method = "010204";//根据设备不同类型修改上传method
                check_swit = false;
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                Log.i(TAG, "返回");
                finish();
                break;
            case R.id.bt_save:
                Log.i(TAG, "保存");
                PostDevice();
                break;
            case R.id.iv_scan_1:
                Log.i(TAG, "开关编号扫描");
                Intent intent = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent, 102);
                break;
            case R.id.bt_upload:
                Log.i(TAG, "上报主站");
                PostDevice();
                break;
            case R.id.iv_location:
                Log.i(TAG, "定位");
                Intent intent1 = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent1, 101);
                break;
            case R.id.iv_take_pic:
                Log.i(TAG, "现场拍照");
                File PHOTO_DIR = new File(GlobalManager.photoPath);
                // 创建照片的存储目录
                if (!PHOTO_DIR.exists()) {
                    PHOTO_DIR.mkdirs();
                }
                filename = MyDateUtils.getCurTimeFormat(MyDateUtils.date_Format3);
                File out = new File(PHOTO_DIR, filename + ".jpg");
                Uri uri = Uri.fromFile(out);
                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.8);
                startActivityForResult(imageCaptureIntent, 104);
                break;
            case R.id.iv_scan:
                Log.i(TAG, "扫描设备编号");

                break;
            case R.id.tv_asset_no:
                Log.i(TAG, "选择设备类型");
                ArrayList<String> types = new ArrayList<>();
                types.add("智能插座");
                types.add("智能开关");
                types.add("电采暖设备");
//                types.add("智能电表");
                showMyDialog("设备类型", 101, types);
                break;
            case R.id.tv_operate_type:
                Log.i(TAG, "选择设备品牌");
                getDeviceBrandType();
                break;
            case R.id.tv_status:
                String type = String.valueOf(tvOperateType.getText());
                if (TextUtils.isEmpty(type)) {
                    ToastUtil.showToast(getMe(), "请先选择设备品牌");
                } else {
                    getDeviceModelType();
                }

                break;
        }
    }

    /**
     * 获取设备型号并弹窗
     */
    private void getDeviceModelType() {
        Map<String, Object> para = new HashMap<>();
        para.put("codeName", "DEV_MODEL");
        para.put("pId", model_pid);
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010601", para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                return_types = new ArrayList();
                try {

                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        Log.i(TAG, "获取终端列表成功");
                        ArrayList types = (ArrayList) result.get("list");

                        for (int i = 0; i < types.size(); i++) {
                            HashMap type = (HashMap) types.get(i);
                            return_types.add(String.valueOf(type.get("codeValueDisplay")));
                        }
                        showMyDialog("设备型号", 103, return_types);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.e(TAG, "获取终端列表失败" + e.toString());
            }
        });
    }

    /**
     * 发送设备消息至主站
     */
    private void PostDevice() {

        String type = tvAssetNo.getText().toString();//设备类型
        String dev_id = tvLocation.getText().toString();//设备资产号
        String brand = tvOperateType.getText().toString();//品牌
        String model = tvStatus.getText().toString();//型号
        String loc = tvLoc.getText().toString();//位置信息
        String ass_no = tvSwitchId.getText().toString();//开关id,电采暖设备需要
        boolean a = !TextUtils.isEmpty(type) &&//非电采暖控制选项
                !TextUtils.isEmpty(dev_id) &&
                !TextUtils.isEmpty(loc);
        boolean b = !TextUtils.isEmpty(type) &&//电采暖控制选项
                !TextUtils.isEmpty(dev_id) &&
                !TextUtils.isEmpty(loc) &&
                !TextUtils.isEmpty(ass_no);
        if (check_swit) {//电采暖
            if (b) {
                add_user_device(dev_id, brand, model, ass_no);
            } else {
                ToastUtil.showToast(getMe(), "缺少必选项");
            }
        } else {//非电采暖
            if (a) {
                add_user_device(dev_id, brand, model, ass_no);
            } else {
                ToastUtil.showToast(getMe(), "缺少必选项");
            }
        }

    }

    private void add_user_device(final String dev_id, String brand, String model, String ass_no) {
        Map<String, Object> para = new HashMap<>();
        //电采暖接口的时候,asset_no为开关资产号字段,dev_asset_no为设备资产号字段

        if (ass_no.equals("")) {
            //非电采暖设备
            para.put("asset_no", dev_id);//开关设备资产号,必选(添加电采暖设备时需要)
        } else {
            //电采暖设备
            para.put("asset_no", ass_no);//开关设备资产号,必选(添加电采暖设备时需要)
        }
        para.put("dev_asset_no", dev_id);                        //设备资产号,必选
        para.put("cust_no", userInfo.getUser_id());              //用户编号,必选
        para.put("gps_lng", lon);                                //位置,必选
        para.put("gps_lat", lat);                                //位置,必选
//        para.put("brand", brand);                                //非必选项,可以为空
//        para.put("model", model);                                //同上
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), post_method, para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        //更新用户信息
                        UserInfoDbHelper dbHelper = UserInfoDbHelper.getInstance(getMe());
                        userInfo.setIs_upload(true);
                        dbHelper.updateInfo(userInfo);
                        //上传图片信息
                        if (files.size() > 0) {
                            Post_Pics(dev_id);
                        } else {
                            ToastUtil.showToast(getMe(), "设备信息添加成功");
                            finish();
                        }

                    } else {
                        ToastUtil.showToast(getMe(), result.get("msg").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.e(TAG, "获取终端列表失败" + e.toString());
            }
        });
    }

    /**
     * 把图片文件上传
     */
    private void Post_Pics(final String dev_id) {
        //把上传的行为转换为添加到数据库的行为
        //每次后要开启一下,上传完毕后自动关闭,节省电量
        startService(new Intent(this, UpLoadService.class));
        for (int i = 0; i < files.size(); i++) {

            String path = String.valueOf(files.get(i));
            path = path.replace("file://", "");
            DeviceInfosDbHelper helper = DeviceInfosDbHelper.getInstance(getMe());

            File file = new File(path);
            long size = file.length();
            Log.i(TAG, "图片大小:" + size);

            DeviceInfos infos = new DeviceInfos();
            infos.setImg_url(path);
            infos.setDev_id(String.valueOf(tvLocation.getText()));
            infos.setState(0);
            helper.add(infos);
        }

        ToastUtil.showToast(getMe(), "设备信息添加成功");
        finish();
    }

    /**
     * 获取设备品牌以及类型
     */
    private void getDeviceBrandType() {
        Map<String, Object> para = new HashMap<>();
        para.put("codeName", "DEV_MANUFACTURER_NO");
        para.put("pId", "0");
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010601", para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                return_types = new ArrayList();
                model_pids = new ArrayList();
                try {

                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        Log.i(TAG, "获取终端列表成功");
                        ArrayList types = (ArrayList) result.get("list");
                        for (int i = 0; i < types.size(); i++) {
                            HashMap type = (HashMap) types.get(i);
                            return_types.add(String.valueOf(type.get("codeValueDisplay")));
                            model_pids.add(String.valueOf(type.get("codeValueId")));
                        }
                        showMyDialog("设备品牌", 102, return_types);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.e(TAG, "获取终端列表失败" + e.toString());
            }
        });
    }

    private void showMyDialog(String title, final int type, ArrayList<String> types_1) {
        int[] views = new int[]{R.id.bt_operate_save, R.id.rg_operate_type, R.id.iv_close};
//                模拟操作类型数据
        ArrayList<String> types = types_1;
        dialog = new CenterDialog(getMe(), R.layout.operate_type, views, types, title, true);
        dialog.setType(type);
        dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
            @Override
            public void OnCenterItemClick(CenterDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.bt_operate_save:
                        Log.i(TAG, "保存操作类型");
                        if (type == 102) {
                            //设备品牌的回调
                            device_type = (String) event.getMsg_body();
                            model_param_pid = Integer.parseInt(event.getTAG());
                            select_by_brand = event.isSelect_by_brand();
                            if (select_by_brand) {
                                model_pid = String.valueOf(model_pids.get(model_param_pid));
                            }
                            tvOperateType.setText(device_type);
                            tvStatus.setText(null);

                        } else if (type == 103) {
                            //设备型号的回调
                            device_type = (String) event.getMsg_body();
                            tvStatus.setText(device_type);
                        }
                        if (dialog.isShowing()) dialog.dismiss();
                        dialog.dismiss();
                        break;
                    case R.id.rg_operate_type:
                        Log.i(TAG, "点击radiogroup");
                        break;
                    case R.id.iv_close:
                        Log.i(TAG, "关闭弹窗");
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 定位监听
     */
    public BDLocationListener myListener = new MyLocationListenner();
    private LocationClient mLocationClient;

    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getMe());

        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);//定位间隔
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        Log.i(TAG, "开始定位__________________________________");

    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                lat = location.getLatitude();
                lon = location.getLongitude();
                tvLoc.setText(lat + "  " + lon);
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
                tvLoc.setText("坐标位置获取失败,请检查网络");
                lat = 0;
                lon = 0;
            }

        }

    }

    private ArrayList files = new ArrayList();
    private String filename;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101://设备资产号
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        tvLocation.setText(result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        showMesage("解析二维码失败!");
                    }
                }
                break;
            case 102://开关资产号
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        tvSwitchId.setText(result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        showMesage("解析二维码失败!");
                    }
                }
                break;
            case 104://拍照
                // 调用压缩的方法。对图片进行一个分辨率的压缩，
                String filepath = GlobalManager.photoPath + File.separator + filename + ".jpg";
                File file = new File(filepath);
                long size = file.length();
                if (size > 0) {
                    //压缩拍后的图片
                    ImageCompressUtil.compressBitmap(filepath, 1024, 768, 80, filepath);
                    files.add("file://" + filepath);
                    adapter.setPicData(files);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
    }
}
