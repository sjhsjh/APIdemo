package com.example.apidemo.utils.background;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.NLog;
import java.util.List;

/**
 * Created by wenmingvs on 2016/1/14.
 */
public class BackgroundUtil {

    /**
     * xx用此方法来判断自身App是否处于后台，仍然是有效的，但是无法判断任何一个应用是否位于前台。
     * <p>
     * getRunningTask方法在Android5.0以上已经被废弃（4.x系统可用），只会返回自己和系统的一些不敏感的task，不再返回其他应用的task，
     * 7.1 实测：
     * 自身在前台，返回执行getRunningTask的应用的包名
     * 其他应用在前台，返回执行getRunningTask的应用的包名
     * 在桌面，返回com.google.android.googlequicksearchbox
     *
     * @param packageName 需要检查是否位于栈顶的App的包名
     * @return
     */
    public static boolean getRunningTask(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        NLog.i("sjh7", "getRunningTask pkg = " + cn.getPackageName());
        return !TextUtils.isEmpty(packageName) && packageName.equals(cn.getPackageName());
    }


    /**
     * 方法1：通过getRunningAppProcesses的IMPORTANCE_FOREGROUND属性判断是否位于前台。不需要权限；只能判断自身是否在前台。
     * ps：当service需要常驻后台时候，此方法失效；在小米 Note上此方法无效，在Nexus上正常.
     * <p>
     * appProcess.processName一直是执行getRunningAppProcesses的应用的包名，但是appProcess.importance能判断应用是否在前台
     * （ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND or ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND）
     * 开启了START_NOT_STICKY的服务就是IMPORTANCE_SERVICE， 执行了service.startForeground(NOTICATION_ID, notification)则是IMPORTANCE_FOREGROUND_SERVICE;
     *
     * @param packageName 需要检查是否位于栈顶的App的包名
     * @return
     */
    public static boolean getRunningAppProcesses(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            NLog.i("sjh7", "getRunningAppProcesses processName = " + appProcess.processName + " importance = " + appProcess.importance);
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 方法2：通过ActivityLifecycleCallbacks来批量统计Activity的生命周期，来做判断，此方法在API 14以上均有效，但是需要在Application中注册此回调接口。
     * 只能判断自身是否在前台。
     * 必须：
     * 1. 在Application中注册ActivityLifecycleCallbacks接口
     * 2. 当Application因为内存不足而被Kill掉时，这个方法仍然能正常使用。虽然全局变量的值会因此丢失，但是再次进入App时候会重新统计一次的
     * ps:应用内跳转相册等其他应用也被认为是当前应用退到后台。
     */
//    public static boolean getApplicationValue(MyApplication myApplication) {
//        return myApplication.getAppCount() > 0;
//    }

    /**
     * 方法二：通过使用UsageStatsManager获取。 >= 5.0；需要权限；可以找到当前的前台应用。
     * PS:魅族和小米手机不能通过UsageStatsManager获取应用使用情况
     * 必须：
     * 1. 此方法只在android5.0以上有效，5.0新增api
     * 2. AndroidManifest中加入此权限 <uses-permission xmlns:tools="http://schemas.android.com/tools"
     * android:name="android.permission.PACKAGE_USAGE_STATS"
     * tools:ignore="ProtectedPermissions" />
     * 3. 打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾
     *
     * @param packageName 需要检查是否位于栈顶的App的包名
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean queryUsageStats(Context context, String packageName) {
        if (!hasUsagePermission(context)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "权限不够\n请打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾", Toast.LENGTH_SHORT).show();
            return false;
        }

        long endTime = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");  // Context.USAGE_STATS_SERVICE
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(usageStatsManager.INTERVAL_BEST, endTime - 1000 * 60 * 60 * 24 * 7, endTime); // 最近一周使用的应用,可能最多100个。

        if (usageStatsList == null || usageStatsList.size() == 0) {
            Toast.makeText(context, "一周内无应用使用记录！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 获取最近使用应用
        UsageStats recentStats = null;
        for (UsageStats usageStats : usageStatsList) {
            if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }
        if (recentStats == null) {
            return false;
        }
        NLog.e("sjh7", "queryUsageStats.  recent pkg = " + recentStats.getPackageName());

//        class RecentUseComparator implements Comparator<UsageStats> {
//            @Override
//            public int compare(UsageStats lhs, UsageStats rhs) {
//                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
//            }
//        }
//        RecentUseComparator mRecentComp = new RecentUseComparator();
//        Collections.sort(usageStatsList, mRecentComp);
//
//        String currentTopPackage = usageStatsList.get(0).getPackageName();
//        for(UsageStats u:usageStatsList){
//            NLog.i("sjh8", "size = " + usageStatsList.size() + " usage = " + u.getPackageName());
//        }
//        NLog.i("sjh7", "queryUsageStats currentTopPackage = " + currentTopPackage);

        return packageName.equals(recentStats.getPackageName());
    }

    /**
     * 判断是否有“有权查看使用情况的权限”
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean hasUsagePermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /**
     * 方法一：通过Android自带的无障碍功能，监控窗口焦点的变化，进而拿到当前焦点窗口对应的包名。需要权限；可以找到当前的前台应用。
     * 必须：
     * 1. 创建ACCESSIBILITY SERVICE INFO 属性文件
     * 2. 清单文件注册AccessibilityService
     * 不再需要轮询的判断当前的应用是不是在前台，系统会在窗口状态发生变化的时候主动回调，耗时和资源消耗都极小
     可以用来判断任意应用甚至 Activity, PopupWindow, Dialog 对象是否处于前台
     在设置中强行停止应用会导致辅助功能变成关闭
     * @return
     */
    public static boolean getFromAccessibilityService(Context context, String packageName) {
        if (isAccessibilitySettingsOn(context)) {
            DetectService detectService = DetectService.getInstance();
            String foreground = detectService.getForegroundPackage();
            NLog.e("sjh7", "getFromAccessibilityService.  当前窗口焦点对应的包名为： " + foreground);

            return packageName.equals(foreground);
        } else {
            AndroidUtils.goAccessibilityServiceSettings(context);
//            Toast.makeText(context, "请开启无障碍功能", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 判断当前应用的辅助功能服务是否开启
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
           e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }


    /**
     * 方法3：无意中看到乌云上有人提的一个漏洞，Linux系统内核会把process进程信息保存在/proc目录下，使用Shell命令去获取，再根据进程的属性判断是否为前台
     * 不需要权限;只能判断自身是否在前台。
     * @param packageName 需要检查是否位于栈顶的App的包名
     */
//    public static boolean getLinuxCoreInfo(Context context, String packageName) {
//        NLog.e("sjh7", "isMyProcessInTheForeground " +    ProcessManager.isMyProcessInTheForeground());
//
//        List<AndroidAppProcess> processes = ProcessManager.getRunningForegroundApps(context);
//        for (AndroidAppProcess appProcess : processes) {
//            if (appProcess.getPackageName().equals(packageName) && appProcess.foreground) {
//                return true;
//            }
//        }
//        return false;
//    }


}
