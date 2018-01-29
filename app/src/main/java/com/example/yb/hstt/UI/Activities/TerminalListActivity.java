package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.andview.refreshview.XRefreshView;
import com.example.yb.hstt.Adpater.Recycle_terminal_list_adappter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback2;
import com.example.yb.hstt.R;
import com.example.yb.hstt.View.ListViewHeaderView;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class TerminalListActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.rc_function)
    RecyclerView rcFunction;//task_commit_list
    @BindView(R.id.terminal_list_toolbar)
    Toolbar terminalListToolbar;
    @BindView(R.id.bt_add_terminal)
    Button btAddTerminal;
    @BindView(R.id.terminal_list_header)
    XRefreshView terminalListHeader;
    private Recycle_terminal_list_adappter adapter;
    private ArrayList terminals;
    private int click_pos;//上次点击的某个条目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_list);
        ButterKnife.bind(this);

        btAddTerminal.setOnClickListener(this);

        terminalListToolbar.setNavigationIcon(R.mipmap.ic_return);
        terminalListToolbar.setNavigationOnClickListener(this);
        //设置布局类型
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.VERTICAL, false);
        rcFunction.setLayoutManager(manager);
        //设置按钮列表数据
        adapter = new Recycle_terminal_list_adappter(this);
        //列表长按删除的点击事件
        adapter.setOnItemLongClickListener(new Recycle_terminal_list_adappter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                //操作逻辑在适配器里,只需要声明即可
            }
        });
        adapter.setOnItemClickListener(new Recycle_terminal_list_adappter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                click_pos = position;

                Intent intent = new Intent(getMe(), TerminalDetailActivity.class);

                HashMap map = (HashMap) terminals.get(position);
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = String.valueOf(entry.getKey());
                    Object val = entry.getValue();
                    String val_null = null;
                    String val_a = String.valueOf(val);
                    if (null==val_a) {
                        String val_s = String.valueOf("数据获取失败");
                        map.put(key, val_s);
                    }

                    if (val_a.equals("null")) {
                        String val_s = String.valueOf("数据获取失败");
                        map.put(key, val_s);
                    }
                }

                intent.putExtra("info", map);
                intent.putExtra("if_edit", true);//点击列表进入,不需要编辑
                startActivity(intent);
            }
        });
        initView();
    }

    private void initView() {
        terminalListHeader.setPullLoadEnable(false);
        terminalListHeader.setCustomHeaderView(new ListViewHeaderView(this));
        terminalListHeader.setDampingRatio(3.6f);
        terminalListHeader.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                initData();
            }
        });
    }

    private static final String TAG = "TerminalListActivity";

    private void initData() {
        Map<String, Object> para = new HashMap<>();
        OkHttpUtils.post(GlobalManager.BaseUrl).execute(new CommonCallback2(getMe(), "010102", para, true) {

            @Override
            public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                try {
                    HashMap result = (HashMap) CommonCallback2.parseGson_map(s);
                    String code = (String) result.get("code");
                    if (null != code && code.equals(SUCCESS_CODE)) {
                        Log.i(TAG, "获取终端列表成功");
                        terminals = (ArrayList) result.get("list");
                        adapter.setData(terminals);
                        rcFunction.setAdapter(adapter);
                        if (click_pos<terminals.size()) {
//                            rcFunction.smoothScrollToPosition(click_pos);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "转换json失败" + e.toString());
                }
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, s, call, response, e);
                terminalListHeader.stopRefresh();
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                Log.e(TAG, "获取终端列表失败" + e.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.bt_add_terminal:
                Intent intent = new Intent(getMe(), AddTerminalActivity.class);
                startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
