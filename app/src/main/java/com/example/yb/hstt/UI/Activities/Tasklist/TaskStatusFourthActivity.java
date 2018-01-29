package com.example.yb.hstt.UI.Activities.Tasklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Adpater.Recycle_pic_hor_adapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.ChangeDeviceActivity;
import com.example.yb.hstt.UI.Activities.MainActivity;
import com.example.yb.hstt.UI.Activities.RepairDeviceActivity;
import com.example.yb.hstt.UI.Activities.TheOrderFinishedListActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.DialogUtil;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.lzy.okhttputils.OkHttpUtils;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 采证完成的界面
 */
public class TaskStatusFourthActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "TaskStatusFourthActivit";

    @BindView(R.id.rl_task_status_fourth)
    RelativeLayout rl_task_status_fourth;
    @BindView(R.id.task_status_third_toolbar)
    Toolbar taskStatusThirdToolbar;
    @BindView(R.id.et_reason)
    EditText etReason;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    @BindView(R.id.v_trans)
    View vTrans;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.bt_more)
    Button btMore;
    @BindView(R.id.tv_equipment_type)
    TextView tvEquipmentType;//设备品牌+厂家
    @BindView(R.id.tv_equipment_content)
    TextView tvEquipmentContent;//设备故障信息
    @BindView(R.id.tv_contact)
    TextView tvContact;//联系人
    @BindView(R.id.tv_contact_phone)
    TextView tvContactPhone;//联系人电话
    @BindView(R.id.tv_contact_addr)
    TextView tvContactAddr;//联系人地址
    @BindView(R.id.rv_info_pic)
    RecyclerView rvInfoPic;//图片列表
    private HashMap task_info;
    private String owrdpId;
    private String owwoId;
    private PopupWindow pop_call;
    Recycle_pic_hor_adapter hor_adapter_pic = new Recycle_pic_hor_adapter(this);
    private PopupWindow more_menu;
    private PopupWindow next_menu;
    private String pop_title;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private double lat;
    private double lon;
    private String dev_info;
    private boolean location_confirm = false;
    private CenterDialog dialog;

    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status_fourth);
        ButterKnife.bind(this);
        //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
        //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
        mLocationClient = new LocationClient(getApplicationContext());
        setLocationListener();
        //注册监听函数
        etReason.setEnabled(false);
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        owrdpId = getIntent().getStringExtra("owrdpId");
        owwoId = String.valueOf(task_info.get("owwoId"));
        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(manager);
        //设置布局类型
        LinearLayoutManager manager1 = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(manager1);
        initMoreMenu();
        initNextMenu();
        initView();
        initData();
        initDevInfo();
        initCallPop();
        initDataPics(GlobalUrl.Get_TaskInfo);
    }

    private void setLocationListener() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);

        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        Log.i(TAG, "开始定位");
        mLocationClient.start();
    }

    private void initView() {

        taskStatusThirdToolbar.setNavigationIcon(R.mipmap.ic_return);
        taskStatusThirdToolbar.setNavigationOnClickListener(this);

        tvContact.setText(String.valueOf(task_info.get("crContactName")));
        tvContactAddr.setText(String.valueOf(task_info.get("crAddress")));
        tvContactPhone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        tvEquipmentContent.setText(String.valueOf(task_info.get("crDescribe")));

        ivCall.setOnClickListener(this);
        btMore.setOnClickListener(this);
        btNext.setOnClickListener(this);
        tvEquipmentContent.setOnClickListener(this);
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

    /**
     * 初始化"继续处理"按钮弹窗
     */
    private void initNextMenu() {
        View view = View.inflate(getMe(), R.layout.next_menu, null);
        next_menu = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        next_menu.setBackgroundDrawable(new BitmapDrawable());    //设置背景，否则setOutsideTouchable无效
        next_menu.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        next_menu.setFocusable(true);
        next_menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setScreenLight();
            }
        });

        RelativeLayout rl_change = (RelativeLayout) next_menu.getContentView().findViewById(R.id.rl_change);
        RelativeLayout rl_repair = (RelativeLayout) next_menu.getContentView().findViewById(R.id.rl_repair);
        RelativeLayout rl_cancel = (RelativeLayout) next_menu.getContentView().findViewById(R.id.rl_cancel);
        rl_change.setOnClickListener(this);
        rl_repair.setOnClickListener(this);
        rl_cancel.setOnClickListener(this);
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
        tv_contact_phone.setOnClickListener(this);
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
                                HashMap task_data = (HashMap) map.get("data");
                                ArrayList dev_datas = (ArrayList) task_data.get("devs");
                                //设备信息不唯一,需要遍历
                                String s_dev_type = "";
                                String s_dev_factory = "";
                                dev_info = "";
                                Iterator ite = dev_datas.iterator();
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

    private void initData() {
        String owwoId = String.valueOf(task_info.get("owwoId"));
        OkHttpUtils.get(GlobalUrl.Get_Success_Save_Info)
                .params("owwoId", owwoId)
                .params("owrdpId", owrdpId)
                .execute(new CommonCallback<Map>(getMe()) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                Log.i(TAG, "获取采证数据接口调用成功");
                                HashMap map1 = (HashMap) map.get("data");

                                //填充文字信息
                                ArrayList comments = (ArrayList) map1.get("info");
                                HashMap tasks = (HashMap) comments.get(0);
                                String comment = String.valueOf(tasks.get("comment"));
                                etReason.setText(comment);

                                //填充图片以及视频信息
                                ArrayList files = (ArrayList) map1.get("files");

                                final ArrayList<String> pics = new ArrayList<>();
                                ArrayList<String> videos = new ArrayList<>();

                                for (int i = 0; i < files.size(); i++) {
                                    HashMap map2 = (HashMap) files.get(i);
                                    String net_path = (String) map2.get("attaFilePath");
                                    if (net_path.contains("jpg") || net_path.contains("png")) {
                                        pics.add(net_path);
                                    } else if (net_path.contains("mp4") || net_path.contains("avi")) {
                                        videos.add(net_path);
                                    }
                                }

                                hor_adapter_pic.setPicData(pics);
                                rvPhotos.setAdapter(hor_adapter_pic);
                                DeviceVideoAdapter videoAdapter = new DeviceVideoAdapter(videos, getMyApplication());
                                rvVideos.setAdapter(videoAdapter);

                            } else {
                                ToastUtil.showToast(getMe(), "获取采证数据接口调用失败,错误码: " + res_code + " 错误信息: " + String.valueOf(map.get("msg")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 故障设备的图片加载
     *
     * @param url
     */
    private void initDataPics(String url) {

        OkHttpUtils.get(url)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .execute(new CommonCallback<Map>(getMe()) {


                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                Log.i(TAG, "获取数据成功,弹窗");
                                HashMap data = (HashMap) map.get("data");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.iv_call:
                if (pop_call.isShowing()) {
                    pop_call.dismiss();
                }
                setScreenDark();
                pop_call.showAtLocation(rl_task_status_fourth, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));
                break;
            case R.id.rl_call_cancel:
                if (pop_call.isShowing()) {
                    pop_call.dismiss();
                }
                break;
            case R.id.tv_contact_phone:
                AppUtils.callPhone(((TextView) v).getText().toString(), getMe());
                pop_call.dismiss();
                break;
            case R.id.rl_change:
                Intent intent4 = new Intent(getMe(), ChangeDeviceActivity.class);
                intent4.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent4.putExtra("owwoId", owwoId + "");
                startActivity(intent4);
                break;
            case R.id.rl_repair:
                Intent intent5 = new Intent(getMe(), RepairDeviceActivity.class);
                intent5.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent5.putExtra("owwoId", owwoId + "");
                startActivity(intent5);
                break;
            case R.id.rl_cancel:
                if (next_menu.isShowing()) next_menu.dismiss();
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
            case R.id.bt_next:
                setScreenDark();
                next_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                next_menu.setAnimationStyle(R.style.popWindow_animation);
                next_menu.showAtLocation(rl_task_status_fourth, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                break;
            case R.id.bt_more:
                setScreenDark();
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(rl_task_status_fourth, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                break;

            case R.id.tv_equipment_content:// 开始接单
                DialogUtil.showNormalDialog(String.valueOf(task_info.get("crDescribe")),getMe());
                break;
        }
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
                            if (pop_title.contains("回退") && location_confirm) {
                                // 走退回接口
                                task_opreate(GlobalUrl.Task_Back, comment);
                            } else if (pop_title.contains("结束") && location_confirm) {
                                // 走结束接口
                                task_opreate(GlobalUrl.Task_Finish, comment);
                            } else if (pop_title.contains("协作") && location_confirm) {
                                // 走协作接口
                                task_opreate(GlobalUrl.Finish_Together, comment);
                            } else {
                                //位置信息获取失败,给用户弹窗
                                show_confirm_dialog(getDealUrl(), comment, 2);
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


    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(TaskStatusFourthActivity.this, R.color.black_trans);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(TaskStatusFourthActivity.this, R.color.transparent);
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

    private void show_confirm_dialog(final String url, final String comment, final int type) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("位置信息确认")
                .setMessage("还未获得当前位置,是否要继续操作?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "开始操作");
                        task_opreate(url, comment);

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

    @Override
    protected void onPause() {
        super.onPause();
        close_dialog_popupwindow();
    }

    private void close_dialog_popupwindow() {
        if (more_menu.isShowing()) more_menu.dismiss();
        if (next_menu.isShowing()) next_menu.dismiss();
        if (null != dialog && dialog.isShowing()) dialog.dismiss();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取到坐标后,停止定位
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
            location_confirm = true;
        }

    }

    @Override
    protected void onDestroy() {
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

