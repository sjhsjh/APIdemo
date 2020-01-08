package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * <br> 使用Scroller进行startScroll和fling
 * Created by jinhui.shao on 2017/9/8.
 */
public class MyView extends RelativeLayout {
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private int mMinVelocity;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();     // 32000
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();     // 200
        mScroller = new Scroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);  // 设置units的值为1000，意思为一秒时间内运动了多少个像素,最大为mMaxVelocity
                int YVelocity = (int) mVelocityTracker.getYVelocity();   // 向下为正数！！
                if (Math.abs(YVelocity) > mMinVelocity) {
                    fling(YVelocity);
                }
                releaseVelocityTracker();
                break;
            default:
        }

        return true;
    }

    /**
     * 需重载computeScroll来使用
     */
    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 1000);
        invalidate();
    }

    /**
     * 需重载computeScroll来使用
     * @param yVelocity
     */
    private void fling(int yVelocity) {
        // 范围是(startY + minY, startY + maxY)！！以startX，startY为参考点。绘制开始时先瞬间回到startX，startY。
        mScroller.fling(0, getScrollY(), 0, -yVelocity, 0, 0, -800, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {  // 返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if(null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

}