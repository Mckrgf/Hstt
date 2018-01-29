package com.example.yb.hstt.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yb.hstt.EventBus.HsttEvent;
import com.example.yb.hstt.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by tfhr on 2017/10/31.
 */

public class CenterDialog extends Dialog implements View.OnClickListener {
    private Context context;      // 上下文
    private int layoutResID;      // 布局文件id
    private int[] listenedItems;  // 要监听的控件id
    private ArrayList<String> types;      //操作类型操作类型
    private String title;
    private int msg_type = -1;
    private boolean select_by_brand;

    private OnCenterItemClickListener listener;
    private HsttEvent event;

    public void setType(int type) {
        this.msg_type = type;
    }

    public interface OnCenterItemClickListener {
        void OnCenterItemClick(CenterDialog dialog, View view);
    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.OnCenterItemClick(this, view);
    }

    //构造方法:不需要添加RadioButton的
    public CenterDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
    }

    //构造方法:需要添加RadioButton的
    public CenterDialog(Context context, int layoutResID, int[] listenedItems, ArrayList types) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
        this.types = types;
    }

    //构造方法:需要添加RadioButton的
    public CenterDialog(Context context, int layoutResID, int[] listenedItems, ArrayList types, String title, boolean select_by_brand) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
        this.types = types;
        this.title = title;
        this.select_by_brand = select_by_brand;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
//        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        setContentView(layoutResID);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失
        //遍历控件id,添加点击事件
        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.tv_dialog_title);
        if (null != textView) {
            textView.setText(title);
        }


        //如果是选择操作类型,遍历按钮列表动态添加
        if (null != types && types.size() > 0) {
            RadioGroup group = (RadioGroup) findViewById(R.id.rg_operate_type);
            for (int i = 0; i < types.size(); i++) {
                RadioButton button = new RadioButton(context);
                final String type = types.get(i);
                button.setText("   " + type);
                group.addView(button);
                //自定义RadioButotn图标
                button.setButtonDrawable(context.getResources().getDrawable(R.drawable.radio_button_operate_type_selector));

                //设置间距
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(30, 30, 30, 0);
                button.setLayoutParams(lp1);
                //设置字体
                button.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.x18));
//                //设置图片/文字间距
//                button.setTextAppearance(R.style.package_radio_style);
                final int finalPos = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().register(context);//注册
                        event = new HsttEvent();
                        event.setTag_device_type(msg_type);
                        event.setTAG(finalPos + "");//AddUserDeviceActivity
                        event.setSelect_by_brand(select_by_brand);
                        event.setMsg_body(type);
                        event.setOperate_type(finalPos);//新建工单专用
                        EventBus.getDefault().post(event);
                        EventBus.getDefault().unregister(context);//街注册
                    }
                });
            }
        }

    }

}
