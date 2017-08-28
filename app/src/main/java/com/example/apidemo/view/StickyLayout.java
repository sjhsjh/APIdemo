package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.apidemo.R;

/**
 * <br>
 * Created by jinhui.shao on 2017/8/28.
 */
public class StickyLayout extends LinearLayout{
    private ListView mListView;

    public StickyLayout(Context context) {
        super(context);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        mListView = (ListView) findViewById(R.id.listview_slide);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    private int mLastTouchX;
    private int mLastTouchY;
    private int mLastInterceptX;
    private int mLstInterceptY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN :
                break;
            case MotionEvent.ACTION_MOVE :
                break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP :
                break;

        }
        return super.onTouchEvent(event);
    }


}
