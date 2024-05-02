/*
 * Copyright (C) 2015-2016 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yy.onepiece.debugmonitor.thread;

import android.util.Log;
import java.util.Map;
import java.util.Set;

/**
 * @author jacks
 * @since 2019-07-29 12:03
 */
public class ThreadUtils {
    private static final String TAG = "ThreadUtils";

    private static ThreadGroup rootGroup = null;

    /**
     * @return threads but its count not equal to real thread count, you need filter null manually.
     */
    public static Thread[] getAllThreads() {
        ThreadGroup rootGroup = ThreadUtils.rootGroup;
        if (rootGroup == null) {
            rootGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup parentGroup;
            while ((parentGroup = rootGroup.getParent()) != null) {
                rootGroup = parentGroup;
            }
            ThreadUtils.rootGroup = rootGroup;
        }

        Thread[] threads = new Thread[rootGroup.activeCount()];
        while (rootGroup.enumerate(threads, true) == threads.length) {
            // thread array not big enough for enumerate try more
            threads = new Thread[threads.length + threads.length / 2];
        }

        return threads;
    }

    /**
     * 获取当前进程的所有线程。
     * Hello world工程的初始线程（Threads size is 14）：
     * Signal Catcher
     * ReferenceQueueDaemon
     * FinalizerDaemon
     * FinalizerWatchdogDaemon
     * HeapTaskDaemon
     * Profile Saver
     * main
     * Jit thread pool worker thread 0
     * Binder:31478_1
     * Binder:31478_2
     * Binder:31478_3
     * Binder:31478_4
     * Binder:interceptor
     * RenderThread
     */
    public static Thread[] getProcessAllThread() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
        Log.d(TAG, "Threads size is " + atualThreads.length);

        for (Thread thread : atualThreads) {
            Log.d(TAG, "Thread name : " + thread.getName());
        }
        return atualThreads;
    }

    /**
     * 进程中的所有线程对应的堆栈信息
     */
    public static void printThreadStack() {
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        Set<Thread> set = stacks.keySet();
        Log.d(TAG, "Thread size: " + set.size());

        for (Thread threadKey : set) {
            Log.w(TAG, "---- thread name : " + threadKey.toString() + " ----");

            // for (StackTraceElement stackTrace : threadKey.getStackTrace()) {
            //     Log.d(TAG, "StackTraceElement: " + stackTrace);
            // }

            // StackTraceElement[] stackTraceElements = stacks.get(threadKey);
            // for (StackTraceElement st : stackTraceElements) {
            //     Log.d(TAG, "StackTraceElement: " + st.toString());
            // }
            Log.w(TAG, "\n");
        }
    }

}
