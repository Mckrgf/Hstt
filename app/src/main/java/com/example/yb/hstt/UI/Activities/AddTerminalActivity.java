package com.example.yb.hstt.UI.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.yb.hstt.dao.bean.DeviceInfos;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class AddTerminalActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.add_terminal_toolbar)
    Toolbar addTerminalToolbar;//标题栏
    @BindView(R.id.bt_save)
    Button btSave;//保存按钮
    @BindView(R.id.bt_upload)
    Button btUpload;//上报主站按钮
    @BindView(R.id.tv_asset_no)
    TextView tvAssetNo;//资产号
    @BindView(R.id.tv_location)
    EditText tvLocation;//位置
    @BindView(R.id.iv_location)
    ImageView ivLocation;//定位
    @BindView(R.id.tv_communication)
    TextView tvCommunication;//通讯地址
    @BindView(R.id.tv_id)
    TextView tvId;//系统ID
    @BindView(R.id.tv_operate_type)
    TextView tvOperateType;//操作类型
    @BindView(R.id.tv_status)
    TextView tvStatus;//调试状
    // 态
    @BindView(R.id.rc_pic)
    RecyclerView rcPic;//现场拍照
    @BindView(R.id.activity_add_terminal)
    LinearLayout activityAddTerminal;//主页面
    @BindView(R.id.iv_scan)
    ImageView ivScan;//扫描按钮
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;//拍照按钮

    /**
     * 定位监听
     */
    public BDLocationListener myListener = new MyLocationListener();
    @BindView(R.id.tv_area_code)
    TextView tvAreaCode;
    @BindView(R.id.iv_area_code)
    ImageView ivAreaCode;
    private double lat;//获取的经纬度
    private double lon;//获取的经纬度
    private LocationClient mLocationClient;
    private String filename;
    private ArrayList<String> files = new ArrayList<>();
    private Recycle_pic_hor_adapter adapter;
    private String area_code_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_terminal);
        ButterKnife.bind(this);

        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }


        setLocationOption();
        //标题栏初始化
        addTerminalToolbar.setNavigationIcon(R.mipmap.ic_return);
        addTerminalToolbar.setNavigationOnClickListener(this);

        //点击事件初始化
        btSave.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivTakePic.setOnClickListener(this);
        tvOperateType.setOnClickListener(this);

        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rcPic.setLayoutManager(manager);
        adapter = new Recycle_pic_hor_adapter(this, files);
        rcPic.setAdapter(adapter);
    }

    private static final String TAG = "AddTerminalActivity";

    @Subscribe
    public void onEvent(HsttEvent event) {
        int type = event.getOperate_type();
        Log.i(TAG, "选择的type是" + type);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                Log.i(TAG, "返回");
                finish();
                break;
            case R.id.bt_save:
                Post_Terminal_Data();
                break;
            case R.id.bt_upload:
                Post_Terminal_Data();
                break;
            case R.id.iv_location:
                Intent intent1 = new Intent(this, SelectLocationActivity.class);
                intent1.putExtra("xdata", lat);
                intent1.putExtra("ydata", lon);
                startActivityForResult(intent1, 103);
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
                Intent intent = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent, 101);
                break;
            case R.id.tv_operate_type:
                showMyDialog("操作类型");
                break;
        }
    }

    /**
     * 把图片文件上传
     */
    private void Post_Pics() {

        //把上传的行为转换为添加到数据库的行为
        //每次后要开启一下,上传完毕后自动关闭,节省电量
        startService(new Intent(this, UpLoadService.class));
        for (int i = 0; i < files.size(); i++) {

            String path = files.get(i).toString();
            path = path.replace("file://", "");
            DeviceInfosDbHelper helper = DeviceInfosDbHelper.getInstance(getMe());

            File file = new File(path);
            long size = file.length();
            Log.i(TAG, "图片大小:" + size);

            DeviceInfos infos = new DeviceInfos();
            infos.setImg_url(path);
            infos.setDev_id(tvAssetNo.getText().toString());
            infos.setState(0);
            helper.add(infos);

        }
        finish();


    }

    /**
     * 上传终端初始数据
     */
    private void Post_Terminal_Data() {
        Log.i(TAG, "保存");
        LatLng latLng = GlobalManager.position;
        if (null!=latLng
                &&!tvAssetNo.getText().toString().equals("")
                &&!tvAreaCode.getText().toString().equals("")
                &&!tvLocation.getText().toString().equals("")) {
            double lat1 = Double.valueOf(df40.format(latLng.latitude));
            double lng1 = Double.valueOf(df40.format(latLng.longitude));
            Map<String, Object> para = new HashMap<>();
            para.put("asset_no", tvAssetNo.getText().toString());
            para.put("area_code", tvAreaCode.getText().toString());
            para.put("term_addr", area_code_addr);//十进制地址信息
            para.put("cp_address", tvLocation.getText().toString());//文字地址
            para.put("gps_lng", lng1);
            para.put("gps_lat", lat1);
            OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010101", para) {

                @Override
                public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                    try {
                        HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                        String code = (String) (result != null ? result.get("code") : null);
                        if (null != code && code.equals(SUCCESS_CODE)) {
                            Log.i(TAG, "绑定终端成功");
                            Post_Pics();
                        }else {
                            ToastUtil.showToast(getMe(), "新建终端失败:"+"错误码"+code+result.get("msg").toString());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "转换json失败" + e.toString());
                    }
                }

                @Override
                public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                    super.onError(isFromCache, call, response, e);
                    if (null!=e)Log.e(TAG, "上传失败" + e.toString());
                }
            });
        }else {
            ToastUtil.showToast(getMe(),"位置信息/资产编号均不能为空");
        }

    }

    private void showMyDialog(String title) {
        int[] views = new int[]{R.id.bt_operate_save, R.id.rg_operate_type, R.id.iv_close};
//                模拟操作类型数据
        ArrayList<String> types = new ArrayList<>();
        types.add("类型1");
        types.add("类型2");
        types.add("类型3");
        types.add("类型4");
        CenterDialog dialog = new CenterDialog(getMe(), R.layout.operate_type, views, types, title,false);

        dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
            @Override
            public void OnCenterItemClick(CenterDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.bt_operate_save:
                        dialog.dismiss();
                        break;
                    case R.id.rg_operate_type:
                        break;
                    case R.id.iv_close:
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.show();
    }


    private DecimalFormat df40 = new DecimalFormat("#.000000");

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
                        String area_code = result != null ? result.substring(10, 14) : null;
                        tvAreaCode.setText(area_code);

                        area_code_addr = result != null ? result.substring(14, 18) : null;
                        area_code_addr = String.valueOf(Integer.parseInt(area_code_addr,16));
                        tvCommunication.setText(area_code_addr);

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
                                Log.i(TAG,"没有检索到结果");
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
            case 104:
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


    public class MyLocationListener implements BDLocationListener {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
    }
}
