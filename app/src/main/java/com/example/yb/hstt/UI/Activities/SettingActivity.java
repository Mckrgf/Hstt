package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Service.LocationService;
import com.example.yb.hstt.Utils.SpUtil;
import com.example.yb.hstt.Utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "SettingActivity";

    @BindView(R.id.setting_toolbar)
    Toolbar settingToolbar;                     //标题栏
    @BindView(R.id.ll_account_management)
    LinearLayout llAccountManagement;           //账号管理
    @BindView(R.id.ll_password_management)
    LinearLayout llPasswordManagement;          //密码管理
    @BindView(R.id.ll_log_management)
    LinearLayout llLogManagement;               //日志系统
    @BindView(R.id.activity_setting)
    RelativeLayout activitySetting;
    @BindView(R.id.ll_exit)
    LinearLayout llExit;
    @BindView(R.id.ll_start_location_service)
    LinearLayout llStartLocationService;
    @BindView(R.id.ll_stop_location_service)
    LinearLayout llStopLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        settingToolbar.setNavigationIcon(R.mipmap.ic_return);
        settingToolbar.setNavigationOnClickListener(this);

        llAccountManagement.setOnClickListener(this);
        llPasswordManagement.setOnClickListener(this);
        llLogManagement.setOnClickListener(this);
        llExit.setOnClickListener(this);
        llStartLocationService.setOnClickListener(this);
        llStopLocationService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.ll_account_management:
                Log.i(TAG, "账号管理");
                break;
            case R.id.ll_password_management:
                Log.i(TAG, "密码管理");
                break;
            case R.id.ll_log_management:
                Intent intent = new Intent(getMe(), LogActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_exit:
                //清除缓存的用户信息
                SpUtil.clear(getMe());
                Intent intent1 = new Intent(getMe(), LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_start_location_service:
                Intent intent2 = new Intent(getMe(), LocationService.class);
                startService(intent2);
                ToastUtil.showToast(getMe(),"开始上传");
                break;
            case R.id.ll_stop_location_service:
                Intent intent3 = new Intent(getMe(), LocationService.class);
                stopService(intent3);
                ToastUtil.showToast(getMe(),"结束上传");
                break;
        }
    }
}
