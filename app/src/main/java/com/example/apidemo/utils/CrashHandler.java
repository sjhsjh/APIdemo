package com.example.apidemo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Process;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * Created by jinhui on 18/1/29.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    //用来存储设备信息和异常信息  
    private String mDeviceInfos;
    private String mCrashDirPath;

    public CrashHandler() {

    }

    /**
     * 初始化UncaughtExceptionHandler 和 崩溃日志保存目录
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        File file = new File("\\sdcard");
        if(file.exists()) {
            mCrashDirPath = "\\sdcard\\crashes";
        }
        else {
            mCrashDirPath = context.getExternalFilesDir(null).getPath()  + File.separator + "crashes";
                          // Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        NLog.d("sjh8", "\\sdcard exists ? " + file.exists() + " mCrashDirPath=" + mCrashDirPath);

        //获取系统默认的UncaughtException处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理 
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        handleException(thread, ex);
        NLog.d("sjh8", "mDefaultHandler = " + mDefaultHandler.toString());
        // 如果系统提供了默认异常处理就交给系统进行处理，否则自己进行处理。
        if (mDefaultHandler != null) {     // mDefaultHandler系统默认是com.android.internal.os.RuntimeInit$UncaughtHandler，不为null。
            mDefaultHandler.uncaughtException(thread, ex);    // 此处用于弹出"停止运行"对话框，后续不再需要killProcess。
        } else {
            Process.killProcess(Process.myPid());
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *
     * @param throwable
     * @return true:如果处理了该异常信息;否则返回false. 
     */
    private boolean handleException(Thread thread, Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        // SD卡未挂载，不保存
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        // 控制台打印信息（System.err，W级别！）
        throwable.printStackTrace();

        //收集设备参数信息  
        collectDeviceInfo();
        //保存日志文件
        saveCrashInfo2File(thread, throwable);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        StringWriter deviceInfos = new StringWriter();
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                deviceInfos.append("versionName=").append(versionName).append("\n");
                deviceInfos.append("versionCode=").append(pi.versionCode + "").append("\n");
            }
        } catch (NameNotFoundException e) {
            NLog.e("CrashHandler collectDeviceInfo() NameNotFoundException--->" + e.getMessage());
        }
        deviceInfos.append("Build.VERSION").append("=").append(Build.VERSION.RELEASE.toString()).append("\n");
        deviceInfos.append("Build.VERSIONCODE").append("=").append(String.valueOf(Build.VERSION.SDK_INT)).append("\n");
        deviceInfos.append("=================================================================================\n");

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                deviceInfos.append(field.getName()).append("=").append(field.get(null).toString()).append("\n");
            } catch (Exception e) {
                NLog.e("CrashHandler collectDeviceInfo() Exception--->" + e.getMessage());
            }
        }

        mDeviceInfos = deviceInfos.toString();
    }

    private String collectMemInfo() {
        StringWriter meminfo = new StringWriter();
        PrintWriter writer = new PrintWriter(meminfo);
        writer.append("=================================================================================\n");
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        writer.append("memoryInfo.availMem=").append(String.valueOf(memoryInfo.availMem)).append("\n");
        writer.append("memoryInfo.lowMemory=").append(String.valueOf(memoryInfo.lowMemory)).append("\n");
        writer.append("memoryInfo.threshold=").append(String.valueOf(memoryInfo.threshold)).append("\n");
//        writer.append("Activity=").append(contextActivity).append("\n");
//        writer.append("Fragment=").append(contextFragment).append("\n");

        collectSelfMem(activityManager, writer);
        writer.close();
        return meminfo.toString();
    }

    private boolean collectSelfMem(ActivityManager am, PrintWriter writer) {
        List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        writer.append("=================================================================================\n");

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            if (runningAppProcessInfo.processName.contains(mContext.getPackageName())) {

                int pids[] = {runningAppProcessInfo.pid};
                Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);

                writer.append("dalvikPrivateDirty=").append(String.valueOf(self_mi[0].dalvikPrivateDirty)).append("\n");
                writer.append("dalvikPss=").append(String.valueOf(self_mi[0].dalvikPss)).append("\n");
                writer.append("nativePrivateDirty=").append(String.valueOf(self_mi[0].nativePrivateDirty)).append("\n");
                writer.append("nativePss=").append(String.valueOf(self_mi[0].nativePss)).append("\n");
                writer.append("nativeSharedDirty=").append(String.valueOf(self_mi[0].nativeSharedDirty)).append("\n");
                writer.append("otherPrivateDirty=").append(String.valueOf(self_mi[0].otherPrivateDirty)).append("\n");
                writer.append("otherPss=").append(String.valueOf(self_mi[0].otherPss)).append("\n");
                writer.append("otherSharedDirty=").append(String.valueOf(self_mi[0].otherSharedDirty)).append("\n");
                writer.append("TotalPrivateDirty=").append(String.valueOf(self_mi[0].getTotalPrivateDirty())).append("\n");
                writer.append("TotalPss=").append(String.valueOf(self_mi[0].getTotalPss())).append("\n");
                writer.append("TotalSharedDirty=").append(String.valueOf(self_mi[0].getTotalSharedDirty())).append("\n");

                return true;
            }
        }

        return false;
    }


    /**
     * 保存错误信息到文件中
     *
     * @param throwable
     * @return
     */
    private boolean saveCrashInfo2File(Thread thread, Throwable throwable) {
        // 检查目录是否存在，不存在则创建
        File dir = new File(mCrashDirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        // 输出设备信息
        printWriter.append(mDeviceInfos);
        // 输出内存信息
        String meminfo = collectMemInfo();
        printWriter.append(meminfo);
        // 输出线程信息
        printWriter.append("=================================================================================\n");
        if (thread != null) {
            String name = String.format("%s(%d)", thread.getName(), thread.getId());
            printWriter.write("The crashed thread is : ");
            printWriter.write(name + "\n\n");
        }
        // 输出部分崩溃栈信息（RuntimeException）
        throwable.printStackTrace(printWriter);

        // 输出完整崩溃栈信息（NullPointerException）
        printWriter.append("=================================================================================\n");
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        // 用于格式化日期,作为日志文件名的一部分
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".txt";
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(mCrashDirPath + File.separator + fileName);
            outputStream.write(stringWriter.toString().getBytes("UTF-8"));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {

                }
            }
        }

    }


}  