package com.example.yb.hstt.UI.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.yb.hstt.Base.itembuild.ImplItemBuilder;
import com.example.yb.hstt.R;


/**
 * Created by TFHR02 on 2016/9/18.
 */
public class DefaulItemBuilder extends ImplItemBuilder {
    //布局
    private LinearLayout ll_itembuild_layout;
    private ImageView iv_itembuild_left, iv_itembuild_right;
    private TextView tv_itembuild_title, tv_itembuild_titlecontent;
    private View dividling;
    //属性值
    private int margintop = 0;
    private int LeftImgResId = 0, RightImgResId = 0;
    private String TitleText, TitleTextContent;
    private int TitleTextColor;
    private boolean isSetDivide;
    private View.OnClickListener listener, rightimglistener;
    /**
     * 是否显示contentview
     */
    private boolean is_showContentView;

    public static synchronized DefaulItemBuilder getInstance(Context context) {
        return new DefaulItemBuilder(context);
    }

    public DefaulItemBuilder(Context context) {
        super(context);
    }

    public DefaulItemBuilder setMarginTop(int margintop) {
        this.margintop = margintop;
        return this;
    }

    public DefaulItemBuilder setLeftImgRes(int leftImgResId) {
        this.LeftImgResId = leftImgResId;
        return this;
    }

    public DefaulItemBuilder setRightImgRes(int rightImgResId) {
        this.RightImgResId = rightImgResId;
        return this;
    }

    public DefaulItemBuilder setTitleText(String TitleText) {
        this.TitleText = TitleText;
        return this;
    }
    public DefaulItemBuilder setTitleTextColor(int color) {
        this.TitleTextColor = color;
        return this;
    }

    public DefaulItemBuilder setTitleContentText(String TitleTextContent) {
        this.TitleTextContent = TitleTextContent;
        return this;
    }

    public DefaulItemBuilder setDivideingline(boolean isSetDivide) {
        this.isSetDivide = isSetDivide;
        return this;
    }

    public DefaulItemBuilder setOnItemBuilderListener(View.OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    public DefaulItemBuilder setRightImgListener(View.OnClickListener rightimglistener) {
        this.rightimglistener = rightimglistener;
        return this;
    }

    public DefaulItemBuilder setIs_showContentView(boolean is_showContentView) {
        this.is_showContentView = is_showContentView;
        return this;
    }

    @Override
    public int getContentView() {
        return R.layout.item_build_default;
    }

    @Override
    public View BindParentView(ViewGroup parentview) {
        View contentview = super.BindParentView(parentview);
        ll_itembuild_layout = (LinearLayout) contentview.findViewById(R.id.ll_itembuild_layout);
        iv_itembuild_left = (ImageView) contentview.findViewById(R.id.iv_itembuild_left);
        tv_itembuild_title = (TextView) contentview.findViewById(R.id.tv_itembuild_title);
        tv_itembuild_titlecontent = (TextView) contentview.findViewById(R.id.tv_itembuild_titlecontent);
        iv_itembuild_right = (ImageView) contentview.findViewById(R.id.iv_itembuild_right);
        dividling = contentview.findViewById(R.id.v_itembuild_dividling);
        //设置距离上个控件的距离（默认0）
        LayoutParams layoutParams = (LayoutParams) ll_itembuild_layout.getLayoutParams();
        layoutParams.setMargins(0, margintop, 0, 0);
        ll_itembuild_layout.setLayoutParams(layoutParams);

        //设置item左边的图片资源
        if (LeftImgResId > 0) {
            iv_itembuild_left.setImageResource(LeftImgResId);
            iv_itembuild_left.setVisibility(View.VISIBLE);
        } else {
            iv_itembuild_left.setVisibility(View.GONE);
        }

        //设置item中间的标题
        if (!TextUtils.isEmpty(TitleText)) {
            tv_itembuild_title.setText(TitleText);
            tv_itembuild_title.setVisibility(View.VISIBLE);
        } else {
            tv_itembuild_title.setText("");
            tv_itembuild_title.setVisibility(View.GONE);
        }
        //设置item中间的标题颜色
        if (TitleTextColor!=-1){
            tv_itembuild_title.setTextColor(getContext().getResources().getColor(TitleTextColor));
        }else{
            tv_itembuild_title.setTextColor(getContext().getResources().getColor(R.color.tv_text_black));
        }

        //设置item中间的标题 内容
        if (!TextUtils.isEmpty(TitleTextContent)) {
            tv_itembuild_titlecontent.setText(TitleTextContent);
            tv_itembuild_titlecontent.setVisibility(View.VISIBLE);
        } else {
            tv_itembuild_titlecontent.setText("");
            tv_itembuild_titlecontent.setVisibility(View.GONE);
        }

        //设置item右边的图片资源
        if (RightImgResId > 0) {
            iv_itembuild_right.setVisibility(View.VISIBLE);
            iv_itembuild_right.setImageResource(RightImgResId);
        }else if (RightImgResId==-1){
            iv_itembuild_right.setVisibility(View.GONE);
        }else{
            iv_itembuild_right.setVisibility(View.VISIBLE);
            iv_itembuild_right.setImageResource(R.drawable.item_jiantou);
        }

        //设置item的点击事件
        if (listener != null) {
            //设置tag用于区分点击事件
            ll_itembuild_layout.setTag(tv_itembuild_title.getText().toString());
            ll_itembuild_layout.setOnClickListener(listener);
        }
        //设置右边图标的点击事件
        if (rightimglistener != null) {
            iv_itembuild_right.setTag(tv_itembuild_title.getText().toString());
            iv_itembuild_right.setOnClickListener(rightimglistener);
        }

        //是否添加分割线dividing
        if (isSetDivide) {
            dividling.setVisibility(View.VISIBLE);
        } else {
            dividling.setVisibility(View.GONE);
        }
        //设置是否显示contentview
        if (is_showContentView) {
            contentview.setVisibility(View.VISIBLE);
        } else {
            contentview.setVisibility(View.GONE);
        }

        return contentview;
    }
}
