package com.example.yb.hstt.Base;

import android.os.Environment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TFHR02 on 2016/10/11.
 */
public class GlobalManager {

    /**
     * 倒计时的配置
     */
    public static long count = 3600000;

    /**
     * 默认位置信息
     */
    public static String Globel_lat = "38.488228";
    public static String Globle_lng = "122.925536";

    //设置聊天服务器相关的参数
    //服务器地址、服务器名称、端口
    public static final String IM_HOST = "47.93.159.225";
    public static final String IM_SERVER = "etfhr.com";
    public static final int IM_PORT = 5222;


    /**
     * 基础的接口地址,(终端安装/设备安装用的接口)================切换需要改变
     */
//    public static String BaseUrl = "http://192.168.1.78:8762/app/" + "debug/service";
    public static String BaseUrl = "http://192.168.1.211:8762/app/" + "debug/service";
//    public static String BaseUrl = "http://47.93.159.225:8762/app/" + "debug/service";

    /**
     * 登录接口================切换需要改变
     */
//    public static String Login_url = "http://192.168.1.78:8765/api/auth/jwt/token";
    public static String Login_url = "http://192.168.1.211:8765/api/auth/jwt/token";
//    public static String Login_url = "http://47.93.159.225:8765/api/auth/jwt/token";

    /**
     * 登录接口================切换需要改变
     */
//    public static String Get_UserInfo = "http://192.168.1.78:8765/api/auth/jwt/user";
    public static String Get_UserInfo = "http://192.168.1.211:8765/api/auth/jwt/user";
//    public static String Get_UserInfo = "http://47.93.159.225:8765/api/auth/jwt/user";
    /**
     * 上传图片地址
     */
    public static String uploadFileuUrl = "http://47.93.159.225:8888/cte/a/app/debug/upload";

    /**
     * 拍照后图片路径
     */
    public static String photoPath = Environment.getExternalStorageDirectory() + "/Hstt" + "/Pic";
    /**
     * 拍照后图片视频
     */
    public static final String VideoPath = Environment.getExternalStorageDirectory() + "/Hstt/media";

    /**
     * 上传位置服务相关
     * 由工单列表页面决定
     * 由位置上传服务获取
     * 每次从列表中点开工单都要修改,上传位置服务中要使用
     */
//    public static String owwoId = null;

    /**
     * 上传位置的间隔时间,正常为慢速,点开工单后设置为快速
     * 5s
     */
//    public static int time_interval = 5*1000;

    /**
     * 步骤id,每次操作工单(接单/到达现场/采证保存/维修/更换/结束/退回/协作)都要修改,上传位置服务中要使用
     */
//    public static String owrdpId = null;

    /**
     * token,用于单独进程的服务
     */
//    public static String token = null;


    public static HashMap deivce_map = new HashMap();//设备类型
    public static HashMap terminal_operate_type = new HashMap();//终端操作类型
    public static boolean upload_select = true;//判断上传条件,true为wifi,false为数据流量

    public static LatLng position;
    public static double lat;//经纬度
    public static double lng;//经纬度
    public static BDLocation location;//位置信息
    public static ArrayList type = new ArrayList();//设备状态
    public static ArrayList<HashMap<Boolean, String>> globle_logs = new ArrayList<>();//log日志 boolean代表正常操作或者错误操作,String代表日志时间+日至内容

    public static void initStatus() {
        type.add("未确认");
        type.add("已确认未调试");
        type.add("已调试");
    }

    public static void initDeviceMap() {
        deivce_map.put(10, "采集终端(集中器)");
        deivce_map.put(11, "智能电表");
        deivce_map.put(12, "智能开关");
        deivce_map.put(13, "智能插座");
        deivce_map.put(14, "电采暖设备(空气源、电采暖、电锅炉)");
    }

    public static double lat_for_task = 0.0;//经纬度,上传的坐标,默认为0.0,以便修改
    public static double lng_for_task = 0.0;//经纬度

}
