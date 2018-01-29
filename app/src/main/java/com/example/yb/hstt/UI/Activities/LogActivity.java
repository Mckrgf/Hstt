package com.example.yb.hstt.UI.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.yb.hstt.Adpater.Recycle_log_list_adappter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogActivity extends HsttBaseActivity implements View.OnClickListener {


    @BindView(R.id.log_toolbar)
    Toolbar logToolbar;
    @BindView(R.id.rc_function)
    RecyclerView rcFunction;
    @BindView(R.id.activity_log)
    RelativeLayout activityLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        logToolbar.setNavigationIcon(R.mipmap.ic_return);
        logToolbar.setNavigationOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getMe(),LinearLayoutManager.VERTICAL,false);
        rcFunction.setLayoutManager(linearLayoutManager);
        Recycle_log_list_adappter adappter = new Recycle_log_list_adappter(getMe(), GlobalManager.globle_logs);
        rcFunction.setAdapter(adappter);
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
