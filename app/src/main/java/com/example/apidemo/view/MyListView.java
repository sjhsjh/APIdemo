package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import com.example.apidemo.utils.NLog;

/**
 * <br> 测试用。目前等同于ListView
 * Created by jinhui.shao on 2017/9/15.
 */
public class MyListView extends ListView{
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        NLog.d("sjh0", "extent = " + computeVerticalScrollExtent() + " offset = " + computeVerticalScrollOffset()
//                + " range = " + computeVerticalScrollRange()
//                + " deta = " + (computeVerticalScrollRange() - computeVerticalScrollExtent() - computeVerticalScrollOffset()));
        return super.onTouchEvent(ev);
    }

}