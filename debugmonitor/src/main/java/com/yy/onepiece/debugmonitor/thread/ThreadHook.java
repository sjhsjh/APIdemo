package com.yy.onepiece.debugmonitor.thread;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;

/**
 * 监控
 * @author shaojinhui@yy.com
 * @date 2020/7/24
 */
public class ThreadHook extends XC_MethodHook {
    private static final String TAG = "sjh2";

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        Log.i(TAG, "=========beforeHookedMethod============");
        if (!ThreadHookManager.hookSwitch) {
            ThreadHookManager.getInstance().unhook();
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log.i(TAG, "=========afterHookedMethod============");       // todo
        super.afterHookedMethod(param);
        Thread thread = (Thread) param.thisObject;

        addThreadSuffix(thread);
        // thread.getName() --> todo Thread-215 ??
        Log.w(TAG, "Thread: " + thread.getName() + "  class:" + thread.getClass() + " is created.");
        Log.w(TAG, "Current execute thread name is : " + Thread.currentThread().getName());
    }


    /**
     * thread 创建时，其name增加来源的后缀
     */
    private static void addThreadSuffix(Thread thread) {
        String stackStr = getThreadConstructStackString(thread);
        if (stackStr != null && thread.getName() != null && !thread.getName().endsWith("_suffix>")) {
            thread.setName(thread.getName() + "_<" + stackStr + "_suffix>");
        }
    }

    /**
     * 获取线程创建的位置
     * Thread construct at : android.os.AsyncTask$1.newThread(AsyncTask.java:195)
     * Thread construct at : android.os.HandlerThread.<init>(HandlerThread.java:44)
     */
    private static String getThreadConstructStackString(Thread thread) {
        // StackTraceElement[] stArray = thread.getStackTrace();    // xxx
        StackTraceElement[] stArray = Thread.currentThread().getStackTrace();   // 执行new thread所在的线程。
        String constructStack = null;
        String constructStackInApp = null;
        if (stArray.length == 9) {     // todo
            Log.d(TAG, "hook调用栈不完整");
            return null;
        }
        for (int i = 0; i < stArray.length; i++) {
            Log.v(TAG, "StackTraceElement: " + stArray[i]);
            // me.weishu.epic.art.entry.Entry.referenceBridge(Entry.java:186)
            // me.weishu.epic.art.entry.Entry64.referenceBridge(Entry64.java:239)
            // Entry64_2 never used
            // 第一个出现内容com.example.apidemo的栈
            if (constructStack == null && stArray[i].toString().startsWith("com.example.apidemo")) {
                constructStack = stArray[i].toString();
                Log.w(TAG, "(In app) Thread construct at : " + constructStack);
            }
            if (stArray[i].toString().contains("referenceBridge(Entry") && i + 1 < stArray.length) {
                constructStackInApp = stArray[i + 1].toString();
                Log.w(TAG, "Thread construct at : " + constructStackInApp);
            }
        }
        return constructStack;
    }
}
