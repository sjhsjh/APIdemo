package com.example.apidemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import com.example.apidemo.ViewUtils;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/9
 */
public class NestChildView extends LinearLayout implements NestedScrollingChild2 {
    private NestedScrollingChildHelper mChildHelper;
    private int mLastMotionY;
    private int mLastFlingY;

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mEventAdjustOffsetY;

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
        initFlingData();
    }

    private int mTouchSlop;
    private int mMaxVelocity;
    private int mMinVelocity;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;

    private void initFlingData() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();  // 32
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();     // 32000
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();     // 200
        mScroller = new OverScroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTracker(event);

        // event仅用于计算手指移动距离，
        // evAdjust为parent移动后手指的更正坐标，parent没有滑动时手指在当前view的本来位置，用于mVelocityTracker.addMovement（即fling）
        MotionEvent evAdjust = MotionEvent.obtain(event);
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mEventAdjustOffsetY = 0;
        }
        evAdjust.offsetLocation(0, mEventAdjustOffsetY);
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(evAdjust);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionY = (int) event.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_MOVE:
                final int y = (int) event.getY();   // 获取的是手指在当前view中的坐标
                int deltaY = mLastMotionY - y;      // 上滑为正
                NLog.d("sjh8", "==deltaY==" + deltaY);

                // ① parent先滑动。mScrollOffset使用window坐标系，即向下为正！！
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                    deltaY -= mScrollConsumed[1];
                    evAdjust.offsetLocation(0, mScrollOffset[1]);
                    mEventAdjustOffsetY += mScrollOffset[1];
                }
                NLog.i("sjh8", "==deltaY2==" + deltaY);
                // ② child滑动
                /****限制child内容移动的边界***/
                NLog.w("sjh8", "==y==" + y + "  ==mScrollOffset[1]==" + mScrollOffset[1]);
                // 手指滑动后且parent滑动前，手指本来在y位置（150），parent滑动后，导致手指在当前view中的坐标变化了（200），
                // 需要更正y坐标为 跟parent没有滑动相同时手指在当前view中的坐标！！！即将从200滑到150模拟成从250滑到200！！
                mLastMotionY = y - mScrollOffset[1];

                final int oldY = getScrollY();
                final int rangeY = ViewUtils.getScrollRange(this);
                final int top = rangeY;      // can change    向上为正
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
                    evAdjust.offsetLocation(0, mScrollOffset[1]);
                    mEventAdjustOffsetY += mScrollOffset[1];
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int yVelocity = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > mMinVelocity) {
                    flingWithNestedDispatch(yVelocity);
                }
                releaseVelocityTracker();
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
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

    // 自行重写两个fling方法，否则使用父类的fling方法（sdk这套嵌套滑动默认不开启）
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * @param velocityY 向下为正
     */
    private void flingWithNestedDispatch(int velocityY) {
        final int scrollY = getScrollY();
        final boolean canFling = (scrollY > 0 || velocityY < 0)
                && (scrollY < ViewUtils.getScrollRange(this) || velocityY > 0);
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);    // todo
            fling(velocityY);
        }
    }

    /**
     * 需重载view的computeScroll来使用（draw中调用computeScroll）
     * @param yVelocity 向下为正
     */
    private void fling(int yVelocity) {
        if (getChildCount() > 0) {
            mScroller.fling(0, getScrollY(), 0, -yVelocity, 0, 0, 0, ViewUtils.getScrollRange(this) * 9);
            // invalidate();

            // TYPE_TOUCH的Up相当于这里TYPE_NON_TOUCH的Down了！！！
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);

            // mScroller.fling(getScrollX(), getScrollY(), // start
            //         0, -yVelocity, // velocities
            //         0, 0, // minX ~ maxX
            //         Integer.MIN_VALUE, Integer.MAX_VALUE, // minY ~ maxY、、
            //         0, 0); // overscroll
            mLastFlingY = getScrollY(); // 相当于down时记录y坐标 todo
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {  // 返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
            final int y = mScroller.getCurrY(); // 相当于手指的坐标

            int deltaY = y - mLastFlingY;  // 上滑为正。y是mScroller动态计算的，与parent是否滚动无关，因此后续mLastFlingY无需校正。
            NLog.d("sjh6", "==y==" + y + "  ==mLastFlingY==" + mLastFlingY + "  ==deltaY==" + deltaY);

            // ① parent先滑动。mScrollOffset使用window坐标系，即向下为正！！
            if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                deltaY -= mScrollConsumed[1];
            }
            NLog.v("sjh6", "==deltaY2==" + deltaY);
            if (deltaY != 0) {
                // ② child滑动
                /****限制child内容移动的边界***/
                final int oldY = getScrollY();
                final int rangeY = ViewUtils.getScrollRange(this);  // 2.75 * 400 = 1100
                final int top = rangeY;      // can change    向上为正
                final int bottom = 0;        // can change

                int newScrollY = getScrollY() + deltaY;
                if (newScrollY < bottom) {
                    newScrollY = bottom;
                } else if (newScrollY > top) {
                    newScrollY = top;
                }
                NLog.i("sjh6", "==newScrollY==" + newScrollY);
                scrollTo(0, newScrollY);
                /****限制child内容移动的边界***/

                // ③ parent再滑动
                final int childConsumedY = getScrollY() - oldY;
                final int unconsumedY = deltaY - childConsumedY;
                if (unconsumedY > 0) {
                    NLog.w("sjh6", "==unconsumedY==" + unconsumedY);
                }
                if (dispatchNestedScroll(0, childConsumedY, 0, unconsumedY, null,
                        ViewCompat.TYPE_NON_TOUCH)) {

                }
            }
            // scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // Finally update the scroll positions and post an invalidation
            mLastFlingY = y;
            postInvalidate();
        } else {    // fling 结束
            if (hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
            }
            mLastFlingY = 0;
        }
    }

    private void initVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        // mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
