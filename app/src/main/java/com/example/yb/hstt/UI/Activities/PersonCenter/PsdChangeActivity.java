package com.example.yb.hstt.UI.Activities.PersonCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.LoginActivity;
import com.example.yb.hstt.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;

import net.grandcentrix.tray.AppPreferences;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class PsdChangeActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "PsdChangeActivity";

    @BindView(R.id.tb_change_psd)
    Toolbar tbChangePsd;
    @BindView(R.id.et_old_psd)
    EditText etOldPsd;
    @BindView(R.id.et_new_psd_a)
    EditText etNewPsdA;
    @BindView(R.id.et_new_psd_b)
    EditText etNewPsdB;
    @BindView(R.id.et_change_psd)
    Button etChangePsd;
    @BindView(R.id.activity_psd_change)
    RelativeLayout activityPsdChange;
    private String old_psd;
    private String new_psd_a;
    private String new_psd_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psd_change);
        ButterKnife.bind(this);
        tbChangePsd.setNavigationIcon(R.mipmap.ic_return);
        tbChangePsd.setNavigationOnClickListener(this);
        etChangePsd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.et_change_psd:

                final AppPreferences appPreferences = new AppPreferences(getMe());
                String token = appPreferences.getString("token_t", "");

                old_psd = etOldPsd.getText().toString();
                new_psd_a = etNewPsdA.getText().toString();
                new_psd_b = etNewPsdB.getText().toString();
                if (!TextUtils.isEmpty(old_psd)&&!TextUtils.isEmpty(new_psd_a)&&!TextUtils.isEmpty(new_psd_b)) {
                    if (new_psd_a.equals(new_psd_b)) {
                        if (new_psd_a.length()>=5 && new_psd_a.length()<=10) {
                            OkHttpUtils.post(GlobalUrl.ChangePsd)
                                    .params("token",token)
                                    .params("oldPassword",old_psd)
                                    .params("newPassword",new_psd_a)
                                    .execute(new CommonCallback<Map>(this,true) {
                                        @Override
                                        public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                                            Log.i(TAG,String.valueOf(map));
                                            int code = (int) map.get("code");
                                            if (code == 10000) {
                                                ToastUtil.showToast(getMe(),"修改密码成功");
                                                Intent intent = new Intent(getMe(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                ToastUtil.showToast(getMe(),String.valueOf(map.get("msg")));
                                            }
                                        }

                                        @Override
                                        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                                            super.onError(isFromCache, call, response, e);
                                            ToastUtil.showToast(getMe(),"密码修改失败" + String.valueOf(e) + "\n" + String.valueOf(response));
                                        }
                                    });
                        }else {
                            ToastUtil.showToast(getMe(),"密码长度应该为6");
                        }

                    }else {
                        ToastUtil.showToast(getMe(),"两次新密码应该相同");
                    }
                }else {
                    ToastUtil.showToast(getMe(),"所有选项均不能为空");
                }

                break;
        }
    }
}
