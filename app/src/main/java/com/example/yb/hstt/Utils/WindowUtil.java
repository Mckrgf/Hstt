package com.example.yb.hstt.Utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 窗体操作工具
 * 界面背景颜色的修改，控件x,y坐标的获取，屏幕宽和高的获取
 * Created by Jelly on 2016/9/15.
 */
public class WindowUtil {
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * 屏幕宽度
     */
    private int screenHeight;

    private static class WindowUtilInstance{

        private static WindowUtil instance = new WindowUtil();

    }

    /**
     * 获得单例对象
     * @return
     */
    public static WindowUtil getInstance(){
        return WindowUtilInstance.instance;
    }


    private WindowUtil(){}

    /**
     * 获取屏幕的宽
     * @param context Context
     * @return 屏幕的宽
     */
    public int getScreenWidth(Activity context){
        if(context == null){
            return 0;
        }
        if(screenWidth != 0){
            return screenWidth;
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 获取屏幕的高
     */
    public int getScreenHeight(Activity context){
        if(context == null){
            return 0;
        }
        if(screenHeight != 0){
            return  screenHeight;
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 获取控件的位置
     * @param view 控件View
     * @return int[] x,y
     */
    public int[] getViewLocation(View view){
        int[] location = new int[2]; //获取筛选按钮的x坐标
        view.getLocationOnScreen(location);
        return location;
    }


    public int getStateBarHeight(Context context){
        Rect rect= new Rect();
        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        return  statusBarHeight;
    }

    /**
     * 设置屏幕变暗
     * @param context
     */
    public void setAlphaDark(Activity context) {
        WindowManager.LayoutParams lp=context.getWindow().getAttributes();
        lp.alpha=0.5f;
        context.getWindow().setAttributes(lp);
    }

    /**f
     * 设置屏幕变暗
     * @param context
     */
    public void setAlphaLight(Activity context) {
        WindowManager.LayoutParams lp=context.getWindow().getAttributes();
        lp.alpha=1f;
        context.getWindow().setAttributes(lp);
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
