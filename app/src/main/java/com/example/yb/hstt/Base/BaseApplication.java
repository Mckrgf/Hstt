package com.example.yb.hstt.Base;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.example.yb.hstt.IM.LoginAsyncTask;
import com.example.yb.hstt.dao.DbOpenHelper;
import com.example.yb.hstt.zxing.activity.ZXingLibrary;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cookie.store.PersistentCookieStore;
import com.lzy.okhttputils.model.HttpHeaders;
import com.yixia.camera.VCamera;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by huangsf on 2016-09-09.
 */
public class BaseApplication extends Application {

    public static BaseApplication instances;
    public static LoginAsyncTask loginAsyncTask = null;
    public static XMPPTCPConnection connection = null;
    private static final String TAG = "BaseApplication";
    private static final String cache_path = Environment.getExternalStorageDirectory() + "/Hstt/mediacache";

    @Override
    public void onCreate() {
        super.onCreate();
        //兼容低版本，必须要初始化
        instances = this;
        // 初始化异常处理信息
        CrashHandler.getInstance().init(this);
        //初始化二维码扫描参数
        ZXingLibrary.initDisplayOpinion(this);
        //数据库初始化
        DbOpenHelper.getInstance(this).init();

        // 必须调用初始化
        //设置全局的网络通信参数
        HttpHeaders headers = new HttpHeaders();
        headers.setUserAgent("YDTYPT (Android)");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate");
        OkHttpUtils.init(this);
        OkHttpUtils.getInstance()//
//                .debug("OkHttp3")
                .setConnectTimeout(60000) // 全局的连接超时时间
                .setReadTimeOut(60000) // 全局的读取超时时间
                .setWriteTimeOut(60000) // 全局的写入超时时间
                .addCommonHeaders(headers)// 设置全局公共头
                .setCookieStore(new PersistentCookieStore());
        GlobalUrl.setBaseUrl();
        GlobalManager.initStatus();
        GlobalManager.initDeviceMap();

        //初始化地图控件
        SDKInitializer.initialize(getApplicationContext());

        File VIDEO_DIR = new File(cache_path);
        if (!VIDEO_DIR.exists()) {
            VIDEO_DIR.mkdirs();
        }
        VCamera.initialize(this);
        VCamera.setVideoCachePath(cache_path);

        //上传服务初始化
//        Intent calculateUpLoadService=new Intent(this, CommitPicService.class);
//        startService(calculateUpLoadService);
    }

    /**
     * 释放资源，退出程序时候调用
     */
    private final void release() {
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e != null) {
                Log.e(TAG, e.toString());
            } else {
                Log.e(TAG, "数据异常" + TAG);
            }
        }
    }

    /**
     * 程序退出
     *
     * @param code
     */
    public final void exit(int code) {
        release();
        System.exit(code);
    }

    /**
     * 实现android 程序退出
     */
    private List<Activity> mList = new LinkedList<Activity>();


    /**
     * 在BaseActivity的onCreate方法中调用 ，维护一个activity队列
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    /**
     * 在BaseActivity的onDestroy方法中调用 如果一个activity已经销毁了
     * 从队列中删除
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        mList.remove(activity);
    }

}
