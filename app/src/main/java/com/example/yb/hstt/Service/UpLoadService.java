package com.example.yb.hstt.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.Utils.NetUtils;
import com.example.yb.hstt.dao.DbHelper.DeviceInfosDbHelper;
import com.example.yb.hstt.dao.bean.DeviceInfos;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.yb.hstt.Base.HsttBaseActivity.SUCCESS_CODE;

/**
 * Created by tfhr on 2017/11/23.
 */

public class UpLoadService extends Service {

    private DeviceInfosDbHelper helper;
    private List<DeviceInfos> files;

    public volatile boolean exit = false;//线程是否继续的标志

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String TAG = "UpLoadService";

    @Override
    public void onCreate() {
        super.onCreate();
        final Thread upload_file_t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exit) {

                    try {
                        Thread.sleep(4000);

                        helper = DeviceInfosDbHelper.getInstance(getApplicationContext());
                        files = helper.select_need_to_upload();

                        //先判断是否有需要上传的文件
                        if (files.size()!=0) {
                            //有就走上传流程
                            upload_step();
                        }else {
                            //没有就关闭服务
                            Log.i(TAG,"关闭服务");
                            stopServiceDestoryThread();

                        }



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        upload_file_t.start();
    }

    private void stopServiceDestoryThread() {
        //关闭服务的同时要结束该线程
        stopService(new Intent(getApplicationContext(),UpLoadService.class));
        exit = true;
    }

    private void upload_step() {
        boolean upload_select = GlobalManager.upload_select;

        if (upload_select) {
            Log.i(TAG, "用户设置为wifi上传");
            boolean isWIFI = NetUtils.isWifi(UpLoadService.this);//判断当前是否为wifi,true为wifi
            boolean has_net = NetUtils.isConnected(UpLoadService.this);//判断是否有网
            if (isWIFI && has_net) {
                //是wifi,上传
                Log.i(TAG, "手机状态为wifi,开始循环上传未上传的图片");
                post_file();
            } else {
                //不是wifi,不上传
                stopServiceDestoryThread();
                Log.i(TAG, "手机状态不是wifi,不上传");
            }
        } else {
            //false为数据流量
            Log.i(TAG, "用户设置为数据流量上传");
            boolean has_net = NetUtils.isConnected(UpLoadService.this);
            if (has_net) {
                //有网络
                Log.i(TAG, "手机状态为数据流量并且有网络,开始循环上传未上传的图片");
                post_file();
            } else {
                //无网络
                Log.i(TAG, "手机状态,没网络,不上传");
                stopService(new Intent(getApplicationContext(),UpLoadService.class));
                exit = true;
            }
        }
    }

    /**
     * 上传文件(图片)
     */
    private void post_file() {

        for (int i = 0; i < files.size(); i++) {
            final DeviceInfos info = files.get(i);
            String img_url = info.getImg_url();
            img_url = img_url.replace("file://", "");
            final File file = new File(img_url);
            long size = file.length();
            Log.i(TAG, "图片大小:" + size);
            final int pos = i;
            OkHttpUtils.post(GlobalManager.uploadFileuUrl)
                    .params("file", file)
                    .execute(new CommonCallback2(this, "upload", null) {

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            info.setState(1);
                        }

                        @Override
                        public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                            try {
                                HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                                String code = (String) result.get("code");
                                if (null != code && code.equals(SUCCESS_CODE)) {
                                    Log.i(TAG, "图片上传成功"+file.getName());
//                                    String img_url = (String) result.get("file_url");//图片url
//                                    info.setImg_url(img_url);//重新设置路径为网络路径
                                    info.setState(2);
                                    helper.updateInfo(info);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "转换json失败" + e.toString());
                            }
                        }

                        @Override
                        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                            super.onError(isFromCache, call, response, e);
                            if (null != e) Log.e(TAG, e.toString());
                            Log.e(TAG, "图片上传失败");
                            info.setState(3);
                        }

                        @Override
                        public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                            super.onAfter(isFromCache, s, call, response, e);
                            if (pos == files.size() - 1) {
                                stopService(new Intent(getApplicationContext(),UpLoadService.class));
                                exit = true;
                            }
                        }
                    });
        }
    }
}
