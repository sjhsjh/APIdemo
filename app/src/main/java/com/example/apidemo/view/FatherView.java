package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
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

//        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                NLog.i("sjh1", "onTouch. " + event.getAction() );
//                return false;
//            }
//        });
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
//        if(ev.getAction() == MotionEvent.ACTION_MOVE){
//            return true;
//        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "fatherView onTouchEvent. " + event.getAction() );
        // new Exception("sjh3").printStackTrace();
        return super.onTouchEvent(event);
    }

    /**
     * 事件分发总结函数
     * 点击事件传递顺序：Activity -> Window -> View
     * View的dispatchTouchEvent中有mOnTouchListener.onTouch(this, event);
     * View的onTouchEvent中有performClick();
     * @param event
     * @return  事件是否已被消费
     */
//    public boolean dispatchTouchEvent(MotionEvent event){
//        boolean consume;
//        // Activity和View都没有onInterceptTouchEvent.  没有子View不能继续派发因此只能当作拦截下来了.
//        // 只有ViewGroup有onInterceptTouchEvent，只有ViewGroup没有onTouchEvent。
//        // ViewGroup的onInterceptTouchEvent默认返回false. Activity的onTouchEvent默认返回false.
//        if(isView || child == null || isViewGroup && onInterceptTouchEvent(event)){
//            if(mOnTouchListener != null && mOnTouchListener.onTouch(this, event)){
//                consume = true;
//            }
//            else{
//                consume = onTouchEvent(event);
//            }
//        }
//        else {
//            consume = child.dispatchTouchEvent(event);
//            if(consume){
//                return true;
//            }
//            else{
//                if(mOnTouchListener != null && mOnTouchListener.onTouch(this, event)){
//                    consume = true;
//                }
//                else{
//                    consume = onTouchEvent(event);
//                }
//            }
//        }
//
//        return consume;
//    }

}