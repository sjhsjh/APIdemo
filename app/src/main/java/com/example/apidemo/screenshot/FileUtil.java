package com.example.apidemo.screenshot;

import android.content.Context;
import android.os.Environment;
import com.blankj.utilcode.util.AppUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ryze on 2016-5-26.
 */
public class FileUtil {
    // 系统保存截图的路径
    public static final String SCREENCAPTURE_PATH = "ScreenCapture" + File.separator + "Screenshots" + File.separator;
    public static final String SCREENSHOT_FILE_NAME_PREFIX = "Screenshot";
    // 系统相册目录
    public static final String GALLERY_PATH = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM;
    private static final String MY_SCREENSHOT_PATH = AppUtils.getAppName() + File.separator;

    // /storage/emulated
    public static String getAppPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return context.getFilesDir().toString();
        }
    }

    public static String createScreenShotDir(Context context) {
        // StringBuffer stringBuffer = new StringBuffer(getAppPath(context));
        // stringBuffer.append(File.separator);
        // stringBuffer.append(SCREENCAPTURE_PATH);
        StringBuffer stringBuffer = new StringBuffer(GALLERY_PATH);
        stringBuffer.append(File.separator);
        stringBuffer.append(MY_SCREENSHOT_PATH);

        File file = new File(stringBuffer.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return stringBuffer.toString();
    }

    public static String getScreenShotsName(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String date = simpleDateFormat.format(new Date());

        StringBuffer stringBuffer = new StringBuffer(createScreenShotDir(context));
        stringBuffer.append(SCREENSHOT_FILE_NAME_PREFIX);
        stringBuffer.append("_");
        stringBuffer.append(date);
        stringBuffer.append(".png");

        return stringBuffer.toString();
    }

}
