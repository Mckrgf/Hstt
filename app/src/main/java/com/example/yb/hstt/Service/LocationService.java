package com.example.yb.hstt.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.MainActivity;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusThirdActivity;
import com.example.yb.hstt.Utils.SpUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.model.HttpParams;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;

import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.getActivity;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private Thread thread;
    private LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();
    private int time_interval = 30 * 1000; //30s,正常退出app.,变成低频没问题,如果是非正常退出,时间间隔还是高频率,所以用如下参数newway
    private int time_interval_new_way = 30 * 1000; //30s
    private String owrdpId;
    private String owwoId;
    private LocationClientOption option;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences preferences = new AppPreferences(this);

        try {

            //刚开启服务的时候要清除一下工单编号和过程id,因为之前上报过的会持久化,用户再进来的时候
            //不一定就是原来的工单编号和过程id了,所以不能用原来存的,要清除一下,等待用户点击任务,持久化两个id后
            //再调用获取两个id,即最新的

            time_interval = preferences.getInt("time_interval");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
        }


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "位置服务开启");
                try {
                    setLocationOption();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }

    /**
     * 初始化位置设置
     */
    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);

        option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(time_interval_new_way);

        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        Log.i(TAG, "开始定位");
    }

    /**
     * 上传位置服务
     */
    private void upLoadLocation(double lat, double lng) {

        AppPreferences appPreferences = new AppPreferences(this);
        final String token = appPreferences.getString("token_t", "");

        Log.i(TAG, "上传服务中token:" + token);
        if (!TextUtils.isEmpty(token)) {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.put("Authorization", token);
            OkHttpUtils.getInstance().addCommonHeaders(httpHeaders);
            Log.i(TAG, "token加载成功");

            //全局变量获取工单编号/进度编号
            try {
                owwoId = appPreferences.getString("owwoId");
                owrdpId = appPreferences.getString("owrdpId");
            } catch (Exception e) {
//                e.printStackTrace();
            }

            //发送请求
            HttpParams params = new HttpParams();
            if (null != owwoId) {
                params.put("owwoId", owwoId + "");
            }
            if (null != owrdpId) {
                params.put("owrdpId", owrdpId + "");
            }
            params.put("trackLng", lng + "");
            params.put("trackLat", lat + "");
            Log.i(TAG, params.toString());
            OkHttpUtils.post(GlobalUrl.location_upload)
                    .params(params)
                    .execute(new CommonCallback<Map>(this) {
                        @Override
                        public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                            Log.i(TAG, "上传位置成功");
                        }

                        @Override
                        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                            super.onError(isFromCache, call, response, e);
                            Log.i(TAG, "上传位置失败");
                            if (null != response) {
                                Log.i(TAG, response.toString());
                            }
                            if (null != e) {
                                Log.i(TAG, e.toString());
                            }
                        }
                    });
        } else {
            Log.i(TAG, "Token过期无法上传");
            stopSelf();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_setting)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("煤改电正在运行") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.app_icon) // 设置状态栏内的小图标
                .setContentText("定位中") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "位置服务关闭");
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        thread.interrupt();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String x_s = location.getLatitude() + "";
            if (!x_s.contains("4.9")) {
                //坐标正常,用户才可以进行下一步操作
                Log.i(TAG, "位置坐标获取成功,开始执行上传位置");
                upLoadLocation(lat, lon);

            } else {
                Log.i(TAG, "位置坐标获取失败");
            }
            int app_status = getAppSatus(LocationService.this, "com.example.yb.hstt");
            if (app_status == 3) {
                option.setScanSpan(60*1000);
                mLocationClient.setLocOption(option);
            } else if (app_status == 2) {
                option.setScanSpan(30*1000);
                mLocationClient.setLocOption(option);
            } else {
                option.setScanSpan(30*1000);
                mLocationClient.setLocOption(option);
            }
            Log.i(TAG, "程序运行状态: " + app_status + "定位频率" + option.getScanSpan());

        }
    }

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    private int getAppSatus(Context context, String pageName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);

        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;//栈里找不到，返回3
        }
    }

}
