package com.example.apidemo;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.example.apidemo.utils.NLog;

/**
 *
 * @date 2020/1/14
 */
public class MyAppBarLayout extends AppBarLayout {

    public MyAppBarLayout(Context context) {
        super(context);
    }

    public MyAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setElevation(float elevation) {
        super.setElevation(elevation);
        // NLog.i("sjh5", "===setElevation=="+ elevation);
        // NLog.w("sjh9", "===setElevation getStackTraceString==" + Log.getStackTraceString(new Exception("sjh9")));
    }

    /**
     * 自身的偏移由父view CoordinatorLayout完成，而不是自身的onTouchEvent进行平移
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NLog.i("sjh9", "===MyAppBarLayout dispatchTouchEvent== getScrollY() = " + getScrollY() + " getY() = " + getY());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        NLog.w("sjh9", "===MyAppBarLayout onTouchEvent== getScrollY() = " + getScrollY() + " getY() = " + getY());
        return super.onTouchEvent(event);
    }
}
