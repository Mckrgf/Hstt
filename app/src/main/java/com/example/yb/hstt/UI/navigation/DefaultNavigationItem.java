package com.example.yb.hstt.UI.navigation;

import android.content.Context;
import android.view.ViewGroup;

import com.example.yb.hstt.Base.navigation.navigationbuilderadapter;
import com.example.yb.hstt.R;


/**
 * Created by TFHR02 on 2016/10/8.
 */
public class DefaultNavigationItem extends navigationbuilderadapter {

    public static synchronized DefaultNavigationItem getInstance(Context context){
        return new DefaultNavigationItem(context);

    }
    public DefaultNavigationItem(Context context) {
        super(context);
    }

    @Override
    public int getNavigationViewId() {
        return R.layout.navigationbarlayout;
    }

    @Override
    public void createAndBind(ViewGroup parent) {
        super.createAndBind(parent);
        setBackgoundRes(getBackground_Resid());
        //设置标题
        setTextViewStyle(R.id.tv_title,getTitle(),null,-1);
        //设置左边图标
        setImageViewStyle(R.id.iv_left,getLefticonRes(),getLeftonClickListener());
        //设置右边文本
        setTextViewStyle(R.id.tv_righttext,getRighttext(),getRighttextonClickListener(),getMyRightTextViewId());
        //设置右边图标
        setImageViewStyle(R.id.iv_right,getRighticonRes(),getRightonClickListener());
        //设置“顶部的填充布局”是否显示
        setStatusBarView(R.id.tv_fitssystemwindows_view);
    }
}
