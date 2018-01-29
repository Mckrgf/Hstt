package com.example.yb.hstt.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.yb.hstt.Adpater.MyLocationListener;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLocationActivity extends HsttBaseActivity implements BDLocationListener, View.OnClickListener {

    @BindView(R.id.select_location_toolbar)
    Toolbar selectLocationToolbar;
    private MapView mapView = null;
    private BaiduMap mBaiduMap = null;
    private DecimalFormat df40 = new DecimalFormat("#.00000");
    boolean isFirstLoc = true; // 是否首次定位
    private static final String TAG = "DefectSelectLocationAct";
    public LocationClient mLocationClient = null;
    private LatLng position;
    private MyLocationListener listener1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_defect_select_location);
        ButterKnife.bind(this);
        //标题栏初始化
        selectLocationToolbar.setNavigationIcon(R.mipmap.ic_return);
        selectLocationToolbar.setNavigationOnClickListener(this);
        mapView = (MapView) findViewById(R.id.bmapView);
        if (mapView != null) {
            mBaiduMap = mapView.getMap();
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
        }

        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             * @param point 点击的地理坐标
             */
            public void onMapClick(LatLng point) {
                Log.i(TAG, point.toString());
                addOverlay(point.latitude, point.longitude);
                //定义Maker坐标点
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_mark1);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);


                //设置可以拖拽
                OverlayOptions options = new MarkerOptions()
                        .position(point)  //设置marker的位置
                        .icon(bitmap)  //设置marker图标
                        .zIndex(9)  //设置marker所在层级
                        .draggable(true);  //设置手势拖拽
                //将marker添加到地图上
                mBaiduMap.clear();
                Marker marker = (Marker) (mBaiduMap.addOverlay(options));
                position = marker.getPosition();
            }

            /**
             * 地图内 Poi 单击事件回调函数
             * @param poi 点击的 poi 信息
             */
            public boolean onMapPoiClick(MapPoi poi) {
                return false;
            }
        };

        //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }

            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
                Log.i(TAG, marker.toString());
                position = marker.getPosition();

            }

            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
            }
        });


        mBaiduMap.setOnMapClickListener(listener);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        listener1 = new MyLocationListener();
        mLocationClient.registerLocationListener(listener1);
        //注册监听函数
        initLocation();
        mLocationClient.start();//开始定位
        Intent intent = getIntent();
        double xdata = intent.getDoubleExtra("xdata", 0);
        double ydata = intent.getDoubleExtra("ydata", 0);
        if (xdata != 0) {
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_mark1);
            addOverlay(xdata, ydata);
            LatLng latLng = new LatLng(xdata, ydata);
            //设置可以拖拽
            OverlayOptions options = new MarkerOptions()
                    .position(latLng)  //设置marker的位置
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)  //设置marker所在层级
                    .draggable(true);  //设置手势拖拽
            //将marker添加到地图上
            mBaiduMap.clear();
            Marker marker = (Marker) (mBaiduMap.addOverlay(options));
            position = marker.getPosition();
            MapStatusUpdate mapStatus = MapStatusUpdateFactory.newLatLngZoom(latLng, 18);
            mBaiduMap.setMapStatus(mapStatus);
        }

    }

    private void addOverlay(double xdata, double ydata) {
        //定义Maker坐标点
        LatLng point1 = new LatLng(xdata, ydata);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_mark1);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point1)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 按钮点击事件
     */
    public void btnMyloc(View view) {
        finish();
    }

    /**
     * 按钮点击事件
     */
    public void confirm(View view) {
        GlobalManager.position = position;
        finish();
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    /**
     * 监听位置变化的监听器
     */
    @Override
    public void onReceiveLocation(BDLocation arg0) {
        // map view 销毁后不在处理新接收的位置
        if (arg0 == null || mapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder().accuracy(arg0.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(arg0.getLatitude()).longitude(arg0.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        double lat;
        double lng;
        // 格式化坐标，保留小数点后5位
        lat = Double.valueOf(df40.format(arg0.getLatitude()));
        lng = Double.valueOf(df40.format(arg0.getLongitude()));
        // 获取的位置点存入全局变量
        GlobalManager.lat = lat;
        GlobalManager.lng = lng;
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng start = new LatLng(arg0.getLatitude(), arg0.getLongitude());

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(start);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(builder.build()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        mLocationClient.unRegisterLocationListener(listener1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
