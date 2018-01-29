package com.example.yb.hstt.View;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.andview.refreshview.utils.Utils;
import com.example.yb.hstt.R;

import java.util.Calendar;

/**
 * Created by TFHR02 on 2018/1/18.
 */
public class ListViewHeaderView extends LinearLayout implements IHeaderCallBack {

    // ListView头部下拉刷新的布局
    private LinearLayout headerView;
    private TextView lvHeaderTipsTv;
    private TextView lvHeaderLastUpdatedTv;
    private ImageView lvHeaderArrowIv;
    private ProgressBar lvHeaderProgressBar;

    private RotateAnimation toUpAnimation;
    private RotateAnimation toDownAnimation;

    public ListViewHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public ListViewHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.lv_header, this);
        lvHeaderTipsTv = (TextView) headerView.findViewById(R.id.lvHeaderTipsTv);// 头部抬头提示
        lvHeaderLastUpdatedTv = (TextView) headerView.findViewById(R.id.lvHeaderLastUpdatedTv);// 更新提示

        lvHeaderArrowIv = (ImageView) headerView.findViewById(R.id.lvHeaderArrowIv);// 图片img
        // 设置下拉刷新图标的最小高度和宽度
        lvHeaderArrowIv.setMinimumWidth(70);
        lvHeaderArrowIv.setMinimumHeight(50);
        lvHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.lvHeaderProgressBar);// 进度条

        // 设置旋转动画事件
        toUpAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        toUpAnimation.setInterpolator(new LinearInterpolator());
        toUpAnimation.setDuration(250);
        toUpAnimation.setFillAfter(true);

        toDownAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        toDownAnimation.setInterpolator(new LinearInterpolator());// 设置动画的变化速度为匀速
        toDownAnimation.setDuration(200);
        toDownAnimation.setFillAfter(true);
    }

    private boolean isBack;

    /**
     * 正常状态
     */
    @Override
    public void onStateNormal() {
        lvHeaderProgressBar.setVisibility(View.GONE);
        lvHeaderTipsTv.setText("下拉刷新");
        lvHeaderArrowIv.setVisibility(View.VISIBLE);
        if (isBack) {
            isBack=false;
            lvHeaderArrowIv.clearAnimation();
            lvHeaderArrowIv.startAnimation(toDownAnimation);
        }

    }

    /**
     * 准备刷新
     */
    @Override
    public void onStateReady() {
        lvHeaderProgressBar.setVisibility(View.GONE);
        lvHeaderTipsTv.setText("松开刷新");
        lvHeaderArrowIv.setVisibility(View.VISIBLE);
        lvHeaderArrowIv.clearAnimation();
        lvHeaderArrowIv.startAnimation(toUpAnimation);
        lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

        isBack = true;
    }

    /**
     * 正在刷新
     */
    @Override
    public void onStateRefreshing() {
        lvHeaderProgressBar.setVisibility(View.VISIBLE);

        lvHeaderArrowIv.clearAnimation();
        lvHeaderArrowIv.setVisibility(View.GONE);
        lvHeaderTipsTv.setText("正在刷新");
        isBack = false;
    }

    /**
     * 刷新结束
     *
     * @param success
     */
    @Override
    public void onStateFinish(boolean success) {
        isBack = false;
    }

    /**
     * 获取headerview显示的高度与headerview高度的比例
     *
     * @param headerMovePercent
     * @param offsetY
     * @param deltaY
     */
    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY, int deltaY) {

    }

    /**
     * 设置显示上一次刷新的时间
     *
     * @param lastRefreshTime
     */
    @Override
    public void setRefreshTime(long lastRefreshTime) {
        // 获取当前时间
        Calendar mCalendar = Calendar.getInstance();
        long refreshTime = mCalendar.getTimeInMillis();
        long howLong = refreshTime - lastRefreshTime;
        int minutes = (int) (howLong / 1000 / 60);
        String refreshTimeText = null;
        Resources resources = getContext().getResources();
        if (minutes < 1) {
            refreshTimeText = resources
                    .getString(com.andview.refreshview.R.string.xrefreshview_refresh_justnow);
        } else if (minutes < 60) {
            refreshTimeText = resources
                    .getString(com.andview.refreshview.R.string.xrefreshview_refresh_minutes_ago);
            refreshTimeText = Utils.format(refreshTimeText, minutes);
        } else if (minutes < 60 * 24) {
            refreshTimeText = resources
                    .getString(com.andview.refreshview.R.string.xrefreshview_refresh_hours_ago);
            refreshTimeText = Utils.format(refreshTimeText, minutes / 60);
        } else {
            refreshTimeText = resources
                    .getString(com.andview.refreshview.R.string.xrefreshview_refresh_days_ago);
            refreshTimeText = Utils.format(refreshTimeText, minutes / 60 / 24);
        }
        lvHeaderLastUpdatedTv.setText(refreshTimeText);
    }

    /**
     * 隐藏Headerview
     */
    @Override
    public void hide() {
        setVisibility(View.GONE);
    }

    /**
     * 显示Headerview
     */
    @Override
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /**
     * 获得headerview的高度,如果不想headerview全部被隐藏，就可以只返回一部分的高度
     *
     * @return
     */
    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
