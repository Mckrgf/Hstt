package com.example.yb.hstt.Base.navigation;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by TFHR02 on 2016/10/8.
 */
public interface navigationbuilder {
    public navigationbuilder setTitle(String title);

    public navigationbuilder setLeftIcon(int lefticonRes);
    public navigationbuilder setLeftIconOnClickListener(View.OnClickListener leftonClickListener);

    public navigationbuilder setRightText(String rightstring);
    public navigationbuilder setRightTextOnClickListener(View.OnClickListener rightonClickListener);

    public navigationbuilder setRightIcon(int righticonRes);
    public navigationbuilder setRightIconOnClickListener(View.OnClickListener rightonClickListener);

    public void createAndBind(ViewGroup parent);

}
