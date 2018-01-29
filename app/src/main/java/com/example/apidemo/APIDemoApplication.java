package com.example.apidemo;

import android.app.Application;
import android.content.Context;
import com.example.apidemo.utils.CrashHandler;
import com.tcl.lockscreen.statistics.StatisticsKind;
import com.tcl.lockscreen.statistics.StatisticsWrapper;

/**
 * Created on 2017/3/29.
 */
public class APIDemoApplication extends Application{
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initStatistics();
        initTraceException();
        initCrashHandler();
    }

    public static Context getContext(){
        return mContext;
    }

    private void initStatistics() {
        StatisticsWrapper.getInstance().init(this);
        StatisticsWrapper.getInstance().addStatisitcs(StatisticsKind.STATISTICS_UMENG);
    }

    private void initTraceException(){
        StatisticsWrapper.getInstance().initTraceException(true);
    }

    private void initCrashHandler(){
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());

    }

}