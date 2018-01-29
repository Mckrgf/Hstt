package com.example.yb.hstt.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by tfhr on 2018/1/5.
 */

public class TaskInfoPopupwindow extends PopupWindow {
    public TaskInfoPopupwindow(Context context) {
        super(context);
    }

    public TaskInfoPopupwindow(Context context,View view) {
        super(context);
        this.setContentView(view);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
    }

    public TaskInfoPopupwindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskInfoPopupwindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TaskInfoPopupwindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TaskInfoPopupwindow() {
        super();
    }

    public TaskInfoPopupwindow(View contentView) {
        super(contentView);
    }
}
