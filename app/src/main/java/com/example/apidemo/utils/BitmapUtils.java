package com.example.apidemo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;


public class BitmapUtils {

    /**
     * 截取可见屏幕，不包括状态栏
     */
    public static Bitmap shotActivity(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        view.setDrawingCacheEnabled(true);
        view.destroyDrawingCache(); // 更新cache
        return Bitmap.createBitmap(view.getDrawingCache(), 0, frame.top, frame.width(), frame.height());
    }

    /**
     * 截取可见屏幕，包括状态栏
     */
    public static Bitmap shotScreen(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Display display = activity.getWindowManager().getDefaultDisplay();
        view.layout(0, 0, display.getWidth(), display.getHeight());
        // 允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
        view.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    /**
     * 截取view的根层 可见屏幕部分的视图
     */
    public static Bitmap getRootViewBitmap(View view) {
        return shotViewBitmap(view.getRootView());
    }

    /**
     * 截取可见屏幕部分的view视图
     */
    public static Bitmap shotViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        Bitmap bmp = null;
        try {
            v.layout(0, 0, v.getWidth(), v.getHeight());
            // 允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
            v.setDrawingCacheEnabled(true);
            v.buildDrawingCache();
            bmp = Bitmap.createBitmap(v.getDrawingCache());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 获取view的完整视图图片（即使没有显示出来的部分）
     */
    public static Bitmap convertBitmap(View view) {
        return convertViewToBitmap(view, view.getWidth(), view.getHeight());
    }

    /**
     * 通过计算的方法宽高后，获取view的完整视图图片（即使没有显示出来的部分）
     */
    public static Bitmap convertMeasureBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.AT_MOST));
        return convertViewToBitmap(view, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    /**
     * convert view to bitmap according to with and height
     */
    public static Bitmap convertViewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }


}
