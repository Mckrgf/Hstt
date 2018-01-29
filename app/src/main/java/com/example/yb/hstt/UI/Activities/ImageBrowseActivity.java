package com.example.yb.hstt.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.yb.hstt.Adpater.ImageBrowsePresenter;
import com.example.yb.hstt.Adpater.ViewPageAdapter;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jelly on 2016/9/3.
 */
public class ImageBrowseActivity extends HsttBaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, ImageBrowseView {

    @BindView(R.id.pic_browse_toolbar)
    Toolbar picBrowseToolbar;
    private ViewPager vp;
    private TextView hint;
    private TextView save;
    private TextView tv_filename;
    private ViewPageAdapter adapter;
    private ImageBrowsePresenter presenter;
    private ArrayList<String> filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        ButterKnife.bind(this);
        //标题栏初始化
        setSupportActionBar(picBrowseToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        picBrowseToolbar.setNavigationOnClickListener(this);
        filename = new ArrayList<>();
        vp = (ViewPager) this.findViewById(R.id.viewPager);
        hint = (TextView) this.findViewById(R.id.hint);
        tv_filename = (TextView) this.findViewById(R.id.tv_filename);
        tv_filename.setText("");
        initPresenter();
        presenter.loadImage();
        hideBottomMenu();
    }
    /**
     * 隐藏底部虚拟按键，且全屏
     */
    private void hideBottomMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int requestedOrientation = getRequestedOrientation();
        if (requestedOrientation!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    public void initPresenter() {
        presenter = new ImageBrowsePresenter(this);
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void setImageBrowse(List<String> images, int position) {
        if (adapter == null && images != null && images.size() != 0) {
            adapter = new ViewPageAdapter(this, images);
            vp.setAdapter(adapter);
            vp.setCurrentItem(position);
            vp.addOnPageChangeListener(this);
            hint.setText(position + 1 + "/" + images.size());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        presenter.setPosition(position);
        hint.setText(position + 1 + "/" + presenter.getImages().size());
        tv_filename.setText("");//不显示名称
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
            case -1:
                finish();
                break;
        }
    }


    public static void startActivity(Context context, ArrayList<String> images, int position) {
        Intent intent = new Intent(context, ImageBrowseActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
