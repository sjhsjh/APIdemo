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
        // new Exception("sjh2").printStackTrace();
        return super.onTouchEvent(event);
    }

    /**
     * 事件分发
     * @param event
     * @return  事件是否已被消费
     */
//    public boolean dispatchTouchEvent(MotionEvent event){
//        boolean consume;
//        if(isView || isViewGroup && onInterceptTouchEvent(event) || child == null){   // Activity和View都没有onInterceptTouchEvent.  没有子View不能继续派发因此只能当作拦截下来了.
//            consume = onTouchEvent(event);
//        }
//        else {
//            consume = child.dispatchTouchEvent(event);
//            if(consume){
//            }
//            else{
//                consume = onTouchEvent(event);
//            }
//        }
//
//        return consume;
//    }

}