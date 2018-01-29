package com.example.yb.hstt.Base;

import com.example.yb.hstt.Utils.SpUtil;

/**
 * Created by TFHR02 on 2017/12/11.
 */
public class GlobalUrl {


    /**
     * 本地服务器(后两个模块)
     */
//    public static String sys_url = "http://192.168.1.78:8762";
    public static String sys_url = "http://192.168.1.211:8762";
//    public static String sys_url = "http://47.93.159.225:8765/api/admin";

    /**
     * 改密码接口,(切换时单独修改)
     */
//    public static String ChangePsd = "http://192.168.1.78:8777/jwt/updatePassword";
    public static String ChangePsd = "http://192.168.1.211:8777/jwt/updatePassword";
//    public static String ChangePsd = "http://47.93.159.225:8765/api/auth/jwt/updatePassword";
    /**
     * 基础的接口地址
     */
    public static String BaseUrl = sys_url + "/handbill";
    /**
     * 新建工单
     */
    public static String New_Order = BaseUrl + "/trade_010101";
    /**
     * 新建工单上传文件
     */
    public static String New_OrderUpLoad = BaseUrl + "/trade_010102";
    /**
     * 更换设备
     */
    public static String Change_Device = BaseUrl + "/trade_010501";
    /**
     * 更换设备上传文件
     */
    public static String Change_DeviceUpLoad = BaseUrl + "/trade_010503";
    /**
     * 更换设备信息
     */
    public static String Change_DeviceInfo = BaseUrl + "/trade_010504";
    /**
     * 维修设备
     */
    public static String Repair_Device = BaseUrl + "/trade_010505";
    /**
     * 维修设备上传文件
     */
    public static String Repair_DeviceUpLoad = BaseUrl + "/trade_010507";
    /**
     * 维修设备信息
     */
    public static String Repair_DeviceInfo = BaseUrl + "/trade_010508";
    /**
     * 查看列表
     */
    public static String TheOrder_FinishedInfo = BaseUrl + "/trade_010509";
    /**
     * 删除更换工单列表某项
     */
    public static String TheOrder_FinishedChangeDelete = BaseUrl + "/trade_010510";
    /**
     * 删除维修工单列表某项
     */
    public static String TheOrder_FinishedRepairDelete = BaseUrl + "/trade_010511";
    /**
     * 删除列表 - 外勤附件
     */
    public static String TheOrder_FinishedFileDelete = BaseUrl + "/trade_010512";
    /**
     * 编辑- 已更换设备
     */
    public static String TheOrder_EditChangedDevice = BaseUrl + "/trade_010502";
    /**
     * 编辑- 已维修设备
     */
    public static String TheOrder_EditRepairedDevice = BaseUrl + "/trade_010506";

    /**
     * 查询个人工单进度(TimeLine)
     */
    public static String Working_TimeLine = BaseUrl + "/trade_010513";

    /**
     * 接口获取设备工单信息
     */
    public static String Get_TaskInfo = BaseUrl + "/trade_010106";
    /**
     * 接口获取工单紧急状态列表
     */
    public static String Get_TaskLevelList = BaseUrl + "/trade_010119";
    /**
     * 待办工单
     */
    public static String TaskList_a = BaseUrl + "/trade_010204";
    /**
     * 已办工单
     */
    public static String TaskList_b = BaseUrl + "/trade_010205";
    /**
     * 我发起的工单
     */
    public static String TaskList_c = BaseUrl + "/trade_010206";
    /**
     * 开始处理
     */
    public static String Start_deal = BaseUrl + "/trade_010301";
    /**
     * 回退工单
     */
    public static String Task_Back = BaseUrl + "/trade_010302";
    /**
     * 结束工单
     */
    public static String Task_Finish = BaseUrl + "/trade_010303";
    /**
     * 到达现场
     */
    public static String Arrive_Place = BaseUrl + "/trade_010304";
    /**
     * 协作完成任务
     */
    public static String Finish_Together = BaseUrl + "/trade_010305";
    /**
     * 现场采证保存数据
     */
    public static String Save_Info = BaseUrl + "/trade_010401";
    /**
     * 获取已经保存成功的现场采证信息
     */
    public static String Get_Success_Save_Info = BaseUrl + "/trade_010403";
    /**
     * 现场采证上传文件
     */
    public static String Save_Info_UpLoad = BaseUrl + "/trade_010402";
    /**
     * 获得倒计时时间
     */
    public static String CountTime = BaseUrl + "/trade_030101";
    /**
     * 服务上传位置信息
     */
    public static String location_upload = BaseUrl + "/trade_020102";


    /**
     * 设置服务器地址
     */
    public static void setBaseUrl() {
        String ip = "";
        ip = (String) SpUtil.get(BaseApplication.instances.getApplicationContext(), "sys_login_ip", "");
        if (null != ip && !"".equals(ip)) {
            sys_url = ip;
        }
    }
}
