package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.View.PullDownRefresh_ListView;
import com.example.yb.hstt.View.SlidingLib.OrderInfoList;
import com.example.yb.hstt.View.SlidingLib.SlideListAdapter;
import com.example.yb.hstt.View.SlidingLib.SlidingContentView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class TheOrderFinishedListActivity extends HsttBaseActivity implements View.OnClickListener {
    public static final String TAG = "TheOrderFinishedListActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_theOrder_FinishedList)
    Toolbar tb_theOrder_FinishedList;
    @BindView(R.id.lv_theOrder_FinishedList)
    PullDownRefresh_ListView lv_theOrder_FinishedList;

    List<OrderInfoList> lists;
    SlideListAdapter adapter;
    /**
     * 外勤工单ID
     */
    private String owwoId;
    private HashMap task_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_order_finished_list);
        ButterKnife.bind(this);
        owwoId = getIntent().getStringExtra("owwoId");
        setSupportActionBar(tb_theOrder_FinishedList);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb_theOrder_FinishedList.setNavigationOnClickListener(this);
        lv_theOrder_FinishedList.setonRefreshListener(new PullDownRefresh_ListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                selectOrderFinishedInfo();
            }
        });
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        lv_theOrder_FinishedList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //正在滑动，立马将之前的已打开的视图关闭
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    adapter.getSlideManager().closeAllLayout();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        lists = new ArrayList<>();
        adapter = new SlideListAdapter(this, lists, new SlideListAdapter.onFunctionListener() {
            @Override
            public void delete(int pos) {//删除
                Log.i(TAG, "pos"+pos+",lists: "+lists.size());
                OrderInfoList orderInfoList = lists.get(pos);
                String status = orderInfoList.getStatus();
                String cd_id = orderInfoList.getCd_id();
                deleteOrderListItem(pos,status,cd_id);
            }

            @Override
            public void edit(int pos) {//编辑
                OrderInfoList orderInfoList = lists.get(pos);
                String status = orderInfoList.getStatus();
                if (status.equals("已换")){
                    Intent changeIntent=new Intent(TheOrderFinishedListActivity.this,ChangeDeviceActivity.class);
                    changeIntent.putExtra("owwoId",owwoId);
                    changeIntent.putExtra("cd_id",orderInfoList.getCd_id());
                    changeIntent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                    changeIntent.putExtra("is_Edit",true);
                    startActivity(changeIntent);
                }else{
                    Intent repairIntent=new Intent(TheOrderFinishedListActivity.this,RepairDeviceActivity.class);
                    repairIntent.putExtra("owwoId",owwoId);
                    repairIntent.putExtra("cd_id",orderInfoList.getCd_id());
                    repairIntent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                    repairIntent.putExtra("is_Edit",true);
                    startActivity(repairIntent);
                }
            }
        });
        lv_theOrder_FinishedList.setAdapter(adapter);
        adapter.setOnItemClickListener(new SlidingContentView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                OrderInfoList orderInfoList = lists.get(position);
                String status = orderInfoList.getStatus();
                if (status.equals("已换")){
                    Intent changeIntent=new Intent(TheOrderFinishedListActivity.this,ChangeDeviceDetailActivity.class);
                    changeIntent.putExtra("cd_id",orderInfoList.getCd_id());
                    startActivity(changeIntent);
                }else{
                    Intent repairIntent=new Intent(TheOrderFinishedListActivity.this,RepairDeviceDetailActivity.class);
                    repairIntent.putExtra("cd_id",orderInfoList.getCd_id());
                    startActivity(repairIntent);
                }
            }
        });
        selectOrderFinishedInfo();
    }

    /**
     * 查询已修或者已换列表
     */
    private void selectOrderFinishedInfo(){
        HttpParams params=new HttpParams();
        params.put("owwoId",owwoId);
        OkHttpUtils.get(GlobalUrl.TheOrder_FinishedInfo).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                lists.clear();
            }
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "onResponse: "+map);
                String code = map.get("code").toString();
                if (code.equals("10000")){
                    Map data = (Map) map.get("data");
                    List<Map> maintainInfo = (List<Map>) data.get("maintainInfo");
                    for (Map repair:maintainInfo){//維修
                        String devManufacturerNo = String.valueOf(repair.get("devManufacturerNo"));
                        String devTypeNo = String.valueOf(repair.get("devTypeNo"));
                        String cdmId = String.valueOf(repair.get("cdmId"));
                        int fileCnt=Integer.parseInt(repair.get("fileCnt").toString());
                        lists.add(new OrderInfoList(R.drawable.ic_repair_tag,devTypeNo,devManufacturerNo,cdmId,"已修",R.color.tv_text_yellow,fileCnt>0?View.VISIBLE:View.INVISIBLE));
                    }
                    List<Map> changeInfo = (List<Map>) data.get("changeInfo");
                    for (Map change:changeInfo){//已换
                        String devManufacturerNo = String.valueOf(change.get("devManufacturerNo"));
                        String devTypeNo = String.valueOf(change.get("devTypeNo"));
                        String cdcId = String.valueOf(change.get("cdcId"));
                        int fileCnt=Integer.parseInt(change.get("fileCnt").toString());
                        lists.add(new OrderInfoList(R.drawable.ic_change_tag,devTypeNo,devManufacturerNo,cdcId,"已换",R.color.tv_text_red,fileCnt>0?View.VISIBLE:View.INVISIBLE));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable Map s, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, s, call, response, e);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv_theOrder_FinishedList.onRefreshComplete();
                    }
                },1500);
            }
        });
    }

    /**
     * 删除工单列表某项
     * @param status 表的状态
     * @param cd_id 设备更换（维修）表ID
     */
    private void deleteOrderListItem(final int pos, String status, String cd_id){
        String url="",key="";
        if (status.equals("已换")){
            url=GlobalUrl.TheOrder_FinishedChangeDelete;
            key="cdcId";
        }else{
            url=GlobalUrl.TheOrder_FinishedRepairDelete;
            key="cdmId";
        }
         OkHttpUtils.post(url).params(key,cd_id).execute(new CommonCallback<Map>(getMe(),"删除中...") {
             @Override
             public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                 if (map.get("code").toString().equals("10000")){
                     lists.remove(pos);
                     adapter.notifyDataSetChanged();
//                     selectOrderFinishedInfo();
                 }
             }
         });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case -1:
                finish();
                break;
        }
    }
}
