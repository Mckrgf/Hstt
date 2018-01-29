package com.example.yb.hstt.Base;


import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.StatusUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.View.MyProgressDialog;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG ="RecorderBaseActivity";
    public Gson gson;
    public Logger logger = LoggerFactory.getLogger("loghr");
    public BaseApplication getMyApplication() {
        return (BaseApplication) this.getApplication();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson=new Gson();
        //记录当前的activity
        getMyApplication().addActivity(this);
        //设置布局
        setMyContentView(initLayoutId());
        //初始化butterknife注解
        ButterKnife.bind(this);
    }
    LinearLayout.LayoutParams params_match=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    LinearLayout.LayoutParams params_weigth1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1);
    protected void setMyContentView(int layoutid){
        View view= LayoutInflater.from(this).inflate(layoutid,null,false);

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setLayoutParams(params_match);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(view,params_weigth1);
        linearLayout.addView(StatusUtils.createBottomKeyLayoutView(this));

        setContentView(linearLayout);
    }
    protected abstract int initLayoutId();
    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         * android:configChanges="orientation|screenSize" 切屏不重走oncreate（）方法
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //自动调节输入法区域
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onResume();
    }

    /**
     * 使用Activity.this的地方统一使用getMe替代
     *
     * @return 当前上下文
     */
    public Context getMe() {
        return this;
    }
    /**
     * 根据手机版本控制view是否显示
     * @param view
     */
    public void setStatusBarView(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//如果系统不支持透明状态栏
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
    private MyProgressDialog mProgressDialog;
    /**
     * 显示加载进度dialog
     *
     * @param message
     * @param cancelable
     */
    protected void showDownloadProgress(String message, boolean cancelable, int Style) {
        if (null == mProgressDialog) {
            mProgressDialog = new MyProgressDialog(this, 0, message);
            mProgressDialog.setProgressStyle(Style);
            mProgressDialog.setProgressNumberFormat("%1s M/%2s M");
            mProgressDialog.setMax(100);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.getWindow().setAttributes(getLayoutParam(500,400));
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeDownload();
            }
        });
        mProgressDialog.show();
    }

    /**
     * 关闭下载
     */
    public void closeDownload(){

    }
    /**
     * 设置进度值
     * @param value
     */
    public void setDownloadProgressValue(int max,int value){
        if (null != mProgressDialog) {
            mProgressDialog.setMax(max);
            mProgressDialog.setProgress(value);
        }
    }

    /**
     * 关闭进度dialog
     */
    protected void dismissDownloadProgress() {
        if (null != mProgressDialog&&mProgressDialog.isShowing()) {
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
        dismissDownloadProgress();
        getMyApplication().removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pop_close_enter_anim, R.anim.pop_exit_anim);
    }

}

