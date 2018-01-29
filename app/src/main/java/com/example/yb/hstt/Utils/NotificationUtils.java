package com.example.yb.hstt.Utils;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.example.yb.hstt.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by TFHR02 on 2016/12/16.
 */
public class NotificationUtils {
    private static NotificationUtils notificationUtils = null;
    private Context context;
    private String content_title;
    private static int notificationnum = -1;
    private String content_text;
    private Intent resultintent;

    private NotificationUtils(Context context) {
        this.context = context;
    }

    public static NotificationUtils getInstance(Context context) {
        if (notificationUtils == null) {
            notificationUtils = new NotificationUtils(context);
        }
        return notificationUtils;
    }

    public NotificationUtils setContenttitle(String contenttitle) {
        this.content_title = contenttitle;
        return this;
    }

    public static void setCancel() {
        if (notificationUtils != null) {
            notificationUtils = null;
        }
    }

    public NotificationUtils setContenttext(String content_text) {
        this.content_text = content_text;
        return this;
    }

    public NotificationUtils setResultintent(Intent resultintent) {
        this.resultintent = resultintent;
        return this;
    }

    public void SendNotification() {
        notificationnum = notificationnum + 1;

        Bitmap largebitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
        int large_w = largebitmap.getWidth();
        //裁剪为圆图
//        largebitmap = ImageCompressUtil.toRoundCorner(largebitmap, large_w / 2);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultintent, PendingIntent.FLAG_UPDATE_CURRENT);
        //通知栏的实体内容
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largebitmap)
                        .setSmallIcon(R.drawable.ic_notifi)
                        .setContentTitle(content_title)//设置标题
                        .setContentText(content_text)//设置内容
                        .setAutoCancel(true)//点击后自动消失
                        .setColor(context.getResources().getColor(R.color.bt_blue))//小图标外接圆圈颜色
                        .setPriority(NotificationCompat.PRIORITY_MAX)//设置广播的优先级
                        .setDefaults(Notification.DEFAULT_ALL)//设置通知的行为,例如声音,震动等;
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentIntent(resultPendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //悬挂式Notification，5.0后显示
//            mBuilder.setFullScreenIntent(resultPendingIntent, true);
//            mBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
//        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(notificationnum, notification);
        largebitmap.recycle();
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 获取通知权限
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
      /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void requestSetPermission(Context context, int requestCode) {
        // TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        }
        return;
    }
}
