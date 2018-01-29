package com.example.yb.hstt.UI.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yb.hstt.Adpater.DeleteFileListener;
import com.example.yb.hstt.Adpater.DevicePhotoAdapter;
import com.example.yb.hstt.Adpater.DeviceVideoAdapter;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.EventBus.RecorderEvent;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.Tasklist.TaskFinishActivity;
import com.example.yb.hstt.UI.Dialog.CenterDialog;
import com.example.yb.hstt.Utils.ImageCompressUtil;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.record.MediaRecorderActivity;
import com.example.yb.hstt.zxing.activity.CaptureActivity;
import com.example.yb.hstt.zxing.activity.CodeUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeDeviceActivity extends HsttBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, DeleteFileListener {
    public static final String TAG = "ChangeDeviceActivity";
    /**
     * 标题栏
     */
    @BindView(R.id.tb_changeDevice)
    Toolbar tb_changeDevice;
    //旧设备布局show-hide
    @BindView(R.id.ll_changeDevice_oldLayoutDealWith)
    LinearLayout ll_changeDevice_oldLayoutDealWith;
    @BindView(R.id.ll_changeDevice_oldLayoutInfo)
    LinearLayout llChangeDeviceOldLayoutInfo;

    //新设备布局show-hide
    @BindView(R.id.ll_changeDevice_newLayoutDealWith)
    LinearLayout ll_changeDevice_newLayoutDealWith;
    @BindView(R.id.ll_changeDevice_newLayoutInfo)
    LinearLayout ll_changeDevice_newLayoutInfo;
    //旧设备拍照列表
    @BindView(R.id.rl_changeDevice_oldDevicePhotoTag)
    RelativeLayout rl_changeDevice_oldDevicePhotoTag;
    //旧设备录制视频列表
    @BindView(R.id.rl_changeDevice_oldDeviceVideoTag)
    RelativeLayout rl_changeDevice_oldDeviceVideoTag;
    //新设备拍照列表
    @BindView(R.id.rl_changeDevice_newDevicePhotoTag)
    RelativeLayout rl_changeDevice_newDevicePhotoTag;
    //新设备录制视频列表
    @BindView(R.id.rl_changeDevice_newDeviceVideoTag)
    RelativeLayout rl_changeDevice_newDeviceVideoTag;

    @BindView(R.id.tv_changeDevice_oldDeviceType)
    TextView tv_changeDevice_oldDeviceType;
    @BindView(R.id.tv_changeDevice_oldDeviceFactory)
    TextView tv_changeDevice_oldDeviceFactory;
    @BindView(R.id.tv_changeDevice_newDeviceType)
    TextView tv_changeDevice_newDeviceType;
    @BindView(R.id.tv_changeDevice_newDeviceFactory)
    TextView tv_changeDevice_newDeviceFactory;

    //操作标签
    @BindView(R.id.rg_changeDevice_old)
    RadioGroup rg_changeDevice_old;
    @BindView(R.id.rg_changeDevice_new)
    RadioGroup rg_changeDevice_new;
    //扫码
    @BindView(R.id.tv_changeDevice_oldDeviceid)
    TextView tv_changeDevice_oldDeviceid;
    @BindView(R.id.tv_changeDevice_newDeviceid)
    TextView tv_changeDevice_newDeviceid;
    /**
     * 旧设备拍照rv
     */
    @BindView(R.id.rv_changeDevice_oldphotos)
    RecyclerView rv_changeDevice_oldphotos;
    /**
     * 旧设备录制视频rv
     */
    @BindView(R.id.rv_changeDevice_oldvideos)
    RecyclerView rv_changeDevice_oldvideos;
    /**
     * 新设备拍照rv
     */
    @BindView(R.id.rv_changeDevice_newphotos)
    RecyclerView rv_changeDevice_newphotos;
    /**
     * 新设备录制视频rv
     */
    @BindView(R.id.rv_changeDevice_newvideos)
    RecyclerView rv_changeDevice_newvideos;

    @BindView(R.id.tv_changeDevice_oldDevicePhotoTimeTag)
    TextView tv_changeDevice_oldDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDevice_oldDeviceVideoTimeTag)
    TextView tv_changeDevice_oldDeviceVideoTimeTag;
    @BindView(R.id.tv_changeDevice_newDevicePhotoTimeTag)
    TextView tv_changeDevice_newDevicePhotoTimeTag;
    @BindView(R.id.tv_changeDevice_newDeviceVideoTimeTag)
    TextView tv_changeDevice_newDeviceVideoTimeTag;
    @BindView(R.id.et_changeDevice_OldWorkResult)
    EditText et_changeDevice_OldWorkResult;
    @BindView(R.id.et_changeDevice_NewWorkResult)
    EditText et_changeDevice_NewWorkResult;
    @BindView(R.id.bt_more)
    Button btMore;
    @BindView(R.id.v_trans)
    View vTrans;
    @BindView(R.id.activity_change_device)
    LinearLayout activityChangeDevice;
    /**
     * 照片文件名
     */
    private String filename;
    private List<String> oldPhoto_paths = new ArrayList<>();
    private List<String> newPhoto_paths = new ArrayList<>();
    //    private List<String> oldVideo_paths = new ArrayList<>();
//    private List<String> newVideo_paths = new ArrayList<>();
    private List<String> oldVideo_Mp4paths = new ArrayList<>();
    private List<String> newVideo_Mp4paths = new ArrayList<>();
    //回收箭头
    private Drawable drawable_down = null;
    private Drawable drawable_up = null;
    /**
     * 录制视频缓存
     */
    private File VIDEO_DIR = new File(GlobalManager.VideoPath);
    //更换设备录制视频标记
    private static final String changeDevice_old = "CHANGEDEVICE_OLD";
    private static final String changeDevice_new = "CHANGEDEVICE_NEW";
    /**
     * 图片列表的分割线
     */
    private MyDecoration photo_list_decoration = null;
    /**
     * 更换（编辑）用的外勤工单id
     */
    private String owwoid;
    /**
     * 更换（编辑）用的设备表ID
     */
    private String change_cdcid, edit_cdcid;
    /**
     * 更换（编辑）用的处理过程ID
     */
    private String change_owrdpId, edit_owwrdpId;
    /**
     * 是否是编辑
     */
    private boolean is_Edit;

    /**
     * 位置类
     */
    public LocationClient mLocationClient = null;
    private String latitude;
    private String longitude;
    /**
     * 定位监听
     */
    public BDAbstractLocationListener myListener = new MyLocationListenner();
    private HashMap task_info;
    private PopupWindow more_menu;
    private String pop_title;
    private CenterDialog dialog;
    private String dev_info;

    /**
     * 删除网络文件
     *
     * @param rv_id
     * @param pos
     */
    @Override
    public void deleteFile(int rv_id, int pos) {
        switch (rv_id) {
            case R.id.rv_changeDevice_oldphotos:
                deleteFilesById(pos, oldPhoto_paths, rv_changeDevice_oldphotos);
                break;
            case R.id.rv_changeDevice_oldvideos:
                deleteFilesById(pos, oldVideo_Mp4paths, rv_changeDevice_oldvideos);
                break;
            case R.id.rv_changeDevice_newphotos:
                deleteFilesById(pos, newPhoto_paths, rv_changeDevice_newphotos);
                break;
            case R.id.rv_changeDevice_newvideos:
                deleteFilesById(pos, newVideo_Mp4paths, rv_changeDevice_newvideos);
                break;
        }
    }

    public class MyLocationListenner extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            if (!TextUtils.isEmpty(latitude) || !TextUtils.isEmpty(longitude)) {
                mLocationClient.stop();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changedevice);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(tb_changeDevice);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setLocationOption();
        owwoid = getIntent().getStringExtra("owwoId");
        edit_cdcid = getIntent().getStringExtra("cd_id");
        is_Edit = getIntent().getBooleanExtra("is_Edit", false);
        task_info = (HashMap) getIntent().getSerializableExtra("TASK_INFO");
        tb_changeDevice.setNavigationOnClickListener(this);
        rg_changeDevice_old.setOnCheckedChangeListener(this);
        rg_changeDevice_new.setOnCheckedChangeListener(this);
        drawable_down = getDrawable(R.drawable.ic_down);
        drawable_up = getDrawable(R.drawable.ic_up);
        photo_list_decoration = new MyDecoration(getMe(), MyDecoration.HORIZONTAL_LIST, R.drawable.recycle_21px_dividing);
        btMore.setOnClickListener(this);
        //设置最后一个默认选中
        RadioButton old_child = (RadioButton) rg_changeDevice_old.getChildAt(rg_changeDevice_old.getChildCount() - 1);
        RadioButton new_child = (RadioButton) rg_changeDevice_new.getChildAt(rg_changeDevice_new.getChildCount() - 1);
        old_child.setChecked(true);
        new_child.setChecked(true);
        //初始化文件的标记时间
        String curTime = MyDateUtils.getCurTime("yyyy.MM.dd");
        tv_changeDevice_oldDevicePhotoTimeTag.setText(curTime);
        tv_changeDevice_oldDeviceVideoTimeTag.setText(curTime);
        tv_changeDevice_newDevicePhotoTimeTag.setText(curTime);
        tv_changeDevice_newDeviceVideoTimeTag.setText(curTime);

        //初始化旧设备录制图片的列表
        initRecycleview(rv_changeDevice_oldphotos, new DevicePhotoAdapter(oldPhoto_paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));
        //初始化旧设备录制视频的列表
        initRecycleview(rv_changeDevice_oldvideos, new DeviceVideoAdapter(oldVideo_Mp4paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));
        //初始化新设备拍照的列表
        initRecycleview(rv_changeDevice_newphotos, new DevicePhotoAdapter(newPhoto_paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));
        //初始化新设备录制视频的列表
        initRecycleview(rv_changeDevice_newvideos, new DeviceVideoAdapter(newVideo_Mp4paths, getMe(), true, is_Edit).setOnDeleteFileListener(is_Edit ? this : null));
        if (is_Edit) {
            rl_changeDevice_oldDevicePhotoTag.setVisibility(View.VISIBLE);
            rl_changeDevice_oldDeviceVideoTag.setVisibility(View.VISIBLE);
            rl_changeDevice_newDevicePhotoTag.setVisibility(View.VISIBLE);
            rl_changeDevice_newDeviceVideoTag.setVisibility(View.VISIBLE);
            //查询更换的信息
            searchChangeDeviceInfo(edit_cdcid);
        }
        initMoreMenu();
        initDevInfo();
    }

    private void initRecycleview(RecyclerView recyclerView, RecyclerView.Adapter devicePhotoAdapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getMe(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(photo_list_decoration);
        recyclerView.setAdapter(devicePhotoAdapter);
    }

    private void setLocationOption() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(1500);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
        //注册定位的监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }

    @OnClick({R.id.tv_changeDevice_oldDeviceid, R.id.tv_changeDevice_newDeviceid, R.id.iv_changeDevice_oldtakephoto,
            R.id.iv_changeDevice_oldtakevideo, R.id.iv_changeDevice_newtakevideo, R.id.tv_changeDevice_oldLayoutDealWith,
            R.id.tv_changeDevice_newLayoutDealWith, R.id.tv_changeDevice_oldLayoutInfo,
            R.id.tv_changeDevice_newLayoutInfo, R.id.iv_changeDevice_newtakephoto, R.id.bt_changeDevice_save})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_changeDevice_oldDeviceid:
                ScanDeviceId(100);
                break;
            case R.id.tv_changeDevice_newDeviceid:
                ScanDeviceId(101);
                break;
            case R.id.iv_changeDevice_oldtakephoto:
                TakePhoto(104);
                break;
            case R.id.iv_changeDevice_newtakephoto:
                TakePhoto(105);
                break;
            case R.id.iv_changeDevice_oldtakevideo:
                toRecordVideo(changeDevice_old);
                break;
            case R.id.iv_changeDevice_newtakevideo:
                toRecordVideo(changeDevice_new);
                break;
            case R.id.tv_changeDevice_oldLayoutDealWith:
                LayoutShowHide(ll_changeDevice_oldLayoutDealWith, (TextView) view);
                break;
            case R.id.tv_changeDevice_newLayoutDealWith:
                LayoutShowHide(ll_changeDevice_newLayoutDealWith, (TextView) view);
                break;
            case R.id.tv_changeDevice_oldLayoutInfo:
                LayoutShowHide(llChangeDeviceOldLayoutInfo, (TextView) view);
                break;
            case R.id.tv_changeDevice_newLayoutInfo:
                LayoutShowHide(ll_changeDevice_newLayoutInfo, (TextView) view);
                break;
            case R.id.bt_changeDevice_save:
                if (is_Edit) {
                    Edit_ChangedDevice(edit_cdcid);
                } else {
                    ChangeDevice();
                }
                break;
        }
    }

    /**
     * 编辑已更换设备
     */
    private void Edit_ChangedDevice(final String cdcid) {
        oldDevid = tv_changeDevice_oldDeviceid.getText().toString();
        newDevId = tv_changeDevice_newDeviceid.getText().toString();
        HttpParams params = new HttpParams();
        params.put("cdcId", cdcid);
        params.put("oldDevId", oldDevid);
        params.put("oldDevComment", et_changeDevice_OldWorkResult.getText().toString());
        params.put("newDevId", newDevId);
        params.put("newDevComment", et_changeDevice_NewWorkResult.getText().toString());
        OkHttpUtils.post(GlobalUrl.TheOrder_EditChangedDevice).params(params).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "bt_changeDevice_save: " + map);
                if (map.get("code").toString().equals("10000")) {
                    List<String> local_oldPhoto_paths = getLocalFiles(oldPhoto_paths);//旧。。图片
                    List<String> local_oldVideo_Mp4paths = getLocalFiles(oldVideo_Mp4paths);//旧。。视频
                    List<String> local_newPhoto_paths = getLocalFiles(newPhoto_paths);//新。。图片
                    List<String> local_newVideo_Mp4paths = getLocalFiles(newVideo_Mp4paths);//新。。视频

                    if (local_oldPhoto_paths.size() == 0 && local_oldVideo_Mp4paths.size() == 0 && local_newPhoto_paths.size() == 0 && local_newVideo_Mp4paths.size() == 0) {
                        ToNext(cdcid);
                        return;
                    }
                    showProgress("上传中...");
                    if (local_oldPhoto_paths.size() != 0 || local_oldVideo_Mp4paths.size() != 0) {
                        need_up_oldFile = true;
                    }
                    if (local_newPhoto_paths.size() != 0 || local_newVideo_Mp4paths.size() != 0) {
                        need_up_newFile = true;
                    }
                    if (need_up_oldFile) {
                        submitOldDeviceFile(local_oldPhoto_paths, local_oldVideo_Mp4paths, oldDevid, edit_owwrdpId, cdcid, true);
                    }
                    if (need_up_newFile) {
                        submitOldDeviceFile(local_newPhoto_paths, local_newVideo_Mp4paths, newDevId, edit_owwrdpId, cdcid, false);
                    }
                }
            }
        });
    }

    /**
     * 从所有文件集合中，返回本地文件集合
     *
     * @param files
     * @return
     */
    private List<String> getLocalFiles(List<String> files) {
        List<String> localFiles = new ArrayList<>();
        for (String path : files) {
            if (path.startsWith("file")) {
                localFiles.add(path);
            }
        }
        return localFiles;
    }

    /**
     * 删除附件
     */
    private void deleteFilesById(final int pos, final List<String> paths, final RecyclerView recycler) {
        String path = paths.get(pos);
        String owwoAttaId = path.split("@@")[1];
        OkHttpUtils.post(GlobalUrl.TheOrder_FinishedFileDelete).params("owwoAttaId", owwoAttaId).execute(new CommonCallback<Map>(getMe(), "删除中。。。") {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                Log.i(TAG, "TheOrder_FinishedFileDelete: " + map);
                if (map.get("code").toString().equals("10000")) {
                    paths.remove(pos);
                    recycler.getAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    private String oldDevid, newDevId;

    /**
     * 更换设备，提交文本
     */
    private void ChangeDevice() {
        oldDevid = tv_changeDevice_oldDeviceid.getText().toString();
        newDevId = tv_changeDevice_newDeviceid.getText().toString();
        HttpParams params = new HttpParams();
        params.put("owwoId", owwoid);
        params.put("oldDevId", oldDevid);
        params.put("oldDevComment", et_changeDevice_OldWorkResult.getText().toString());
        params.put("newDevId", newDevId);
        params.put("newDevComment", et_changeDevice_NewWorkResult.getText().toString());
        params.put("currentLng", longitude);
        params.put("currentLat", latitude);

        OkHttpUtils.post(GlobalUrl.Change_Device).params(params).execute(new CommonCallback<Map>(getMe()) {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                int code = (int) map.get("code");
                if (code == 10000) {
                    Map data = (Map) map.get("data");
                    change_owrdpId = String.valueOf(data.get("owrdpId"));
                    change_cdcid = String.valueOf(data.get("cdcId"));

                    if (oldPhoto_paths.size() == 0 && oldVideo_Mp4paths.size() == 0 && newPhoto_paths.size() == 0 && newVideo_Mp4paths.size() == 0) {
                        ToNext(change_cdcid);
                        return;
                    }
                    showProgress("上传中...");
                    if (oldPhoto_paths.size() != 0 || oldVideo_Mp4paths.size() != 0) {
                        need_up_oldFile = true;
                    }
                    if (newPhoto_paths.size() != 0 || newVideo_Mp4paths.size() != 0) {
                        need_up_newFile = true;
                    }
                    if (need_up_oldFile) {
                        submitOldDeviceFile(oldPhoto_paths, oldVideo_Mp4paths, oldDevid, change_owrdpId, change_cdcid, true);
                    }
                    if (need_up_newFile) {
                        submitOldDeviceFile(newPhoto_paths, newVideo_Mp4paths, newDevId, change_owrdpId, change_cdcid, false);
                    }
                } else {
                    ToastUtil.showToast(getMe(), "数据异常！");
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
            }
        });
    }

    /**
     * 查询更换设备信息
     */
    private void searchChangeDeviceInfo(String cdcId) {
        HttpParams params = new HttpParams();
        params.put("cdcId", cdcId);
        OkHttpUtils.get(GlobalUrl.Change_DeviceInfo).params(params)
                .execute(new CommonCallback<Map>(getMe(), true) {
                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        Log.i(TAG, "Change_DeviceInfo: " + map);
                        String code = map.get("code").toString();
                        if (code.equals("10000")) {
                            Map data = (Map) map.get("data");
                            List<Map> oldInfo = (List<Map>) data.get("oldInfo");
                            setDeviceInfo(oldInfo, tv_changeDevice_oldDeviceType, tv_changeDevice_oldDeviceid,
                                    tv_changeDevice_oldDeviceFactory, et_changeDevice_OldWorkResult);
                            List<Map> select_infos = (List<Map>) data.get("newInfo");
                            setDeviceInfo(select_infos, tv_changeDevice_newDeviceType, tv_changeDevice_newDeviceid,
                                    tv_changeDevice_newDeviceFactory, et_changeDevice_NewWorkResult);

                            List<Map> oldFiles = (List<Map>) data.get("oldFiles");
                            setDeviceFileInfo(oldFiles, true);
                            List<Map> select_files = (List<Map>) data.get("newFiles");
                            setDeviceFileInfo(select_files, false);
                        }
                    }
                });
    }

    /**
     * 设置设备类型
     *
     * @param deviceInfo
     * @param tv_deviceid
     */
    private void setDeviceInfo(List<Map> deviceInfo, TextView tv_deviceType, TextView tv_deviceid, TextView tv_factory, TextView tv_workResult) {
        if (deviceInfo != null && deviceInfo.size() > 0) {
            String factory = deviceInfo.get(0).get("devManufacturerNo").toString();
            String deviceType = deviceInfo.get(0).get("devTypeNo").toString();
            String devId = deviceInfo.get(0).get("devId").toString();
            String comment = deviceInfo.get(0).get("comment").toString();
            edit_owwrdpId = deviceInfo.get(0).get("owrdpId").toString();
            tv_deviceType.setText(deviceType);
            tv_factory.setText(factory);
            tv_deviceid.setText(devId);
            tv_workResult.setText(comment);
        }
    }

    /**
     * 设置设备的文件信息
     *
     * @param Files
     * @param is_old
     */
    private void setDeviceFileInfo(List<Map> Files, boolean is_old) {
        for (int i = 0; i < Files.size(); i++) {
            Map files = Files.get(i);
            String attaFilePath = (String) files.get("attaFilePath");
            String owwoAttaId = files.get("owwoAttaId").toString();
            if (is_old) {
                if (attaFilePath.contains("jpg")) {
                    oldPhoto_paths.add(attaFilePath + "@@" + owwoAttaId);
                } else {
                    oldVideo_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
                }
            } else {
                if (attaFilePath.contains("jpg")) {
                    newPhoto_paths.add(attaFilePath + "@@" + owwoAttaId);
                } else {
                    newVideo_Mp4paths.add(attaFilePath + "@@" + owwoAttaId);
                }
            }
        }

        if (is_old) {
            rv_changeDevice_oldphotos.getAdapter().notifyDataSetChanged();
            rv_changeDevice_oldvideos.getAdapter().notifyDataSetChanged();
        } else {
            rv_changeDevice_newphotos.getAdapter().notifyDataSetChanged();
            rv_changeDevice_newvideos.getAdapter().notifyDataSetChanged();
        }
    }


    //新旧设备信息上传标志
    private int is_success_uploadOld = -1, is_success_uploadNew = -1;//1，成功。0，失败。
    private boolean need_up_oldFile = false, need_up_newFile = false;

    /**
     * 上传文件
     */
    private void submitOldDeviceFile(List<String> photoFiles, List<String> videoFiles, final String deviceId, String owrdpId, final String cdcId, final boolean is_OldDevice) {
        List<File> files = new ArrayList<>();
        for (String path : photoFiles) {
            files.add(new File(path.replace("file://", "")));
        }
        for (String path : videoFiles) {
            files.add(new File(path.replace("file://", "")));
        }
        HttpParams params = new HttpParams();
        params.put("owrdpId", owrdpId);
        params.put("owwoId", owwoid);
        params.put("cdcId", cdcId);
        params.put("devId", deviceId);
        params.put("attaLng", longitude);
        params.put("attaLat", latitude);
        OkHttpUtils.post(GlobalUrl.Change_DeviceUpLoad).addFileParams("file", files).params(params)
                .execute(new CommonCallback<String>(getMe()) {
                    @Override
                    public void onResponse(boolean b, String s, Request request, @Nullable Response response) {
                        try {
                            Log.i(TAG, "onResponse: " + s);
                            JSONObject jsonObject = new JSONObject(s);
                            String code = jsonObject.getString("code");
                            if (is_OldDevice) {
                                is_success_uploadOld = code.equals("10000") ? 1 : 0;
                            } else {
                                is_success_uploadNew = code.equals("10000") ? 1 : 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        Log.i(TAG, "submitOldDeviceFile_onError: " + e);
                        if (is_OldDevice) {
                            is_success_uploadOld = 0;
                        } else {
                            is_success_uploadNew = 0;
                        }
                    }

                    @Override
                    public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onAfter(isFromCache, s, call, response, e);
                        upLoadAfter(cdcId);
                    }
                });
    }

    /**
     * 上传完后
     */
    private void upLoadAfter(String cdcId) {
        //都执行后，
        if (need_up_oldFile && (!need_up_newFile)) {
            if (is_success_uploadOld != -1) {
                dismissProgress();
                if (is_success_uploadOld == 1) {
                    ToNext(cdcId);
                } else {
                    ToastUtil.showToast(getMe(), "上传失败！");
                }
            }
        } else if (need_up_newFile && (!need_up_oldFile)) {
            if (is_success_uploadNew != -1) {
                dismissProgress();
                if (is_success_uploadNew == 1) {
                    ToNext(cdcId);
                } else {
                    ToastUtil.showToast(getMe(), "上传失败！");
                }
            }
        } else if (need_up_newFile && need_up_oldFile) {
            if (is_success_uploadNew != -1 && is_success_uploadOld != -1) {
                dismissProgress();
                if (is_success_uploadNew == 1 && is_success_uploadOld == 1) {
                    ToNext(cdcId);
                } else {
                    ToastUtil.showToast(getMe(), "上传失败！");
                }
            }
        }
    }

    /**
     * 下一步
     */
    private void ToNext(String cdcId) {
        Intent nextIntent = new Intent(getMe(), ChangeDeviceSuccessActivity.class);
        nextIntent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
        nextIntent.putExtra("cdcId", cdcId);
        startActivity(nextIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                returnBack();
                break;
            case R.id.bt_more:
                setScreenDark();
                more_menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                more_menu.setAnimationStyle(R.style.popWindow_animation);
                more_menu.showAtLocation(activityChangeDevice, Gravity.BOTTOM, 0, 0);//parent为泡泡窗体所在的父容器view
                initListener();
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;

            case R.id.rl_task_return:
                Log.i(TAG, "回退工单");
                pop_title = "回退原因";
                showMyDialog();
                break;
            case R.id.rl_task_end:
                Log.i(TAG, "结束工单");
                pop_title = "回退原因";
                showMyDialog();
                break;
            case R.id.rl_do_together://协作完成
                pop_title = "协作完成";
                showMyDialog();
                break;
            case R.id.rl_return_main://返回首页
                Intent intent = new Intent(getMe(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 展示回退/结束工单按钮之后的布局
     */
    private void showMyDialog() {
        int[] views = new int[]{R.id.cb_reason_a,
                R.id.cb_reason_b,
                R.id.cb_reason_c,
                R.id.bt_confirm,
                R.id.bt_cnacel};
        dialog = new CenterDialog(getMe(), R.layout.dialog_return_end, views);

        dialog.show();
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_reason);
        final TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        final ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_dialog_title.setText(pop_title);

        dialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
            @Override
            public void OnCenterItemClick(CenterDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.cb_reason_a:
                        et_content.setText("无法处理");
                        break;
                    case R.id.cb_reason_b:
                        et_content.setText("其他人处理");
                        break;
                    case R.id.cb_reason_c:
                        et_content.setText("已完成");
                        break;
                    case R.id.bt_confirm:
                        String comment = et_content.getText().toString();
                        if (!TextUtils.isEmpty(comment)) {
                            if (pop_title.contains("回退")) {
                                // 走退回接口
                                task_opreate(GlobalUrl.Task_Back, comment);
                            } else if (pop_title.contains("结束")) {
                                // 走结束接口
                                task_opreate(GlobalUrl.Task_Finish, comment);
                            } else if (pop_title.contains("协作")) {
                                // 走协作接口
                                task_opreate(GlobalUrl.Finish_Together, comment);
                            }
                        } else {
                            ToastUtil.showToast(getMe(), "原因不能为空");
                        }

                        break;
                    case R.id.bt_cnacel:
                        Log.i(TAG, "关闭弹窗");
                        dialog.dismiss();
                        break;
                    case R.id.iv_close:
                        Log.i(TAG, "关闭弹窗");
                        dialog.dismiss();
                        break;
                }
            }
        });

    }

    /**
     * 对工单的操作,包括退回和结束工单
     *
     * @param task_back
     */
    private void task_opreate(final String task_back, String comment) {
        OkHttpUtils.post(task_back)
                .params("owwoId", owwoid)
                .params("comment", comment)
                .params("currentLng", String.valueOf(longitude))
                .params("currentLat", String.valueOf(longitude))
                .execute(new CommonCallback<Map>(getMe(),true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        int code = (int) map.get("code");
                        if (code == 10000) {


                            try {
                                //获取步骤id,赋值给全局变量以供上传位置服务使用
                                HashMap map1 = (HashMap) map.get("data");
                                String owrdpId = String.valueOf(map1.get("owrdpId"));
                                AppPreferences preferences = new AppPreferences(getMe());
                                preferences.put("owrdpId", owrdpId);
                            } catch (Exception e) {
                                Log.i(TAG, "类型转换异常");
                            }

                            if (task_back.contains("302")) {
                                //回退接口,进入主页面
                                Intent intent = new Intent(getMe(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (task_back.contains("303")) {
                                //结束接口,也进入结束工单页面
                                Intent intent = new Intent(getMe(), TaskFinishActivity.class);
                                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                                intent.putExtra("DEVICE_INFO", dev_info);//问题设备信息
                                intent.putExtra("PICS", dev_info);//问题设备信息
                                startActivity(intent);
                            } else if (task_back.contains("305")) {
                                //协作接口
                                Intent intent = new Intent(getMe(), TaskFinishActivity.class);
                                intent.putExtra("TASK_INFO", task_info);//工单信息,不包含问题设备信息
                                intent.putExtra("DEVICE_INFO", dev_info);//问题设备信息
                                intent.putExtra("PICS", dev_info);//问题设备信息
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.showToast(getMe(), "数据异常！");
                        }

                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                    }
                });
    }

    /**
     * 获取设备信息,包括设备品牌及厂家
     */
    private void initDevInfo() {
        OkHttpUtils.get(GlobalUrl.Get_TaskInfo)
                .params("owwoId", String.valueOf(task_info.get("owwoId")))
                .execute(new CommonCallback<Map>(getMe(),true) {

                    @Override
                    public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                        try {
                            int res_code = (int) map.get("code");
                            if (res_code == 10000) {
                                HashMap task_data = (HashMap) map.get("data");
                                ArrayList dev_datas = (ArrayList) task_data.get("devs");
                                //设备信息不唯一,需要遍历
                                String s_dev_type = "";
                                String s_dev_factory = "";
                                dev_info = "";
                                Iterator ite = dev_datas.iterator();
                                if (!ite.hasNext()) {
                                } else {
                                    while (ite.hasNext()) {
                                        HashMap dev = (HashMap) ite.next();
                                        s_dev_type = s_dev_type + String.valueOf(dev.get("devTypeNo"));
                                        s_dev_factory = s_dev_factory + String.valueOf(dev.get("devManufacturerNo"));
                                        dev_info = s_dev_type + "-" + s_dev_factory;
                                        if (ite.hasNext()) {
                                            dev_info = dev_info + "\n";
                                        }
                                    }

                                }


                            } else {
                                ToastUtil.showToast(getMe(), "请求任务信息失败,错误码: " + res_code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (null != e) {
                            ToastUtil.showToast(getMe(), "请求任务信息失败" + e.toString());
                        } else {
                            ToastUtil.showToast(getMe(), "请求任务信息失败");
                        }
                    }
                });
    }

    /**
     * 初始化弹窗的点击事件
     */
    private void initListener() {
        View view = more_menu.getContentView();
        RelativeLayout rl_task_return = (RelativeLayout) view.findViewById(R.id.rl_task_return);
        RelativeLayout rl_task_end = (RelativeLayout) view.findViewById(R.id.rl_task_end);
        rl_task_return.setOnClickListener(this);
        rl_task_end.setOnClickListener(this);
        RelativeLayout rl_do_together = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_do_together);
        RelativeLayout rl_return_main = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_return_main);
        rl_do_together.setOnClickListener(this);
        rl_return_main.setOnClickListener(this);
    }

    /**
     * 初始化"更多"弹窗
     */
    private void initMoreMenu() {
        View view = View.inflate(getMe(), R.layout.more_menu, null);
        more_menu = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        more_menu.setBackgroundDrawable(new BitmapDrawable());    //设置背景，否则setOutsideTouchable无效
        more_menu.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        more_menu.setFocusable(true);
        more_menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setScreenLight();
            }
        });

        RelativeLayout rl_task_return = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_task_return);
        RelativeLayout rl_task_end = (RelativeLayout) more_menu.getContentView().findViewById(R.id.rl_task_end);
        rl_task_return.setOnClickListener(this);
        rl_task_end.setOnClickListener(this);
    }

    private void setScreenDark() {
        vTrans.setVisibility(View.VISIBLE);
        WindowUtil.setWindowStatusBarColor(ChangeDeviceActivity.this, R.color.black_trans);
    }

    private void setScreenLight() {
        vTrans.setVisibility(View.GONE);
        WindowUtil.setWindowStatusBarColor(ChangeDeviceActivity.this, R.color.transparent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = ButterKnife.findById(group, checkedId);
        switch (group.getId()) {
            case R.id.rg_changeDevice_old:
                et_changeDevice_OldWorkResult.setText(radioButton.getText().toString());
                break;
            case R.id.rg_changeDevice_new:
                et_changeDevice_NewWorkResult.setText(radioButton.getText().toString());
                break;
        }
    }

    /**
     * 去录制视频
     *
     * @param functionLabel
     */
    private void toRecordVideo(String functionLabel) {
        if (!VIDEO_DIR.exists()) {
            VIDEO_DIR.mkdirs();
        }
        Intent intent = new Intent(this, MediaRecorderActivity.class);
        intent.putExtra("filename", MyDateUtils.getCurTime("yyyy-MM-dd_hhmmss"));
        intent.putExtra("dir", VIDEO_DIR.toString());
        intent.putExtra("functionLabel", functionLabel);
        startActivity(intent);
    }

    /**
     * 录制完视频回调
     *
     * @param event
     */
    @Subscribe
    public void RecorderEvent(RecorderEvent event) {
        String functionL = event.getFunctionLabel();
        String path = event.getMsg();
        switch (functionL) {
            case changeDevice_old:
//                oldVideo_paths.add("file://" + path + ".jpg");
                oldVideo_Mp4paths.add("file://" + path + ".mp4");
                if (!rl_changeDevice_oldDeviceVideoTag.isShown()) {
                    rl_changeDevice_oldDeviceVideoTag.setVisibility(View.VISIBLE);
                }
                rv_changeDevice_oldvideos.getAdapter().notifyDataSetChanged();
                rv_changeDevice_oldvideos.smoothScrollToPosition(oldVideo_Mp4paths.size() - 1);
                break;
            case changeDevice_new:
//                newVideo_paths.add("file://" + path + ".jpg");
                newVideo_Mp4paths.add("file://" + path + ".mp4");
                if (!rl_changeDevice_newDeviceVideoTag.isShown()) {
                    rl_changeDevice_newDeviceVideoTag.setVisibility(View.VISIBLE);
                }
                rv_changeDevice_newvideos.getAdapter().notifyDataSetChanged();
                rv_changeDevice_newvideos.smoothScrollToPosition(newVideo_Mp4paths.size() - 1);
                break;
        }
    }

    /**
     * 扫描获取设备编号
     *
     * @param requestCode
     */
    private void ScanDeviceId(int requestCode) {
        Intent intent = new Intent(getMe(), CaptureActivity.class);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 布局先是隐藏
     *
     * @param v2 要显示或隐藏的布局
     * @param v1 点击的布局
     */
    private void LayoutShowHide(LinearLayout v2, TextView v1) {
        boolean shown = v2.isShown();
        v2.setVisibility(shown ? View.GONE : View.VISIBLE);
        v1.setCompoundDrawablesWithIntrinsicBounds(null, null, shown ? drawable_up : drawable_down, null);
    }

    /**
     * 调用本机摄像头拍照
     *
     * @param requestCode 请求码
     */
    private void TakePhoto(int requestCode) {
        File PHOTO_DIR = new File(GlobalManager.photoPath);
        // 创建照片的存储目录
        if (!PHOTO_DIR.exists()) {
            PHOTO_DIR.mkdirs();
        }
        filename = MyDateUtils.getCurTimeFormat(MyDateUtils.date_Format3);
        File out = new File(PHOTO_DIR, filename + ".jpg");
        // 创建照片的存储目录
        if (!PHOTO_DIR.exists()) {
            PHOTO_DIR.mkdirs();
        }
        Uri uri = Uri.fromFile(out);
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.8);
        startActivityForResult(imageCaptureIntent, requestCode);
    }

    /**
     * 扫设备二维码回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100://扫码
            case 101://扫码
                if (data != null) {
                    Bundle extras = data.getExtras();
                    int resultInt = extras.getInt(CodeUtils.RESULT_TYPE);
                    if (resultInt == CodeUtils.RESULT_SUCCESS) {
                        String result = extras.getString(CodeUtils.RESULT_STRING);
                        selectDeviceinfo(requestCode, result);
                    }
                }
                break;
            case 104://拍照
            case 105:
                NotifyPhotoDataChanged(requestCode);
                break;
        }
    }

    /**
     * 查询设备编号
     *
     * @param requestCode
     * @param devAssetCode
     */
    private void selectDeviceinfo(final int requestCode, final String devAssetCode) {
        OkHttpUtils.get(GlobalUrl.sys_url + "/device/queryDeviceInfo")
                .params("devAssetCode", devAssetCode).execute(new CommonCallback<Map>(getMe()) {
            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                String code = String.valueOf(map.get("code"));
                if (code.equals("10000")) {
                    Map device_data = (Map) map.get("data");
                    String devId = device_data.get("devId").toString();
                    String DEV_TYPE_NO = device_data.get("devTypeNo").toString();
                    String DEV_MANUFACTURER_NO = device_data.get("devManufacturerNo").toString();
                    if (requestCode == 100) {
                        tv_changeDevice_oldDeviceid.setText(devId);
                        tv_changeDevice_oldDeviceType.setText(DEV_TYPE_NO);
                        tv_changeDevice_oldDeviceFactory.setText(DEV_MANUFACTURER_NO);
                    } else {
                        tv_changeDevice_newDeviceid.setText(devId);
                        tv_changeDevice_newDeviceType.setText(DEV_TYPE_NO);
                        tv_changeDevice_newDeviceFactory.setText(DEV_MANUFACTURER_NO);
                    }
                }

            }
        });

    }

    /**
     * 拍完照后刷新
     *
     * @param requestCode
     */
    private void NotifyPhotoDataChanged(int requestCode) {
        String filepath = GlobalManager.photoPath + File.separator + filename + ".jpg";
        File file = new File(filepath);
        if (!file.exists()) {
            return;
        }
        //压缩拍后的图片
        ImageCompressUtil.compressBitmap(filepath);
        if (requestCode == 104) { //旧设备 or 新设备
            oldPhoto_paths.add("file://" + filepath);
            if (!rl_changeDevice_oldDevicePhotoTag.isShown()) {
                rl_changeDevice_oldDevicePhotoTag.setVisibility(View.VISIBLE);
            }
            rv_changeDevice_oldphotos.getAdapter().notifyDataSetChanged();
            rv_changeDevice_oldphotos.smoothScrollToPosition(oldPhoto_paths.size() - 1);
        } else if (requestCode == 105) {
            newPhoto_paths.add("file://" + filepath);
            if (!rl_changeDevice_newDevicePhotoTag.isShown()) {
                rl_changeDevice_newDevicePhotoTag.setVisibility(View.VISIBLE);
            }
            rv_changeDevice_newphotos.getAdapter().notifyDataSetChanged();
            rv_changeDevice_newphotos.smoothScrollToPosition(newPhoto_paths.size() - 1);
        }
    }

    @Override
    public void onBackPressed() {
        returnBack();
    }

    AlertDialog Backdialog;

    /**
     * 退出页面提示
     */
    private void returnBack() {
        if (Backdialog == null) {
            Backdialog = new AlertDialog.Builder(getMe())
                    .setTitle("确定退出吗？")
                    .setMessage("退出，将不保存！")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
        }
        Backdialog.show();
    }

}
