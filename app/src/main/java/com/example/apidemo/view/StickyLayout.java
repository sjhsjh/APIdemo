package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * <br>
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
        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = mListView.getLayoutParams();
        mHeadViewHeight = mTopView.getHeight() / 2;
        lp.height = getMeasuredHeight() - mHeadViewHeight;   // 不需要requestLayout，因为后面的onLayout会布局。 1984 - 400
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int)ev.getRawX();
        int y = (int)ev.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN :

                break;
            case MotionEvent.ACTION_MOVE :
                NLog.d("sjh3", "dispatchTouchEvent ACTION_MOVE  y = " + y);
                // 若只平移父view，注意mHeadView它一直在父view的左上角，因此此时mHeadView.getY()恒为0。
                // 若listview和headview单独平移，listiew的平移会有延迟导致两者分离。分离了就不能判定listview已到顶部了。
                if (y - mLastInterceptY > 0 && isListViewTop()) {   // 临界点下拉时允许父控件拦截！！！！！！核心②
                    // requestDisallowInterceptTouchEvent(false);

                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    dispatchTouchEvent(ev);
                    NLog.e("sjh3", "dispatchTouchEvent ACTION_CANCEL===============  y = " + y);
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev);
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
        int x = (int)ev.getRawX();
        int y = (int)ev.getY();
        boolean intercept = false;

        switch (action){
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                NLog.d("sjh3", "onInterceptTouchEvent ACTION_MOVE  y = " + y);
                // 父控件拦截条件：
                // 1. headView可见
                // 2. headView已隐藏且ListView处于顶部且下拉
                if (getScrollY() < mHeadViewHeight || y - mLastInterceptY > 0 && isListViewTop()) {
                    intercept = true;
                }

                NLog.i("sjh3", "intercept = " + intercept);
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                break;

        }
        mLastTouchX = x;
        mLastTouchY = y;
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
        int x = (int)event.getRawX();
        int y = (int)event.getY();
        boolean result = false;

        switch (action){
            case MotionEvent.ACTION_DOWN :
                // 使用mTopView.setClickable(true);让子view处理了down事件 or 此处返回true由父view处理了事件 才能从topView拖动。 否则后续的move事件都来不到这层
                return true;
            case MotionEvent.ACTION_MOVE :
                // scrollBy(0, -(y - mLastTouchY));
                scrollTo(0, Math.max(0, -(y - mLastTouchY) + getScrollY()));    // headView完全可见时不允许再下拉
                // 上滑到临界点时瞬间把父view中处理的事件交给了子view！！！核心①！
                if (getScrollY() >= mHeadViewHeight) {
                    event.setAction(MotionEvent.ACTION_DOWN);
                    result = dispatchTouchEvent(event);
                }

                NLog.v("sjh3", "onTouchEvent ACTION_MOVE  y = " + y + " getScrollY = " + getScrollY());
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                break;

        }
        mLastTouchX = x;
        mLastTouchY = y;
        return result || super.onTouchEvent(event);
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
