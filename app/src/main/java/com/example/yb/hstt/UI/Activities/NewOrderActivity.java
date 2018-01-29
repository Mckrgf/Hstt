package com.example.yb.hstt.UI.Activities;

import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.HsttEvent;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.DisplayUtil;
import com.example.yb.hstt.Utils.ImageCompressUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.bean.OrderFileInfo;
import com.example.yb.hstt.bean.OrderInfo;
import com.example.yb.hstt.dao.DbHelper.LoginInfoDbHelper;
import com.example.yb.hstt.dao.bean.LoginInfo;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class NewOrderActivity extends HsttBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "NewOrderActivity";
    @BindView(R.id.tb_new_order)
    Toolbar tb_new_order;
    @BindView(R.id.et_new_order_pressingstatus)
    TextView et_new_order_pressingstatus;
    @BindView(R.id.et_new_order_sitephotos)
    TextView et_new_order_sitephotos;
    @BindView(R.id.bt_new_order_save)
    Button bt_new_order_save;
    @BindView(R.id.rv_new_order_photos)
    RecyclerView rv_new_order_photos;
    @BindView(R.id.et_new_order_deviceid)
    TextView et_new_order_deviceid;
    @BindView(R.id.et_new_order_contacts)
    EditText et_new_order_contacts;
    @BindView(R.id.et_new_order_address)
    EditText et_new_order_address;
    @BindView(R.id.et_new_order_devicetype)
    EditText et_new_order_devicetype;
    @BindView(R.id.et_new_order_contactway)
    EditText et_new_order_contactway;
    @BindView(R.id.et_new_order_Describe)
    EditText et_new_order_Describe;
    @BindView(R.id.rg_new_order_outsideOffice)
    RadioGroup rg_new_order_outsideOffice;
    @BindView(R.id.tv_new_order_username)
    TextView tv_new_order_username;
    @BindView(R.id.tv_new_order_jobNumber)
    TextView tv_new_order_jobNumber;
    @BindView(R.id.tv_new_order_userPhone)
    TextView tv_new_order_userPhone;
    @BindView(R.id.rg_new_order_task_level)
    RadioGroup rgNewOrderTaskLevel;
    @BindView(R.id.tv_em_level1)
    TextView tvEmLevel1;

    private List<String> photo_files = new ArrayList<>();
    /**
     * 照片文件名
     */
    private String filename;
    private DevicePhotoAdapter photosAdapter = null;
    /**
     * 位置类
     */
    public LocationClient mLocationClient = null;
    private String Photo_latitude;
    private String photo_longitude;
    private LoginInfo logininfo;
    /**
     * 定位监听
     */
    public BDAbstractLocationListener myListener = new MyLocationListenner();
    private String task_level_flag;
    private ArrayList task_level_list_string;
    private String operate_type;
    private ArrayList task_level_list_content_string;

    public class MyLocationListenner extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            Photo_latitude = String.valueOf(location.getLatitude());
            photo_longitude = String.valueOf(location.getLongitude());
            if (!TextUtils.isEmpty(Photo_latitude) || !TextUtils.isEmpty(photo_longitude)) {
                mLocationClient.unRegisterLocationListener(this);
                mLocationClient.stop();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        setLocationOption();
        setSupportActionBar(tb_new_order);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initlistener();
        initData();
        getTaskLevelList();
    }

    /**
     * 获取任务紧急状态列表
     */
    private void getTaskLevelList() {
        OkHttpUtils.get(GlobalUrl.Get_TaskLevelList)
                .params("codeName", "OWWO_LEVEL_TYPE_NO")
                .execute(new CommonCallback<Map>(getMe(), true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        int code = (int) map.get("code");
                        if (code == 10000) {
                            HashMap map1 = (HashMap) map.get("data");
                            ArrayList task_level_list = (ArrayList) map1.get("select");
                            task_level_list_string = new ArrayList();//显示的列表
                            task_level_list_content_string = new ArrayList();//上传的列表
                            for (int i = 0; i < task_level_list.size(); i++) {
                                HashMap map2 = (HashMap) task_level_list.get(i);

                                //获取显示的数值
                                String level_string = String.valueOf(map2.get("codeValueDisplay"));
                                if (!level_string.equals("null")&&!level_string.equals("")) {
                                    task_level_list_string.add(level_string);
                                }

                                //获取需要上传的数值
                                String level_content_string = String.valueOf(map2.get("codeValue"));
                                if (!level_string.equals("null")&&!level_string.equals("")) {
                                    task_level_list_content_string.add(level_content_string);
                                }
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

    /**
     * 初始化监听
     */
    private void initlistener() {
        tb_new_order.setNavigationOnClickListener(this);
        et_new_order_pressingstatus.setOnClickListener(this);
        et_new_order_sitephotos.setOnClickListener(this);
        bt_new_order_save.setOnClickListener(this);
        et_new_order_deviceid.setOnClickListener(this);
        rg_new_order_outsideOffice.setOnCheckedChangeListener(this);
        rgNewOrderTaskLevel.setOnCheckedChangeListener(this);
        tvEmLevel1.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        logininfo = LoginInfoDbHelper.getInstance(getMe()).select();
        tv_new_order_username.setText(logininfo.getUser_name());
        tv_new_order_jobNumber.setText("工号：" + logininfo.getJob_number());
        tv_new_order_userPhone.setText("联系方式：" + logininfo.getUser_phone());

        //让第一个选中
        ((RadioButton) rg_new_order_outsideOffice.getChildAt(0)).setChecked(true);
        //让第一个选中
        ((RadioButton) rgNewOrderTaskLevel.getChildAt(0)).setChecked(true);
        //初始化图片列表
        LinearLayoutManager linearlayout = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        MyDecoration decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        rv_new_order_photos.setLayoutManager(linearlayout);
        rv_new_order_photos.addItemDecoration(decoration);
        photosAdapter = new DevicePhotoAdapter(photo_files, getMe(), true);
        rv_new_order_photos.setAdapter(photosAdapter);


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
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.et_new_order_pressingstatus://打开工单状态
                openOrderStatusDialog();
                break;
            case R.id.et_new_order_deviceid://扫码
                Intent intent = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent, 101);
                break;
            case R.id.et_new_order_sitephotos://拍照
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
            case R.id.bt_new_order_save://保存工单
                saveOrder();
                break;
            case R.id.tv_em_level1:
                if (task_level_list_string.size()>0) {
                    showMyDialog("紧急状态");
                }else {
                    ToastUtil.showToast(getMe(),"获取工单紧急状态失败,请检查网络");
                }

                break;
        }
    }

    @Subscribe
    public void onEvent(HsttEvent event) {
        operate_type = String.valueOf(event.getMsg_body());
        tvEmLevel1.setText(operate_type);
        int pos = event.getOperate_type();
        task_level_flag =  (String) task_level_list_content_string.get(pos);
        Log.i(TAG,"收到了点击事件" + task_level_flag + operate_type);
    }

    private void showMyDialog(String title) {
        int[] views = new int[]{R.id.bt_operate_save, R.id.rg_operate_type, R.id.iv_close};
//                模拟操作类型数据
//        ArrayList<String> types = new ArrayList<>();
//        types.add("类型1");
//        types.add("类型2");
//        types.add("类型3");
//        types.add("类型4");
        CenterDialog dialog = new CenterDialog(getMe(), R.layout.operate_type, views, task_level_list_string, title,false);

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

    /**
     * 是否指派外勤标志
     */
    private String ifOutworkFlag = "是";

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_new_order_outsideOffice:
                RadioButton outside_officc = ButterKnife.findById(group, checkedId);
                String text = outside_officc.getText().toString();
                ifOutworkFlag = text.equals("是") ? "1" : "0";
                break;
            case R.id.rg_new_order_task_level:
                RadioButton task_level = ButterKnife.findById(group, checkedId);
                String level = task_level.getText().toString();
                if (level.contains("1")) {
                    task_level_flag = "01";
                } else if (level.contains("2")) {
                    task_level_flag = "02";
                } else if (level.contains("3")) {
                    task_level_flag = "03";
                }
                break;
        }

    }

    private void saveOrder() {
        HttpParams params = new HttpParams();
        params.put("crChannelTypeNo", "03");
        params.put("ifScwoDispatchSelf", "1");
        params.put("custOrgNo", "123123");
        params.put("custNo", "201711111");
        params.put("crContactName", et_new_order_contacts.getText().toString());
        params.put("crContactPhoneNum", et_new_order_contactway.getText().toString());
        params.put("crAddress", et_new_order_address.getText().toString());
        params.put("crAddressLng", photo_longitude);
        params.put("crAddressLat", Photo_latitude);
        params.put("crDescribe", et_new_order_Describe.getText().toString());
        params.put("ifOutworkFlag", ifOutworkFlag);
        params.put("devAssetCode", et_new_order_deviceid.getText().toString());
        params.put("owwoLevelTypeNo", task_level_flag);
        OkHttpUtils.post(GlobalUrl.New_Order).params(params).execute(new CommonCallback<Map>(getMe(), true) {
            @Override
            public void onResponse(boolean b, Map s, Request request, @Nullable Response response) {
                Log.i(TAG, "onResponse: " + s);
                String code = s.get("code").toString();
                if (code.equals("10000")) {
                    Map<String, Object> data = (Map<String, Object>) s.get("data");
                    String msg = data.get("msg").toString();
                    String crwoId = data.get("crwoId").toString();
                    if (photo_files.size() > 0) {
                        //上传文件
                        submitFile(crwoId);
                    } else {
                        toNewOrderSuccess(null);
                    }
                    ToastUtil.showToast(getMe(), msg);
                } else {
                    ToastUtil.showToast(getMe(), "创建失败！");
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
            }
        });
    }

    /**
     * 上传文件
     *
     * @param crwoId
     */
    private void submitFile(String crwoId) {
        List<File> files = new ArrayList<>();
        for (String path : photo_files) {
            files.add(new File(path.replace("file://", "")));
        }
        if (files.size() == 0) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("crwoId", crwoId);
        params.put("attaLng", photo_longitude);
        params.put("attaLat", Photo_latitude);
        String progress_message = "上传图片中...";
        OkHttpUtils.post(GlobalUrl.New_OrderUpLoad)
                .addFileParams("file", files)
                .params(params)
                .execute(new CommonCallback<String>(getMe(), progress_message) {
                    @Override
                    public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String code = jsonObject.getString("code");
                            if (code.equals("10000")) {
                                String data = jsonObject.getString("data");
                                List<OrderFileInfo> lists = new Gson().fromJson(data, new TypeToken<List<OrderFileInfo>>() {
                                }.getType());
                                toNewOrderSuccess(lists);
                            } else {
                                ToastUtil.showToast(getMe(), "上传失败！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                    }
                });
    }

    /**
     * 去新建完成页面
     *
     * @param lists 工单文件列表
     */
    private void toNewOrderSuccess(List<OrderFileInfo> lists) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAddress(et_new_order_address.getText().toString());
        orderInfo.setDevice_type(et_new_order_devicetype.getText().toString());
        orderInfo.setOrder_status(order_status_text);
        orderInfo.setCrDescribe(et_new_order_Describe.getText().toString());
        orderInfo.setFiles(lists);
        Intent nextIntent = new Intent(getMe(), NewOrderSuccessActivity.class);
        nextIntent.putExtra("orderInfo", orderInfo);
        startActivity(nextIntent);
    }

    /**
     * 紧急状态
     */
    private String order_status_text = "";

    public void openOrderStatusDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_custom);
        View view = LayoutInflater.from(this).inflate(R.layout.new_order_dialog_pressingstate, null);
        ImageView iv_new_order_Close = ButterKnife.findById(view, R.id.iv_new_order_dialog_close);
        RadioGroup rg_new_order_dialog_status = ButterKnife.findById(view, R.id.rg_new_order_dialog_status);
        Button bt_new_order_dialog_save = ButterKnife.findById(view, R.id.bt_new_order_dialog_save);
        iv_new_order_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rg_new_order_dialog_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radiobutton = ButterKnife.findById(group, checkedId);
                order_status_text = radiobutton.getText().toString();
            }
        });
        bt_new_order_dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getMe(), order_status_text);
                et_new_order_pressingstatus.setText(order_status_text);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.getWindow().getAttributes().width = (int) (DisplayUtil.Width(this) * 0.75);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    int resultInt = extras.getInt(CodeUtils.RESULT_TYPE);
                    if (resultInt == CodeUtils.RESULT_SUCCESS) {
                        String result = extras.getString(CodeUtils.RESULT_STRING);
                        et_new_order_deviceid.setText(result);
                    }
                }
                break;
            case 104:
                String filepath = GlobalManager.photoPath + File.separator + filename + ".jpg";
                Log.i(TAG, "filepath: " + filepath);
                File file = new File(filepath);
                if (file.exists()) {
//                    getPhotoLocaltion(file);
                    //压缩拍后的图片
                    ImageCompressUtil.compressBitmap(filepath);
                    photo_files.add("file://" + filepath);
                    if (!rv_new_order_photos.isShown()) {
                        rv_new_order_photos.setVisibility(View.VISIBLE);
                    }
                    photosAdapter.notifyDataSetChanged();
                    rv_new_order_photos.smoothScrollToPosition(photosAdapter.getItemCount() - 1);
                }
                break;
        }
    }

}
