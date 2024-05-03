package com.yy.onepiece.debugmonitor.thread;

import java.util.Set;
import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Android 创建线程主要是通过以下几种方式：
 *
 * Thread 及其子类
 * TheadPoolExecutor 及其子类、Executors、ThreadFactory 实现类
 * AsyncTask
 * Timer 及其子类
 *
 *
 * @author shaojinhui@yy.com
 * @date 2020/7/24
 */
public class ThreadHookManager {
    public static boolean hookSwitch = true;   // 是否开启hook thread
    private static ThreadHookManager sInstance;

    public static ThreadHookManager getInstance() {
        if (null == sInstance) {
            synchronized (ThreadHookManager.class) {
                if (null == sInstance) {
                    sInstance = new ThreadHookManager();
                }
            }
        }
        return sInstance;
    }

    private Set<XC_MethodHook.Unhook> unhooks;
    private ThreadHook mThreadHook;

    private ThreadHookManager() {
        mThreadHook = new ThreadHook();
    }

    /**
     * hook thread构造函数并为线程名称 加上创建位置的后缀
     */
    public void beginHook() {
        if (!hookSwitch) {
            return;
        }
        unhooks = DexposedBridge.hookAllConstructors(Thread.class, mThreadHook);
    }

    public void unhook() {
        for (XC_MethodHook.Unhook unhook : unhooks) {
            DexposedBridge.unhookMethod(unhook.getHookedMethod(), mThreadHook);
        }
    }
}
