package com.example.yb.hstt.UI.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DeleteFileListener;
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.RecorderEvent;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskFinishActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.ImageCompressUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.record.MediaRecorderActivity;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class RepairDeviceActivity extends HsttBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, DeleteFileListener {
    public static final String TAG = "RepairDeviceActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_repairDevice)
    Toolbar tb_repairDevice;
    //旧设备布局show-hide
    @BindView(R.id.ll_repairDevice_LayoutDealWith)
    LinearLayout ll_repairDevice_LayoutDealWith;
    @BindView(R.id.ll_repairDevice_LayoutInfo)
    LinearLayout llrepairDeviceLayoutInfo;

    //新设备拍照列表
    @BindView(R.id.rl_repairDevice_DevicePhotoTag)
    RelativeLayout rl_repairDevice_DevicePhotoTag;

    //旧设备录制视频列表
    @BindView(R.id.rl_repairDevice_DeviceVideoTag)
    RelativeLayout rl_repairDevice_DeviceVideoTag;
    /**
     * 旧设备拍照rv
     */
    @BindView(R.id.rv_repairDevice_photos)
    RecyclerView rv_repairDevice_photos;
    /**
     * 旧设备录制视频rv
     */
    @BindView(R.id.rv_repairDevice_videos)
    RecyclerView rv_repairDevice_videos;

    //操作标签
    @BindView(R.id.rg_repairDevice)
    RadioGroup rg_repairDevice;
    @BindView(R.id.tv_repairDevice_Deviceid)
    TextView tv_repairDevice_Deviceid;
    @BindView(R.id.tv_repairDevice_DeviceType)
    EditText tv_repairDevice_DeviceType;
    @BindView(R.id.tv_repairDevice_DeviceFactory)
    EditText tv_repairDevice_DeviceFactory;
    @BindView(R.id.et_repairDevice_OldWorkResult)
    EditText et_repairDevice_OldWorkResult;
    @BindView(R.id.bt_more)
    Button btMore;
    @BindView(R.id.v_trans)
    View vTrans;
    @BindView(R.id.activity_repair_device)
    LinearLayout activityRepairDevice;
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    /**
     * 照片文件名
     */
    private String filename;
    /**
     * 录制视频缓存
     */
    private File VIDEO_DIR = new File(GlobalManager.VideoPath);
    /**
     * 录制视频的标记
     */
    private static final String repairDevice_video__tag = "repairDevice_VIDEO_TAG";
    private List<String> Photo_paths = new ArrayList<>();
    private List<String> Video_Mp4paths = new ArrayList<>();
    /**
     * 维修（编辑）设备提交附件的外勤工单id
     */
    private String owwoid;

    /**
     * 维修(编辑)的设备上传文件的，处理过程ID
     */
    private String repair_owrdpId, edit_owrdpId;
    /**
     * 维修(编辑)设备表ID
     */
    private String repair_cdmId, edit_cdmId;
    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;
    /**
     * 是否是编辑
     */
    private boolean is_Edit;
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
    private PopupWindow more_menu;
    private String pop_title;
    private String dev_info;
    private HashMap task_info;

    @Override
    public void deleteFile(int rv_id, int pos) {
        switch (rv_id) {
            case R.id.rv_repairDevice_photos:
                deleteFilesById(pos, Photo_paths, rv_repairDevice_photos);
                break;
            case R.id.rv_repairDevice_videos:
                deleteFilesById(pos, Video_Mp4paths, rv_repairDevice_videos);
                break;
        }

    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_device);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(tb_repairDevice);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setLocationOption();
        owwoid = getIntent().getStringExtra("owwoId");
        edit_cdmId = getIntent().getStringExtra("cd_id");
        is_Edit = getIntent().getBooleanExtra("is_Edit", false);
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        tb_repairDevice.setNavigationOnClickListener(this);
        rg_repairDevice.setOnCheckedChangeListener(this);
        btMore.setOnClickListener(this);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);

        //初始化旧设备拍照的列表
        initRecycleview(rv_repairDevice_photos, new DevicePhotoAdapter(Photo_paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));

        //初始化旧设备录制视频的列表
        initRecycleview(rv_repairDevice_videos, new DeviceVideoAdapter(Video_Mp4paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));
        if (is_Edit) {
            rl_repairDevice_DevicePhotoTag.setVisibility(View.VISIBLE);
            rl_repairDevice_DeviceVideoTag.setVisibility(View.VISIBLE);
            //查询更换的信息
            searchRepairDeviceInfo(edit_cdmId);
        }
        initMoreMenu();
        initDevInfo();
    }

    /**
     * 查询维修设备信息
     */
    private void searchRepairDeviceInfo(String search_cdmId) {
        HttpParams params = new HttpParams();
        params.put("cdmId", search_cdmId);
        OkHttpUtils.get(GlobalUrl.Repair_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> select_infos = (List<Map>) data.get("info");
                            setDeviceInfo(select_infos);

                            List<Map> select_files = (List<Map>) data.get("files");
                            setDeviceFileInfo(select_files);
                        }
                    }
                });
    }

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
            edit_owrdpId = deviceInfo.get(0).get("owrdpId").toString();
            tv_repairDevice_DeviceType.setText(deviceType);
            tv_repairDevice_DeviceFactory.setText(factory);
            tv_repairDevice_Deviceid.setText(devId);
            et_repairDevice_OldWorkResult.setText(comment);
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
            String owwoAttaId = files.get("owwoAttaId").toString();
            if (attaFilePath.contains("jpg")) {
                Photo_paths.add(attaFilePath + "@@" + owwoAttaId);
            } else {
                Video_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
            }
        }
        rv_repairDevice_photos.getAdapter().notifyDataSetChanged();
        rv_repairDevice_videos.getAdapter().notifyDataSetChanged();
    }

    private void initRecycleview(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(photo_list_decoration);
        recyclerView.setAdapter(devicePhotoAdapter);
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

    private CenterDialog dialog;


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
                            if (pop_title.contains("回退")) {
                                // 走退回接口
                                task_opreate(GlobalUrl.Task_Back, comment);
                            } else if (pop_title.contains("结束")) {
                                // 走结束接口
                                task_opreate(GlobalUrl.Task_Finish, comment);
                            } else if (pop_title.contains("协作")) {
                                // 走协作接口
                                task_opreate(GlobalUrl.Finish_Together, comment);
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
     * 获取设备信息,包括设备品牌及厂家
     */
    private void initDevInfo() {
        OkHttpUtils.get(GlobalUrl.Get_TaskInfo)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .execute(new CommonCallback<Map>(getMe(),true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                HashMap task_data = (HashMap) map.get("data");
                                ArrayList dev_datas = (ArrayList) task_data.get("devs");
                                //设备信息不唯一,需要遍历
                                String s_dev_type = "";
                                String s_dev_factory = "";
                                dev_info = "";
                                Iterator ite = dev_datas.iterator();
                                if (!ite.hasNext()) {
                                } else {
                                    while (ite.hasNext()) {
                                        HashMap dev = (HashMap) ite.next();
                                        s_dev_type = s_dev_type + String.valueOf(dev.get("devTypeNo"));
                                        s_dev_factory = s_dev_factory + String.valueOf(dev.get("devManufacturerNo"));
                                        dev_info = s_dev_type + "-" + s_dev_factory;
                                        if (ite.hasNext()) {
                                            dev_info = dev_info + "\n";
                                        }
                                    }

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
     * 对工单的操作,包括退回和结束工单
     *
     * @param task_back
     */
    private void task_opreate(final String task_back, String comment) {
        OkHttpUtils.post(task_back)
                .params("owwoId", owwoid)
                .params("comment", comment)
                .params("currentLng", String.valueOf(longitude))
                .params("currentLat", String.valueOf(longitude))
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
                            } else if (task_back.contains("305")) {
                                //协作接口
                                Intent intent = new Intent(getMe(), TaskFinishActivity.class);
                                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                                intent.putExtra("DEVICE_INFO", dev_info);//问题设备信息
                                intent.putExtra("PICS", dev_info);//问题设备信息
                                startActivity(intent);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                returnBack();
                break;
            case R.id.bt_more:
                setScreenDark();
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(activityRepairDevice, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                initListener();
                break;
            case R.id.iv_close:
                dialog.dismiss();
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
        }
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
        RelativeLayout rl_do_together = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_do_together);
        RelativeLayout rl_return_main = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_return_main);
        rl_do_together.setOnClickListener(this);
        rl_return_main.setOnClickListener(this);
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
    }

    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(RepairDeviceActivity.this, R.color.black_trans);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(RepairDeviceActivity.this, R.color.transparent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = ButterKnife.findById(group, checkedId);
        et_repairDevice_OldWorkResult.setText(radioButton.getText().toString());

    }

    @OnClick({R.id.tv_repairDevice_Deviceid, R.id.tv_repairDevice_LayoutInfo, R.id.tv_repairDevice_LayoutDealWith,
            R.id.iv_repairDevice_takephoto, R.id.iv_repairDevice_takevideo, R.id.bt_repairDevice_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_repairDevice_Deviceid:
                Intent intent = new Intent(getMe(), CaptureActivity.class);
                startActivityForResult(intent, 101);
                break;
            case R.id.tv_repairDevice_LayoutInfo:
                LayoutShowHide(llrepairDeviceLayoutInfo, (TextView) view);
                break;
            case R.id.tv_repairDevice_LayoutDealWith:
                LayoutShowHide(ll_repairDevice_LayoutDealWith, (TextView) view);
                break;

            case R.id.iv_repairDevice_takephoto:
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
            case R.id.iv_repairDevice_takevideo:
                if (!VIDEO_DIR.exists()) {
                    VIDEO_DIR.mkdirs();
                }
                Intent intent2 = new Intent(this, MediaRecorderActivity.class);
                intent2.putExtra("filename", MyDateUtils.getCurTime("yyyy-MM-dd_hhmmss"));
                intent2.putExtra("dir", VIDEO_DIR.toString());
                intent2.putExtra("functionLabel", repairDevice_video__tag);
                startActivity(intent2);
                break;
            case R.id.bt_repairDevice_save:
                if (is_Edit) {
                    Edit_RepairedDevice(edit_cdmId);
                } else {
                    RepairDevice();
                }
                break;
        }
    }

    /**
     * 编辑已维修设备
     */
    private void Edit_RepairedDevice(final String cdmId) {
        String cdmDevId = tv_repairDevice_Deviceid.getText().toString();
        HttpParams params = new HttpParams();
        params.put("cdmId", cdmId);
        params.put("cdmDevId", cdmDevId);
        params.put("comment", et_repairDevice_OldWorkResult.getText().toString());
        OkHttpUtils.post(GlobalUrl.TheOrder_EditRepairedDevice).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "bt_changeDevice_save: " + map);
                int code = (int) map.get("code");
                if (code == 10000) {
                    List<String> local_Photo_paths = getLocalFiles(Photo_paths);
                    List<String> local_Video_Mp4paths = getLocalFiles(Video_Mp4paths);

                    if (local_Photo_paths.size() == 0 && local_Video_Mp4paths.size() == 0) {
                        ToNext(cdmId);
                        return;
                    }
                    showProgress("上传中...");
                    submitOldDeviceFile(local_Photo_paths, local_Video_Mp4paths, edit_owrdpId, owwoid, cdmId);
                } else {
                    ToastUtil.showToast(getMe(), "数据异常！");
                }
            }
        });
    }

    /**
     * 从所有文件集合中，返回本地文件集合
     *
     * @param files
     * @return
     */
    private List<String> getLocalFiles(List<String> files) {
        List<String> localFiles = new ArrayList<>();
        for (String path : files) {
            if (path.startsWith("file")) {
                localFiles.add(path);
            }
        }
        return localFiles;
    }

    /**
     * 删除附件
     */
    private void deleteFilesById(final int pos, final List<String> paths, final RecyclerView recycler) {
        String path = paths.get(pos);
        String owwoAttaId = path.split("@@")[1];
        OkHttpUtils.post(GlobalUrl.TheOrder_FinishedFileDelete).params("owwoAttaId", owwoAttaId).execute(new CommonCallback<Map>(getMe(), "删除中。。。") {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "TheOrder_FinishedFileDelete: " + map);
                if (map.get("code").toString().equals("10000")) {
                    paths.remove(pos);
                    recycler.getAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    private int is_success_uploadOld = -1;//1，成功。0，失败。

    private void RepairDevice() {
        HttpParams params = new HttpParams();
        params.put("owwoId", owwoid);
        params.put("cdmDevId", tv_repairDevice_Deviceid.getText().toString());
        params.put("comment", et_repairDevice_OldWorkResult.getText().toString());
        params.put("currentLng", longitude);
        params.put("currentLat", latitude);

        OkHttpUtils.post(GlobalUrl.Repair_Device).params(params).execute(new CommonCallback<Map>(getMe()) {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "Repair_Device: " + map);
                int code = (int) map.get("code");
                if (code == 10000) {
                    Map data = (Map) map.get("data");
                    repair_owrdpId = String.valueOf(data.get("owrdpId"));
                    repair_cdmId = String.valueOf(data.get("cdmId"));

                    if (Photo_paths.size() == 0 && Video_Mp4paths.size() == 0) {
                        ToNext(repair_cdmId);
                        return;
                    }
                    showProgress("上传中...");
                    submitOldDeviceFile(Photo_paths, Video_Mp4paths, repair_owrdpId, owwoid, repair_cdmId);
                } else {
                    ToastUtil.showToast(getMe(), "数据异常！");
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.i(TAG, "Repair_Device: " + e);
            }
        });
    }

    private void submitOldDeviceFile(List<String> Photo_paths, List<String> Video_paths, String owrdpId, String owwoid, final String cdmId) {
        List<File> files = new ArrayList<>();
        for (String path : Photo_paths) {
            files.add(new File(path.replace("file://", "")));
        }
        for (String path : Video_paths) {
            String path2 = path.replace("file://", "");
            String path3 = path2.substring(0, path2.length() - 3);
            files.add(new File(path3 + "mp4"));
        }
        HttpParams params = new HttpParams();
        params.put("owrdpId", owrdpId);
        params.put("owwoId", owwoid);
        params.put("cdmId", cdmId);
        params.put("attaLng", longitude);
        params.put("attaLat", latitude);
        OkHttpUtils.post(GlobalUrl.Repair_DeviceUpLoad).addFileParams("file", files).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map s, Request request, @Nullable Response response) {

                JSONObject jsonObject = new JSONObject(s);
                try {
                    String code = jsonObject.getString("code");
                    if (code.equals("10000")) {
                        is_success_uploadOld = 1;
                    } else {
                        is_success_uploadOld = 0;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                is_success_uploadOld = 0;
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable Map s, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, s, call, response, e);
                dismissProgress();
                if (is_success_uploadOld == 1) {
                    ToNext(cdmId);
                } else {
                    ToastUtil.showToast(getMe(), "上传失败！");
                }
            }
        });
    }

    /**
     * 下一步
     */
    private void ToNext(String cdmId) {
        Intent nextIntent = new Intent(getMe(), RepairDeviceSuccssActivity.class);
        nextIntent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
        nextIntent.putExtra("cdmId", cdmId);
        startActivity(nextIntent);
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
     * 录制完回调
     *
     * @param event
     */
    @Subscribe
    public void RecorderEvent(RecorderEvent event) {
        String functionL = event.getFunctionLabel();
        String path = event.getMsg();
        switch (functionL) {
            case repairDevice_video__tag:
                Log.i("TAG", "RecorderEvent: " + path);
                Video_Mp4paths.add("file://" + path + ".mp4");
                if (!rl_repairDevice_DeviceVideoTag.isShown()) {
                    rl_repairDevice_DeviceVideoTag.setVisibility(View.VISIBLE);
                }
                rv_repairDevice_videos.getAdapter().notifyDataSetChanged();
                rv_repairDevice_videos.smoothScrollToPosition(Video_Mp4paths.size() - 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101://扫码
                if (data != null) {
                    Bundle extras = data.getExtras();
                    int resultInt = extras.getInt(CodeUtils.RESULT_TYPE);
                    if (resultInt == CodeUtils.RESULT_SUCCESS) {
                        String result = extras.getString(CodeUtils.RESULT_STRING);
                        selectDeviceinfo(result);
                    }
                }
                break;
            case 104://拍照
                NotifyPhotoDataChanged(requestCode);
                break;
        }
    }

    private void selectDeviceinfo(String devAssetCode) {
        OkHttpUtils.get(GlobalUrl.sys_url + "/device/queryDeviceInfo")
                .params("devAssetCode", devAssetCode).execute(new CommonCallback<Map>(getMe()) {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "selectDeviceinfo: " + map);
                String code = String.valueOf(map.get("code"));
                if (code.equals("10000")) {
                    Map device_data = (Map) map.get("data");
                    String devId = device_data.get("devId").toString();
                    String DEV_TYPE_NO = device_data.get("devTypeNo").toString();
                    String DEV_MANUFACTURER_NO = device_data.get("devManufacturerNo").toString();
                    tv_repairDevice_Deviceid.setText(devId);
                    tv_repairDevice_DeviceType.setText(DEV_TYPE_NO);
                    tv_repairDevice_DeviceFactory.setText(DEV_MANUFACTURER_NO);
                }

            }
        });

    }

    /**
     * 拍完照后刷新
     *
     * @param requestCode
     */
    private void NotifyPhotoDataChanged(int requestCode) {
        String filepath = GlobalManager.photoPath + File.separator + filename + ".jpg";
        File file = new File(filepath);
        if (!file.exists()) {
            return;
        }
        //压缩拍后的图片
        ImageCompressUtil.compressBitmap(filepath);
        if (requestCode == 104) { //旧设备 or 新设备
            Photo_paths.add("file://" + filepath);
            if (!rl_repairDevice_DevicePhotoTag.isShown()) {
                rl_repairDevice_DevicePhotoTag.setVisibility(View.VISIBLE);
            }
            rv_repairDevice_photos.getAdapter().notifyDataSetChanged();
            rv_repairDevice_photos.smoothScrollToPosition(Photo_paths.size() - 1);
        }
    }

    @Override
    public void onBackPressed() {
        returnBack();
    }

    AlertDialog Backdialog;

    private void returnBack() {
        if (Backdialog == null) {
            Backdialog = new AlertDialog.Builder(getMe())
                    .setTitle("确定退出吗？")
                    .setMessage("退出，将不保存！")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
        }
        Backdialog.show();
    }
}
