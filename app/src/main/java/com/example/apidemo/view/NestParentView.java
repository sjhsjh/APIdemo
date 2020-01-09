package com.example.apidemo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/9
 */
public class NestParentView extends LinearLayout implements NestedScrollingParent2 {
    private NestedScrollingParentHelper mParentHelper;

    public NestParentView(Context context) {
        super(context);
        initView();
    }

    public NestParentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    private int mHeadHeight;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View firstChild = getChildAt(0);
        firstChild.post(new Runnable() {
            @Override
            public void run() {
                mHeadHeight = firstChild.getHeight();
            }
        });
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);    // 仅仅保存滚动方向
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);      // 仅仅清除滚动方向
    }

    /**
     * 使用scrollY的坐标系，即dy向上为正
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0 && getScrollY() < mHeadHeight                                 // 手指上滑. 0~mHeadHeight
                || dy < 0 && target.getScrollY() <= 0 && getScrollY() > 0) {     // 手指下滑 且 child到顶
            // todo boundaryd
            NLog.d("sjh7", "onNestedPreScroll====dy==" + dy);

            int newScrollY = Math.max(0, getScrollY() + dy);   // 防止view越界
            newScrollY = Math.min(getScrollY(), newScrollY);
            int detaY = newScrollY - getScrollY();
            scrollTo(0, newScrollY);                        // parent滑动
            consumed[1] = detaY;
        }
    }

    /**
     * 使用scrollY的坐标系，即dy向上为正
     * PS : dx(dy)Consumed、dx(dy)Unconsumed 4个都为0 时就不调用了
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type) {
        // todo boundary
        if (dyUnconsumed > 0 && getScrollY() < mHeadHeight * 3      // 手指上滑. 0~ 3 * mHeadHeight
                || dyUnconsumed < 0 && getScrollY() > 0) {          // 手指下滑
            NLog.d("sjh7", "onNestedScroll====dyUnconsumed==" + dyUnconsumed);

            int newScrollY = Math.max(0, getScrollY() + dyUnconsumed);  // 防止view越界
            scrollTo(0, newScrollY);                                 // parent滑动
        }
    }
}
