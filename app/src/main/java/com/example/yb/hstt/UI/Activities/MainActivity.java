package com.example.yb.hstt.UI.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.Adpater.Recycle_function_adappter;
import com.example.yb.hstt.Base.BaseApplication;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.SgeoEvent;
import com.example.yb.hstt.IM.LoginAsyncTask;
import com.example.yb.hstt.IM.SmackUtils;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Service.LocationService;
import com.example.yb.hstt.UI.Activities.PersonCenter.FeedbackActivity;
import com.example.yb.hstt.UI.Activities.PersonCenter.PsdChangeActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskListActivity;
import com.example.yb.hstt.Utils.NotificationUtils;
import com.example.yb.hstt.Utils.SpUtil;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.dao.DbHelper.LoginInfoDbHelper;
import com.example.yb.hstt.dao.bean.LoginInfo;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends HsttBaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @BindView(R.id.rc_function)
    RecyclerView rcFunction;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.rl_left)
    RelativeLayout rlLeft;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.activity_log)
    DrawerLayout activityLog;
    @BindView(R.id.log_toolbar)
    Toolbar logToolbar;
    @BindView(R.id.user_name)
    TextView userName;//姓名
    @BindView(R.id.user_phone)
    TextView userPhone;//电话
    @BindView(R.id.tv_employe)
    TextView tvEmploye;//部门+工号
    @BindView(R.id.ll_change_psd)
    LinearLayout llChangePsd;//修改密码
    @BindView(R.id.ll_feefback)
    LinearLayout llFeefback;//意见反馈
    @BindView(R.id.ll_exit)
    LinearLayout llExit;//退出
    @BindView(R.id.ll_start_location_service)
    LinearLayout llStartLocationService;
    @BindView(R.id.ll_stop_location_service)
    LinearLayout llStopLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        logToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.user_center));
        logToolbar.setNavigationOnClickListener(this);

        //设置位置上传间隔
        AppPreferences appPreferences = new AppPreferences(getMe());
        appPreferences.put("time_interval", 10 * 1000); //10s

        LoginInfoDbHelper loginInfoDbHelper = LoginInfoDbHelper.getInstance(this);
        LoginInfo loginInfo = loginInfoDbHelper.select();

        userName.setText(loginInfo.getUser_name());
        userPhone.setText(loginInfo.getUser_phone());
        String job_no = loginInfo.getJob_number();
        String org = loginInfo.getUser_org_name();
        tvEmploye.setText("部门/工号 : " + org + "/" + job_no);


        llChangePsd.setOnClickListener(this);
        llFeefback.setOnClickListener(this);
        llExit.setOnClickListener(this);
        llStartLocationService.setOnClickListener(this);
        llStopLocationService.setOnClickListener(this);

        //动态申请相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        ArrayList<HashMap<String, Object>> function_list = new ArrayList<>();
        initButtonData(function_list);

        //设置为GridView
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rcFunction.addItemDecoration(new MyDecoration(this, -1, R.drawable.recycle_dividing_shape));
        rcFunction.setLayoutManager(manager);
        //设置按钮列表数据
        Recycle_function_adappter adapter = new Recycle_function_adappter(this, function_list);
        //设置点击事件
        adapter.setOnItemClickListener(new Recycle_function_adappter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0://终端列表
                        Log.i(TAG, "AC里position + " + position);
                        Intent intent0 = new Intent(getMe(), TerminalListActivity.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Log.i(TAG, "AC里position + " + position);
                        Intent intent1 = new Intent(getMe(), DeviceListActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        //新建工单
                        Intent intent2 = new Intent(getMe(), NewOrderActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        //我的工单
                        Intent intent3 = new Intent(getMe(), TaskListActivity.class);
                        startActivity(intent3);
                        break;
                }

            }
        });
        rcFunction.setAdapter(adapter);
        LoginSmackPush();

        activityLog.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
                Log.i("drawer", "drawer的状态：" + arg0);
            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                Log.i("drawer", arg1 + "");
                int width0 = rlLeft.getWidth();

                int move = (int) (width0 * arg1);
                rlMain.scrollTo(-move, 0);
            }

            @Override
            public void onDrawerOpened(View arg0) {
            }

            @Override
            public void onDrawerClosed(View arg0) {
            }
        });

    }


    /**
     * 登陆smack推送
     */
    public void LoginSmackPush() {
        String phoneNo = (String) SpUtil.get(this, "phoneNo", "");
        String phonePsD = (String) SpUtil.get(this, "phonePsD", "");
        if (!TextUtils.isEmpty(phoneNo)) {
            //执行异步登录任务
            BaseApplication.loginAsyncTask = new LoginAsyncTask(this);
            BaseApplication.loginAsyncTask.execute(phoneNo, "123456");
        }
    }

    private void imLoginOK() {
        if (BaseApplication.loginAsyncTask != null && BaseApplication.loginAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            BaseApplication.loginAsyncTask.cancel(true);
        }
    }

    /*初始化主页按钮数据*/
    private ArrayList<HashMap<String, Object>> initButtonData(ArrayList<HashMap<String, Object>> function_list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("title_text", "终端安装");
        map.put("title_image", R.drawable.terminal_install);
        function_list.add(map);

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("title_text", "设备安装");
        map1.put("title_image", R.drawable.device_install);
        function_list.add(map1);

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("title_text", "新建工单");
        map2.put("title_image", R.drawable.log);
        function_list.add(map2);

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("title_text", "我的工单");
        map3.put("title_image", R.drawable.setting);
        function_list.add(map3);

        return function_list;
    }

    long lasttime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lasttime < 2000) {
            AppPreferences preferences = new AppPreferences(getMe());
            preferences.put("time_interval", 30 * 1000);
            getMyApplication().exit(0);
        } else {
            ToastUtil.showToast(this, "再点一次退出！");
        }
        lasttime = System.currentTimeMillis();
    }

    public void onNotifi(View view) {
        Notification("测试");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                activityLog.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_setting:
                Intent intent = new Intent(getMe(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_change_psd:
                Intent intent0 = new Intent(getMe(), PsdChangeActivity.class);
                startActivity(intent0);
                break;
            case R.id.ll_feefback:
                Intent intent4 = new Intent(getMe(), FeedbackActivity.class);
                startActivity(intent4);
                break;
            case R.id.ll_exit:
                SpUtil.clear(getMe());
                Intent intent1 = new Intent(getMe(), LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.ll_start_location_service:
                Intent intent2 = new Intent(getMe(), LocationService.class);
                startService(intent2);
                ToastUtil.showToast(getMe(), "开始上传");
                break;
            case R.id.ll_stop_location_service:
                Intent intent3 = new Intent(getMe(), LocationService.class);
                stopService(intent3);
                ToastUtil.showToast(getMe(), "结束上传");
                break;
        }
    }

    @Subscribe()
    public void onEventMainThread(SgeoEvent event) {

        String eventid = event.getEventId();
        String msg = event.getMsg();
        if (eventid != null && eventid.equals(SgeoEvent.IM_LOGIN_OK)) {
            imLoginOK();
        }
        if (eventid != null && eventid.equals(SgeoEvent.IM_CHAT_REC)) {
            Notification(msg);
        }
    }

    AlertDialog NotifSetDiaolg;
    private void Notification(String msg) {
        if (NotificationUtils.isNotificationEnabled(this)) {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationUtils.getInstance(getMe())
                    .setContenttitle(getResources().getString(R.string.app_name))
                    .setContenttext(msg)
                    .setResultintent(intent)
                    .SendNotification();
        }else{
            if (NotifSetDiaolg==null){
                NotifSetDiaolg=new AlertDialog.Builder(this)
                        .setTitle("权限设置：")
                        .setMessage("您的通知权限未开启，是否去设置？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotificationUtils.requestSetPermission(getMe(),0);
                            }
                        }).create();
            }
            NotifSetDiaolg.show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        imLoginOK();
        SmackUtils.getInstance().exitConnect();
        EventBus.getDefault().unregister(this);
        if (NotifSetDiaolg!=null&&NotifSetDiaolg.isShowing()){
            NotifSetDiaolg.dismiss();
        }
    }

}
