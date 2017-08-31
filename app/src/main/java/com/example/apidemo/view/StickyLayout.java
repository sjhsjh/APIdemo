package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * <br>
 * Created by jinhui.shao on 2017/8/28.
 */
public class StickyLayout extends RelativeLayout{
    private ListView mListView;
    private Button mHeadView;

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
        mHeadView = (Button)findViewById(R.id.head_view);   // 要clickable处理了down事件的才能拖动.
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int)ev.getRawX();
        int y = (int)ev.getRawY();
        boolean intercept = false;
        switch (action){
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                NLog.d("sjh3", "onInterceptTouchEvent ACTION_MOVE  y = " + y);
                if(mHeadView.getY() > -mHeadView.getHeight() && mHeadView.getY() <= 0
                        || (isListViewTop() && y - mLastInterceptY > 0)){   // mHeadView.getY()恒为0,因为它一直在父view的左上角。
                    mHeadView.setTranslationY(Math.min(0 , mHeadView.getTranslationY() + y - mLastInterceptY));
                    intercept = true;
                }
                else {
                    intercept = false;
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

        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int action = ev.getAction();
//        int x = (int)ev.getRawX();
//        int y = (int)ev.getRawY();
//        boolean intercept = false;
//              //  (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0)
//        switch (action){
//            case MotionEvent.ACTION_DOWN :
//                break;
//            case MotionEvent.ACTION_MOVE :
//                NLog.d("sjh3", "onInterceptTouchEvent ACTION_MOVE  y = " + y);
//
//                if(getY() > -mHeadView.getHeight() && getY() <= 0
//                        || (isListViewTop() && y - mLastInterceptY > 0)){   // mHeadView.getY()恒为0,因为它一直在父view的左上角。
//                    mHeadView.setTranslationY(Math.min(0 , mHeadView.getTranslationY() + y - mLastInterceptY));
//                    intercept = true;
//                }
//                else {
//                    intercept = false;
//                }
//
//                NLog.i("sjh3", "intercept = " + intercept);
//                break;
//            case MotionEvent.ACTION_CANCEL :
//            case MotionEvent.ACTION_UP :
//                break;
//
//        }
//        mLastTouchX = x;
//        mLastTouchY = y;
//        mLastInterceptX = x;
//        mLastInterceptY = y;
//        return super.onInterceptTouchEvent(ev);
//    }

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

    private int mLastTouchX;
    private int mLastTouchY;
    private int mLastInterceptX;
    private int mLastInterceptY;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        int x = (int)event.getRawX();
//        int y = (int)event.getRawY();
//        boolean intercept = false;
//
//        switch (action){
//            case MotionEvent.ACTION_DOWN :
//                break;
//            case MotionEvent.ACTION_MOVE :
//
//
//                NLog.v("sjh3", "onTouchEvent ACTION_MOVE  y = " + y);
//                break;
//            case MotionEvent.ACTION_CANCEL :
//            case MotionEvent.ACTION_UP :
//                break;
//
//        }
//        mLastTouchX = x;
//        mLastTouchY = y;
//        return super.onTouchEvent(event);
//    }


}
