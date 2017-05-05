package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.example.apidemo.utils.NLog;

/**
 *  2016/12/21.
 */
public class FatherView extends LinearLayout{
    private static final boolean DEBUG = true;

    public FatherView(Context context) {
        super(context);
    }

    public FatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "fatherView dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        NLog.i("sjh1", "fatherView onInterceptTouchEvent. " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "fatherView onTouchEvent. " + event.getAction() );
        new Exception("sjh2").printStackTrace();
        return super.onTouchEvent(event);
    }



}