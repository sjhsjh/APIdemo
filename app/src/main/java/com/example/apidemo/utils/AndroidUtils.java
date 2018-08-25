package com.example.apidemo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.example.apidemo.R;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/19.
 */
public class AndroidUtils {

    public static String debugLog(long receiveTime) {
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(receiveTime);
        return simpledate.format(date);
    }

    public static boolean isNightMode(Context context) {
        return (0x30 & context.getResources().getConfiguration().uiMode) == 32;    // UI_MODE_NIGHT_YES
    }

    public static void printMsgs(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getMemoryClass();    // 256.设备分配给app的最大堆内存
        int maxHeapSize = manager.getLargeMemoryClass();    // 521.是当清单文件配置了android:largeHeap="true" 才有的最大堆内存，一般是heapSize的2-3倍
        NLog.d("sjh8", "heapSize =  " + heapSize + " maxHeapSize = " + maxHeapSize);

        NLog.i("sjh8", "isDebug" + ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0));
    }



}