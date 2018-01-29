package com.example.yb.hstt.Base.itembuild;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by TFHR02 on 2016/9/18.
 */
public abstract class ImplItemBuilder implements ItemBuilder {

    Context context;

    public abstract int getContentView();

    public ImplItemBuilder(Context context) {
        this.context=context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public View BindParentView(ViewGroup parentview) {
        View contentview= LayoutInflater.from(getContext()).inflate(getContentView(),parentview,false);
        ViewGroup contentparentview= (ViewGroup) contentview.getParent();
        if (contentparentview!=null){
            contentparentview.removeView(contentview);
        }
        parentview.addView(contentview);
        return contentview;
    }

}
