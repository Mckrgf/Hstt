package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class TerminalDetailActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "TerminalDetailActivity";
    @BindView(R.id.terminal_detail_toolbar)
    Toolbar terminalDetailToolbar;//标题栏
    @BindView(R.id.tv_asset_no)
    EditText tvAssetNo;//资产号
    @BindView(R.id.tv_location)
    EditText tvLocation;//位置
    @BindView(R.id.tv_communication)
    EditText tvCommunication;//通讯地址
    @BindView(R.id.tv_id)
    EditText tvId;//id
    @BindView(R.id.tv_operate_type)
    TextView tvOperateType;//操作类型
    @BindView(R.id.tv_status)
    EditText tvStatus;//设备状态
    @BindView(R.id.rc_pic)
    RecyclerView rcPic;//照片列表
    @BindView(R.id.bt_edit)
    Button btEdit;//编辑按钮
    @BindView(R.id.iv_scan)
    ImageView ivScan;//扫描图标
    @BindView(R.id.iv_location)
    ImageView ivLocation;//位置图标
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;//拍照图标
    @BindView(R.id.ll_take_pic)
    LinearLayout llTakePic;//拍照布局
    @BindView(R.id.bt_upload)
    Button btUpload;//上报主站按钮
    @BindView(R.id.rl_upload)
    RelativeLayout rlUpload;//上报主站布局
    @BindView(R.id.bt_more_data)
    Button btMoreData;
    private boolean isEditing = false;
    private HashMap info;//终端信息
    private int operate_type;//选择的终端操作类型
    private CenterDialog dialog;

    private ArrayList files = new ArrayList();//显示在页面的图片集合
    private ArrayList files_need_to_upload = new ArrayList();//需要上传的(新的图片)集合
    private Recycle_pic_hor_adapter adapter;
    private String filename;
    private LocationClient mLocationClient;
    private double lat;//获取的经纬度
    private double lon;//获取的经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_detail);
        ButterKnife.bind(this);
        initData();
        setLocationOption();
    }

    /**
     * 定位监听
     */
    public BDLocationListener myListener = new MyLocationListenner();

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

    private void initData() {
        //获取终端数据
        Intent intent = getIntent();
        info = (HashMap) intent.getSerializableExtra("info");

        //初始化编辑状态
        isEditing = intent.getBooleanExtra("if_edit", false);
        deal_with_bottom_button();

        //标题栏以及点击事件
        terminalDetailToolbar.setNavigationIcon(R.mipmap.ic_return);
        terminalDetailToolbar.setNavigationOnClickListener(this);
        btEdit.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivTakePic.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        tvOperateType.setOnClickListener(this);
        btMoreData.setOnClickListener(this);

        //数据填充
        int state = Integer.parseInt(info.get("state").toString());
        String state_content = "";
        switch (state) {
            case 00:
                state_content = (String) GlobalManager.type.get(0);
                break;
            case 01:
                state_content = (String) GlobalManager.type.get(1);
                break;
            case 02:
                state_content = (String) GlobalManager.type.get(2);
                break;
        }
        tvLocation.setText(info.get("cp_address").toString());
        tvCommunication.setText(info.get("term_addr").toString());
        tvAssetNo.setText(info.get("asset_no").toString());
        tvStatus.setText(state_content);
        tvId.setText(info.get("term_id").toString());

        //设置图片列表
        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rcPic.setLayoutManager(manager);
        //设置按钮列表数据

        DeviceInfosDbHelper helper = DeviceInfosDbHelper.getInstance(getMe());
        ArrayList<DeviceInfos> img_list = (ArrayList) helper.select(info.get("asset_no").toString());

        ArrayList img_urls = new ArrayList();
        for (int i = 0; i < img_list.size(); i++) {
            String img_url = img_list.get(i).getImg_url();
            img_urls.add("file://"+img_url);
            files.add("file://"+img_url);

        }
        adapter = new Recycle_pic_hor_adapter(this, img_urls);
        rcPic.setAdapter(adapter);
    }

    @Subscribe
    public void onEvent(HsttEvent event) {
        operate_type = event.getOperate_type();
        Log.i(TAG, "选择的type是" + operate_type);
//        post_terminal_operate();
    }

    /**
     * 终端操作
     */
    private void post_terminal_operate() {
        HashMap map = new HashMap();


        HashMap terminal_operate_type = new HashMap();//终端操作类型
        terminal_operate_type.put(0, "reset");
        terminal_operate_type.put(1, "start_reg");
        terminal_operate_type.put(2, "read_reg");
        terminal_operate_type.put(3, "read_stat");


        String op = (String) terminal_operate_type.get(operate_type);
        map.put("asset_no", info.get("asset_no"));
        map.put("fn", op);
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010401", map) {
            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        Log.i(TAG, "执行终端命令成功");
                        dialog.dismiss();
                    } else {
                        ToastUtil.showToast(getMe(), result.get("msg").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.bt_edit:
                deal_with_bottom_button();
                if (!isEditing) {
                    update_terminal();
                }
                break;
            case R.id.iv_scan:
                Log.i(TAG, "扫描");
                ToastUtil.showToast(getMe(), "该项不可修改");
                break;
            case R.id.iv_location:
                Log.i(TAG, "位置");
                Intent intent0 = new Intent(this, SelectLocationActivity.class);
                intent0.putExtra("xdata", lat);
                intent0.putExtra("ydata", lon);
                startActivityForResult(intent0, 103);
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
                // 创建照片的存储目录
                if (!PHOTO_DIR.exists()) {
                    PHOTO_DIR.mkdirs();
                }
                Uri uri = Uri.fromFile(out);
                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.8);
                startActivityForResult(imageCaptureIntent, 104);
                break;
            case R.id.bt_upload:
                Log.i(TAG, "上报主站");
                if (!isEditing) {
                    update_terminal();
                }
                break;
            case R.id.tv_operate_type:
                Log.i(TAG, "弹窗获取操作类型");
                showMyDialog("操作类型");
                break;
            case R.id.bt_more_data:
                Intent intent = new Intent(getMe(),MoreDataActivity.class);
                intent.putExtra("device",info);
                startActivity(intent);
        }
    }

    /**
     * 保存/编辑按钮的处理逻辑
     */
    private void deal_with_bottom_button() {
        if (!isEditing) {
            //进入编辑模式
            isEditing = true;
            ivLocation.setVisibility(View.VISIBLE);//位置图标
            ivScan.setVisibility(View.GONE);//扫描图标
            rlUpload.setVisibility(View.VISIBLE);//上报主站布局
            llTakePic.setVisibility(View.VISIBLE);//拍照布局
            btEdit.setText("保存");
            tvOperateType.setClickable(true);//不可选择类型
            tvLocation.setEnabled(true);
        } else {
            //进入完成模式
            isEditing = false;
            btEdit.setText("编辑");
            ivLocation.setVisibility(View.GONE);//位置图标
            ivScan.setVisibility(View.GONE);//扫描图标
            rlUpload.setVisibility(View.GONE);//上报主站布局
            llTakePic.setVisibility(View.GONE);//拍照布局
            tvStatus.setEnabled(false);
            tvOperateType.setClickable(false);//可以选择类型
            tvLocation.setEnabled(false);

        }
    }

    /**
     * 更新终端信息
     */
    private void update_terminal() {
        //检查资产编号/位置是否变化 变化就修改并提交,成功后上报图片,不变化就检查图片是否增加,变化就提交
        String term_addr = (String) info.get("cp_address");
        String term_addr_now = tvLocation.getText().toString();

        if (!term_addr.equals(term_addr_now)) {
            //位置信息改变
            Post_Terminal_Data();
        }else {
            //位置信息没变
            if (files_need_to_upload.size()>0) {
                Post_Pics();
            }else {
                ToastUtil.showToast(getMe(),"没有修改任何信息");
            }
        }
    }

    private DecimalFormat df40 = new DecimalFormat("#.000000");

    /**
     * 上传终端初始数据
     */
    private void Post_Terminal_Data() {
        Log.i(TAG, "保存");
        LatLng latLng = GlobalManager.position;
        if (null!=latLng) {
            double lat1 = Double.valueOf(df40.format(latLng.latitude));
            double lng1 = Double.valueOf(df40.format(latLng.longitude));
            Map<String, Object> para = new HashMap<>();
            para.put("asset_no", tvAssetNo.getText().toString());
            para.put("area_code", String.valueOf(info.get("area_code")));//行政区划码
            para.put("term_addr", String.valueOf(info.get("term_addr")));//十进制地址信息
            para.put("cp_address", tvLocation.getText().toString());//文字地址
            para.put("gps_lng", lng1);
            para.put("gps_lat", lat1);
            OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010101", para,true) {

                @Override
                public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                    try {
                        HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                        String code = (String) result.get("code");
                        if (null != code && code.equals(SUCCESS_CODE)) {
                            Log.i(TAG, "绑定终端成功");
                            if (files_need_to_upload.size()==0) {
                                ToastUtil.showToast(getMe(),"修改成功");
                            }else {
                                Post_Pics();
                            }

                        }else {
                            ToastUtil.showToast(getMe(), "终端编辑失败:"+"错误码"+code+result.get("msg").toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "转换json失败" + e.toString());
                    }
                }

                @Override
                public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                    super.onError(isFromCache, call, response, e);
                    Log.e(TAG, "上传失败" + e.toString());
                }
            });
        }else {


        }

    }

    /**
     * 把图片文件上传
     */
    private void Post_Pics() {
        //把上传的行为转换为添加到数据库的行为
        //每次后要开启一下,上传完毕后自动关闭,节省电量
        startService(new Intent(this, UpLoadService.class));
        for (int i = 0; i < files_need_to_upload.size(); i++) {

            String path = files_need_to_upload.get(i).toString();
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
        //上传完毕后,把这个 "files_need_to_upload" 变量清空
        files_need_to_upload.clear();
        ToastUtil.showToast(getMe(),"上传图片完成");
    }


    private void showMyDialog(String title) {
        int[] views = new int[]{R.id.bt_operate_save, R.id.rg_operate_type, R.id.iv_close};
//                模拟操作类型数据
        ArrayList types = new ArrayList<>();

        types.add("重启");
        types.add("启动台区识别及测量点注册");
        types.add("查询台区识别及注册状态");
        types.add("查询开关和插座总数");
        dialog = new CenterDialog(getMe(), R.layout.operate_type, views, types, title,false);

        dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
            @Override
            public void OnCenterItemClick(CenterDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.bt_operate_save:
                        Log.i(TAG, "保存操作类型");
                        post_terminal_operate();

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
                    files_need_to_upload.add("file://" + filepath);
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
