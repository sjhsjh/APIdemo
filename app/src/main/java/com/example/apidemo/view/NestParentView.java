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
import android.widget.OverScroller;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/9
 */
public class NestParentView extends LinearLayout implements NestedScrollingParent2 {
    private NestedScrollingParentHelper mParentHelper;
    private OverScroller mScroller;

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
        mScroller = new OverScroller(getContext());
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
        // return false;
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
     * 使用scrollY 的坐标系，即dy向上为正
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        // 手指上滑. 0~mHeadHeight
        if (dy > 0 && getScrollY() < mHeadHeight) {
            NLog.d("sjh7", "onNestedPreScroll====dy==" + dy);
            int newScrollY = getScrollY() + dy;
            if (newScrollY < 0) {       // 防止view越界
                newScrollY = 0;
            }
            if (newScrollY > mHeadHeight) {
                newScrollY = mHeadHeight;
            }
            int detaY = newScrollY - getScrollY();
            scrollTo(0, newScrollY);             // parent滑动
            consumed[1] = detaY;
        }

        // 手指下滑 且 child到顶
        if (dy < 0 && target.getScrollY() <= 0 && getScrollY() > 0) {
            NLog.d("sjh7", "onNestedPreScroll====dy==" + dy);
            int newScrollY = getScrollY() + dy;
            if (newScrollY < 0) {       // 防止view越界
                newScrollY = 0;
            }
            int detaY = newScrollY - getScrollY();
            scrollTo(0, newScrollY);             // parent滑动
            consumed[1] = detaY;
        }
    }

    /**
     * 使用scrollY 的坐标系，即dy向上为正
     * PS : dx(dy)Consumed、dx(dy)Unconsumed 4个都为0 时就不调用了
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type) {
        if (dyUnconsumed > 0 && getScrollY() < mHeadHeight * 1.5    // 手指上滑. 0~ 1.5 * mHeadHeight
                || dyUnconsumed < 0 && getScrollY() > 0) {          // 手指下滑
            NLog.i("sjh7", "onNestedScroll====dyUnconsumed==" + dyUnconsumed);

            int newScrollY = Math.max(0, getScrollY() + dyUnconsumed);  // 防止view越界
            scrollTo(0, newScrollY);                                 // parent滑动
        }
    }

    // 自行重写两个fling方法，否则使用父类的fling方法（sdk这套嵌套滑动默认不开启）
    /**
     * @param velocityY 方向由dispatchNestedPreFling的传参决定，此处 向下为正
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        // // 手指上滑. 0~mHeadHeight
        // if (velocityY < 0 && getScrollY() < mHeadHeight) {
        //     NLog.d("sjh7", "onNestedPreFling====getScrollY()==" + getScrollY());
        //     fling((int) velocityY);
        //     return true;
        // }
        // // 手指下滑 且 child到顶
        // if (velocityY > 0 && target.getScrollY() <= 0 && getScrollY() > 0) {
        //     NLog.i("sjh7", "onNestedPreFling====getScrollY()==" + getScrollY());
        //     fling((int) velocityY);
        //     return true;
        // }
        return super.onNestedPreFling(target, velocityX, velocityY);    // false
    }

    /**
     * 嵌套滑动fling需要fling两段距离，从scroller无法获取在临界位置的速度，因此fling无法传递。xxx   // todo
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        // if (!consumed) {
        //     flingWithNestedDispatch((int) velocityY);
        //     return true;
        // }    // todo
        return super.onNestedFling(target, velocityX, velocityY, consumed); // false
    }

    /**
     * 需重载view的computeScroll来使用（draw中调用computeScroll）
     * @param yVelocity 向下为正
     */
    private void fling(int yVelocity) {
        // 范围是(minY, maxY),并不是(startY + minY, startY + maxY)！！以startX，startY为参考点。绘制开始时先瞬间回到startX，startY。
        mScroller.fling(0, getScrollY(), 0, -yVelocity, 0, 0, 0, mHeadHeight);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {  // 返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
