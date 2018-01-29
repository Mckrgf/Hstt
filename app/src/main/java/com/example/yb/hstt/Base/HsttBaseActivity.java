package com.example.yb.hstt.Base;


import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.NetBroadcastReceiver;
import com.example.yb.hstt.Utils.NetUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.MyProgressDialog;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HsttBaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvevt {
    public Gson gson;
    public Logger logger = LoggerFactory.getLogger("loghr");
    public static String SUCCESS_CODE = "10000";

    public static NetBroadcastReceiver.NetEvevt netEvevt;
    /**
     * 网络类型
     */
    private int netMobile;

    public BaseApplication getMyApplication() {
        Application application = getApplication();
        return (BaseApplication) application;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netEvevt = this;
        initNetType();
        gson = new Gson();
        //记录当前的activity
        getMyApplication().addActivity(this);

        //判断sdk，大于等于5.0时，设置状态栏为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private boolean initNetType() {
        this.netMobile = NetUtils.getNetWorkState(this);
        return isNetConnect(this.netMobile);
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect(int state) {
        if (state == 1) {//wifi
            return true;
        } else if (state == 0) {//mobile
            return true;
        } else if (state == -1) {//没网
            return false;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        int requestedOrientation = getRequestedOrientation();
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //每次都要判断是否打开了GPS
        initGPS();
    }

    /**
     * 使用Activity.this的地方统一使用getMe替代
     *
     * @return 当前上下文
     */
    public Context getMe() {
        return this;
    }

    private MyProgressDialog mProgressDialog;

    /**
     * 显示加载进度dialog
     *
     * @param message
     */
    public void showDownloadProgress(String message) {
        showProgress(message, MyProgressDialog.STYLE_HORIZONTAL);
    }

    public void showProgress(String message) {
        showProgress(message, MyProgressDialog.STYLE_SPINNER);
    }

    private void showProgress(String message, int Style) {
        if (null == mProgressDialog) {
            mProgressDialog = new MyProgressDialog(this, 0, message);
            mProgressDialog.setProgressStyle(Style);
            mProgressDialog.setProgressNumberFormat("%1s M/%2s M");
            mProgressDialog.setMax(100);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.getWindow().setAttributes(getLayoutParam(500, 400));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeDownload();
            }
        });
        mProgressDialog.show();
    }

    ;


    /**
     * 关闭下载
     */
    public void closeDownload() {

    }

    /**
     * 设置进度值
     *
     * @param value
     */
    public void setDownloadProgressValue(int max, int value) {
        if (null != mProgressDialog) {
            mProgressDialog.setMax(max);
            mProgressDialog.setProgress(value);
        }
    }

    /**
     * 关闭进度dialog
     */
    protected void dismissProgress() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public WindowManager.LayoutParams getLayoutParam(int height, int width) {
        WindowManager.LayoutParams wmParam = null;
        if (wmParam == null) {
            //为windowManager.layoutParams添加type和flag,这样发送广播才不会出现token is null这样的错误
            wmParam = new WindowManager.LayoutParams(-1, 4);
            wmParam.windowAnimations = 0;
            wmParam.format = PixelFormat.TRANSLUCENT
                    | WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;

        }
        wmParam.height = height;
        wmParam.width = width;
        return wmParam;
    }

    /**
     * 基础提示框
     *
     * @param message
     */
    public void showMesage(String message) {
        ToastUtil.showToast(this, message);
    }

    /**
     * destroy从列表中删除activity
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        dismissProgress();
        getMyApplication().removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pop_close_enter_anim, R.anim.pop_exit_anim);
    }

    @Override
    public void onNetChange(int netMobile) {
        if (this.netMobile==netMobile){
            return;
        }
        this.netMobile = netMobile;
        boolean netConnect = isNetConnect(netMobile);
        String falseTag = "当前网络不可用，请检查你的网络设置！";
        String trueTag = "网络已恢复！";
        ToastUtil.showToast(getMe(),netConnect?trueTag:falseTag);
    }

    private static final String TAG = "HsttBaseActivity";

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为方便操作任务，请先打开GPS");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(getMe(), "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.show();
        } else {
            Log.i(TAG,"GPS是开着的");
        }
    }


}

