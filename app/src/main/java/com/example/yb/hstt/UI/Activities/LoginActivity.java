package com.example.yb.hstt.UI.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.lzy.okhttputils.request.BaseRequest;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends HsttBaseActivity implements View.OnClickListener {
    public static final String TAG = "LoginActivity";
    public static final String LoginMethod = "010001";
    private static final String servicetag = "DownloadTag";

    @BindView(R.id.et_login_phonenumber)
    EditText etLoginPhonenumber;            //账号
    @BindView(R.id.et_login_sms_verify)
    EditText etLoginSmsVerify;              //密码
    @BindView(R.id.bt_login_tologin)
    Button bt_login_tologin;                //登录

//    private AlertDialog NewVersiondialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //自动添加用户名和密码
        String phoneNo = (String) SpUtil.get(this, "phoneNo", "");
        etLoginPhonenumber.setText(phoneNo);
        String phonePsD = (String) SpUtil.get(this, "phonePsD", "");
        etLoginSmsVerify.setText(phonePsD);

        //登录按钮点击
        bt_login_tologin.setOnClickListener(this);

        //密码输入完成后点击回车直接登录
        etLoginSmsVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i==KeyEvent.ACTION_DOWN) {
                    Go_Login();
                }
                return false;
            }
        });
    }

    private void saveNumber2SP() {
        String phoneNo = etLoginPhonenumber.getText().toString();
        SpUtil.put(this, "phoneNo", phoneNo);
        String phonePsD = etLoginSmsVerify.getText().toString();
        SpUtil.put(this, "phonePsD", phonePsD);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_tologin:
                Go_Login();
                break;
        }
    }

//    /**
//     * 检测版本
//     */
//    private void CheckVersion() {
//        OkHttpUtils.get("http://47.93.159.225/apk/cte/version.json").execute(new StringCallback() {
//            @Override
//            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
//                final VersionEntity entity = gson.fromJson(s, VersionEntity.class);
//                //获取本地的缓存的版本
//                int local_cache_appVersion = (int) SpUtil.get(getMe(), "Local_Cache_AppVersion", -1);
//                //大于本地缓存的版本，且大于app应用的版本
//                if (entity.getVersion() > local_cache_appVersion && entity.getVersion() > AppUtils.getVersionCode(getBaseContext())) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getMe());
//                    builder.setMessage("有新版本[" + entity.getVersion() + "]，是否更新？")
//                            .setNegativeButton("取消", null)
//                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //存一下服务端的版本号
//                                    SpUtil.put(getMe(), "Local_Cache_AppVersion", entity.getVersion());
//                                    updateDownLoad(entity.getMemo(), entity.getFile_path());
//                                }
//                            });
//                    NewVersiondialog = builder.create();
////                    NewVersiondialog.getWindow().setAttributes(getLayoutParam(500,400));
//                    NewVersiondialog.show();
//                }
//            }
//
//        });
//    }
//
//    /**
//     * 下载apk
//     *
//     * @param message 信息
//     * @param downpath 路径
//     */
//    private void updateDownLoad(final String message, String downpath) {
//        OkHttpUtils.get(downpath).tag(servicetag).execute(new FileCallback(GlobalManager.DownFilepath, "homeauto.apk") {
//            @Override
//            public void onBefore(BaseRequest request) {
//                showDownloadProgress(message + "\n下载中...", true, MyProgressDialog.STYLE_HORIZONTAL);
//            }
//
//            @Override
//            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
//                int progress_value = (int) (progress * totalSize);
//                int total_value = (int) totalSize;
//                setDownloadProgressValue(total_value, progress_value);
//            }
//
//            @Override
//            public void onResponse(boolean b, File file, Request request, @Nullable Response response) {
//                if (file != null) {
//                    FileUtils.OpenAPK(getBaseContext(), file.getAbsolutePath());
//                }
//            }
//
//            @Override
//            public void onAfter(boolean isFromCache, @Nullable File file, Call call, @Nullable Response response, @Nullable Exception e) {
//                dismissDownloadProgress();
//            }
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void closeDownload() {
        super.closeDownload();
        //取消网络加载
        OkHttpUtils.getInstance().cancelTag(servicetag);
//        ToastUtil.showToast(this, "下载关闭了！");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果弹框还在-关闭弹框
        dismissProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (NewVersiondialog != null && NewVersiondialog.isShowing()) {
//            NewVersiondialog.dismiss();
//        }
    }

    /**
     * 去登陆
     */
    private void Go_Login() {
        boolean permisson_granted = AppUtils.selfPermissionGranted(getMe(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permisson_granted) {
            Log.i(TAG, "权限已经授予");
            final String phoneNo = etLoginPhonenumber.getText().toString().replace(" ", "");
            final String bornCode = etLoginSmsVerify.getText().toString();
            if (!TextUtils.isEmpty(phoneNo) && !TextUtils.isEmpty(bornCode)) {
                Map<String, String> para = new HashMap<>();
                para.put("username", phoneNo);
                para.put("password", bornCode);
                JSONObject jsonObject = new JSONObject(para);
                OkHttpUtils.post(GlobalManager.Login_url).postJson(jsonObject.toString()).execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        showProgress("登录中。。。");
                    }

                    @Override
                    public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                        try {
                            Log.i(TAG, "Go_Login: " + s);
                            //添加header(token)
                            Map map_token = CommonCallback2.parseGson_map(s);
                            if (null != map_token) {
                                String token = (String) map_token.get("token");
                                if (null == token) {
                                    ToastUtil.showToast(getMe(), "登录失败,请检查用户名/密码");
                                } else {
                                    SpUtil.put(getMe(), "token", token);
                                    if (!TextUtils.isEmpty(token)) {
                                        final AppPreferences appPreferences = new AppPreferences(getMe());
                                        appPreferences.clear();
                                        appPreferences.put("token_t", token);
                                        //登录成功,sp存用户信息
                                        saveNumber2SP();
                                        Log.i(TAG, "设备token: " + token);
                                        HttpHeaders httpHeaders = new HttpHeaders();
                                        httpHeaders.put("Authorization", token);
                                        OkHttpUtils.getInstance().addCommonHeaders(httpHeaders);

                                        getUserInfo();
                                    } else {
                                        ToastUtil.showToast(getMe(), "登录失败,请检查用户名/密码");
                                    }
                                }
                            } else {
                                ToastUtil.showToast(getMe(), "后台数据解析错误");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (null != e && null != response)
                            Log.i(TAG, e.toString() + "/n" + response.toString());
                    }

                    @Override
                    public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onAfter(isFromCache, s, call, response, e);
                        dismissProgress();
                    }
                });
            } else {
                ToastUtil.showToast(getMe(), "用户名和密码不能为空");
            }
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
                    logininfo.setUser_phone(mobilePhone);
                    logininfo.setUser_org_name(orgName);
                    LoginInfoDbHelper.getInstance(getMe()).insert(logininfo);
                    Intent intent = new Intent(getMe(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                if (null != e && null != response)
                    Log.i(TAG, e.toString() + "/n" + response.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        getMyApplication().exit(0);
    }

}
