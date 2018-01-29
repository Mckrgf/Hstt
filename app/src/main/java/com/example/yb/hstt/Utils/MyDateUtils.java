package com.example.yb.hstt.Utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtils {


    public static final String date_Format = "yyyy-MM-dd HH:mm:ss";
    public static final String date_Format2 = "yyyy-MM-dd";
    public static final String date_Format3 = "yyyyMMddHHmmssS";
    public static final String date_format4 = "yyyy-MM-dd hh:mm";
    public static final String date_format5 = "HH:mm";
    public static final String date_format6 = "HH";
    public static final String date_format7 = "yyyy-mm-dd hh:mm";
    public static final String date_Format8 = "yyyy-MM-dd HH:mm";

    public static final SimpleDateFormat sdf2 = new SimpleDateFormat(date_Format2);

    /**
     * 查询当天时间
     *
     * @param dFormat 时间格式
     * @return
     */
    public static String getCurTime(String dFormat) {
        String dtime = "";
        if (TextUtils.isEmpty(dFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(date_Format);
            return sdf.format(new Date());
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dFormat);
                return sdf.format(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dtime;
    }

    /**
     * 获取时：分
     *
     * @return
     */
    public static String getTime() {
        Calendar now = Calendar.getInstance();
        String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(now.get(Calendar.MINUTE));
        String time = hour + ":" + minute;
        return time;
    }

    /**
     * 根据时间戳，和数据格式，获取响应格式的时间
     *
     * @param delta
     * @param formatType
     * @return
     */
    public static String getStrDate(long delta, String formatType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        return simpleDateFormat.format(new Date(delta));
    }

    /**
     * 根据时间戳获取时间
     *
     * @param value
     * @return
     */
    public static String getDateFromLong(Long value, String formatType) {
        Date date = new Date(value);
        SimpleDateFormat format = new SimpleDateFormat(formatType);
        return format.format(date);
    }

    /**
     * string类型时间转换成时间戳
     *
     * @param value
     * @return
     */
    public static Long getLongDateFromString(String value, String formatType) throws ParseException {
        SimpleDateFormat mformat = new SimpleDateFormat(formatType);
        return mformat.parse(value).getTime();
    }

    /**
     * 根据日期获取时间
     *
     * @param date
     * @return
     */
    public static String getDateFromDate(Date date, String formatType) {
        SimpleDateFormat format = new SimpleDateFormat(formatType);
        return format.format(date);
    }

    /**
     * 得到本周周一
     *
     * @return yyyy-MM-dd
     */
    public static String getMondayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        //英式，周日是第一天（即周日时：c.get(Calendar.DAY_OF_WEEK)=1），周六是最后一天（即周六时：c.get(Calendar.DAY_OF_WEEK)=7）
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        return sdf2.format(c.getTime());
    }

    /**
     * 得到明天的日期
     *
     * @return yyyy-MM-dd
     */
    public static String getLastday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        return sdf2.format(c.getTime());
    }

    /**
     * 得到本周周日
     *
     * @return yyyy-MM-dd
     */
    public static String getSundayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 7);
        return sdf2.format(c.getTime());
    }

    /**
     * 得到上周周一
     *
     * @return yyyy-MM-dd
     */
    public static String getMondayOfLastWeek() {
        Calendar c = Calendar.getInstance();
        //英式，周日是第一天（即周日时：c.get(Calendar.DAY_OF_WEEK)=1），周六是最后一天（即周六时：c.get(Calendar.DAY_OF_WEEK)=7）
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1 - 7);
        return sdf2.format(c.getTime());
    }

    /**
     * 得到上周周日
     *
     * @return yyyy-MM-dd
     */
    public static String getSundayOfLastWeek() {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 7 - 7);
        return sdf2.format(c.getTime());
    }

    public static void main(String[] args) {
        System.out.println(getMondayOfLastWeek());
    }

    /**
     * 查询当天时间
     *
     * @param dFormat 时间格式
     * @return
     */
    public static String getCurTimeFormat(String dFormat) {
        String dtime = "";
        if (TextUtils.isEmpty(dFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(date_Format);
            return sdf.format(new Date());
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dFormat);
                return sdf.format(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dtime;
    }

    /**
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        if (mss<0) {
            return "已超时";
        }
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return hours + ":" + minutes + ":" + seconds;
    }
}
