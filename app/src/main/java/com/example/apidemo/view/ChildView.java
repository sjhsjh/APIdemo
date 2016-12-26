package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 2016/12/21.
 */
public class ChildView extends LinearLayout{
    private static final boolean DEBUG = true;

    public ChildView(Context context) {
        super(context);
    }

    public ChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (DEBUG) Log.i("sjh1", "childview dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (DEBUG) Log.i("sjh1", "childview onInterceptTouchEvent. " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) Log.i("sjh1", "childview onTouchEvent. " + event.getAction());
        return super.onTouchEvent(event);
    }


}