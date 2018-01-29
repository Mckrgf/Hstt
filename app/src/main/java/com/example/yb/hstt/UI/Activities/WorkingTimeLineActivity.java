package com.example.yb.hstt.UI.Activities;

import android.app.Dialog;
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
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.WorkingTimeLineAdapter;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskFinishActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskListActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusFirstActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusFourthActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusSecondActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusThirdActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.DialogUtil;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.bean.workTimeLineInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

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

public class WorkingTimeLineActivity extends HsttBaseActivity implements View.OnClickListener {
    public static final String TAG = "WorkingTimeLineActivity";
    @BindView(R.id.tb_finalFinishedOrder)
    Toolbar tb_finalFinishedOrder;
    @BindView(R.id.rv_workingTimeLine_lists)
    RecyclerView rv_workingTimeLine_lists;

    TextView tv_workingTimeLine_TypeWithFactory;
    TextView tv_workingTimeLine_comment;
    TextView tv_workingTimeLine_user;
    TextView tv_workingTimeLine_phone;
    TextView tv_workingTimeLine_address;
    RecyclerView rv_workingTimeLine_photos;
    @BindView(R.id.tv_workTimeLine_start_deal)
    Button tvWorkTimeLineStartDeal;
    @BindView(R.id.bt_workTimeLine_more)
    Button btWorkTimeLineMore;
    @BindView(R.id.activity_working_time_line)
    RelativeLayout activityWorkingTimeLine;
    @BindView(R.id.iv_commit_list)
    ImageView iv_commit_list;
    private MyLocationListener myListener = new MyLocationListener();

    private PopupWindow next_menu;
    private PopupWindow more_menu;

    private String owwoId = "10000062";
    private List<String> photoFiles = new ArrayList<>();
    private List<workTimeLineInfo> timeLineInfos = new ArrayList<>();
    private WorkingTimeLineAdapter timeLineAdapter;
    private String owrdpTypeNo;//步骤id
    private HashMap task_info;
    private String pop_title;
    private CenterDialog dialog;
    private double lat;
    private double lon;
    private boolean location_confirm;

    public LocationClient mLocationClient = null;
    private String dev_info;
    private ImageView iv_call;
    private PopupWindow pop_call;
    private String task_status = "";//整体任务状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_time_line);
        ButterKnife.bind(this);
        owwoId = getIntent().getStringExtra("owwoId");
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");

        task_status = String.valueOf(task_info.get("owwoStatusTypeNo"));

        setSupportActionBar(tb_finalFinishedOrder);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tb_finalFinishedOrder.setNavigationOnClickListener(this);
        iv_commit_list.setOnClickListener(this);
        initNextMenu();
        initMoreMenu();
        //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
        mLocationClient = new LocationClient(getApplicationContext());
        setLocationListener();

        //初始化时间线 头布局
        View head = getLayoutInflater().inflate(R.layout.work_time_line_head, null);
        tv_workingTimeLine_TypeWithFactory = ButterKnife.findById(head, R.id.tv_equipment_type);
        tv_workingTimeLine_comment = ButterKnife.findById(head, R.id.tv_equipment_content);
        tv_workingTimeLine_user = ButterKnife.findById(head, R.id.tv_contact);
        tv_workingTimeLine_phone = ButterKnife.findById(head, R.id.tv_contact_phone);
        tv_workingTimeLine_address = ButterKnife.findById(head, R.id.tv_contact_addr);
        rv_workingTimeLine_photos = ButterKnife.findById(head, R.id.rv_workingTimeLine_photos);
        iv_call = ButterKnife.findById(head, R.id.iv_call);

        tv_workingTimeLine_comment.setOnClickListener(this);

        iv_call.setOnClickListener(this);
        initRecyclerView(rv_workingTimeLine_photos, new DevicePhotoAdapter(photoFiles, getMe()));

        //初始化时间线 适配器
        timeLineAdapter = new WorkingTimeLineAdapter(this, head, timeLineInfos);
        rv_workingTimeLine_lists.setLayoutManager(new LinearLayoutManager(this));
        rv_workingTimeLine_lists.setAdapter(timeLineAdapter);

        //查询头部工单信息
        selectTaskInfo();
        //查询时间线信息
        reqestTimeLine();

        initCallPop();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing));
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    /**
     * 查询任务信息
     */
    private void selectTaskInfo() {
        OkHttpUtils.get(GlobalUrl.Get_TaskInfo).params("owwoId", owwoId)
                .execute(new CommonCallback<Map>(getMe()) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        Log.i(TAG, "Get_TaskInfo: " + map);
                        if (map.get("code").toString().equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> info = (List<Map>) data.get("info");

                            String comment = info.get(0).get("crDescribe").toString();
                            String user = info.get(0).get("crContactName").toString();
                            String crContactPhoneNum = info.get(0).get("crContactPhoneNum").toString();
                            String crAddress = info.get(0).get("crAddress").toString();
                            tv_workingTimeLine_comment.setText(comment);
                            tv_workingTimeLine_user.setText(user);
                            tv_workingTimeLine_phone.setText(crContactPhoneNum);
                            tv_workingTimeLine_address.setText(crAddress);

                            List<Map> files = (List<Map>) data.get("files");
                            for (Map maps : files) {
                                String attaFilePath = maps.get("attaFilePath").toString();
                                photoFiles.add(attaFilePath);
                            }
                            rv_workingTimeLine_photos.getAdapter().notifyDataSetChanged();

                            List<Map> devs = (List<Map>) data.get("devs");
                            if (devs != null && devs.size() > 0) {
                                String devManufacturerNo = devs.get(0).get("devManufacturerNo").toString();
                                String devTypeNo = devs.get(0).get("devTypeNo").toString();
                                tv_workingTimeLine_TypeWithFactory.setText(devTypeNo + "" + devManufacturerNo);
                            }

                            String s_dev_type = "";
                            String s_dev_factory = "";
                            dev_info = "";
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
                        }
                    }
                });
    }

    private void reqestTimeLine() {
        HttpParams params = new HttpParams();
        Log.i(TAG, "reqestTimeLine: " + owwoId);
        params.put("owwoId", owwoId);
        OkHttpUtils.get(GlobalUrl.Working_TimeLine).params(params).execute(new CommonCallback<String>(getMe(), true) {

            @Override
            public void onResponse(boolean b, String result, Request request, @Nullable Response response) {
                Log.i(TAG, "Working_TimeLine: " + result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    String code = jsonobject.get("code").toString();
                    if (code.equals("10000")) {
                        String data = jsonobject.get("data").toString();
                        JSONObject dataObject = new JSONObject(data);
                        String selfProcess = dataObject.get("selfProcess").toString();
                        Gson gson = new Gson();
                        List<workTimeLineInfo> timeLines = gson.fromJson(selfProcess,
                                new TypeToken<List<workTimeLineInfo>>() {
                                }.getType());
                        timeLineInfos.clear();
                        timeLineInfos.addAll(timeLines);
                        timeLineAdapter.notifyDataSetChanged();
                        if (timeLineInfos.size() > 0) {
                            owrdpTypeNo = timeLineInfos.get(0).getOwrdpTypeNo();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                Intent intent1 = new Intent(getMe(), MainActivity.class);
                startActivity(intent1);
                finish();
            case R.id.iv_commit_list://返回首页
                Intent intent = new Intent(this, TheOrderFinishedListActivity.class);
                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent.putExtra("owwoId", owwoId);
                startActivity(intent);
                break;
            case R.id.iv_call://返回首页
                pop_call.setAnimationStyle(R.style.popWindow_animation);
                pop_call.showAtLocation(activityWorkingTimeLine, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));//parent为泡泡窗体所在的父容器view
                WindowUtil.getInstance().setAlphaDark(WorkingTimeLineActivity.this);
                break;
            case R.id.rl_call_a:
                Log.i(TAG, "打电话");
                pop_call.dismiss();
                //用intent启动拨打电话
                AppUtils.callPhone(task_info.get("crContactPhoneNum") + "", getMe());
                break;
            case R.id.rl_call_cancel:
                Log.i(TAG, "取消打电话");
                if (pop_call.isShowing()) {
                    pop_call.dismiss();
                }
                break;
            case R.id.tv_equipment_content:
                String comment = String.valueOf(tv_workingTimeLine_comment.getText());
                DialogUtil.showNormalDialog(comment,getMe());
                break;
        }
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
                WindowUtil.getInstance().setAlphaLight(WorkingTimeLineActivity.this);
            }
        });

        RelativeLayout rl_call_a = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_a);
        RelativeLayout rl_call_cancel = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_cancel);
        TextView tv_contact_phone = (TextView) pop_call.getContentView().findViewById(R.id.tv_contact_phone);
        tv_contact_phone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        rl_call_a.setOnClickListener(this);
        rl_call_cancel.setOnClickListener(this);
    }


    @OnClick({R.id.tv_workTimeLine_start_deal, R.id.bt_workTimeLine_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_workTimeLine_start_deal:

//              ("02", "开始处理");//赶赴现场中.2状态
//              ("03", "到达现场");//采证页面,3状态
//              ("04", "现场采证");//采证完成,4状态
//              ("05", "现场处理");//维修/更换
//              ("06", "结束工单");
//              ("07", "退回");
//              ("08", "协作完成");
                Intent intent = new Intent();
                if (owrdpTypeNo.equals("02")) {
                    intent = new Intent(getMe(), TaskStatusSecondActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                } else if (owrdpTypeNo.equals("03")) {
                    intent = new Intent(getMe(), TaskStatusThirdActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                } else if (owrdpTypeNo.equals("04")) {
                    intent = new Intent(getMe(), TaskStatusFourthActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                } else if (owrdpTypeNo.equals("05")) {
                    //修改"下一步"按钮为弹窗:维修:更换
                    show_repair_change();
                    break;
                } else if (owrdpTypeNo.equals("06")) {
                    find_way_out();
                } else if (owrdpTypeNo.equals("07")) {
                    find_way_out();
                } else if (owrdpTypeNo.equals("08")) {
                    find_way_out();
                }
                break;
            case R.id.bt_workTimeLine_more:
                WindowUtil.getInstance().setAlphaDark(this);
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(activityWorkingTimeLine, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
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
                R.id.bt_cnacel,
                R.id.iv_close};
        dialog = new CenterDialog(getMe(), R.layout.dialog_return_end, views);

        dialog.show();
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_reason);
        final TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
//        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
//        iv_close.setOnClickListener(this);
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
                                // 走写作借口
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
     * 对工单的操作,包括退回和结束工单
     *
     * @param task_back
     */
    private void task_opreate(final String task_back, String comment) {
        if (location_confirm) {
            OkHttpUtils.post(task_back)
                    .params("owwoId", String.valueOf(task_info.get("owwoId")))
                    .params("comment", comment)
                    .params("currentLng", String.valueOf(lon))
                    .params("currentLat", String.valueOf(lat))
                    .execute(new CommonCallback<Map>(getMe()) {

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

        } else {
            ToastUtil.showToast(getMe(), "位置信息未确认,无法使用退回/结束/协作功能,请确认位置权限是否开启,或者在本页面停留几秒钟等待系统获得位置信息");
        }

    }

    private void find_way_out() {
        Intent intent;//循环列表,看该状态之前的最后一个状态是什么
        for (int i = 0; i < timeLineInfos.size(); i++) {
            workTimeLineInfo info = timeLineInfos.get(i);
            String id = info.getOwrdpTypeNo();
            if (id.equals("06") || id.equals("07") || id.equals("08")) {
                // TODO: 2018/1/26 先获取整体状态,如果是678,则不能操作,如果不是678,则根据个人状态 owrdpTypeNo
                // TODO: 2018/1/26 需要考虑未结单就直接退的情况(01)
//              ("02", "开始处理");//赶赴现场中.2状态
//              ("03", "到达现场");//采证页面,3状态
//              ("04", "现场采证");//采证完成,4状态
//              ("05", "现场处理");//维修/更换
//              ("06", "结束工单");
//              ("07", "退回");
//              ("08", "协作完成");
                if (task_status.equals("06") | task_status.equals("07") | task_status.equals("08")) {
                    ToastUtil.showToast(getMe(), "任务已经结束/退回/协作完成.不能继续处理");
                } else {
                    deal_next();
                }
                Log.i(TAG, "继续循环");
                break;
            } else {
                Log.i(TAG, "找到结束/退回/协作工单前的最后一个操作步骤:" + id);
                owrdpTypeNo = id;
                deal_next();
                break;//跳出循环
            }
        }
    }

    /**
     * 根据不同任务状态选择不同页面
     */
    private void deal_next() {
        Intent intent;//根据不同的id进入不同的操作流程
        if (owrdpTypeNo.equals("02")) {
            intent = new Intent(getMe(), TaskStatusSecondActivity.class);
            intent.putExtra("TASK_INFO", task_info);
            startActivity(intent);
            finish();
        } else if (owrdpTypeNo.equals("03")) {
            intent = new Intent(getMe(), TaskStatusThirdActivity.class);
            intent.putExtra("TASK_INFO", task_info);
            startActivity(intent);
            finish();
        } else if (owrdpTypeNo.equals("04")) {
            intent = new Intent(getMe(), TaskStatusFourthActivity.class);
            intent.putExtra("TASK_INFO", task_info);
            startActivity(intent);
            finish();
        } else if (owrdpTypeNo.equals("05")) {
            //修改"下一步"按钮为弹窗:维修:更换
            show_repair_change();
        } else {
            //如果任务对应的个人状态是06/07/08,则需要获取06/07/08的前一个状态,根据前一个状态来决定要进入哪个页面
            for (int i = timeLineInfos.size() - 1; i >= 0; i--) {
                workTimeLineInfo info = timeLineInfos.get(i);
                String task_status_before_finished = info.getOwrdpTypeNo();
                if (task_status_before_finished.equals("02")) {
                    intent = new Intent(getMe(), TaskStatusSecondActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                    finish();
                    break;
                } else if (task_status_before_finished.equals("03")) {
                    intent = new Intent(getMe(), TaskStatusThirdActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                    finish();
                    break;
                } else if (task_status_before_finished.equals("04")) {
                    intent = new Intent(getMe(), TaskStatusFourthActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                    finish();
                    break;
                } else if (task_status_before_finished.equals("05")) {
                    //修改"下一步"按钮为弹窗:维修:更换
                    show_repair_change();
                    break;
                }
                if (i == 0) {
                    //如果循环到0 还是找不到任务状态为2/3/4/5的,说明这个任务对于这个人来说并没有操作过(02是接单),那就直接让他接单
                    intent = new Intent(getMe(), TaskStatusFirstActivity.class);
                    intent.putExtra("TASK_INFO", task_info);
                    startActivity(intent);
                    finish();
                    break;
                }
            }
        }
    }

    private void show_repair_change() {
        WindowUtil.getInstance().setAlphaDark(this);
        next_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        next_menu.setAnimationStyle(R.style.popWindow_animation);
        next_menu.showAtLocation(activityWorkingTimeLine, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
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
                WindowUtil.getInstance().setAlphaLight(WorkingTimeLineActivity.this);
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
                WindowUtil.getInstance().setAlphaLight(WorkingTimeLineActivity.this);
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
            String x_s = location.getLatitude() + "";
            if (!x_s.contains("e")) {
                //坐标正常,用户才可以进行下一步操作
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i(TAG, "得到定位");
                location_confirm = true;

                //获取到坐标后,停止定位
                mLocationClient.unRegisterLocationListener(myListener);
                mLocationClient.stop();
            }

        }

    }

    private void setLocationListener() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        Log.i(TAG, "开始定位");
        mLocationClient.start();
    }
}
