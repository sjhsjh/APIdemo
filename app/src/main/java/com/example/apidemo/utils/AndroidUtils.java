package com.example.apidemo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.os.Parcel;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.blankj.utilcode.util.BarUtils;
import com.example.apidemo.activity.AutoClickActivity;
import com.example.apidemo.broadcast.AlarmBroadcastReceiver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/19.
 */
public class AndroidUtils {
    public static final String BITMAP_FOLDER = "bitmap";

    /**
     * writeXXX()和readXXX()执行后 都会产生偏移量； 且偏移量是共用；
     * dataSize: 得到当前parcel对象的实际存储空间; 随着写入数据，数值增大；
     * dataCapacity: 表示 Parcel 对象能够容纳的数据总量的上限，即 Parcel 内部缓冲区的大小、 >=dataSize()、到达上限时会自动扩容、
     */
    public static void parcelTest() {
        Parcel parcel = Parcel.obtain();
        for (int i = 0; i < 10; i++) {
            parcel.writeInt(i);
            Log.i("sjh1", "write double ----> " + i);
        }
        parcel.setDataPosition(0);  //  设置偏移量 指针位置
        while (parcel.dataPosition() < parcel.dataSize()) {
            String fvalue = parcel.readString();
            Log.i("sjh1",
                    " read double is=" + fvalue + ", --->" + parcel.dataPosition() + ", --->" +
                            parcel.dataSize() + ", --->" + parcel.dataCapacity());
        }


        // for (int i = 0; i < 17; i++) {
        //     parcel.writeDouble(i);
        //     Log.i("sjh1", "write double ----> ");
        // }

        // 方法一
        // int i = 0;
        // int datasize = parcel.dataSize();    //  得到当前parcel对象的实际存储空间
        // while (i < datasize) {
        //     parcel.setDataPosition(i);
        //     double fvalue = parcel.readDouble();
        //     Log.i("sjh1", " read double is=" + fvalue + ", --->"  + parcel.dataPosition());
        //     i += 8; // double占用字节为 8byte
        // }

        // 方法二，由于对象的类型一致，我们可以直接利用readXXX()读取值会产生偏移量
        // parcel.setDataPosition(0);  //  设置偏移量 指针位置
        // while (parcel.dataPosition() < parcel.dataSize()) {
        //     double fvalue = parcel.readDouble();
        //     Log.i("sjh1",
        //             " read double is=" + fvalue + ", --->" + parcel.dataPosition() + ", --->" +
        //                     parcel.dataSize() + ", --->" + parcel.dataCapacity());
        // }


        // Parcel parcel = Parcel.obtain();
        // for (int i = 8; i < 10; i++) {
        //     parcel.writeInt(i);             // 长度4
        // }
        // // parcel.writeFloat(4234234.4f);   // 长度4
        // // parcel.writeLong(99L);           // 长度8
        // // parcel.writeDouble(4234234.4);  // 长度8
        // /*
        //  * Parcel.cpp中
        //  * 1、先写入了当前数据的长度writeInt32(len);
        //  * 以长度为4对齐，不足4也需要填充为4；
        //  * 两个字母长4；
        //  * const size_t padded = pad_size(len);
        //  */
        // parcel.writeString("abcee");     // 长度变化  8   12  12  16   16
        //

    }


    public static String debugLog(long receiveTime) {
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(receiveTime);
        return simpledate.format(date);
    }

    /**
     * 应用层获取 ANR Info
     */
    public void checkProcessesInErrorState(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.ProcessErrorStateInfo> processesInError =
                    activityManager.getProcessesInErrorState();
            if (processesInError != null && !processesInError.isEmpty()) {
                for (ActivityManager.ProcessErrorStateInfo process : processesInError) {
                    // 这里可以处理每个错误状态的进程
                    // process.pid 是进程的 PID
                    // process.info 是关于进程的额外信息
                    // ...
                    // ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING;

                    NLog.d("sjh8","ProcessErrorStateInfo =  " + process + " longMsg = " + process.longMsg);
                }
            } else {
                // 正常的进程
            }
        } else {
            // 获取 ActivityManager 失败
        }
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
     * 前往更多设置-系统安全-特殊应用权限-电池优化界面；
     * 低电耗模式和应用待机模式优化的白名单
     */
    @SuppressWarnings({"ConstantConditions"})
    public static void goBatteryOptimizationSettings(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            String packageName = context.getPackageName();
            Intent intent = new Intent();
            // 所弹系统dialog文案“设置--应用和通知”有误
            if (pm.isIgnoringBatteryOptimizations(packageName)){                        // 若已在白名单中
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS); // 跳转电池优化
            } else {
                // 弹窗允许后台运行；需要加上权限：android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 是否在系统电池优化白名单
     */
    public static boolean isInBatteryOptimizationWhiteList(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            String packageName = context.getPackageName();
            NLog.w("sjh5", "--isIgnoringBatteryOptimizations--" + pm.isIgnoringBatteryOptimizations(packageName));
            return pm.isIgnoringBatteryOptimizations(packageName);
        }
        return true;
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
    /**
     * tips:小米应用在“亮屏非前台”和“灭屏（无论是否disablekeyguard）”都不一定能触发到时间的AlarmManager；
     * 因为小米默认智能限制应用后台运行，即无前台服务的应用可能会被休眠。因此使用AlarmManager时，要不为应用添加前台服务，要不把应用的休眠限制改为无限制。
     */
    public static void openAlarm(Context context, boolean isRepeat, long beginMs, long intervalMs,
                                 AutoClickActivity.TimeUpCallback timeUpCallback) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent broastIntent = new Intent();//context, AlarmBroadcastReceiver.class);  xx
        broastIntent.setAction(AlarmBroadcastReceiver.ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, broastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        currentPendingIntent = pendingIntent;

        // 注册处理到时任务和循环任务的广播
        destoryBroadcast(context);
        alarmReceiver = new AlarmBroadcastReceiver(alarmManager, isRepeat, intervalMs, timeUpCallback, pendingIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmBroadcastReceiver.ACTION_ALARM);
        context.getApplicationContext().registerReceiver(alarmReceiver, filter); // 广播生命周期跟随registerReceiver的context

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NLog.v("sjh5", "---set alarm---" + TimeUtils.getTimeStringFromMillis(beginMs));
            // 触发时间太靠近当前时间的话，第一次执行的时刻不准！！
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, beginMs, pendingIntent);
            // alarmManager.setExact(AlarmManager.RTC_WAKEUP, beginMs, pendingIntent);
            // setWindow的windowLengthMillis为0时等价于setExact；[0,2]->0s触发；[0,30]->7s、11s、30s触发；[0,60]->0s、9s、32s触发；
            // alarmManager.setWindow(AlarmManager.RTC_WAKEUP, beginMs, 30000, pendingIntent);
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
            currentPendingIntent = null;
        }
    }

    public static boolean isAlarmRunning() {
        return currentPendingIntent != null;
    }

    public static void cancelAlarmAndBroadcast(Context context) {
        destoryBroadcast(context);
        cancelAlarm(context);
    }

    /**
     * 沉浸式。使当前activity的状态栏变成沉浸式
     * ps：若在activity.onCreate结束后再执行，则应用区域不上移。
     */
    public static void immersionStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 沉浸式。效果等同于immersionStatusBar
     * 来自BarUtils.transparentStatusBar
     */
    public static void immersionStatusBar2(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int vis = window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(option | vis);
            } else {
                window.getDecorView().setSystemUiVisibility(option);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 状态栏变色步骤：
     * 1、BarUtils.transparentStatusBar 让状态栏变透明 + 应用区域上移；即沉浸式
     * 2、activity布局根view加上topMargin；
     * 3、activity的parentView add自定义状态栏view；
     */
    public static void changeStatusBarColor(Activity activity, View activityRootView, int color) {
        BarUtils.setStatusBarColor(activity, color);
        BarUtils.addMarginTopEqualStatusBarHeight(activityRootView);    // activity布局根view
    }

    /**
     * 应用区域铺满全屏 + 状态栏和导航栏透明
     */
    public static void fitComprehensiveScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
            activity.getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            activity.getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            activity.getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 状态栏直接隐藏与否！！无需重启activity
     */
    public static void setFullScreen(Activity activity, boolean isFullScreen) {
        if (isFullScreen) {
            // 对小米，全屏触发隐藏状态栏，但是却把应用区域顶下来了!!状态栏变成黑色区域
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 判断CPU是32位还是64位
     */
    public static boolean isCpu64() {
        String cpuAbiList = System.getProperty("ro.product.cpu.abilist");
        if (cpuAbiList != null) {
            NLog.i("sjh0", "cpuAbiList = " + cpuAbiList);
            if (cpuAbiList.contains("64")) {
                // CPU支持64位
                return true;
            } else {
                // CPU只支持32位
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            "x86_64".equals(Build.SUPPORTED_ABIS[0]);
            NLog.i("sjh0", "Build.SUPPORTED_ABIS[0] = " + Build.SUPPORTED_ABIS[0]);
        }


        String cpuAbi = System.getProperty("os.arch");
        NLog.i("sjh0", "cpuAbi = " + cpuAbi);   // aarch64

        if (cpuAbi.contains("64")) {
            // CPU是64位的
            return true;
        } else {
            // CPU是32位的
            return false;
        }

    }

}