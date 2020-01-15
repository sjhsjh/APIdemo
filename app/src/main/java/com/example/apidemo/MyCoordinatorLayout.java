package com.example.apidemo;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/14
 */
public class MyCoordinatorLayout extends CoordinatorLayout {
    public MyCoordinatorLayout(Context context) {
        super(context);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NLog.v("sjh8", "==dispatchTouchEvent==CoordinatorLayout== ScrollY = "
                                     + getScrollY() + "  y = " + getY());                       // CoordinatorLayout本身并无移动
        NLog.d("sjh8", "==dispatchTouchEvent==AppBarLayout== ScrollY = "
                + getChildAt(0).getScrollY() + "  y = " + getChildAt(0).getY());  // AppBarLayout
        NLog.w("sjh8", "==dispatchTouchEvent==NestedScrollView== ScrollY = "
                + getChildAt(1).getScrollY() + "  y = " + getChildAt(1).getY());  // NestedScrollView

        return super.dispatchTouchEvent(ev);
    }
}
