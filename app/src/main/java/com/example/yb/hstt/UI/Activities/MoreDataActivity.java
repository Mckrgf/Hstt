package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class MoreDataActivity extends HsttBaseActivity implements View.OnClickListener {


    @BindView(R.id.terminal_more_toolbar)
    Toolbar terminalMoreToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_data);


        ButterKnife.bind(this);
        terminalMoreToolbar.setNavigationIcon(R.mipmap.ic_return);
        terminalMoreToolbar.setNavigationOnClickListener(this);
        Intent intent = getIntent();
        HashMap device = (HashMap) intent.getSerializableExtra("device");
        initData();
    }

    private static final String TAG = "MoreDataActivity";

    private void initData() {
        Map<String, Object> para = new HashMap<>();
        para.put("data_type", "01");
        para.put("asset_no", "BJJNJ3170806089105");
        para.put("fn", "" + "2");
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010403", para) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap map = (HashMap) CommonCallback2.parseGson_map(s);
                    String msg = (String) map.get("msg");
                    if (TextUtils.isEmpty(msg)) {

                        finish();
                    } else {
                        String code = (String) map.get("code");
                        ToastUtil.showToast(getMe(), "错误码" + code + ": " + msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.i(TAG, e.toString() + "/n" + response.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
