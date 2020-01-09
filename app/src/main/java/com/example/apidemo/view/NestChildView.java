package com.example.apidemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.example.apidemo.ViewUtils;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/9
 */
public class NestChildView extends LinearLayout implements NestedScrollingChild2 {
    private NestedScrollingChildHelper mChildHelper;
    private int mLastMotionY;
    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;

    public NestChildView(Context context) {
        super(context);
        initView();
    }

    public NestChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        mChildHelper = new NestedScrollingChildHelper(this);
        mChildHelper.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) event.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_MOVE:
                final int y = (int) event.getY();   // 获取的是手指在当前view中的坐标
                int deltaY = mLastMotionY - y;  // 上滑为正
                NLog.d("sjh8", "==deltaY==" + deltaY);

                // ① parent先滑动。mScrollOffset使用window坐标系，即向下为正！！
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset,
                        ViewCompat.TYPE_TOUCH)) {
                    deltaY -= mScrollConsumed[1];
                    // ev2.offsetLocation(0, mScrollOffset[1]);
                    // mNestedYOffset += mScrollOffset[1];
                }
                NLog.i("sjh8", "==deltaY2==" + deltaY);
                // ② child滑动
                /****限制child内容移动的边界***/
                // Scroll to follow the motion event
                NLog.w("sjh8", "==y==" + y + "  ==mScrollOffset[1]==" + mScrollOffset[1]);
                mLastMotionY = y - mScrollOffset[1];    // parent滑动后，影响了手指在当前view中的坐标，需要更正y坐标为手指在当前view中的新坐标！！

                final int oldY = getScrollY();
                final int rangeY = ViewUtils.getScrollRange(this);
                final int top = rangeY;    // can change    向上为正
                final int bottom = 0;        // can change

                int newScrollY = getScrollY() + deltaY;
                if (newScrollY < bottom) {
                    newScrollY = bottom;
                } else if (newScrollY > top) {
                    newScrollY = top;
                }
                scrollTo(0, newScrollY);
                /****限制child内容移动的边界***/

                // ③ parent再滑动
                final int childConsumedY = getScrollY() - oldY;
                final int unconsumedY = deltaY - childConsumedY;
                if (dispatchNestedScroll(0, childConsumedY, 0, unconsumedY, mScrollOffset,
                        ViewCompat.TYPE_TOUCH)) {
                    mLastMotionY -= mScrollOffset[1];
                    // ev2.offsetLocation(0, mScrollOffset[1]);
                    // mNestedYOffset += mScrollOffset[1];
                }
                break;
            case MotionEvent.ACTION_UP:
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }


    private void flingWithNestedDispatch(int velocityY) {
        final int scrollY = getScrollY();
        final boolean canFling = (scrollY > 0 || velocityY > 0)
                && (scrollY < ViewUtils.getScrollRange(this) || velocityY < 0);
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);
            fling(velocityY);
        }
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            // startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
            // mScroller.fling(getScrollX(), getScrollY(), // start
            //         0, velocityY, // velocities
            //         0, 0, // x
            //         Integer.MIN_VALUE, Integer.MAX_VALUE, // y
            //         0, 0); // overscroll
            // mLastScrollerY = getScrollY();
            // ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
