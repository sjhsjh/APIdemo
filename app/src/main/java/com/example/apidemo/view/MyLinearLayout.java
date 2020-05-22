package com.example.apidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 *
 * Created by Administrator on 2018/8/4 0004.
 */

public class MyLinearLayout extends LinearLayout  {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // setVisibility(GONE);
            // setVisibility(VISIBLE);
            getLayoutParams().width = 100;
            requestLayout();
            NLog.v("sjh5", " dispatchTouchEvent = ");

            // ((TextView) findViewById(R.id.tv_sjh)).setText("qqqqqqqqqqqqqqqqqqqqqqqqqqq");
            // ((TextView) findViewById(R.id.tv_sjh)).setText("qqqqqqqqqqqqqqqqqqqqqqqqqqq");
            // setVisibility(1);
            layout(2,2,2,2);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        NLog.w("sjh5", "onMeasure widthMeasureSpec = " + widthMeasureSpec
                + "  heightMeasureSpec = " + heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        NLog.w("sjh5", "onLayout left = " + left
                + "  right = " + right);
        // requestLayout();
        // invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        NLog.i("sjh5", "onDraw");
        super.onDraw(canvas);
    }
}
