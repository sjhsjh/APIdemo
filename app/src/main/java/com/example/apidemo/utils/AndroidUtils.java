package com.example.apidemo.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import com.example.apidemo.broadcast.AlarmBroadcastReceiver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/19.
 */
public class AndroidUtils {
    public static final String BITMAP_FOLDER = "bitmap";

    public static String debugLog(long receiveTime) {
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(receiveTime);
        return simpledate.format(date);
    }

    public static boolean isNightMode(Context context) {
        return (0x30 & context.getResources().getConfiguration().uiMode) == 32;    // UI_MODE_NIGHT_YES
    }

    public static void printMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getMemoryClass();    // 256.设备分配给app的最大堆内存。nexeus: 192
        int maxHeapSize = manager.getLargeMemoryClass();// 521.是当清单文件配置了android:largeHeap="true"才有的最大堆内存，一般是heapSize的2-3倍。nexeus: 512
        NLog.d("sjh8", "heapSize =  " + heapSize + " maxHeapSize = " + maxHeapSize);

        NLog.i("sjh8", "isDebug" + ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0));
        Debug.startMethodTracing();
    }

    /**
     * 将文本拷贝至剪贴板
     */
    public static void copyText(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//        clipboardManager.setText(text.trim());
//
//        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));

        ClipData clip = ClipData.newPlainText("simple text copy", text);
        clipboardManager.setPrimaryClip(clip);
    }


    /**
     * 保存bitmap至指定文件夹
     */
    public static String saveBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        File folder = context.getExternalFilesDir(BITMAP_FOLDER);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return null;
            }
        }
        String bitmapFileName = System.currentTimeMillis() + ".jpg";
        // String bitmapFileName = System.currentTimeMillis() + "";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(folder + "/" + bitmapFileName);
            boolean successful = bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);

            if (successful)
                return folder.getAbsolutePath() + "/" + bitmapFileName;
            else
                return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 192.168.0.100  --> 192.168.0.104
     * @return
     */
    public static String getLocalIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        // NLog.i("sjh", "ipAddress = "+ipAddress);
        if (ipAddress == 0) return "";
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager connectivity;
        NetworkInfo activeNetwork;
        try {
            connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = connectivity.getActiveNetworkInfo();
        } catch (Exception e) {
            return false;
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * 前往开启辅助服务界面
     */
    public static void goAccessibilityServiceSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * 展开通知栏
     */
    public static void openStatusBar(Context mContext) {
        String methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
        openOrCloseStatusBar(mContext, methodName);
    }

    /**
     * 收起通知栏
     */
    public static void closeStatusBar(Context mContext) {
        String methodName = (Build.VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
        openOrCloseStatusBar(mContext, methodName);
    }

    @SuppressLint("WrongConstant")
    private static void openOrCloseStatusBar(Context context, String methodName) {
        Object service = context.getSystemService("statusbar"); // @hide : Context.STATUS_BAR_SERVICE
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");

            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);
            method.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static AlarmBroadcastReceiver alarmReceiver; // 全局广播，对应context.getApplicationContext().registerReceiver
    public static PendingIntent currentPendingIntent;

    @SuppressWarnings({"ConstantConditions"})   // 忽略lint空指针警告
    public static void openAlarm(Context context, boolean isRepeat, long beginMs, long intervalMs, Runnable timeUpRunnable) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent broastIntent = new Intent();//context, AlarmBroadcastReceiver.class);  xx
        broastIntent.setAction(AlarmBroadcastReceiver.ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, broastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        currentPendingIntent = pendingIntent;

        // 注册处理到时任务和循环任务的广播
        destoryBroadcast(context);
        alarmReceiver = new AlarmBroadcastReceiver(alarmManager, isRepeat, intervalMs, timeUpRunnable, pendingIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmBroadcastReceiver.ACTION_ALARM);
        context.getApplicationContext().registerReceiver(alarmReceiver, filter); // 广播生命周期跟随registerReceiver的context

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NLog.v("sjh5", "---set alarm---");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, beginMs, pendingIntent);
            // alarmManager.setExact(AlarmManager.RTC_WAKEUP, beginMs, pendingIntent);
            // // setWindow的触发时间比较不确定。[0,2]->5s触发；[0,60]->62s、5s触发；setWindow的windowLengthMillis为0时等价于setExact；
            // alarmManager.setWindow(AlarmManager.RTC_WAKEUP, beginMs, 2000, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT || !isRepeat) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, beginMs, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, beginMs, intervalMs, pendingIntent);
        }
    }

    /**
     * 主动结束AlarmManager循环任务
     */
    public static void destoryBroadcast(Context context) {
        if (alarmReceiver != null) {
            context.getApplicationContext().unregisterReceiver(alarmReceiver);
            alarmReceiver = null;
        }
    }
    /**
     * 主动取消AlarmManager定时任务
     */
    public static void cancelAlarm(Context context) {
        if (currentPendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(currentPendingIntent);
        }
    }

    public static void cancelAlarmAndBroadcast(Context context) {
        destoryBroadcast(context);
        cancelAlarm(context);
    }
}