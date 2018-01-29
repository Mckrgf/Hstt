package com.example.yb.hstt.UI.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.SpUtil;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.dao.DbHelper.LoginInfoDbHelper;
import com.example.yb.hstt.dao.bean.LoginInfo;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpHeaders;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends HsttBaseActivity {

    private String phoneNo;
    private String phonePsD;
    boolean b = false;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        b = !b;
        Log.i(TAG, "页面加载完毕,b为" + b);
        if (b) {
            try {
                boolean permisson_granted = AppUtils.selfPermissionGranted(getMe(), Manifest.permission.ACCESS_COARSE_LOCATION);
                if (!permisson_granted) {
                    //如果权限没给,直接跳转登录页面
                    Intent intent = new Intent(getMe(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //权限给了,走自动登录
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            phoneNo = (String) SpUtil.get(getMe(), "phoneNo", "");
                            phonePsD = (String) SpUtil.get(getMe(), "phonePsD", "");
                            if (TextUtils.isEmpty(phoneNo) || TextUtils.isEmpty(phonePsD)) {
                                Intent intent = new Intent(getMe(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //                setLocationListener();
                                Go_Login();
                            }
                        }
                    }, 1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 去登陆
     */
    private void Go_Login() {
        boolean permisson_granted = AppUtils.selfPermissionGranted(getMe(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permisson_granted) {
            Map<String, String> para = new HashMap<>();
            para.put("username", phoneNo);
            para.put("password", phonePsD);
            JSONObject jsonObject = new JSONObject(para);
            Log.i(TAG, "开始登陆");
            OkHttpUtils.post(GlobalManager.Login_url).postJson(jsonObject.toString()).execute(new StringCallback() {
                @Override
                public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                    try {
                        Log.i(TAG, "Go_Login: " + s);
                        //添加header(token)
                        Map map_token = CommonCallback2.parseGson_map(s);
                        if (null != map_token) {
                            String token = (String) map_token.get("token");

                            SpUtil.put(getMe(), "token", token);
                            Log.i(TAG, "持久化token" + token);

                            final AppPreferences appPreferences = new AppPreferences(getMe());
                            appPreferences.clear();
                            appPreferences.put("token_t", token);


                            if (null != token) {
                                Log.i(TAG, "设备token: " + token);
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.put("Authorization", token);
                                OkHttpUtils.getInstance().addCommonHeaders(httpHeaders);

                                getUserInfo();

                            } else {
                                int err_code = (int) map_token.get("status");
                                ToastUtil.showToast(getMe(), "登录失败,错误码: " + err_code);
                                //登录失败,跳转登录页面
                                Intent intent = new Intent(getMe(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }else {
                                ToastUtil.showToast(getMe(), "后台数据解析错误");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        //登录失败,跳转登录页面
                        Intent intent = new Intent(getMe(), LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                    super.onError(isFromCache, call, response, e);
                    if (null != e && null != response) {
                        Log.i(TAG, e.toString() + "/n" + response.toString());
                    }
                    Log.i(TAG, "登陆失败");
                    //登录失败,跳转登录页面
                    Intent intent = new Intent(getMe(), LoginActivity.class);
                    startActivity(intent);

                }
            });
        } else {
            Log.i(TAG, "权限未授予");
            ToastUtil.showToast(getApplicationContext(), "请打开定位权限");
        }

    }

    private void getUserInfo() {
        OkHttpUtils.post(GlobalManager.Get_UserInfo).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map s, Request request, @Nullable Response response) {
                try {
                    Log.i(TAG, "getUserInfo: " + s);
                    String id = (String) s.get("id");
                    String name = (String) s.get("name");
                    String mobilePhone = (String) s.get("mobilePhone");
                    String employeeNumber = (String) s.get("employeeNumber");
                    String orgName = (String) s.get("orgName");
                    LoginInfo logininfo = new LoginInfo();
                    logininfo.setId(id);
                    logininfo.setUser_name(name);
                    logininfo.setJob_number(employeeNumber);
                    logininfo.setJob_number(employeeNumber);
                    logininfo.setUser_phone(mobilePhone);
                    logininfo.setUser_org_name(orgName);
                    LoginInfoDbHelper.getInstance(getMe()).insert(logininfo);
                    Intent intent = new Intent(getMe(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    //登录失败,跳转登录页面
                    Intent intent = new Intent(getMe(), LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                if (null != e && null != response)
                    Log.i(TAG, e.toString() + "/n" + response.toString());
                //登录失败,跳转登录页面
                Intent intent = new Intent(getMe(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
