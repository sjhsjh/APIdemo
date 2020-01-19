package com.example.apidemo.utils;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/19.
 */
public class AndroidUtils {
    public static final String BITMAP_FOLDER = "bitmap";

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

    /**
     * 将文本拷贝至剪贴板
     */
    public static void copyText(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//        clipboardManager.setText(text.trim());
//
//        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));

        ClipData clip = ClipData.newPlainText("simple text copy", text);
        clipboardManager.setPrimaryClip(clip);
    }


    /**
     * 保存bitmap至指定文件夹
     */
    public static String saveBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        File folder = context.getExternalFilesDir(BITMAP_FOLDER);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return null;
            }
        }
        String bitmapFileName = System.currentTimeMillis() + ".jpg";
        // String bitmapFileName = System.currentTimeMillis() + "";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(folder + "/" + bitmapFileName);
            boolean successful = bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);

            if (successful)
                return folder.getAbsolutePath() + "/" + bitmapFileName;
            else
                return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 192.168.0.100  --> 192.168.0.104
     * @return
     */
    public static String getLocalIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        // NLog.i("sjh", "ipAddress = "+ipAddress);
        if (ipAddress == 0) return "";
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager connectivity;
        NetworkInfo activeNetwork;
        try {
            connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = connectivity.getActiveNetworkInfo();
        } catch (Exception e) {
            return false;
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * 前往开启辅助服务界面
     */
    public static void goAccessibilityServiceSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }
}