package com.yy.onepiece.debugmonitor.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils;
import com.tencent.matrix.resource.analyzer.onepiece.util.ToastUtil;
import com.yy.onepiece.debugmonitor.MonitorConfig;
import de.robv.android.xposed.XC_MethodHook;

/**
 * setImageBitmap、setImageDrawable、setImageResource时，图片超宽于view则记录异常
 * @author shaojinhui@yy.com
 * @date 2020/8/11
 */
public class SetImageHook extends XC_MethodHook {
    private static final String TAG = "sjh3";

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (!ImageHookManager.Companion.getHookSwitch()) {
            ImageHookManager.getInstance().unhookAll();
        }
    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Log.w(TAG, "===SetImageHook afterHookedMethod method name :" + param.method.getName() + "===");

        ImageView imageView = (ImageView) param.thisObject;
        checkBitmap(imageView, ((ImageView) param.thisObject).getDrawable());
    }

    /**
     * 图片超宽检测。图片宽高都大于view的2倍以上则警告
     */
    private static void checkBitmap(Object imageViewObj, Drawable drawable) {
        if (drawable instanceof BitmapDrawable && imageViewObj instanceof View) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                final View view = (View) imageViewObj;
                int width = view.getWidth();
                int height = view.getHeight();

                if (width > 0 && height > 0) {
                    if (bitmap.getWidth() >= width * 2 && bitmap.getHeight() >= height * 2) {
                        warn(bitmap.getWidth(), bitmap.getHeight(), width, height,
                                new RuntimeException("Bitmap size is too larger than the ImageView'size" +
                                        "(when setImageXXX)"));
                    }
                } else {
                    final Throwable stackTrace = new RuntimeException();
                    view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            int w = view.getWidth();
                            int h = view.getHeight();

                            if (w > 0 && h > 0) {
                                if (bitmap.getWidth() >= w * 2 && bitmap.getHeight() >= h * 2) {
                                    warn(bitmap.getWidth(), bitmap.getHeight(), w, h, stackTrace);
                                }
                                view.getViewTreeObserver().removeOnPreDrawListener(this);
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

    private static void warn(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight, Throwable t) {
        String warnInfo = "Bitmap size is too larger than the ImageView'size(when setImageXXX): " +
                "\n real size: (" + bitmapWidth + ',' + bitmapHeight + ')' +
                "\n desired size: (" + viewWidth + ',' + viewHeight + ')' +
                "\n call stack trace: " + Log.getStackTraceString(t) + "\n\n";

        ToastUtil.toastAnyThread(warnInfo);
        Log.w(TAG, "===warnInfo===" + warnInfo);
        writeSD(warnInfo);
    }

    private static void writeSD(String content) {
        String timeString = TimeUtils.getFormatTimeString(System.currentTimeMillis(),
                "year-mon-day_hour:min:sec");
        FileIOUtils.writeFileFromString(MonitorConfig.INSTANCE.getSetImageHookFile(),
                timeString + " : " + content, true);
    }
}