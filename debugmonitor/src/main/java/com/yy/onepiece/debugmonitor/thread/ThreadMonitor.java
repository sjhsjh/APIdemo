package com.yy.onepiece.debugmonitor.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.Utils2;
import com.yy.onepiece.debugmonitor.AbstractMonitor;
import com.yy.onepiece.debugmonitor.MonitorConfig;
import com.yy.onepiece.debugmonitor.Utils;
import java.io.File;

/**
 * The thread debugger entrance.
 * 定时线程监控
 * @author shaojinhui@yy.com
 * @date 2020/8/06
 */
public class ThreadMonitor extends AbstractMonitor {
    private static final String TAG = "sjh1";
    private static final int UPDATE_DURATION = 10 * 60 * 1000;    // ms
    private static final int MAX_THREAD_NUM = 200;                // 线程数量限制值
    private static ThreadMonitor sInstance;
    private Handler mHandler;
    private ThreadDebugManager mThreadDebugManager;

    public static ThreadMonitor getInstance() {
        if (null == sInstance) {
            synchronized (ThreadMonitor.class) {
                if (null == sInstance) {
                    sInstance = new ThreadMonitor();
                }
            }
        }
        return sInstance;
    }

    /**
     * 开始监控
     */
    public synchronized void init() {
        exit();
        mThreadDebugManager = new ThreadDebugManager();

        HandlerThread handlerThread = new HandlerThread(CommonThreadKey.Others.THREAD_DEBUGGER);
        handlerThread.start();

        mHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {
                if (!isSwitchOpen()) {
                    exit();
                    return true;
                }
                dumpThreadNow(false);

                mHandler.sendEmptyMessageDelayed(0, UPDATE_DURATION);
                return false;
            }
        });

        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    public void dumpThreadNow(boolean writeSD) {
        int threadSize = mThreadDebugManager.dumpThreadData();
        String threadInfo = mThreadDebugManager.drawUpEachThreadSize();
        Utils.INSTANCE.log(TAG, "--dumpThreadNow  start--\n" + threadInfo + "---end---");

        if (threadSize >= MAX_THREAD_NUM) {
            Toast.makeText(Utils2.getContext(),
                    "线程数量已超过" + MAX_THREAD_NUM + " 详情请查看日志 " + MonitorConfig.INSTANCE.getThreadMonitorDir(),
                    Toast.LENGTH_LONG).show();
            writeSD(threadInfo);
        } else if (writeSD) {
            Toast.makeText(Utils2.getContext(),
                    "当前线程数量 ： " + threadSize + " 详情请查看日志 " + MonitorConfig.INSTANCE.getThreadMonitorDir(),
                    Toast.LENGTH_LONG).show();
            writeSD(threadInfo);
        }
    }

    private void writeSD(String content) {
        String timeString = TimeUtils
                .getFormatTimeString(System.currentTimeMillis(), "year-mon-day_hour:min:sec");

        String filePath = MonitorConfig.INSTANCE.getThreadMonitorDir() + File.separator + timeString + ".txt";
        FileIOUtils.writeFileFromString(filePath, content);
    }

    /**
     * @return {@code true} If uninstall the thread debugger successfully. If it occur failed, there
     * is already no thread debugger in this process.
     */
    public synchronized boolean exit() {
        if (mHandler == null) {
            return false;
        }
        mHandler.removeMessages(0);
        mHandler.getLooper().quit();

        return true;
    }
}
