package com.example.yb.hstt.UI.Activities.Tasklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Adpater.Recycle_pic_hor_adapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.RecorderEvent;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.MainActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.DialogUtil;
import com.example.yb.hstt.Utils.ImageCompressUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.record.MediaRecorderActivity;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
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
 * 状态3:任务现场采证,最下面的按钮是"保存"
 */
public class TaskStatusThirdActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.task_status_third_toolbar)
    Toolbar taskStatusThirdToolbar;
    @BindView(R.id.rb_reason_a)
    RadioButton rbReasonA;
    @BindView(R.id.rb_reason_b)
    RadioButton rbReasonB;
    @BindView(R.id.rb_reason_c)
    RadioButton rbReasonC;
    @BindView(R.id.et_reason)
    EditText etReason;
    @BindView(R.id.iv_status_third_take_pic)
    ImageView ivStatusThirdTakePic;
    @BindView(R.id.iv_status_third_take_video)
    ImageView ivStatusThirdTakeVideo;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    @BindView(R.id.tv_equipment_type)
    TextView tvEquipmentType;
    @BindView(R.id.tv_equipment_content)
    TextView tvEquipmentContent;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_contact_phone)
    TextView tvContactPhone;
    @BindView(R.id.tv_contact_addr)
    TextView tvContactAddr;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.ll_contract_info)
    RelativeLayout llContractInfo;
    @BindView(R.id.rv_info_pic)
    RecyclerView rvInfoPic;
    @BindView(R.id.rl_need_to_hide)
    RelativeLayout rlNeedToHide;
    @BindView(R.id.activity_task_get_evidence)
    RelativeLayout activityTaskGetEvidence;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.bt_more)
    Button btMore;
    @BindView(R.id.rl_photos)
    RelativeLayout rlPhotos;
    @BindView(R.id.rl_videos)
    RelativeLayout rlVideos;
    @BindView(R.id.v_trans)
    View vTrans;
    private HashMap task_info;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private double lat = GlobalManager.lat_for_task;
    private double lon = GlobalManager.lng_for_task;
    private String filename;

    private List<String> Photo_paths = new ArrayList<>();
    private List<String> Video_paths = new ArrayList<>();
    private List<String> Video_Mp4paths = new ArrayList<>();

    private DevicePhotoAdapter PhotosAdapter = null;
    private DeviceVideoAdapter VideosAdapter = null;

    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;

    /**
     * 录制视频缓存
     */
    private File VIDEO_DIR = new File(GlobalManager.VideoPath);
    /**
     * 录制视频的标记
     */
    private static final String GET_EVIDENCE_VIDEO_TAG = "get_EVIDENCE_VIDEO_TAG";
    private PopupWindow pop_call;
    private HashMap data;
    private PopupWindow more_menu;
    private String pop_title;
    private String dev_info;
    private CenterDialog dialog;
    private boolean location_confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status_third);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        initView();
        initMoreMenu();
        setLocationOption();
        initData(GlobalUrl.Get_TaskInfo);
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
                                //列表初始化
                                LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
                                rvInfoPic.setLayoutManager(manager);
                                ArrayList pics = (ArrayList) data.get("files");
                                ArrayList<String> pic_urls = new ArrayList<>();
                                for (int i = 0; i < pics.size(); i++) {
                                    HashMap map1 = (HashMap) pics.get(i);
                                    String pic_url = (String) map1.get("attaFilePath");
                                    pic_urls.add(pic_url);
                                }
                                Recycle_pic_hor_adapter adapter = new Recycle_pic_hor_adapter(getMe(), pic_urls);
                                rvInfoPic.setAdapter(adapter);
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


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            lat = location.getLatitude();
            lon = location.getLongitude();

            //获取到坐标后,停止定位
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
            String x_s = location.getLatitude() + "";
            if (!x_s.contains("4.9E")) {
                location_confirm = true;
            } else {
                location_confirm = false;
            }


        }
    }

    /**
     * 初始化位置设置
     */
    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getMe());

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        Log.i(TAG, "开始定位");
    }

    private void initView() {
        taskStatusThirdToolbar.setNavigationIcon(R.mipmap.ic_return);
        taskStatusThirdToolbar.setNavigationOnClickListener(this);

        btMore.setOnClickListener(this);
        btSave.setOnClickListener(this);

        rbReasonA.setOnClickListener(this);
        rbReasonB.setOnClickListener(this);
        rbReasonC.setOnClickListener(this);
        tvEquipmentContent.setOnClickListener(this);
        ivCall.setOnClickListener(this);

        ivStatusThirdTakePic.setOnClickListener(this);
        ivStatusThirdTakeVideo.setOnClickListener(this);

        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);

        //初始化旧设备拍照的列表
        LinearLayoutManager photo_linearManager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(photo_linearManager);
        rvPhotos.addItemDecoration(photo_list_decoration);
        PhotosAdapter = new DevicePhotoAdapter(Photo_paths, getMe(),true,true);
        rvPhotos.setAdapter(PhotosAdapter);

        //初始化旧设备录制视频的列表
        LinearLayoutManager video_linearManager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(video_linearManager);
        rvVideos.addItemDecoration(photo_list_decoration);
        VideosAdapter = new DeviceVideoAdapter(Video_Mp4paths, getMe());
        rvVideos.setAdapter(VideosAdapter);

        tvContact.setText(String.valueOf(task_info.get("crContactName")));
        tvContactAddr.setText(String.valueOf(task_info.get("crAddress")));
        tvContactPhone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        tvEquipmentContent.setText(String.valueOf(task_info.get("crDescribe")));
        initDevInfo();
        initCallPop();
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
                                Log.i(TAG, "上传文件成功");
                                HashMap task_data = (HashMap) map.get("data");
                                ArrayList dev_datas = (ArrayList) task_data.get("devs");
                                //设备信息不唯一,需要遍历
                                String s_dev_type = "";
                                String s_dev_factory = "";
                                dev_info = "";
                                Iterator ite = dev_datas.iterator();
                                if (!ite.hasNext()) {
                                    tvEquipmentType.setText("无设备信息");
                                } else {
                                    while (ite.hasNext()) {
                                        HashMap dev = (HashMap) ite.next();
                                        s_dev_type = s_dev_type + String.valueOf(dev.get("devTypeNo"));
                                        s_dev_factory = s_dev_factory + String.valueOf(dev.get("devManufacturerNo"));
                                        dev_info = s_dev_type + "-" + s_dev_factory;
                                        if (ite.hasNext()) {
                                            dev_info = dev_info + "\n";
                                        }
                                        tvEquipmentType.setText(dev_info);
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
     * 录制完回调
     *
     * @param event
     */
    @Subscribe
    public void RecorderEvent(RecorderEvent event) {
        String functionL = event.getFunctionLabel();
        String path = event.getMsg();
        switch (functionL) {
            case GET_EVIDENCE_VIDEO_TAG:
                Log.i("TAG", "RecorderEvent: " + path);
                Video_paths.add("file://" + path + ".jpg");
                Video_Mp4paths.add("file://" + path + ".mp4");
                if (!rlVideos.isShown()) {
                    rlVideos.setVisibility(View.VISIBLE);
                }
                VideosAdapter.notifyDataSetChanged();
                rvVideos.smoothScrollToPosition(VideosAdapter.getItemCount() - 1);
                break;
        }
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
            case R.id.rb_reason_a://标签a
                etReason.setText(rbReasonA.getText().toString());
                break;
            case R.id.rb_reason_b://标签b
                etReason.setText(rbReasonB.getText().toString());
                break;
            case R.id.rb_reason_c://标签c
                etReason.setText(rbReasonC.getText().toString());
                break;
            case R.id.iv_status_third_take_video:
                if (!VIDEO_DIR.exists()) {
                    VIDEO_DIR.mkdirs();
                }
                Intent intent2 = new Intent(this, MediaRecorderActivity.class);
                intent2.putExtra("filename", MyDateUtils.getCurTime("yyyy-MM-dd_hhmmss"));
                intent2.putExtra("dir", VIDEO_DIR.toString());
                intent2.putExtra("functionLabel", GET_EVIDENCE_VIDEO_TAG);
                startActivity(intent2);
                break;
            case R.id.iv_status_third_take_pic:
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
            case R.id.iv_call:
                //打电话的弹窗
                setScreenDark();
                pop_call.setAnimationStyle(R.style.popWindow_animation);
                pop_call.showAtLocation(activityTaskGetEvidence, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));//parent为泡泡窗体所在的父容器view
                vTrans.setVisibility(View.VISIBLE);
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
            case R.id.bt_save://保存数据
                if (location_confirm) {
                    SaveData();
                } else {
                    show_confirm_dialog("","",1);
                }

                break;
            case R.id.bt_more://更多
                setScreenDark();
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(activityTaskGetEvidence, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                initListener();
                break;
            case R.id.tv_equipment_content:// 开始接单
                DialogUtil.showNormalDialog(String.valueOf(task_info.get("crDescribe")),getMe());
                break;
        }
    }

    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(TaskStatusThirdActivity.this, R.color.black_trans);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(TaskStatusThirdActivity.this, R.color.transparent);
    }

    private void show_confirm_dialog(final String url, final String comment, final int type) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("位置信息确认")
                .setMessage("还未获得当前位置,是否要继续操作?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "开始操作");
                        if (type==1) {
                            SaveData();
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
                .params("currentLng", String.valueOf(lon))
                .params("currentLat", String.valueOf(lat))
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        EventBus.getDefault().unregister(this);
    }

    private static final String TAG = "TaskStatusThirdActivity";

    /**
     * 保存数据
     */
    private void SaveData() {
        String comment = String.valueOf(etReason.getText());
        String lat_s = String.valueOf(lat);
        String lng_s = String.valueOf(lon);
        if (!TextUtils.isEmpty(comment)) {
            OkHttpUtils.post(GlobalUrl.Save_Info)
                    .params("owwoId", String.valueOf(task_info.get("owwoId")))
                    .params("comment", comment)
                    .params("currentLng", lng_s)
                    .params("currentLat", lat_s)
                    .execute(new CommonCallback<Map>(getMe(),true) {
                        @Override
                        public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                            try {
                                int res_code = (int) map.get("code");
                                String owrdpId = "";
                                try {
                                    //获取步骤id,赋值给全局变量以供上传位置服务使用
                                    HashMap map1 = (HashMap) map.get("data");
                                    owrdpId = String.valueOf(map1.get("owrdpId"));
                                    AppPreferences preferences = new AppPreferences(getMe());
                                    preferences.put("owrdpId", owrdpId);
                                } catch (Exception e) {
                                    Log.i(TAG, "类型转换异常");
                                }

                                if (res_code == 10000) {
                                    Log.i(TAG, "采证信息保存成功");

                                    //文字信息保存成功后开始上传图片信息
                                    submitFile("", owrdpId);

                                } else {
                                    ToastUtil.showToast(getMe(), "采证信息保存,错误码: " + res_code);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            ToastUtil.showToast(getMe(), "故障原因不能为空");
        }

    }

    /**
     * 上传文件
     *
     * @param crwoId
     */
    private void submitFile(String crwoId, final String owrdpId) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < Photo_paths.size(); i++) {
            files.add(new File(Photo_paths.get(i).replace("file://", "")));
            Log.i(TAG, "图片大小: " + files.get(i).length());
        }
        for (int i = 0; i < Video_Mp4paths.size(); i++) {
            files.add(new File(Video_Mp4paths.get(i).replace("file://", "")));
            Log.i(TAG, "视频大小: " + files.get(i).length());
        }
        if (files.size() == 0) {
            Intent intent = new Intent(getMe(), TaskStatusFourthActivity.class);
            intent.putExtra("TASK_INFO", task_info);
            intent.putExtra("owrdpId", owrdpId);
            startActivity(intent);
            finish();
        }else {
            HttpParams params = new HttpParams();
            params.put("owrdpId", owrdpId);
            params.put("owwoId", String.valueOf(task_info.get("owwoId")));
            params.put("attaLng", lon + "");
            params.put("attaLat", lat + "");
            String progress_message = "上传文件中...";
            OkHttpUtils.post(GlobalUrl.Save_Info_UpLoad)
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
//                                List<OrderFileInfo> lists = new Gson().fromJson(data, new TypeToken<List<OrderFileInfo>>() {
//                                }.getType());
//                                OrderInfo orderInfo = new OrderInfo();
//                                orderInfo.setAddress(et_new_order_address.getText().toString());
//                                orderInfo.setDevice_type(et_new_order_devicetype.getText().toString());
//                                orderInfo.setOrder_status(order_status_text);
//                                orderInfo.setCrDescribe("压缩机噪音太大");
//                                orderInfo.setFiles(lists);
                                    Intent intent = new Intent(getMe(), TaskStatusFourthActivity.class);
                                    intent.putExtra("TASK_INFO", task_info);
                                    intent.putExtra("owrdpId", owrdpId);
                                    startActivity(intent);
                                    finish();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 104://拍照
                NotifyPhotoDataChanged(requestCode);
                break;
        }
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
            if (!rlPhotos.isShown()) {
                rlPhotos.setVisibility(View.VISIBLE);
            }
            Photo_paths.add("file://" + filepath);
            PhotosAdapter.notifyDataSetChanged();
            rvPhotos.smoothScrollToPosition(PhotosAdapter.getItemCount() - 1);
        }
    }
}
