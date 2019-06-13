package com.example.apidemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import com.example.apidemo.utils.NLog;

/**
 * <br> 外部拦截
 * Created by jinhui.shao on 2017/8/22.
 */
public class MyHorizontalScrollView extends HorizontalScrollView{
    private int touchSlop = 0 ;
    private VelocityTracker mVelocityTracker;
    private float mLastTouchX, mLastTouchY;
    private float mLastInterceptX, mLastInterceptY;
    private int mCurrentIndex;
    private int mScreenWidth;
    private ValueAnimator valueAnimator;

    public MyHorizontalScrollView(Context context) {
        super(context);
        initView(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm2 = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm2);
        mScreenWidth = dm2.widthPixels;
        // ScrollView和HorizontalScrollView中的scrollTo(x, y)的x、y限制最少值为0！！因此要加入左右padding
        setPadding(dm2.widthPixels, getPaddingTop(), dm2.widthPixels, getPaddingBottom());  // 此时起始的scrollX就是1440了。

        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        NLog.i("sjh1", "touchSlop = " + touchSlop);    // 32
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // super.onInterceptTouchEvent(ev);
        int action = ev.getAction();
        boolean intercept = false;
        float detaX, detaY;
        float x = ev.getX();
        float y = ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN :
                if(valueAnimator != null && valueAnimator.isRunning()){
                    valueAnimator.cancel();     // 优化滑动体验，按着view暂停滑动.
                    intercept = true;           // 水平滑动到之中间状态因此务必拦截给该水平的view
                }
                break;
            case MotionEvent.ACTION_MOVE :
                detaX = x - mLastInterceptX;
                detaY = y - mLastInterceptY;
                if(Math.abs(detaX) < touchSlop){
                    detaX = 0;
                }
                if(Math.abs(detaY) < touchSlop){
                    detaY = 0;
                }
                if(Math.abs(detaX) > Math.abs(detaY)){
                    intercept = true;
                }
                else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                NLog.d("sjh1", "onInterceptTouchEvent ACTION_CANCEL");
            case MotionEvent.ACTION_UP :
                break;

        }
        // onInterceptTouchEvent只要拦截处理过就不会再跑
        mLastTouchX = x;    // 拦截事件后，初始化第一次mLastTouchX的值！！！！
        mLastTouchY = y;
        mLastInterceptX = x;
        mLastInterceptY = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float detaX, detaY;
        float x = ev.getX();
        float y = ev.getY();
        acquireVelocityTracker(ev);

        switch (action){
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                detaX = x - mLastTouchX;
                detaY = y - mLastTouchY;
                NLog.d("sjh1", "detaX = " + detaX);
                scrollBy(-(int)detaX, 0);
                break;
            case MotionEvent.ACTION_CANCEL: // 水平移动时按home键再手指离开屏幕，会收到cancel事件！！
                NLog.d("sjh1", "onTouchEvent ACTION_CANCEL");
            case MotionEvent.ACTION_UP :    // 归位
                mVelocityTracker.computeCurrentVelocity(1000);  // 设置units的值为1000，意思为一秒时间内运动了多少个像素
                float XVelocity = mVelocityTracker.getXVelocity();
                NLog.d("sjh1", "ACTION_UP " + getScrollX() + " XVelocity = " + XVelocity);
                if(Math.abs(XVelocity) > 100){
                    // 先向右移动一大截，再快速左滑，此时判断快滑方向是否和当前index平移的方向相反。若相反，则先按位置更新下位置。
                    // 若此时要推走大于半页，则推走该半页，若只要推走小于半页，则推走该半页同时移到下一页
                    if((getScrollX() - mScreenWidth * mCurrentIndex) * XVelocity > 0){
                        mCurrentIndex = (int)(getScrollX() + 0.5 * mScreenWidth) / mScreenWidth;
                    }
                    mCurrentIndex = XVelocity > 0 ? mCurrentIndex - 1: mCurrentIndex + 1;
                }
                else {
                    mCurrentIndex = (int)(getScrollX() + 0.5 * mScreenWidth) / mScreenWidth;
                }
                mCurrentIndex = Math.max(1, Math.min(mCurrentIndex, 3));
                startScrollBack();

                releaseVelocityTracker();
                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;
        return true;
    }

    private void startScrollBack(){
        valueAnimator = ValueAnimator.ofInt(getScrollX(), mCurrentIndex * mScreenWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((int)animation.getAnimatedValue(), 0);
            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
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