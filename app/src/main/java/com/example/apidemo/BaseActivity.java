package com.example.apidemo;

import android.app.Activity;
import android.os.Bundle;
import com.tcl.lockscreen.statistics.StatisticsWrapper;

/**
 * <br>
 * Created  on 2017/3/29.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsWrapper.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatisticsWrapper.getInstance().onPause(this);
    }

}