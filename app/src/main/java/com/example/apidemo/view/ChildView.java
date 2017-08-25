package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.utils.NLog;

/**
 * 2016/12/21.
 */
public class ChildView extends LinearLayout {

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
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "childview dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        NLog.i("sjh1", "childview onInterceptTouchEvent. " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "childview onTouchEvent. " + event.getAction());
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            // requestDisallowInterceptTouchEvent(true);
            return true;
        }
        return super.onTouchEvent(event);
    }


}