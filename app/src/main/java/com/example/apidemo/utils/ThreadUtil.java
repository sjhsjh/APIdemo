package com.example.apidemo.utils;

import java.lang.reflect.Method;

public class ThreadUtil {
    private static Object lock = new Object();
    private static String TAG = "sjh2";


    /**
     * 拿到线程的持有锁和等待锁信息，再配合有向无环图算法，检测是否存在循环依赖，就可以做到死锁检测。
     *
     * VMStack 源码: https://cs.android.com/android/platform/superproject/+/master:libcore/libart/src/main/java/dalvik/system/VMStack.java;l=35?q=VMStack&sq=&ss=android%2Fplatform%2Fsuperproject
     *
     * AnnotatedStackTraceElement 源码: https://cs.android.com/android/platform/superproject/+/master:libcore/libart/src/main/java/dalvik/system/AnnotatedStackTraceElement.java
     *
     */
    public static void getThreadLock(Thread thread) {
        try {
            Class<?> vmStackClass = Class.forName("dalvik.system.VMStack");

            Method getStackTraceMethod =
                    vmStackClass.getMethod("getAnnotatedThreadStackTrace", Thread.class);

            Object elementArray = getStackTraceMethod.invoke(null, thread);

            Object[] arr = (Object[]) elementArray;
            NLog.d(TAG, "==elementArray length=" + arr.length);

            for (Object annotatedStackTraceElement : arr) {
                // AnnotatedStackTraceElement
                NLog.d(TAG, annotatedStackTraceElement.toString());
                Class<?> cls = annotatedStackTraceElement.getClass();

                Method m = cls.getMethod("getHeldLocks");
                Method m2 = cls.getMethod("getBlockedOn");
                Object heldLocks = m.invoke(annotatedStackTraceElement);
                Object blockedOn = m2.invoke(annotatedStackTraceElement);
                NLog.d(TAG, "heldLockArray = " + heldLocks + " blockedOn = " + blockedOn);

                Object[] heldLockArray = (Object[]) heldLocks;
                if (heldLockArray != null) {
                    NLog.i(TAG, "==heldLockArray length=" + heldLockArray.length);
                    for (Object heldLock : heldLockArray) {
                        NLog.i(TAG, "heldLock = " + heldLock);
                    }
                }
            }

        } catch (Exception e) {
            NLog.e(TAG, "error = " + e);
        }
    }

    public static void checkDeadlock(Thread thread) {
        // todo
    }

    public static void test() {
        new Thread("myThread") {
            @Override
            public void run() {
                synchronized (lock) {
                    ThreadUtil.getThreadLock(Thread.currentThread());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    NLog.w(TAG, "finish");
                }

            }
        }.start();
    }

}
