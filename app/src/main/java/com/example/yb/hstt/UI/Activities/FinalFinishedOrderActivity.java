package com.example.yb.hstt.UI.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskListActivity;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.DisplayUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class FinalFinishedOrderActivity extends HsttBaseActivity implements View.OnClickListener {
    private static final String TAG = "FinalFinishedOrderActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_finalFinishedOrder)
    Toolbar tb_finalFinishedOrder;

    @BindView(R.id.tv_finished_order_TypeWithFactory)
    TextView tv_finished_order_TypeWithFactory;
    @BindView(R.id.tv_finished_order_comment)
    TextView tv_finished_order_comment;
    @BindView(R.id.tv_finalFinishedOrder_user)
    TextView tv_finalFinishedOrder_user;
    @BindView(R.id.tv_finalFinishedOrder_phone)
    TextView tv_finalFinishedOrder_phone;
    @BindView(R.id.tv_finalFinishedOrder_address)
    TextView tv_finalFinishedOrder_address;
    @BindView(R.id.tv_finalFinishedOrder_collectComment)
    TextView tv_finalFinishedOrder_collectComment;

    @BindView(R.id.tv_finalFinishedOrder_deviceTypeWithFactory)
    TextView tv_finalFinishedOrder_deviceTypeWithFactory;
    @BindView(R.id.tv_finalFinishedOrder_deviceId)
    TextView tv_finalFinishedOrder_deviceId;
    @BindView(R.id.tv_finalFinishedOrder_dealWithResult)
    TextView tv_finalFinishedOrder_dealWithResult;

    @BindView(R.id.rv_finalFinishedOrder_photos)
    RecyclerView rv_finalFinishedOrder_photos;
    @BindView(R.id.rv_finalFinishedOrder_collectPhotos)
    RecyclerView rv_finalFinishedOrder_collectPhotos;
    @BindView(R.id.rv_finalFinishedOrder_CollectVideos)
    RecyclerView rv_finalFinishedOrder_CollectVideos;
    @BindView(R.id.rv_finalFinishedOrder_DevicePhotos)
    RecyclerView rv_finalFinishedOrder_DevicePhotos;
    @BindView(R.id.rv_finalFinishedOrder_DeviceVideos)
    RecyclerView rv_finalFinishedOrder_DeviceVideos;

    @BindView(R.id.ll_finalFinishedOrder_localeDealWith)
    LinearLayout ll_finalFinishedOrder_localeDealWith;
    @BindView(R.id.ll_finalFinishedOrder_DealWithType)
    LinearLayout ll_finalFinishedOrder_DealWithType;
    private List<String> photoFiles = new ArrayList<>();
    private List<String> collectPhotoFiles = new ArrayList<>();
    private List<String> collectVideoFiles = new ArrayList<>();
    private List<String> DevicePhotoFiles = new ArrayList<>();
    private List<String> DeviceVideoFiles = new ArrayList<>();

    private Dialog bottom_menu_dialog;
    private String owwoId,owrdpId;

    //查询维修的用户信息，文件信息。
    private List<Map> select_infos,select_files;
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    private HashMap task_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_finished_order);
        ButterKnife.bind(this);
        setSupportActionBar(tb_finalFinishedOrder);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        initRecyclerView(rv_finalFinishedOrder_photos, new DevicePhotoAdapter(photoFiles, getMe()));
        initRecyclerView(rv_finalFinishedOrder_collectPhotos, new DevicePhotoAdapter(collectPhotoFiles, getMe()));
        initRecyclerView(rv_finalFinishedOrder_CollectVideos, new DeviceVideoAdapter(collectVideoFiles, getMe()));
        initRecyclerView(rv_finalFinishedOrder_DevicePhotos, new DevicePhotoAdapter(DevicePhotoFiles, getMe()));
        initRecyclerView(rv_finalFinishedOrder_DeviceVideos, new DeviceVideoAdapter(DeviceVideoFiles, getMe()));
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        owwoId = getIntent().getStringExtra("owwoId");
        owrdpId = getIntent().getStringExtra("owrdpId");

        select_infos= (List<Map>) getIntent().getSerializableExtra("select_infos");
        select_files= (List<Map>) getIntent().getSerializableExtra("select_files");
        initDeviceData();
        selectTaskInfo();
        selectCollectEvidence();

    }

    /**
     * 初始化设备信息
     */
    private void initDeviceData(){
        String factory = select_infos.get(0).get("devManufacturerNo").toString();
        String deviceType = select_infos.get(0).get("devTypeNo").toString();
        String devId = select_infos.get(0).get("devId").toString();
        String comment = select_infos.get(0).get("comment").toString();
        tv_finalFinishedOrder_deviceTypeWithFactory.setText(deviceType + "-" + factory);
        tv_finalFinishedOrder_deviceId.setText(devId);
        tv_finalFinishedOrder_dealWithResult.setText(comment);
        for (Map files:select_files) {
            String attaFilePath = (String) files.get("attaFilePath");
            if (attaFilePath.contains("jpg")) {
                DevicePhotoFiles.add(attaFilePath);
            } else {
                DeviceVideoFiles.add(attaFilePath);
            }
        }
        rv_finalFinishedOrder_DevicePhotos.getAdapter().notifyDataSetChanged();
        rv_finalFinishedOrder_DeviceVideos.getAdapter().notifyDataSetChanged();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing));
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    /**
     * 查询任务信息
     *
     */
    private void selectTaskInfo() {
        OkHttpUtils.get(GlobalUrl.Get_TaskInfo).params("owwoId", owwoId)
                .execute(new CommonCallback<Map>(getMe()) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        if (map.get("code").toString().equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> info = (List<Map>) data.get("info");

                            String comment = info.get(0).get("comment").toString();
                            String user = info.get(0).get("crContactName").toString();
                            String crContactPhoneNum = info.get(0).get("crContactPhoneNum").toString();
                            String crAddress = info.get(0).get("crAddress").toString();
                            tv_finished_order_comment.setText(comment);
                            tv_finalFinishedOrder_user.setText(user);
                            tv_finalFinishedOrder_phone.setText(crContactPhoneNum);
                            tv_finalFinishedOrder_address.setText(crAddress);

                            List<Map> files = (List<Map>) data.get("files");
                            for (Map maps : files) {
                                String attaFilePath = maps.get("attaFilePath").toString();
                                photoFiles.add(attaFilePath);
                            }
                            rv_finalFinishedOrder_photos.getAdapter().notifyDataSetChanged();

                            List<Map> devs = (List<Map>) data.get("devs");
                            if (devs!=null&&devs.size()>0){
                                String devManufacturerNo = devs.get(0).get("devManufacturerNo").toString();
                                String devTypeNo = devs.get(0).get("devTypeNo").toString();
                                tv_finished_order_TypeWithFactory.setText(devTypeNo + "" + devManufacturerNo);
                            }
                        }
                    }
                });
    }

    /**
     * 查询采证信息
     */
    private void selectCollectEvidence(){
        HttpParams params=new HttpParams();
        params.put("owwoId",owwoId);
//        params.put("owrdpId",owrdpId);
        OkHttpUtils.get(GlobalUrl.Get_Success_Save_Info).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "onResponse: "+map);
                if (map.get("code").toString().equals("10000")){
                    Map data = (Map) map.get("data");
                    List<Map> infos = (List<Map>) data.get("info");
                    if (infos!=null&&infos.size()>0){
                        String comment = infos.get(0).get("comment").toString();
                        tv_finalFinishedOrder_collectComment.setText(comment);
                    }
                    List<Map> files = (List<Map>) data.get("files");
                    if (files!=null&&files.size()>0){
                      for (Map m:files){
                          String attaFilePath = m.get("attaFilePath").toString();
                          if (attaFilePath.endsWith(".jpg")){
                              collectPhotoFiles.add(attaFilePath);
                          }else{
                              collectVideoFiles.add(attaFilePath);
                          }
                      }
                        rv_finalFinishedOrder_collectPhotos.getAdapter().notifyDataSetChanged();
                        rv_finalFinishedOrder_CollectVideos.getAdapter().notifyDataSetChanged();
                    }

                }
            }
        });

    }

    /**
     * 继续处理
     */
    private void continue_dealwith() {
        if (bottom_menu_dialog == null) {
            bottom_menu_dialog = new Dialog(this, R.style.dialog_custom);
            View v = LayoutInflater.from(this).inflate(R.layout.continue_dealwith_layout, null);
            Button bt_continue_dealwith_change = ButterKnife.findById(v, R.id.bt_continue_dealwith_change);
            Button bt_continue_dealwith_repair = ButterKnife.findById(v, R.id.bt_continue_dealwith_repair);
            Button bt_continue_dealwith_cancel = ButterKnife.findById(v, R.id.bt_continue_dealwith_cancel);
            bt_continue_dealwith_change.setOnClickListener(this);
            bt_continue_dealwith_repair.setOnClickListener(this);
            bt_continue_dealwith_cancel.setOnClickListener(this);
            bottom_menu_dialog.setContentView(v);
            WindowManager.LayoutParams attributes = bottom_menu_dialog.getWindow().getAttributes();
            attributes.gravity = Gravity.BOTTOM;
            attributes.width = DisplayUtil.Width(getMe());
            bottom_menu_dialog.getWindow().setAttributes(attributes);
        }
        bottom_menu_dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_continue_dealwith_change://继续更换
                Intent intent2 = new Intent(getMe(), ChangeDeviceActivity.class);
                intent2.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                intent2.putExtra("owwoId", owwoId + "");
                startActivity(intent2);
                break;
            case R.id.bt_continue_dealwith_repair://继续维修
                Intent intent1 = new Intent(getMe(), RepairDeviceActivity.class);
                intent1.putExtra("owwoId", owwoId + "");
                intent1.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                startActivity(intent1);
                break;
            case R.id.bt_continue_dealwith_cancel://取消
                if (bottom_menu_dialog.isShowing())
                    bottom_menu_dialog.dismiss();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bottom_menu_dialog!=null&&bottom_menu_dialog.isShowing()) {
            bottom_menu_dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //禁止返回按键
    }

    @OnClick({R.id.bt_finalFinishedOrder_myOrder, R.id.bt_finalFinishedOrder_toMain, R.id.bt_finalFinishedOrder_continueDealWith,
            R.id.iv_finalFinishedOrder_takephone, R.id.tv_finalFinishedOrder_localeDealWith,
            R.id.tv_finalFinishedOrder_DealWithType})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_finalFinishedOrder_myOrder:
                //我的工单
                Intent intent3 = new Intent(getMe(), TaskListActivity.class);
                startActivity(intent3);
                break;
            case R.id.bt_finalFinishedOrder_toMain:
                startActivity(new Intent(getMe(), MainActivity.class));
                break;
            case R.id.bt_finalFinishedOrder_continueDealWith:
                continue_dealwith();
                break;
            case R.id.iv_finalFinishedOrder_takephone:
                AppUtils.callPhone(tv_finalFinishedOrder_phone.getText().toString(),getMe());
                break;
            case R.id.tv_finalFinishedOrder_localeDealWith:
                LayoutShowHide(ll_finalFinishedOrder_localeDealWith, (TextView) view);
                break;
            case R.id.tv_finalFinishedOrder_DealWithType:
                LayoutShowHide(ll_finalFinishedOrder_DealWithType, (TextView) view);
                break;
        }
    }
    /**
     * 布局先是隐藏
     *
     * @param v2 要显示或隐藏的布局
     * @param v1 点击的布局
     */
    private void LayoutShowHide(LinearLayout v2, TextView v1) {
        boolean shown = v2.isShown();
        v2.setVisibility(shown ? View.GONE : View.VISIBLE);
        v1.setCompoundDrawablesWithIntrinsicBounds(null, null, shown ? drawable_up : drawable_down, null);
    }
}
