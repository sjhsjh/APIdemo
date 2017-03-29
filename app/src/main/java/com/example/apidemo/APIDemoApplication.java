package com.example.apidemo;

import android.app.Application;

import com.tcl.lockscreen.statistics.StatisticsKind;
import com.tcl.lockscreen.statistics.StatisticsWrapper;

/**
 * Created on 2017/3/29.
 */
public class APIDemoApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        initStaticstis();
        initTraceException();
    }

    private void initStaticstis() {
        StatisticsWrapper.getInstance().init(this);
        StatisticsWrapper.getInstance().addStatisitcs(StatisticsKind.STATISTICS_UMENG);
        // StatisticsWrapper.getInstance().onResume(null);
    }

    private void initTraceException(){
        StatisticsWrapper.getInstance().initTraceException(true);
    }

}