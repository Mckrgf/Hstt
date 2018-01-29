package com.example.yb.hstt.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.item.BuilderItemEntity;
import com.example.yb.hstt.UI.item.DefaulItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TFHR02 on 2017/6/29.
 */
public class BuilderItemUtil {
    /**
     * 通过上下文（content）将对象entit填入view容器中
     *
     * @param content
     * @param view
     * @param entities
     */
    public static void toBuilder(Context content, ViewGroup view, BuilderItemEntity... entities) {
        for (int i = 0; i < entities.length; i++) {
            BuilderItemEntity e = entities[i];
            DefaulItemBuilder builder = DefaulItemBuilder.getInstance(content)
                    .setMarginTop(e.margintop)
                    .setLeftImgRes(e.leftImgResId)
                    .setRightImgRes(e.rightImgResId)
                    .setTitleText(e.TitleText)
                    .setTitleContentText(e.TitleTextContent)
                    .setOnItemBuilderListener(e.listener)
                    .setRightImgListener(e.rightImgListener)
                    .setDivideingline(e.isSetDivide)
                    .setIs_showContentView(e.is_showContentView)
                    .setTitleTextColor(e.TitleTextColor);
           builder.BindParentView(view);
        }
    }

    /**
     * 根据父容器、view标记，设置指定的view隐藏显示
     *
     * @param parentview
     * @param tag
     * @param visibility
     */
    public static void setchildViewVisibility(ViewGroup parentview, String tag, int visibility) {
        for (int i = 0; i < parentview.getChildCount(); i++) {
            View childAt = parentview.getChildAt(i);
            if (childAt.getTag().equals(tag)) {
                childAt.setVisibility(visibility);
                break;
            }
        }
        setLastOne_TwoDividlVisibility(parentview);
    }


    /**
     * 根据父容器、view标记，找到右边图标按钮
     *
     * @param parentview
     * @param tag
     */
    public static View getSwitchView(ViewGroup parentview, String tag) {
        View switchview=null;
        for (int i = 0; i < parentview.getChildCount(); i++) {
            View childAt = parentview.getChildAt(i);
            if (childAt.getTag().equals(tag)) {
                switchview=childAt.findViewById(R.id.iv_itembuild_right);
                break;
            }
        }
        return switchview;
    }

    /**
     * 设置父容器的已经显示的子view中，最后一个分割线隐藏，倒数第二个显示
     *
     * @param parentview
     */
    public static void setLastOne_TwoDividlVisibility(ViewGroup parentview) {
        //已经显示的view集合
        List<View> showedViews = new ArrayList<>();
        for (int i = 0; i < parentview.getChildCount(); i++) {
            View childAt = parentview.getChildAt(i);
            if (childAt.isShown()) {
                showedViews.add(childAt);
            }
        }
        for (int j = 0; j < showedViews.size(); j++) {
            View view=showedViews.get(j);
            if ( j== showedViews.size() - 1) {
                View divid = view.findViewById(R.id.v_itembuild_dividling);
                divid.setVisibility(View.GONE);
            } else if (j == showedViews.size() - 2) {
                View divid = view.findViewById(R.id.v_itembuild_dividling);
                divid.setVisibility(View.VISIBLE);
            }
        }
    }

}
