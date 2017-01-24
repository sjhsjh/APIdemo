package com.example.apidemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import com.example.apidemo.R;

public class EventDispatchActivity extends Activity {
    private static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dispatch);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (DEBUG) Log.i("sjh1", "activity dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) Log.i("sjh1", "activity onTouchEvent. " + event.getAction());
        return super.onTouchEvent(event);
    }


}
