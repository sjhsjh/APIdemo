package com.yy.onepiece.debugmonitor;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.ToastUtil;
import com.tencent.matrix.resource.analyzer.onepiece.util.Utils2;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author shaojinhui@yy.com
 * @date 2020/7/20
 */
public class MemoryManager {
    private static final String TAG = "sjh6";
    private static MemoryManager sInstance;
    private ActivityManager mActivityManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static MemoryManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (MemoryManager.class) {
                if (null == sInstance) {
                    sInstance = new MemoryManager(context);
                }
            }
        }
        return sInstance;
    }

    private MemoryManager(Context context) {
        mActivityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public String getAllMemoryDetail() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMemoryData2());
        sb.append("\n");
        sb.append(getMemoryData());
        sb.append("\n");
        sb.append(getRunTimeMemory());
        sb.append("\n");
        sb.append(getDeviceMemoryInfo());

        sb.append("\n\n");
        sb.append(getMaxThreadNumLimit());
        sb.append("\n");
        sb.append(getVmSize());
        sb.append("\n");
        sb.append(getFDLimit());
        sb.append("\n");
        sb.append(getFDLength());
        sb.append("\n");
        sb.append(getGCInfo());

        return sb.toString();
    }

    /**
     * 手机运行内存
     * Get a MemoryInfo object for the device's current memory status.
     */
    public String getDeviceMemoryInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);

        long totalMem = memoryInfo.totalMem / 1024 / 1024;      // 设备总内存 MB
        long availMem = memoryInfo.availMem / 1024 / 1024;      // 设备当前可用内存 MB
        long threshold = memoryInfo.threshold / 1024 / 1024;    // 系统开始杀进程的阈值
        boolean isLowMem = memoryInfo.lowMemory;                // 是否达到最低内存

        stringBuilder.append("Device MemoryInfo -> totalMem:" + totalMem + " MB, availMem:" + availMem + " MB, " +
                "threshold:" + threshold + " MB, isLowMem:" + isLowMem);
        Log.d(TAG, stringBuilder.toString());

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        if (!memoryInfo.lowMemory) {
            // Do memory intensive work ...
        }
        return stringBuilder.toString();
    }

    private float memoryRatio = 0f;

    public float getMemoryRatio() {
        return memoryRatio;
    }

    /**
     * 应用堆内存信息
     * 当前应用内存(dalvik内存) 占 最大内存的比例
     */
    public String getRunTimeMemory() {
        StringBuilder stringBuilder = new StringBuilder();
        int memoryClass = mActivityManager.getMemoryClass();            // 虚拟机java堆大小的上限，分配对象时突破这个大小就会OOM MB
        int largeMemoryClass = mActivityManager.getLargeMemoryClass();  // manifest中设置 largeHeap=true 时虚拟机java堆的上限 MB
        stringBuilder.append("Java Heap -> MemoryClass:" + memoryClass
                + " MB, largeMemoryClass:" + largeMemoryClass + " MB");

        // 虚拟机java堆大小的上限，超过则OOM
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        float ratio = (float) usedMemory / maxMemory;
        stringBuilder.append("\nRuntime -> totalMemory = " + Runtime.getRuntime().totalMemory() / 1024 / 1024
                + " MB, usedMemory = " + usedMemory  / 1024 / 1024
                + " MB, freeMemory = " + Runtime.getRuntime().freeMemory()  / 1024 / 1024 + " MB");
        stringBuilder.append("\nRuntime -> maxMemory = " + maxMemory / 1024 / 1024 + " MB, ratio = " + ratio);

        memoryRatio = Math.round(ratio * 10000) / 10000f;

//        totalMemory = 6497887 usedMemory = 5462816 freeMemory = 1035071
//        maxMemory = 536870912 ratio = 0.010175288
        Log.d(TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }

    private long mTotalPss = 0;

    public long getTotalPss() {
        return mTotalPss;
    }

    /**
     * 当前app内存占用大小。
     * 耗时和耗CPU的操作
     */
    public String getMemoryData() {
        StringBuilder stringBuilder = new StringBuilder();
        Debug.MemoryInfo info = new Debug.MemoryInfo();
        Debug.getMemoryInfo(info);
        stringBuilder.append("totalPss:" + info.getTotalPss()    //  total PSS memory usage in kB.
                + " kB, nativeSize:" + info.nativePss
                + " kB ,dalvikPss:" + info.dalvikPss
                + " kB, otherPss:" + info.otherPss);
        if (Build.VERSION.SDK_INT >= 23) {
            stringBuilder.append("\ngetMemoryStats (KB):" + info.getMemoryStats()
                    + " nativeHeapAllocatedSize = " + Debug.getNativeHeapAllocatedSize() / 1024 / 1024 + " MB.");
        }
        Log.d(TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 当前app内存占用大小，同perfDog
     * 子线程内执行
     */
    public String getMemoryData2() {
        StringBuilder stringBuilder = new StringBuilder();
        float mem = 0.0F;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    mem = totalPss / 1024.0F;      // Mem in MB
                    mTotalPss = (long) mem;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getMemoryData2 fail: " + e.toString());
        }
        stringBuilder.append("totalPss :" + mem + " MB");
        Log.d(TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 系统的线程数限制(小米该文件不存在,华为部分机型上该上限被修改的很低（大约500)
     * nexeus：13033
     * cat /proc/sys/kernel/threads-max
     */
    public String getMaxThreadNumLimit() {
        File file = new File("proc/sys/kernel/threads-max");
        Log.w(TAG, "-getMaxThreadNumLimit file.exists()---" + file.exists());
        if (file.exists()) {
            String numStr = FileIOUtils.readFile2String(file, null);
            String result = "getMaxThreadNumLimit num = " + numStr;
            Log.w(TAG, result);
            return result;
        }
        return "";
    }

    /**
     * 当前进程所占用的虚拟内存
     * VmPeak: 4468340 kB
     * VmSize: 4466888 kB
     */
    public String getVmSize() {
        File file = new File("/proc/" + Process.myPid() + "/status");
        Log.w(TAG, "-getVmSize file.exists()---" + file.exists());
        if (file.exists()) {
            String statusStr = FileIOUtils.readFile2String(file, null);
            String vmSizeStr = getOneLineContent(statusStr, "VmSize");
            // String threadNumStr = getOneLineContent(statusStr, "Threads");   // bigger
            // String result = "getVmSize vmSize = " + vmSizeStr + "\n" + threadNumStr;

            String result = "getVmSize vmSize = " + vmSizeStr;
            Log.w(TAG, result);
            return result;
        }
        return "";
    }

    /**
     * 在/proc/pid/limits描述着Linux系统对对应进程的限制，其中Max open files就代表可创建FD的最大数目。
     * Limit                     Soft Limit           Hard Limit           Units
     * Max open files            32768                32768                files
     */
    public String getFDLimit() {
        File file = new File("/proc/" + Process.myPid() + "/limits");
        Log.w(TAG, "-getFDLimit file.exists()---" + file.exists());
        if (file.exists()) {
            String fdLimitStr = FileIOUtils.readFile2String(file, null);
            fdLimitStr = getOneLineContent(fdLimitStr, "Max open files");
            String result = "getFDLimit fdLimit = " + fdLimitStr;
            Log.w(TAG, result);
            return result;
        }
        return "";
    }

    /**
     * 获取文件某一列的内容
     */
    private String getOneLineContent(String content, String name) {
        String subStr;
        if (!TextUtils.isEmpty(content)) {
            int index = content.indexOf(name);
            if (index != -1) {
                subStr = content.substring(index);
                int index2 = subStr.indexOf("\n");
                if (index2 != -1) {
                    return subStr.substring(0, index2);
                }
            }
        }
        return "";
    }

    /**
     * 进程中创建的FD（文件描述符）记录在/proc/pid/fd中
     */
    public String getFDLength() {
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File("/proc/" + Process.myPid() + "/fd");
        File[] files = file.listFiles();
        if (files == null) {
            return "";
        }
        int length = files.length; // 即进程中的fd数量,约72
        stringBuilder.append("created fd length : " + length);

        // for (int i = 0; i < length; i++) {
        //     if (Build.VERSION.SDK_INT >= 21) {
        //         try {
        //             String str = Os.readlink(files[i].getAbsolutePath()); // 得到软链接实际指向的文件
        //             // todo 对单个FD如“anon_inode:[eventfd]”进行计数
        //             stringBuilder.append("readlink : " + str);
        //         } catch (ErrnoException e) {
        //             e.printStackTrace();
        //         }
        //     } else {
        //         // 6.0以下系统可以通过执行readlink命令去得到软连接实际指向文件，但是耗时较久
        //     }
        // }

        Log.w(TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 消耗性能
     */
    public void startAllocCounting() {
        Debug.startAllocCounting();
    }

    /**
     * 消耗性能
     */
    public void stopAllocCounting() {
        long allocCount = Debug.getGlobalAllocCount();      // 内存分配次数(start 与 stop之间)
        long allocSize = Debug.getGlobalAllocSize();        // 内存分配大小
        long gcCount = Debug.getGlobalGcInvocationCount();  // GC次数
        Log.w(TAG, "allocCount = " + allocCount + " allocSize = " + allocSize + " gcCount = " + gcCount);
        Debug.stopAllocCounting();
    }

    public String getGCInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String gcCount = Debug.getRuntimeStat("art.gc.gc-count");                   // 运行的GC次数
            String gcTime = Debug.getRuntimeStat("art.gc.gc-time");                     // GC使用的总耗时，单位是ms
            String blockingGcCount = Debug.getRuntimeStat("art.gc.blocking-gc-count");  // 阻塞式GC的次数
            String blockingGcTime = Debug.getRuntimeStat("art.gc.blocking-gc-time");    // 阻塞式GC的总耗时

            Log.w(TAG, "--gc-count--" + gcCount);
            Log.w(TAG, "--gc-time--" + gcTime);
            Log.w(TAG, "--blocking-gc-count--" + blockingGcCount);
            Log.w(TAG, "--blocking-gc-time--" + blockingGcTime);

            try {
                int blockingCount = Integer.parseInt(blockingGcCount);
                stringBuilder.append("gcCount = ");
                stringBuilder.append(gcCount);
                stringBuilder.append(" gcTime = ");
                stringBuilder.append(gcTime);
                stringBuilder.append(" blockingGcCount = ");
                stringBuilder.append(blockingGcCount);
                stringBuilder.append(" blockingGcTime = ");
                stringBuilder.append(blockingGcTime);
                stringBuilder.append("\n");

                if (blockingCount > 0) {
                    ToastUtil.toastAnyThread("阻塞式GC的次数为" + blockingCount + "次\n" + "GC总耗时 " + blockingGcTime + "ms");
                    String timeString =
                            TimeUtils.getFormatTimeString(System.currentTimeMillis(), "year-mon-day_hour:min:sec");
                    writeSD(timeString + " : " + stringBuilder.toString());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void writeSD(String content) {
        FileIOUtils.writeFileFromString(MonitorConfig.INSTANCE.getGcInfoFile(), content, true);
    }

    /**
     * 清除GPU绘图缓存
     * 在局部时间内让应用的一系列GPU缓存被清理，相当于硬件加速失效。
     */
    public void cleanGraphicsCache() {
        String methodName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // WindowManagerGlobal.getInstance().trimMemory(TRIM_MEMORY_COMPLETE);
            methodName = "trimMemory";
        } else {
            // WindowManagerGlobal.getInstance().startTrimMemory(TRIM_MEMORY_COMPLETE);
            methodName = "startTrimMemory";
        }
        try {
            Class threadClazz = Class.forName("android.view.WindowManagerGlobal");
            Object wmgInstnace = threadClazz.getMethod("getInstance").invoke(null, (Object[]) null);

            Method method = threadClazz.getDeclaredMethod(methodName, int.class);
            method.invoke(wmgInstnace, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放GPU绘图缓存 + 回收部分glide缓存
     */
    public void trimMemory() {
        cleanGraphicsCache();

        handler.removeCallbacksAndMessages(null);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.get(Utils2.getContext())
                        .trimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN);
            }
        });
    }
}
