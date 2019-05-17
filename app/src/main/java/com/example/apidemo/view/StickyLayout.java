package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * <br> ListView悬浮导航栏
 * Created by jinhui.shao on 2017/8/28.
 */
public class StickyLayout extends LinearLayout{
    private ListView mListView;
    private LinearLayout mTopView;
    private Scroller mScroller;
    private int mHeadViewHeight;
    private int mLastTouchX;
    private int mLastTouchY;
    private int mLastInterceptX;
    private int mLastInterceptY;
    private int mTouchSlop;
    private int mMaxVelocity;
    private int mMinVelocity;
    private VelocityTracker mVelocityTracker;

    public StickyLayout(Context context) {
        super(context);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView(){
        mListView = (ListView) findViewById(R.id.listview_slide);
        mTopView = (LinearLayout) findViewById(R.id.top_view);
        // mTopView.setClickable(true);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();  // 32
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();     // 32000
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();     // 200
        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = mListView.getLayoutParams();
        mHeadViewHeight = mTopView.getHeight() / 2;
        lp.height = getMeasuredHeight() - mHeadViewHeight;   // 不需要requestLayout，因为后面的onLayout会布局,此处非常重要。 2272 - 400    只看到17?????
        // lp.topMargin = xx;
        NLog.i("sjh3", "getMeasuredHeight() " + getMeasuredHeight() + " mHeadViewHeight = " + mHeadViewHeight + " lp.height = " + lp.height
         + "  mListView.getMeasuredHeight() = " + mListView.getMeasuredHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int)ev.getX();
        int y = (int)ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN :
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE :
                NLog.d("sjh3", "dispatchTouchEvent ACTION_MOVE  y = " + y);
                // 若只平移父view，注意mHeadView它一直在父view的左上角，因此此时mHeadView.getY()恒为0。
                // 若listview和headview单独平移，listiew的平移会有延迟导致两者分离。分离了就不能判定listview已到顶部了。
                if (y - mLastTouchY > 0 && isListViewTop()) {   // 临界点下拉时允许父控件拦截！！！！！！核心②
                    requestDisallowInterceptTouchEvent(false);  // 方法一

//                    ev.setAction(MotionEvent.ACTION_DOWN);  // 方法二。再加个标志位，每次下拉到临界点只执行一次。
//                    return dispatchTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int)ev.getX(); // 只是用于detax的话可用getRawX
        int y = (int)ev.getY();
        boolean intercept = false;

        switch (action){
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                NLog.d("sjh3", "onInterceptTouchEvent ACTION_MOVE  y = " + y + " mLastInterceptY = " + mLastInterceptY);
                // if(Math.abs(y - mLastInterceptY) > mTouchSlop){     // 微小滑动不拦截，子ListView的onTouchEvent会返回true处理掉。
                /* 对ListView，当Math.abs(downnY - mMotionY) > mTouchSlop时，即累计滑动了超过mTouchSlop距离就开始滑动！
                   如果微小的deta距离都抛弃，父view不拦截，listview达到累计距离就会错误地滑动起来了。
                   因此不能使用上述每次滑动判断是否超过mTouchSlop的判断。 可以使用累加滑动是否超过mTouchSlop的判断。*/

                if(Math.abs(x - mLastTouchX) > mTouchSlop || Math.abs(y - mLastTouchY) > mTouchSlop) {
                    // 不要拦截水平方向的事件
                    if (Math.abs(y - mLastTouchY) > Math.abs(x - mLastTouchX)) {
                        // 父控件拦截条件：
                        // 1. headView可见
                        // 2. headView已隐藏且ListView处于顶部且下拉
                        if (getScrollY() < mHeadViewHeight){
                            intercept = true;

                        }
                        if(y - mLastInterceptY > 0 && isListViewTop()){
                            intercept = true;
                            mLastTouchX = x;    // down的时候初始化这些值; 下滑到临界点的时候无down事件，父view直接将move事件拦截给自己，因此move的时候也初始化onTouchEvent的x、y值;
                            mLastTouchY = y;
                        }
                        NLog.i("sjh3", "intercept = " + intercept);

                    }
                }

                // }
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                break;

        }

        mLastInterceptX = x;
        mLastInterceptY = y;
        if(intercept){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        initVelocityTracker(event);
        boolean result = false;

        switch (action){
            case MotionEvent.ACTION_DOWN :
                // 使用mTopView.setClickable(true);让子view处理了down事件 or 此处返回true由父view处理了事件 才能从topView拖动。 否则后续的move事件都来不到这层
                return true;
            case MotionEvent.ACTION_MOVE :
                // scrollBy(0, -(y - mLastTouchY));
                int newY = -(y - mLastTouchY) + getScrollY();
                newY = Math.max(0, newY);   // headView完全可见时不允许再下拉,限定移动范围0~mHeadViewHeight
                newY = Math.min(newY, mHeadViewHeight);
                scrollTo(0, newY);
                // 上滑到临界点时瞬间把父view中处理的事件交给了子view！！！核心①！
                if (getScrollY() >= mHeadViewHeight) {
                    event.setAction(MotionEvent.ACTION_DOWN);
                    result = dispatchTouchEvent(event);
                }

                NLog.v("sjh3", "onTouchEvent ACTION_MOVE  y = " + y);
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int yVelocity = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > mMinVelocity) {
                    fling(yVelocity);
                }
                releaseVelocityTracker();
                break;

        }
        mLastTouchX = x;
        mLastTouchY = y;
        return result || super.onTouchEvent(event);
    }

    /**
     * 需重载view的computeScroll来使用（draw中调用computeScroll）
     * @param yVelocity
     */
    private void fling(int yVelocity) {
        // 范围是(startY + minY, startY + maxY)！！以startX，startY为参考点。绘制开始时先瞬间回到startX，startY。
        mScroller.fling(0, getScrollY(), 0, -yVelocity, 0, 0, 0, mHeadViewHeight);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {  // 返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }


    private void initVelocityTracker(MotionEvent event){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker(){
        if(mVelocityTracker != null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private boolean isListViewTop(){
        boolean result = false;
        if(mListView.getFirstVisiblePosition() == 0){
            View view = mListView.getChildAt(0);
            if(view != null && view.getTop() >= 0){ // 证明该item跟listview顶部平齐.
                result = true;
            }
        }
        return result;
    }

}
