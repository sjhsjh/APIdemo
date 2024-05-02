package com.yy.onepiece.debugmonitor.image;

import android.graphics.Bitmap;
import android.util.Log;
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.ResolutionUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.ToastUtil;
import com.tencent.matrix.resource.analyzer.onepiece.util.Utils2;
import com.yy.onepiece.debugmonitor.MonitorConfig;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Bitmap.createBitmap()
 * <p>
 * BitmapFactory.decodeResource
 * BitmapFactory.decodeFile
 * BitmapFactory.decodeStream
 * BitmapFactory.decodeByteArray
 * BitmapFactory.decodeFileDescriptor
 * BitmapFactory.decodeResourceStream
 *
 * @author shaojinhui@yy.com
 * @date 2020/8/12
 */
public class CreateImageHook extends XC_MethodHook {
    private static final String TAG = "sjh4";

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (!ImageHookManager.Companion.getHookSwitch()) {
            ImageHookManager.getInstance().unhookAll();
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Log.w(TAG, "===CreateImageHook afterHookedMethod method name :" + param.method.getName() + "===");

        String reason = "";
        if (param.method.getName().startsWith("createBitmap")) {
            reason = "Bitmap.createBitmap()";
        } else {
            reason = "BitmapFactory.decodeXXX";
        }
        Bitmap bitmap = (Bitmap) param.getResult();
        checkBitmap(bitmap, reason);

        // if (bitmap != null) {    // test
        //     warn(bitmap, reason);
        // }
        ImageMonitor.getInstance().tryAddBitmap(bitmap, Log.getStackTraceString(new Exception(reason)));
    }

    /**
     * 图片超宽检测。图片宽高都大于屏幕的2倍以上则警告
     */
    private static void checkBitmap(Bitmap bitmap, String reason) {
        int screenWidth = ResolutionUtils.getScreenWidth(Utils2.getContext());
        int screenHeight = ResolutionUtils.getScreenHeight(Utils2.getContext());

        if (bitmap != null && screenWidth > 0 && screenHeight > 0) {
            if (bitmap.getWidth() >= screenWidth * 2 && bitmap.getHeight() >= screenHeight * 2) {
                warn(bitmap, reason);
            }
        }
    }

    private static void warn(Bitmap bitmap, String reason) {
        String warnInfo = "Bitmap size is too larger than the Screen'size by " + reason +
                "\n Bitmap info : " + bitmap.getWidth() + " x " + bitmap.getHeight() +
                "\n Bitmap size: " + bitmap.getAllocationByteCount() +
                "\n call stack trace: " + Log.getStackTraceString(new Exception(reason)) + "\n\n";
        // com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool.createBitmap(LruBitmapPool.java:149)
        // com.bumptech.glide.load.resource.bitmap.Downsampler.decodeStream(Downsampler.java:583)

        ToastUtil.toastAnyThread(warnInfo);
        Log.w(TAG, "===warnInfo===" + warnInfo);
        writeSD(warnInfo);
    }

    private static void writeSD(String content) {
        String timeString = TimeUtils
                .getFormatTimeString(System.currentTimeMillis(), "year-mon-day_hour:min:sec");
        FileIOUtils.writeFileFromString(MonitorConfig.INSTANCE.getCreateImageHookFile(),
                timeString + " : " + content, true);
    }
}