package com.example.apidemo;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @date 2020/1/9
 */
public class ViewUtils {

    /**
     * ScrollView内的child可竖直滑动的距离,最小值为0.
     * @param scrollView 可竖直滚动的view，如ScrollView和NestedScrollView
     */
    public static int getScrollRange(FrameLayout scrollView) {
        int scrollRange = 0;
        if (scrollView.getChildCount() > 0) {
            View child = scrollView.getChildAt(0);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            int childSize = child.getHeight() + lp.topMargin + lp.bottomMargin;
            int parentSpace = scrollView.getHeight() - scrollView.getPaddingTop() - scrollView.getPaddingBottom();
            scrollRange = Math.max(0, childSize - parentSpace);
        }
        return scrollRange;
    }

    public static int getScrollRange(LinearLayout scrollView) {
        int scrollRange = 0;
        if (scrollView.getChildCount() > 0) {
            View child = scrollView.getChildAt(0);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
            int childSize = child.getHeight() + lp.topMargin + lp.bottomMargin;
            int parentSpace = scrollView.getHeight() - scrollView.getPaddingTop() - scrollView.getPaddingBottom();
            scrollRange = Math.max(0, childSize - parentSpace);
        }
        return scrollRange;
    }
}
