package com.example.apidemo;

import android.app.Application;
import android.content.Context;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.example.apidemo.manager.ADManager;
import com.example.apidemo.utils.CrashHandler;
import com.example.apidemo.utils.NLog;
import com.squareup.leakcanary.LeakCanary;
import com.tcl.lockscreen.statistics.StatisticsKind;
import com.tcl.lockscreen.statistics.StatisticsWrapper;

/**
 * Created on 2017/3/29.
 */
public class APIDemoApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
//        ArcFaceWrapper.getInstance(this);
        super.onCreate();
        mContext = getApplicationContext();
        initStatistics();
        initTraceException();
        initCrashHandler();
        initNLog();
        DoraemonKit.install(this);
        ADManager.getInstance().initAD(mContext);
        // initLeakCanary();
    }

    public static Context getContext() {
        return mContext;
    }

    private void initStatistics() {
        StatisticsWrapper.getInstance().init(this);
        StatisticsWrapper.getInstance().addStatisitcs(StatisticsKind.STATISTICS_UMENG);
    }

    private void initTraceException() {
        StatisticsWrapper.getInstance().initTraceException(true);
    }

    private void initCrashHandler() {
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());
    }

    private void initNLog() {
        NLog.setLogDirPath(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/APIDemoLog");
        // android.os.Environment.getExternalStorageDirectory().getAbsolutePath()  getExternalFilesDir(null).getPath()
    }

    private void initLeakCanary() {
        NLog.i("sjh1", "initLeakCanary IS_DEBUG = " + BuildConfig.IS_DEBUG);
        if (BuildConfig.IS_DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
    }
}