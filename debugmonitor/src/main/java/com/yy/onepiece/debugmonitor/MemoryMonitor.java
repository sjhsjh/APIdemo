package com.yy.onepiece.debugmonitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.Utils2;
import com.yy.onepiece.debugmonitor.image.ImageMonitor;
import java.io.File;

/**
 * 定时内存大小监控
 * @author shaojinhui@yy.com
 * @date 2020/8/20
 */
public class MemoryMonitor extends AbstractMonitor {
    private static final String TAG = "sjh5";
    private static final int UPDATE_DURATION = 10 * 60 * 1000;   // ms
    private static final float MAX_MEMORY_RATIO = 0.8f;          // 堆内存最大占用比例. 0~1
    private static MemoryMonitor sInstance;
    private Handler mHandler;

    public static MemoryMonitor getInstance() {
        if (null == sInstance) {
            synchronized (MemoryMonitor.class) {
                if (null == sInstance) {
                    sInstance = new MemoryMonitor();
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

        HandlerThread handlerThread = new HandlerThread("MemoryMonitor");
        handlerThread.start();

        mHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {
                if (!isSwitchOpen()) {
                    exit();
                    return true;
                }
                dumpMemoryNow(false);

                mHandler.sendEmptyMessageDelayed(0, UPDATE_DURATION);
                return false;
            }
        });

        mHandler.sendEmptyMessageDelayed(0, 10000);
    }

    /**
     * 获取当前 内存状况 和 bitmap状况，有需要时记录和释放部分内存
     * @param writeSD
     */
    public void dumpMemoryNow(boolean writeSD) {
        ImageMonitor.getInstance().dumpBitmapInfoNow(writeSD);      // 检查bitmap使用状况

        MemoryManager memoryManager = MemoryManager.getInstance(Utils2.getContext());
        String memInfo = memoryManager.getAllMemoryDetail();
        Utils.INSTANCE.log(TAG, "--start--\n" + memInfo + "---end---");

        if (memoryManager.getMemoryRatio() >= MAX_MEMORY_RATIO) {
            Toast.makeText(Utils2.getContext(),
                    "内存占用已超80%！ \n当前占用最大堆内存比例 ： " + String.format("%.2f", memoryManager.getMemoryRatio() * 100)
                            + "%  详情请查看日志 " + MonitorConfig.INSTANCE.getMemoryMonitorDir(),
                    Toast.LENGTH_LONG).show();
            writeSD(memInfo);

            memoryManager.trimMemory();
        } else if (writeSD) {
            Toast.makeText(Utils2.getContext(),
                    "当前占用最大堆内存比例 ： " + String.format("%.2f", memoryManager.getMemoryRatio() * 100)
                            + "%  详情请查看日志 " + MonitorConfig.INSTANCE.getMemoryMonitorDir(),
                    Toast.LENGTH_LONG).show();
            writeSD(memInfo);
        }
    }

    private void writeSD(String content) {
        String timeString = TimeUtils
                .getFormatTimeString(System.currentTimeMillis(), "year-mon-day_hour:min:sec");

        String filePath =
                MonitorConfig.INSTANCE.getMemoryMonitorDir() + File.separator + timeString + ".txt";
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
