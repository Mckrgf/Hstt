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
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class UserDeviceDetailActivity extends HsttBaseActivity implements View.OnClickListener {

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
    @BindView(R.id.et_device_brand)
    EditText etDeviceBrand;
    @BindView(R.id.et_device_type)
    EditText etDeviceType;
    private boolean isEditing = false;
    private String filename;
    private ArrayList files = new ArrayList();//显示在页面的图片集合
    private ArrayList files_need_to_upload = new ArrayList();//需要上传的(新的图片)集合
    private Recycle_pic_hor_adapter adapter;
    private HashMap user_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_device_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        user_device = (HashMap) intent.getSerializableExtra("user_device");
        String type = String.valueOf(user_device.get("dev_type"));
        if (!type.equals("数据获取失败")) {
            type = (String) GlobalManager.deivce_map.get(Integer.parseInt(String.valueOf(user_device.get("dev_type"))));
        }
        String dev_id = String.valueOf(user_device.get("device_id"));
        String asset_id = String.valueOf(user_device.get("asset_no"));
        String brand = String.valueOf(user_device.get("brand"));
        String model = String.valueOf(user_device.get("model"));
        String dev_barcode = String.valueOf(user_device.get("dev_barcode"));
        tvAssetNo.setText(type);//类别
        tvLocation.setText(dev_id);//编号
        tvId.setText(asset_id);//资产号
        etDeviceBrand.setText(brand);//品牌
        etDeviceType.setText(model);//型号


        isEditing = intent.getBooleanExtra("if_edit", false);
        deal_with_bottom_button();
        terminalDetailToolbar.setNavigationIcon(R.mipmap.ic_return);
        terminalDetailToolbar.setNavigationOnClickListener(this);
        btEdit.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivTakePic.setOnClickListener(this);
        btUpload.setOnClickListener(this);

//        initData();

        //设置图片列表
        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rcPic.setLayoutManager(manager);
        //设置按钮列表数据

        //数据库寻找对应的图片并显示
        DeviceInfosDbHelper helper = DeviceInfosDbHelper.getInstance(getMe());
        ArrayList<DeviceInfos> img_list = (ArrayList) helper.select(dev_barcode);
        ArrayList img_urls = new ArrayList();
        for (int i = 0; i < img_list.size(); i++) {
            String img_url = img_list.get(i).getImg_url();
            img_urls.add("file://"+img_url);
            files.add("file://"+img_url);
        }
        adapter = new Recycle_pic_hor_adapter(this,img_urls);
        rcPic.setAdapter(adapter);

    }

    private void initData() {
        HashMap para = new HashMap();
        para.put("device_id", user_device.get("device_id"));                                //同上
        para.put("dev_type", user_device.get("dev_type"));                                //同上
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010105", para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
;
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        //更新用户信息
                        HashMap infoList = (HashMap) result.get("infoList");
                        HashMap deviceInfo = (HashMap) infoList.get("deviceInfo");
                        if (null!=deviceInfo) {
                            String factory = (String) deviceInfo.get("dev_brand");
                            String model = (String) deviceInfo.get("dev_type");
                            etDeviceBrand.setText(factory);
                            etDeviceType.setText(model);
                        }else {
                            ToastUtil.showToast(getMe(), "该设备无数据");
                        }

                    } else {
                        ToastUtil.showToast(getMe(), String.valueOf(result.get("msg")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + String.valueOf(e));
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.e(TAG, "获取终端列表失败" + String.valueOf(e));
            }
        });
    }

    @Subscribe
    public void onEvent(HsttEvent event) {
        int type = event.getOperate_type();
        Log.i(TAG, "选择的type是" + type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.bt_edit:
                deal_with_bottom_button();
                break;
            case R.id.iv_scan:
                Log.i(TAG, "扫描");
                break;
            case R.id.iv_location:
                Log.i(TAG, "位置");
                break;
            case R.id.iv_take_pic:
                Log.i(TAG, "拍照");
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
            case R.id.bt_upload:
                Log.i(TAG, "上报主站");
                deal_with_bottom_button();
                break;
        }
    }

    /**
     * 保存/编辑按钮的处理逻辑
     */
    private void deal_with_bottom_button() {
        if (!isEditing) {
            //进入编辑模式
            isEditing = true;
//            ivLocation.setVisibility(View.VISIBLE);//位置图标
//            ivScan.setVisibility(View.VISIBLE);//扫描图标
            rlUpload.setVisibility(View.VISIBLE);//上报主站布局
            llTakePic.setVisibility(View.VISIBLE);//拍照布局
            btEdit.setText("保存");
        } else {
            //进入完成模式
            isEditing = false;
            btEdit.setText("编辑");
//            ivLocation.setVisibility(View.GONE);//位置图标
//            ivScan.setVisibility(View.GONE);//扫描图标
            rlUpload.setVisibility(View.GONE);//上报主站布局
            llTakePic.setVisibility(View.GONE);//拍照布局

            //上传图片
            Post_Pics();
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

            String path = String.valueOf(files_need_to_upload.get(i));
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
        files_need_to_upload.clear();
        ToastUtil.showToast(getMe(),"设备信息更新成功");
        finish();
    }



    private void showMyDialog(String title) {
        int[] views = new int[]{R.id.bt_operate_save, R.id.rg_operate_type, R.id.iv_close};
//                模拟操作类型数据
        ArrayList types = new ArrayList<>();
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
                        Log.i(TAG, "保存操作类型");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
}
