package com.example.yb.hstt.UI.Activities.Tasklist;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.example.yb.hstt.Adpater.TaskListAdapter;
import com.example.yb.hstt.Base.GlobalUrl;
import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.Http.CommonCallback;
import com.example.yb.hstt.R;
import com.example.yb.hstt.UI.Activities.WorkingTimeLineActivity;
import com.example.yb.hstt.Utils.AppUtils;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.SystemAppUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.Utils.WindowUtil;
import com.example.yb.hstt.View.ListViewHeaderView;
import com.example.yb.hstt.View.MyDecoration;
import com.example.yb.hstt.dao.DbHelper.TaskInfoDbHelper;
import com.example.yb.hstt.dao.bean.TaskInfo;
import com.lzy.okhttputils.OkHttpUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import net.grandcentrix.tray.AppPreferences;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.yb.hstt.Utils.MyDateUtils.date_Format;

public class TaskListActivity extends HsttBaseActivity implements View.OnClickListener {

    private static final String TAG = "TaskListActivity";

    @BindView(R.id.iv_sort)
    ImageView ivSort;                       //排序图标
    @BindView(R.id.task_list_toolbar)
    Toolbar taskListToolbar;                //标题栏
    @BindView(R.id.rl_need_to_do)
    RelativeLayout rlNeedToDo;              //底部待办
    @BindView(R.id.rl_have_done)
    RelativeLayout rlHaveDone;              //底部已办
    @BindView(R.id.rl_my_task)
    RelativeLayout rlMyTask;                //底部我发起的
    @BindView(R.id.rv_task_list)
    RecyclerView rvTaskList;                //工单列表
    @BindView(R.id.et_search)
    EditText etSearch;                      //搜索
    @BindView(R.id.activity_task_list)
    RelativeLayout activityTaskList;        //主页面
    @BindView(R.id.iv_wait)
    ImageView ivWait;
    @BindView(R.id.tv_need_to_do)
    TextView tvNeedToDo;                    //待办
    @BindView(R.id.iv_finish)
    ImageView ivFinish;
    @BindView(R.id.tv_have_done)
    TextView tvHaveDone;                    //已办
    @BindView(R.id.iv_launch)
    ImageView ivLaunch;
    @BindView(R.id.tv_my_task)
    TextView tvMyTask;                      //我发起的
    @BindView(R.id.yv_nodata)
    TextView yvNodata;                      //没数据

    @BindView(R.id.xrv_refreshOrder)
    XRefreshView xrv_refreshOrder;
    private PopupWindow pop_sort;
    private PopupWindow pop_call;
    private TextView tv_contact_phone;
    private String number;
    private TaskInfoDbHelper helper;
    private ArrayList tasks;
    private ArrayList<HashMap> tasks_selected = new ArrayList<>();
    private boolean sort_type = true;//true为正排序,false为返排序
    private int task_type_selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        helper = TaskInfoDbHelper.getInstance(getMe());
        ButterKnife.bind(this);
        initView();
        initData(GlobalUrl.TaskList_a);
    }

    /**
     * 初始化界面以及点击事件
     */
    private void initView() {
        //标题栏初始化
        taskListToolbar.setNavigationIcon(R.mipmap.ic_return);
        taskListToolbar.setNavigationOnClickListener(this);
        rlNeedToDo.setOnClickListener(this);
        rlHaveDone.setOnClickListener(this);
        rlMyTask.setOnClickListener(this);
        xrv_refreshOrder.setPullLoadEnable(false);
        xrv_refreshOrder.setCustomHeaderView(new ListViewHeaderView(this));
        xrv_refreshOrder.setDampingRatio(3.6f);
        xrv_refreshOrder.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                switch (task_type_selected) {
                    case 1://待办
                        initData(GlobalUrl.TaskList_a);
                        changeColor_a();
                        break;
                    case 2://已办
                        initData(GlobalUrl.TaskList_b);
                        changeColor_b();
                        break;
                    case 3://我发起的
                        initData(GlobalUrl.TaskList_c);
                        changeColor_c();
                        break;
                }
            }
        });

        //列表初始化
        LinearLayoutManager manager = new LinearLayoutManager(getMe(), LinearLayoutManager.VERTICAL, false);
        rvTaskList.setLayoutManager(manager);
        rvTaskList.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST, R.drawable.recycle_21px_dividing));

        initSortPop();
        initCallPop();
        changeColor_a();
    }


    /**
     * 初始化打电话弹窗
     */
    private void initCallPop() {
        View view = View.inflate(getMe(), R.layout.task_list_call, null);
        pop_call = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_call.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        pop_call.setFocusable(true);
        pop_call.setBackgroundDrawable(new BitmapDrawable());
        pop_call.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowUtil.getInstance().setAlphaLight(TaskListActivity.this);
            }
        });

        RelativeLayout rl_call_a = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_a);
        RelativeLayout rl_call_cancel = (RelativeLayout) pop_call.getContentView().findViewById(R.id.rl_call_cancel);
        tv_contact_phone = (TextView) pop_call.getContentView().findViewById(R.id.tv_contact_phone);
        rl_call_a.setOnClickListener(this);
        rl_call_cancel.setOnClickListener(this);
    }

    /**
     * 初始化排序弹窗
     */
    private void initSortPop() {
        View view = View.inflate(getMe(), R.layout.task_list_sort, null);
        pop_sort = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_sort.setOutsideTouchable(true);                        //设置点击PopupWindow以外的地方关闭PopupWindow
        pop_sort.setFocusable(true);
        pop_sort.setBackgroundDrawable(new BitmapDrawable());
        pop_sort.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowUtil.getInstance().setAlphaLight(TaskListActivity.this);
            }
        });
        LinearLayout ll_sort_time = (LinearLayout) pop_sort.getContentView().findViewById(R.id.ll_sort_time);
        LinearLayout ll_sort_level = (LinearLayout) pop_sort.getContentView().findViewById(R.id.ll_sort_level);
        LinearLayout ll_sort_addr = (LinearLayout) pop_sort.getContentView().findViewById(R.id.ll_sort_addr);
        ll_sort_time.setOnClickListener(this);
        ll_sort_level.setOnClickListener(this);
        ll_sort_addr.setOnClickListener(this);
        ivSort.setOnClickListener(this);
        etSearch.setSingleLine();
        etSearch.addTextChangedListener(new EditChangedListener());
    }

    private void initData(String url) {
        OkHttpUtils.get(url).execute(new CommonCallback<Map>(getMe(), true) {

            @Override
            public void onResponse(boolean b, Map map, Request request, @Nullable Response response) {
                try {
                    int res_code = (int) map.get("code");
                    if (res_code == 10000) {
                        tasks = (ArrayList) map.get("data");
                        if (tasks.size() > 0) {
                            setDataAndListener(tasks);
                            yvNodata.setVisibility(View.GONE);
                            rvTaskList.setVisibility(View.VISIBLE);
                        } else {
                            yvNodata.setVisibility(View.VISIBLE);
                            rvTaskList.setVisibility(View.GONE);
                        }

                        //工单列表加入数据库
                        for (int i = 0; i < tasks.size(); i++) {
                            HashMap data = (HashMap) tasks.get(i);


                            String task_id = String.valueOf(data.get("owwoId"));
                            TaskInfo taskInfo = helper.select(task_id);
                            if (null == taskInfo) {
                                TaskInfo info = new TaskInfo();
                                info.setTask_id(task_id);
                                info.setHas_chpsen(false);
                                helper.add(info);
                            }
                        }

                    } else {
                        ToastUtil.showToast(getMe(), "请求工单列表失败,错误码: " + res_code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                if (null != e) {
                    ToastUtil.showToast(getMe(), "请求工单列表失败" + e.toString());
                } else {
                    ToastUtil.showToast(getMe(), "请求工单列表失败");
                }
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable Map s, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, s, call, response, e);
                xrv_refreshOrder.stopRefresh();
            }
        });


    }

    private void setDataAndListener(final ArrayList tasks) {
        rvTaskList.setAdapter(new TaskListAdapter(getMe(), R.layout.item_task_list, tasks) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                final HashMap data = (HashMap) tasks.get(position);

                //对小蓝点进行处理
                final String task_id = String.valueOf(data.get("owwoId"));
                TaskInfo taskInfo = helper.select(task_id);
                if (null != taskInfo) {
                    boolean has_chosen = taskInfo.getHas_chpsen();
                    if (has_chosen) {
                        holder.setVisible(R.id.ll_blue_dian, false);
                    } else {
                        holder.setVisible(R.id.ll_blue_dian, true);
                    }
                }
                String filetag = String.valueOf(data.get("fileCnt"));
                if (!filetag.equals("null")&&!filetag.equals("")) {
                    int file_tag = Integer.parseInt(filetag);

                    if (file_tag > 0) {
                        holder.setVisible(R.id.iv_file_tag,true);
                    }else {
                        holder.setVisible(R.id.iv_file_tag,false);
                    }
                }


                final String status = String.valueOf(data.get("owrdpTypeNo"));
                final String status_txt = deal_with_status(status);
                holder.setText(R.id.tv_task_status, status_txt);//状态
                holder.setText(R.id.tv_person, String.valueOf(data.get("crContactName")));//联系人
                holder.setText(R.id.tv_content, String.valueOf(data.get("crAddress")));//地址
                holder.setText(R.id.tv_task_level, String.valueOf(data.get("owwoLevelTypeValue")));//等级
                holder.setText(R.id.tv_time, String.valueOf(data.get("crwoCreaDt")));//时间
                holder.setText(R.id.tv_content_reason, String.valueOf(data.get("crDescribe")));//任务描述

                holder.setOnClickListener(R.id.ll_item_task_list, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_0 = null;

                        //设置全局变量,给位置上传服务使用
//                        GlobalManager.owwoId = String.valueOf(data.get("owwoId"));
                        AppPreferences appPreferences = new AppPreferences(getMe());
                        appPreferences.put("owwoId", String.valueOf(data.get("owwoId")));

                        if (TextUtils.equals(status_txt, "已派单")) {
                            intent_0 = new Intent(getMe(), TaskStatusFirstActivity.class);
                        } else if (TextUtils.equals(status_txt, "已处理")) {
                            //已处理,未到达现场
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "已到达现场")) {
                            //已到达现场,开始现场采证
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "已现场取证")) {
                            //已到达现场,开始现场采证
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "现场处理中")) {
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "处理完成")) {
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "工单结束")) {
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        } else if (TextUtils.equals(status_txt, "协作完成")) {
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));

                        }else {
                            intent_0 = new Intent(getMe(), WorkingTimeLineActivity.class);
                            intent_0.putExtra("owwoId", String.valueOf(data.get("owwoId")));
                        }

                        if (null != intent_0) {
                            //获得该条数据并更新
                            TaskInfo taskinfo = helper.select(task_id);
                            if (null != taskinfo) {
                                taskinfo.setHas_chpsen(true);
                                helper.updateInfo(taskinfo);
                            }

                            intent_0.putExtra("TASK_INFO", (Serializable) tasks.get(position));
                            startActivity(intent_0);
                        } else {

                            ToastUtil.showToast(getMe(), "未派单");
                        }
                    }
                });

                holder.setOnClickListener(R.id.iv_call, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打电话的弹窗
                        pop_call.setAnimationStyle(R.style.popWindow_animation);
                        pop_call.showAtLocation(activityTaskList, Gravity.BOTTOM, 0, SystemAppUtils.getBottomStatusHeight(getMe()));//parent为泡泡窗体所在的父容器view
                        number = (String) ((HashMap) tasks.get(position)).get("crContactPhoneNum");
                        tv_contact_phone.setText(number);
                        WindowUtil.getInstance().setAlphaDark(TaskListActivity.this);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != tasks) setDataAndListener(tasks);
    }

    @NonNull
    private String deal_with_status(String status) {
        if (status.equals("00") | status.equals("07")) {
            status = "未派单";
        } else if (status.equals("01")) {
            status = "已派单";
        } else if (status.equals("02")) {
            status = "已处理";
        } else if (status.equals("03")) {
            status = "已到达现场";
        } else if (status.equals("04")) {
            status = "已现场取证";
        } else if (status.equals("05")) {
            status = "现场处理中";
        } else if (status.equals("06")) {
            status = "处理完成";
        } else if (status.equals("99")) {
            status = "工单结束";
        } else if (status.equals("08")) {
            status = "协作完成";
        }
        return status;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
            case R.id.iv_sort:
                int x = taskListToolbar.getWidth();
                pop_sort.showAsDropDown(taskListToolbar, x - 300, 0);
                //设置背景颜色变暗
                WindowUtil.getInstance().setAlphaDark(TaskListActivity.this);
                break;
            case R.id.ll_sort_time:
                Log.i(TAG, "最新任务在上");
                Collections.sort(tasks, comparator2);
                setDataAndListener(tasks);
                pop_sort.dismiss();
                break;
            case R.id.ll_sort_level:
                Log.i(TAG, "最新任务在下");
                Collections.sort(tasks, comparator1);
                setDataAndListener(tasks);
                pop_sort.dismiss();
                break;
            case R.id.ll_sort_addr:
                Log.i(TAG, "按地址排序");
                pop_sort.dismiss();
                break;
            case R.id.rl_call_a:
                Log.i(TAG, "打电话");
                //用intent启动拨打电话
                AppUtils.callPhone(number, getMe());
                pop_call.dismiss();
                break;
            case R.id.rl_call_cancel:
                Log.i(TAG, "取消打电话");
                pop_call.dismiss();
                break;
            case R.id.rl_need_to_do://待办
                xrv_refreshOrder.stopRefresh();
                task_type_selected = 1;
                initData(GlobalUrl.TaskList_a);
                changeColor_a();
                break;
            case R.id.rl_have_done://已办
                xrv_refreshOrder.stopRefresh();
                task_type_selected = 2;
                initData(GlobalUrl.TaskList_b);
                changeColor_b();
                break;
            case R.id.rl_my_task://我发起的
                xrv_refreshOrder.stopRefresh();
                task_type_selected = 3;
                initData(GlobalUrl.TaskList_c);
                changeColor_c();
                break;
        }
    }


    private Comparator<Map<String, Object>> comparator1 = new Comparator<Map<String, Object>>() {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            String date_a = (String) o1.get("crwoCreaDt");
            String date_b = (String) o2.get("crwoCreaDt");
            try {
                long time_a = MyDateUtils.getLongDateFromString(date_a, date_Format);
                long time_b = MyDateUtils.getLongDateFromString(date_b, date_Format);
                if (time_a >= time_b) {
                    return 1;
                } else {
                    return -1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    private Comparator<Map<String, Object>> comparator2 = new Comparator<Map<String, Object>>() {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            String date_a = (String) o1.get("crwoCreaDt");
            String date_b = (String) o2.get("crwoCreaDt");
            try {
                long time_a = MyDateUtils.getLongDateFromString(date_a, date_Format);
                long time_b = MyDateUtils.getLongDateFromString(date_b, date_Format);
                if (time_a >= time_b) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    private void changeColor_c() {
        tvNeedToDo.setTextColor(getResources().getColor(R.color.black));
        tvHaveDone.setTextColor(getResources().getColor(R.color.black));
        tvMyTask.setTextColor(getResources().getColor(R.color.blue_submit));
        ivWait.setImageDrawable(getResources().getDrawable(R.drawable.wait_default));
        ivFinish.setImageDrawable(getResources().getDrawable(R.drawable.finish_default));
        ivLaunch.setImageDrawable(getResources().getDrawable(R.drawable.launch_selected));
    }

    private void changeColor_b() {
        tvNeedToDo.setTextColor(getResources().getColor(R.color.black));
        tvHaveDone.setTextColor(getResources().getColor(R.color.blue_submit));
        tvMyTask.setTextColor(getResources().getColor(R.color.black));
        ivWait.setImageDrawable(getResources().getDrawable(R.drawable.wait_default));
        ivFinish.setImageDrawable(getResources().getDrawable(R.drawable.finish_select));
        ivLaunch.setImageDrawable(getResources().getDrawable(R.drawable.launch_default));
    }

    private void changeColor_a() {
        tvNeedToDo.setTextColor(getResources().getColor(R.color.blue_submit));
        tvHaveDone.setTextColor(getResources().getColor(R.color.black));
        tvMyTask.setTextColor(getResources().getColor(R.color.black));
        ivWait.setImageDrawable(getResources().getDrawable(R.drawable.wait_select));
        ivFinish.setImageDrawable(getResources().getDrawable(R.drawable.finish_default));
        ivLaunch.setImageDrawable(getResources().getDrawable(R.drawable.launch_default));
    }

    class EditChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i(TAG, s.toString());
            boolean is_origin = false;
            tasks_selected.clear();
            if (null!=tasks) {
                for (int i = 0; i < tasks.size(); i++) {
                    HashMap data = (HashMap) tasks.get(i);
                    String data_content = data.toString();
                    if (data_content.contains(s.toString()) && !TextUtils.isEmpty(s.toString())) {
                        tasks_selected.add(data);
                        is_origin = false;//false为筛选过后的列表.用于查询时候区分
                    } else if (TextUtils.isEmpty(s.toString())) {
                        is_origin = true;
                        break;
                    }
                }
            }


            setDataAndListener(tasks_selected);
            if (!is_origin) {
                if (tasks_selected.size() > 0) {
                    yvNodata.setVisibility(View.GONE);
                } else {
                    yvNodata.setVisibility(View.VISIBLE);
                }
            } else {
                setDataAndListener(tasks);
                yvNodata.setVisibility(View.GONE);
            }


        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task_type_selected == 1) {
            initData(GlobalUrl.TaskList_a);
        } else if (task_type_selected == 2) {
            initData(GlobalUrl.TaskList_b);
        } else if (task_type_selected == 3) {
            initData(GlobalUrl.TaskList_c);
        }
    }
}
