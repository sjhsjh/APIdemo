package com.example.apidemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

public class EventDispatchActivity extends BaseActivity {
    private ScrollView   mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dispatch);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);



    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        NLog.i("sjh1", "activity dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        new Exception("sjh2").printStackTrace();
        NLog.i("sjh1", "activity onTouchEvent. " + event.getAction());
        return super.onTouchEvent(event);
    }


}
