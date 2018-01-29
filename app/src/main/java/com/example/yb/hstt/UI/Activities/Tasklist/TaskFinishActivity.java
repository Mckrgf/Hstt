package com.example.yb.hstt.UI.Activities.Tasklist;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.MainActivity;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.WindowUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskFinishActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.task_finish_toolbar)
    Toolbar taskFinishToolbar;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.rv_info_pic)
    RecyclerView rvInfoPic;//图片列表
    @BindView(R.id.bt_my_task)
    Button btMyTask;//我的工单
    @BindView(R.id.bt_main_actvity)
    Button btMainActvity;//首页
    @BindView(R.id.bt_continue)
    Button btContinue;//继续处理
    @BindView(R.id.divide_a)
    View divideA;
    @BindView(R.id.tv_equipment_type)
    TextView tvEquipmentType;
    @BindView(R.id.tv_equipment_content)
    TextView tvEquipmentContent;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.divide_b)
    View divideB;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_contact_phone)
    TextView tvContactPhone;
    @BindView(R.id.tv_contact_addr)
    TextView tvContactAddr;
    @BindView(R.id.ll_contract_info)
    RelativeLayout llContractInfo;
    @BindView(R.id.divide_c)
    View divideC;
    @BindView(R.id.rl_need_to_hide)
    RelativeLayout rlNeedToHide;
    @BindView(R.id.activity_task_finish)
    RelativeLayout activityTaskFinish;
    @BindView(R.id.v_trans)
    View vTrans;
    private String device_info;
    private HashMap task_info;
    private PopupWindow pop_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finish);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        task_info = (HashMap) intent.getSerializableExtra("TASK_INFO");
        device_info = intent.getStringExtra("DEVICE_INFO");
        initView();


    }

    private void initView() {
        taskFinishToolbar.setNavigationIcon(R.mipmap.ic_return);
        taskFinishToolbar.setNavigationOnClickListener(this);

        tvEquipmentType.setText(device_info);
        tvContact.setText(String.valueOf(task_info.get("crContactName")));
        tvContactAddr.setText(String.valueOf(task_info.get("crAddress")));
        tvContactPhone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        tvEquipmentContent.setText(String.valueOf(task_info.get("crDescribe")));
        btMyTask.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        btMainActvity.setOnClickListener(this);

        initCallPop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.bt_main_actvity:
                Intent intent0 = new Intent(getMe(), MainActivity.class);
                startActivity(intent0);
                finish();
                break;
            case R.id.bt_continue:
                Intent intent1 = new Intent(getMe(), TaskListActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.bt_my_task:
                Intent intent2 = new Intent(getMe(), TaskListActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.iv_call:// 打电话
                //打电话的弹窗
                pop_call.setAnimationStyle(R.style.popWindow_animation);
                pop_call.showAtLocation(activityTaskFinish, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));//parent为泡泡窗体所在的父容器view
                setScreenDark();
        }
    }

    /**
     * 初始化打电话弹窗
     */
    private void initCallPop() {
        View view = View.inflate(getMe(), R.layout.task_list_call, null);
        pop_call = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_call.setBackgroundDrawable(new BitmapDrawable());    //设置背景，否则setOutsideTouchable无效
        pop_call.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        pop_call.setFocusable(true);
        pop_call.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setScreenLight();
//                sflLimit.setVisibility(View.VISIBLE);
            }
        });

        RelativeLayout rl_call_a = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_a);
        RelativeLayout rl_call_cancel = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_cancel);
        TextView tv_contact_phone = (TextView) pop_call.getContentView().findViewById(R.id.tv_contact_phone);
        tv_contact_phone.setText(String.valueOf(task_info.get("crContactPhoneNum")));
        rl_call_a.setOnClickListener(this);
        rl_call_cancel.setOnClickListener(this);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(TaskFinishActivity.this, R.color.transparent);
    }

    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(TaskFinishActivity.this, R.color.black_trans);
    }
}
