package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.bean.OrderFileInfo;
import com.example.yb.hstt.bean.OrderInfo;
import com.example.yb.hstt.dao.DbHelper.LoginInfoDbHelper;
import com.example.yb.hstt.dao.bean.LoginInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewOrderSuccessActivity extends HsttBaseActivity {

    @BindView(R.id.rv_newOrderSuccess_photos)
    RecyclerView rv_newOrderSuccess_photos;
    @BindView(R.id.tv_newOrderSuccess_status)
    TextView tv_newOrderSuccess_status;
    @BindView(R.id.tv_newOrderSuccess_typeWithFactory)
    TextView tv_newOrderSuccess_typeWithFactory;
    @BindView(R.id.tv_newOrderSuccess_describe)
    TextView tv_newOrderSuccess_describe;
    @BindView(R.id.tv_newOrderSuccess_user)
    TextView tv_newOrderSuccess_user;
    @BindView(R.id.tv_newOrderSuccess_phone)
    TextView tv_newOrderSuccess_phone;
    @BindView(R.id.tv_newOrderSuccess_address)
    TextView tv_newOrderSuccess_address;

    private List<String> photo_files = new ArrayList<>();

    private DevicePhotoAdapter photosAdapter = null;
    private OrderInfo orderInfo = null;
    private LoginInfo logininfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_success);
        ButterKnife.bind(this);
        logininfo = LoginInfoDbHelper.getInstance(getMe()).select();
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("orderInfo");
        tv_newOrderSuccess_status.setText("工单信息-" + orderInfo.getOrder_status());
        String dev_fac_type = null;
        if (!TextUtils.isEmpty(orderInfo.getDevice_type())) {
            dev_fac_type = orderInfo.getDevice_type();
        }else {
            dev_fac_type = "设备类型为空";
        }
        if (!TextUtils.isEmpty(orderInfo.getFactory())) {
            dev_fac_type = dev_fac_type + "-" + orderInfo.getFactory();
        }else {
            dev_fac_type = dev_fac_type + "-" + "设备厂家为空";
        }
        tv_newOrderSuccess_typeWithFactory.setText(dev_fac_type);
        tv_newOrderSuccess_describe.setText(orderInfo.getCrDescribe());
        tv_newOrderSuccess_user.setText(logininfo.getUser_name());
        tv_newOrderSuccess_phone.setText(logininfo.getUser_phone());
        tv_newOrderSuccess_address.setText(orderInfo.getAddress());

        File files = new File(GlobalManager.photoPath);
        if (!files.exists()) {
            files.mkdirs();
        }
        photo_files = new ArrayList<>();
        List<OrderFileInfo> orderFiles = orderInfo.getFiles();
        if (orderFiles!=null&&orderFiles.size()>0){
            for (OrderFileInfo info : orderFiles) {
                photo_files.add(info.getFile_url());
            }
        }
        LinearLayoutManager linearlayout = new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false);
        MyDecoration decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        rv_newOrderSuccess_photos.setLayoutManager(linearlayout);
        rv_newOrderSuccess_photos.addItemDecoration(decoration);
        photosAdapter = new DevicePhotoAdapter(photo_files, getMe());
        rv_newOrderSuccess_photos.setAdapter(photosAdapter);
    }

    @Override
    public void onBackPressed() {
        //禁止返回按键
    }

    @OnClick({R.id.iv_newOrderSuccess_takephone, R.id.bt_newOrderSuccess_toBack, R.id.bt_newOrderSuccess_toMain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_newOrderSuccess_takephone:
                AppUtils.callPhone("12345678900", this);
                break;
            case R.id.bt_newOrderSuccess_toBack:
                startActivity(new Intent(this, NewOrderActivity.class));
                break;
            case R.id.bt_newOrderSuccess_toMain:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
