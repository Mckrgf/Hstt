package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.andview.refreshview.XRefreshView;
import com.example.yb.hstt.Adpater.Recycle_device_list_adappter;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;
import com.example.yb.hstt.View.ListViewHeaderView;
import com.example.yb.hstt.dao.DbHelper.UserInfoDbHelper;
import com.example.yb.hstt.dao.bean.UserInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.rc_function)
    RecyclerView rcFunction;//task_commit_list
    @BindView(R.id.device_list_toolbar)
    Toolbar deviceListToolbar;
    @BindView(R.id.bt_add_device)
    Button btAddDevice;
    @BindView(R.id.user_list_header)
    XRefreshView userListHeader;
    private Recycle_device_list_adappter adapter;
    private List<UserInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);
        btAddDevice.setOnClickListener(this);

        deviceListToolbar.setNavigationIcon(R.mipmap.ic_return);
        deviceListToolbar.setNavigationOnClickListener(this);
        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.VERTICAL, false);
        rcFunction.setLayoutManager(manager);
        //设置按钮列表数据
        adapter = new Recycle_device_list_adappter(this);
        //列表长按删除的点击事件
        adapter.setOnItemLongClickListener(new Recycle_device_list_adappter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                //操作逻辑在适配器里,只需要声明即可
            }
        });
        adapter.setOnItemClickListener(new Recycle_device_list_adappter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getMe(), AddUserActivity.class);
                intent.putExtra("user_info", (Serializable) infos.get(position));
                intent.putExtra("AddDevice", false);//用户详情
                startActivity(intent);
            }
        });
        initView();
    }

    private void initView() {
        userListHeader.setPullLoadEnable(false);
        userListHeader.setCustomHeaderView(new ListViewHeaderView(this));
        userListHeader.setDampingRatio(3.6f);
        userListHeader.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                initData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private static final String TAG = "DeviceListActivity";


    /**
     * 获取所有用户信息(本地数据库)
     */
    private void initData() {
        infos = UserInfoDbHelper.getInstance(getMe()).getAllUser();
        adapter.setData(infos);
        rcFunction.setAdapter(adapter);
        userListHeader.stopRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.bt_add_device:
                //添加用户(同时可添加设备)
                Intent intent = new Intent(getMe(), AddUserActivity.class);
                intent.putExtra("AddDevice", true);//添加用户
                startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
