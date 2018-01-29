package com.example.yb.hstt.Base.navigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.DensityUtil;

import butterknife.ButterKnife;

/**
 * Created by TFHR02 on 2016/10/8.
 */
public abstract class navigationbuilderadapter implements navigationbuilder {
    private Context context;//上下文
    private String title;//标题
    private String righttext;//右边文字
    private int myRightTextViewId;//右边文本id
    private int myLeftImageViewId;//左边控件id
    private int myRightImageViewId;//右边图片id
    private int lefticonRes;//左边图标资源
    private int righttextBg = 0;//右边文本背景
    private int righticonRes;//右边图标资源
    private View.OnClickListener leftonClickListener;
    private View.OnClickListener righttextonClickListener;
    private View.OnClickListener rightonClickListener;
    private View view;
    private int background_Resid = R.color.transparent;
    private int is_showstatusbar = View.VISIBLE;
    private boolean is_showPopupMenu = false;//是否显示下拉菜单
    private PopupWindow popupWindow;
    private View.OnClickListener poplistener;

    //----------------------------------------------------------------get---------------------------------------------------

    public View.OnClickListener getPoplistener() {
        return poplistener;
    }

    public boolean getIs_showPopupMenu() {
        return is_showPopupMenu;
    }

    public Context getContext() {
        return context;
    }

    public navigationbuilderadapter(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public int getMyRightTextViewId() {
        return myRightTextViewId;
    }

    public int getMyLeftImageViewId() {
        return myLeftImageViewId;
    }

    public int getMyRightImageViewId() {
        return myRightImageViewId;
    }

    public int getRighttextBg() {
        return righttextBg;
    }

    public int getLefticonRes() {
        return lefticonRes;
    }

    public View.OnClickListener getLeftonClickListener() {
        return leftonClickListener;
    }

    public String getRighttext() {
        return righttext;
    }

    public View.OnClickListener getRighttextonClickListener() {
        return righttextonClickListener;
    }

    public int getRighticonRes() {
        return righticonRes;
    }

    public View.OnClickListener getRightonClickListener() {
        return rightonClickListener;
    }

    public View getView() {
        return view;
    }

    public int getBackground_Resid() {
        return background_Resid;
    }

    public int getIs_showstatusbar() {
        return is_showstatusbar;
    }
    //----------------------------------------------------------------set---------------------------------------------------

    public navigationbuilderadapter setPoplistener(View.OnClickListener poplistener) {
        this.poplistener = poplistener;
        return this;
    }

    public navigationbuilderadapter setBackground_Resid(int background_Resid) {
        this.background_Resid = background_Resid;
        return this;
    }

    public navigationbuilderadapter setTitle(String title) {
        this.title = title;
        return this;
    }

    public navigationbuilderadapter setLeftIcon(int lefticonRes) {
        this.lefticonRes = lefticonRes;
        return this;
    }

    public navigationbuilderadapter setMyRightTextViewId(int myRightTextViewId) {
        this.myRightTextViewId = myRightTextViewId;
        return this;
    }

    public navigationbuilderadapter setMyLeftImageViewId(int myLeftImageViewId) {
        this.myLeftImageViewId = myLeftImageViewId;
        return this;
    }

    public navigationbuilderadapter setMyRightImageViewId(int myRightImageViewId) {
        this.myRightImageViewId = myRightImageViewId;
        return this;
    }

    public navigationbuilderadapter setLeftIconOnClickListener(View.OnClickListener leftonClickListener) {
        this.leftonClickListener = leftonClickListener;
        return this;
    }

    public navigationbuilderadapter setRightText(String righttext) {
        this.righttext = righttext;
        return this;
    }

    public navigationbuilderadapter setRighttextBg(int righttextBg) {
        this.righttextBg = righttextBg;
        return this;
    }

    public navigationbuilderadapter setRightTextOnClickListener(View.OnClickListener righttextonClickListener) {
        this.righttextonClickListener = righttextonClickListener;
        return this;
    }

    public navigationbuilderadapter setRightIcon(int righticonRes) {
        this.righticonRes = righticonRes;
        return this;
    }

    public navigationbuilderadapter setRightIconOnClickListener(View.OnClickListener rightonClickListener) {
        this.rightonClickListener = rightonClickListener;
        return this;
    }

    public navigationbuilderadapter setStatusBarVisibilty(int visibilty) {
        this.is_showstatusbar = visibilty;
        return this;
    }

    public navigationbuilderadapter setIs_showPopupMenu(boolean is_showPopupMenu) {
        this.is_showPopupMenu = is_showPopupMenu;
        return this;
    }

    //----------------------------------------------------------------处理布局--------------------------------------------------
    public abstract int getNavigationViewId();

    //绑定父容器
    public void createAndBind(ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(getNavigationViewId(), parent, false);
        ViewGroup viewparent = (ViewGroup) view.getParent();
        if (viewparent != null) {
            viewparent.removeView(view);
        }
        parent.addView(view, 0);
        initPopupwindow();
    }

    /**
     * 初始化popwindow
     */
    private void initPopupwindow() {
        if (popupWindow == null) {
            View popupview = LayoutInflater.from(context).inflate(R.layout.navigation_popup_menu, null);
            TextView tv_navigation_popup_fix = ButterKnife.findById(popupview, R.id.tv_navigation_popup_fix);
            TextView tv_navigation_popup_updateDevname = ButterKnife.findById(popupview, R.id.tv_navigation_popup_updateDevname);
            tv_navigation_popup_fix.setOnClickListener(getPoplistener());
            tv_navigation_popup_updateDevname.setOnClickListener(getPoplistener());
            popupWindow = new PopupWindow(context);
            popupWindow.setWidth(DensityUtil.getpxByDimensize(context, R.dimen.x333));
            popupWindow.setHeight(DensityUtil.getpxByDimensize(context, R.dimen.y228));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setPopWindowBackgroundAlpha(1f);
                }
            });
            popupWindow.setContentView(popupview);
        }
    }

    /**
     * 设置透明度(context)
     *
     * @param bgAlpha [0-1] 1表示不透明
     */
    public void setPopWindowBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //[0.0-1.0]
        if (bgAlpha == 1) {
            //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug,而我遇到的是半透明无效，采用了该方案
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            //此行代码主要是解决在华为手机上半透明效果无效的bug
            ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        // activity.getWindow().setAttributes(lp);
        ((Activity) context).getWindow().setAttributes(lp);
    }

    //设置文本容器属性
    public void setTextViewStyle(final int textviewid, String CharSequence, final View.OnClickListener l, int rightviewid) {
        final TextView textview = (TextView) getView().findViewById(textviewid);
        if (!TextUtils.isEmpty(CharSequence)) {
            textview.setVisibility(View.VISIBLE);
            textview.setText(CharSequence);
            //如果右边文字传入背景则设置
            if (textviewid == R.id.tv_righttext) {
                if (getRighttextBg() != 0) {
                    textview.setBackgroundResource(getRighttextBg());
                }
                if (rightviewid != (-1)) {
                    textview.setId(rightviewid);
                }
            }
            textview.setOnClickListener(l);
        } else {
            textview.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置背景
     *
     * @param resid
     */
    public void setBackgoundRes(int resid) {
        if (resid != -1) {
            getView().setBackgroundResource(resid);
        }
    }

    //设置图片容器属性
    public void setImageViewStyle(final int imageviewid, int resId, final View.OnClickListener l) {
        final ImageView imageview = (ImageView) getView().findViewById(imageviewid);
        if (resId != 0) {
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageResource(resId);
            if (imageviewid == R.id.iv_left) {
                imageview.setId(getMyLeftImageViewId());
                imageview.setOnClickListener(l);
            } else {
                imageview.setId(getMyRightImageViewId());
                imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //设置下拉菜单
                        if (getIs_showPopupMenu()) {
                            showPopupwindow(imageview);
                        } else {
                            if (l != null) {
                                l.onClick(v);
                            }
                        }
                    }
                });
            }
        } else {
            imageview.setVisibility(View.GONE);
        }
    }

    /**
     * 设置布局显示隐藏
     *
     * @param viewid
     */
    public void setStatusBarView(int viewid) {
        View view = getView().findViewById(viewid);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//如果系统不支持透明状态栏
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(getIs_showstatusbar());
        }
    }

    /**
     * 显示下拉菜单
     */
    public void showPopupwindow(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        setPopWindowBackgroundAlpha(0.4f);
        popupWindow.showAsDropDown(view, 0, DensityUtil.getpxByDimensize(context, R.dimen.y18));
    }

    /**
     * 关闭下拉菜单
     */
    public void closePopuowindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

}
