package com.example.yb.hstt.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.UI.Activities.LoginActivity;
import com.example.yb.hstt.Utils.MyDateUtils;
import com.example.yb.hstt.Utils.SignUtils;
import com.example.yb.hstt.Utils.ToastUtil;
import com.google.gson.Gson;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TFHR02 on 2017/7/11.
 */
public abstract class CommonCallback2 extends AbsCallback<String> {
    private static final String TAG = "CommonCallback2";
    private String method;
    private Map<String, Object> bodyparams;
    private Gson gson = null;
    public Context context;
    private static final int EXIT_APP_CODE = 20001;
    private boolean isshowDialog = false;
    private ProgressDialog dialog;

    public CommonCallback2(Context context, String method, Map<String, Object> bodyparams) {
        this(context, method, bodyparams, false);
    }

    public CommonCallback2(Context context, String method, Map<String, Object> bodyparams, boolean isshowDialog) {
        this.method = method;
        this.bodyparams = bodyparams;
        this.gson = new Gson();
        this.context = context;
        this.isshowDialog = isshowDialog;
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        if (isshowDialog) {
            showProgress();
        }
        request.params("method", method);

        String body = null, jsondatas = null, sign = null;
        if (bodyparams != null) {
            //封装body实体数据
            jsondatas = gson.toJson(bodyparams);
            //数据加密
            body = jsondatas;
            if (method != null) {
                if (method.equals(LoginActivity.LoginMethod)) {//登陆时才加密
                    body = SignUtils.rsaEncrypt(jsondatas);
                }
            }
            //数据签名
            sign = SignUtils.rsaSign(jsondatas);
        }

        //每次请求都添加进log日志
        HashMap hm_log = new HashMap();
        if (null!=body&&!body.toString().equals("{}")) {
            hm_log.put(true, MyDateUtils.getTime()+"  "+body.toString());
            GlobalManager.globle_logs.add(hm_log);
        }


        request.params("body", body);
        request.params("sign", sign);
    }

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        String result = response.body().string();
        JSONObject jsonObject = new JSONObject(result);
        //处理后台退出的情况
        HashMap map = (HashMap) parseGson_map(jsonObject.toString());
        HashMap map_1 = (HashMap) parseGson_map(map.get(method).toString());

        //每次相应都添加进log日志
        HashMap hm_log = new HashMap();
        hm_log.put(true,MyDateUtils.getTime()+"  "+map_1.toString());
        GlobalManager.globle_logs.add(hm_log);

        String code = (String) map_1.get("code");
        if (code != null && Integer.valueOf(code).equals(EXIT_APP_CODE)) {
            final String mome = (String) map_1.get("msg");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(context, mome);
                }
            });

            context.startActivity(new Intent(context, LoginActivity.class));
            return null;
        }
        //解析签名
        boolean check_result = false;
        String data = "";
        if (jsonObject.has("sign")) {
            String sign = jsonObject.getString("sign");
            data = jsonObject.getString(method);
            //验签
            check_result = SignUtils.rsaCheck(data, sign);
            if (null == data || "".equals(data)) {
                check_result = false;
            }
        }
        //验证签名正确
        if (check_result) {
            return data;
        } else {
            //如果验证签名失败，返回-1
            ToastUtil.showToast(context, "验证签名失败！");
            return null;
        }
    }

    @Override
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onError(isFromCache, call, response, e);
        ToastUtil.showToast(context, "网络异常！");
        Log.i(TAG, "onError: " + e);

        //每次请求都添加进log日志
        HashMap hm_log = new HashMap();
        if (null!=e)hm_log.put(false,MyDateUtils.getTime()+"  "+e.toString());
        GlobalManager.globle_logs.add(hm_log);
        HashMap hm_log1 = new HashMap();
        if (null!=response)hm_log.put(false,MyDateUtils.getTime()+"  "+response.toString());
        GlobalManager.globle_logs.add(hm_log1);
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onAfter(isFromCache, s, call, response, e);
        if (isshowDialog) {
            closeProgress();
        }
    }

    /**
     * json转map
     *
     * @param results
     * @return
     * @throws Exception
     */
    public static Map<String, Object> parseGson_map(String results) throws Exception {
        String responseData = results;
        if (TextUtils.isEmpty(responseData)) return null;
        JSONTokener jsonParser = new JSONTokener(responseData);
        JSONObject jsonObj = (JSONObject) jsonParser.nextValue();
        HashMap<String, Object> json = new HashMap();
        json = jsonObjToMap(jsonObj);

        return json;
    }

    /**
     * JSONObject转map
     *
     * @param json
     * @return
     * @throws JSONException
     */
    private static HashMap<String, Object> jsonObjToMap(JSONObject json) throws JSONException {
        if (json == null) {
            HashMap<String, Object> map = new HashMap();
            return map;
        }
        HashMap<String, Object> map = new HashMap();
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                value = jsonObjToMap((JSONObject) value); //递归调用
            } else if (value instanceof JSONArray) {
                value = jsonArrayToArrayList((JSONArray) value); //递归调用
            }
            //字符串直接放进去
            map.put(key, value);
        }
        return map;
    }

    /**
     * JSONArray转ArrayList
     *
     * @param json
     * @return
     * @throws JSONException
     */
    private static ArrayList jsonArrayToArrayList(JSONArray json) throws JSONException {
        if (json == null
                || json.length() <= 0) {
            ArrayList al = new ArrayList();
            return al;
        }
        ArrayList al = new ArrayList();
        for (int i = 0; i < json.length(); i++) {
            Object obj = json.get(i);
            if (obj instanceof JSONObject) {
                al.add(jsonObjToMap((JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                al.add(jsonArrayToArrayList((JSONArray) obj));
            } else {
                al.add(obj);
            }
        }
        return al;
    }

    /**
     * 打开弹窗
     */
    private void showProgress() {
        dialog = new ProgressDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 关闭弹窗
     */
    private void closeProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}