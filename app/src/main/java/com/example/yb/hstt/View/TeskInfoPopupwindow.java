package com.example.yb.hstt.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.yb.hstt.EventBus.HsttEvent;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskStatusSecondActivity;
import com.example.yb.hstt.Utils.SystemAppUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


public abstract class TeskInfoPopupwindow extends PopupWindow implements OnClickListener, OnTouchListener {


    private Activity context;

    private RotateAnimation r_animation = null;
    private TextView tv_left_time;
    private TextView tv_equipment_type;
    private TextView tv_equipment_content;
    private TextView tv_contact;
    private TextView tv_contact_phone;
    private TextView tv_contact_addr;
    private RecyclerView rv_info_pic;
    private ImageView iv_call;
    private Button tv_start_deal;
    private Button bt_more;
    private LinearLayout ll_title;
    private LinearLayout rl_need_to_hide;
    private boolean aa = false;
//    private final View view;

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
    }

    public TeskInfoPopupwindow(Activity context ,View view1) {
        super(context);
        this.context = context;
//        view = LayoutInflater.from(context).inflate(viewid, null);
        this.setContentView(view1);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
        initView(view1);
        // 初始化旋转动画
    }

    public void setView(int viewid) {
        View view1 = LayoutInflater.from(context).inflate(viewid, null);
        this.setContentView(view1);
    }

    private SildingFinishLayout sfl_id;
    private RelativeLayout ll_popupwindow_basemenu_deal;

    private void initView(final View view) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        final int windowheight = display.heightPixels;

        initchildview(view);
        sfl_id.setTouchView(ll_title, ll_popupwindow_basemenu_deal, windowheight);
        sfl_id.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

            @Override
            public void onSildingFinish(int curry) {
                getFinishCurry(curry);
            }
        });
        ll_title.post(new Runnable() {

            @Override
            public void run() {
                int y_axis = -windowheight + (ll_popupwindow_basemenu_deal.getHeight() + SystemAppUtils.getStatusHeight(context));
                view.scrollTo(0, y_axis);
                getFinishCurry(Math.abs(y_axis));
            }
        });


    }

    private void initchildview(View view) {
        // 上拉展开按钮
        ll_title = (LinearLayout) view.findViewById(R.id.ll_title_pop);
        tv_left_time = (TextView) view.findViewById(R.id.tv_left_time);
        tv_equipment_type = (TextView) view.findViewById(R.id.tv_equipment_type);
        tv_equipment_content = (TextView) view.findViewById(R.id.tv_equipment_content);
        tv_contact = (TextView) view.findViewById(R.id.tv_contact);
        tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
        tv_contact_addr = (TextView) view.findViewById(R.id.tv_contact_addr);
        rv_info_pic = (RecyclerView) view.findViewById(R.id.rv_info_pic);
        iv_call = (ImageView) view.findViewById(R.id.iv_call);
        tv_start_deal = (Button) view.findViewById(R.id.tv_start_deal);
        bt_more = (Button) view.findViewById(R.id.bt_more);
        sfl_id = (SildingFinishLayout) view.findViewById(R.id.sfl);
        ll_popupwindow_basemenu_deal = (RelativeLayout) view.findViewById(R.id.ll_popupwindow_basemenu_deal);
        rl_need_to_hide = (LinearLayout) view.findViewById(R.id.rl_need_to_hide);

        ll_title.setOnClickListener(this);
        bt_more.setOnClickListener(this);
//        tv_start_deal.setOnClickListener(this);
    }


    public abstract void getFinishCurry(int curry);

    /**
     * 设置当前设备的数据
     *
     * @param
     */
    public void setDevicedata() {
    }

    // 设置回调监听接口，用于回调上报位置信息
    public interface OnPositionInfoListener {
        public void PositionInfoListener(int haspic, int hasvideo, int hasmsg, int hasnormal);
    }

    private OnPositionInfoListener callbackListener;

    public void setOnPositionInfoListener(OnPositionInfoListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    private Map<String, String> taskInfo;

    /**
     * 设置当前任务列表 信息
     *
     * @param taskInfo
     */
    public void setDeviceTaksinfo(Map<String, String> taskInfo) {
        this.taskInfo = taskInfo;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_call:
                // 打电话
                break;

            case R.id.bt_more:// 更多菜单
                HsttEvent event = new HsttEvent();
                event.setTAG("1");
                EventBus.getDefault().postSticky(event);
                break;
        }
    }



    /**
     * 通知界面刷新弹窗
     */
    private void post_msg() {
        HsttEvent event = new HsttEvent();
        event.setREFRESH_POPUPWINDOW(event.getREFRESH_POPUPWINDOW());
        EventBus.getDefault().post(event);
    }


    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        return true;
    }

    /**
     * 初始化旋转动画，
     *
     * @param fromDegress 开始角度
     * @param toDegress   结束角度
     * @return
     */
    public RotateAnimation getAnimation(int fromDegress, int toDegress, View view) {
        r_animation = new RotateAnimation(fromDegress, toDegress, view.getWidth() / 2,
                view.getHeight() / 2);
        r_animation.setDuration(800);
        r_animation.setFillAfter(true);
        return r_animation;
    }

}
