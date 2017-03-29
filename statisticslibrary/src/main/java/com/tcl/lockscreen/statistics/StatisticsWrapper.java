package com.tcl.lockscreen.statistics;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.tcl.statisticsdk.agent.StatisticsAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <br> 供外部调用的统计类.
 * Created by jinhui.shao on 2016/12/26.
 */
public class StatisticsWrapper implements IStatistics{

    private static final int DEFAULT_STATISTICS_NUMBER = 3;
    private static final String STATISTICS_MODULE_NOT_INIT = "Statistics module not init";
    private List<IStatistics> mReportImpl = new ArrayList<>(DEFAULT_STATISTICS_NUMBER);
    private List<StatisticsKind> mStatisticsKind = new ArrayList<>(DEFAULT_STATISTICS_NUMBER);
    private IStatiticsSwitch mStatiticsSwitch = new DefaultStatisticsSwitch();
    private Context contextApp;
    private boolean mdiagnosticExistAndOpen;

    private static class StatisticsWrapperProduce {
        private static StatisticsWrapper instance = new StatisticsWrapper();
    }

    public static StatisticsWrapper getInstance(){
        return StatisticsWrapperProduce.instance;
    }

    /**
     * 外部接口，控制使用的统计类型.相应的统计对象的初始化。
     * @param statisticsKind
     */
    public void addStatisitcs(StatisticsKind statisticsKind) {
        if(!mStatisticsKind.contains(statisticsKind)) {
            mStatisticsKind.add(statisticsKind);
            IStatistics iStatistics = StatisticsFactory.produce(statisticsKind);    // new出统计对象
            if(iStatistics == null){
                return;
            }
            if (contextApp == null) {
                throw new NullPointerException(STATISTICS_MODULE_NOT_INIT);
            }
            iStatistics.init(contextApp);  // 统计初始化
            mReportImpl.add(iStatistics);
        }
    }

    /**
     * 是否打开统计的总开关，已默认打开。mIsDiagnostic 打开的时候才执行.
     * @param statiticsSwitch
     */
    public void setStatiticsSwitch(IStatiticsSwitch statiticsSwitch) {
        if(mdiagnosticExistAndOpen){
            mStatiticsSwitch = statiticsSwitch;
        }
    }

    @Override
    public void init(Context context) {
        contextApp = context.getApplicationContext();
        updateDiagnosticSwitch();
    }

    /**
     * 读取settings中最新的diagnostic值，从而判断是否继续运行统计代码.
     * 每次模拟启动之前（即上传之前）执行该方法，以防用户使用中途关闭了diagnostic开关.
     */
    public void updateDiagnosticSwitch(){
        final boolean diagnosticExistAndOpen =
            Settings.Global.getInt(contextApp.getContentResolver(), "def.diagnostic.on", -1) == 1 ? true : false;  // 根据Diagnostics总开关决定是否上传数据
        mdiagnosticExistAndOpen = diagnosticExistAndOpen;
        mStatiticsSwitch = new IStatiticsSwitch(){

            @Override
            public boolean isOpenStatistics() {
                return diagnosticExistAndOpen;    // Diagnostics开关存在
            }
        };
    }

    @Override
    public void onEvent(Context context, String eventName) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onEvent(context, eventName);
            }
        }
    }

    @Override
    public void onEvent(Context context, String eventName, String value) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onEvent(context, eventName, value);
            }
        }
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onEvent(context, eventName, map);
            }
        }
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map, int value) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onEvent(context, eventName, map, value);
            }
        }
    }

    @Override
    public void onResume(Context context) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onResume(context);
            }
        }
    }

    @Override
    public void onPause(Context context) {
        if(context == null) {
            context = contextApp;
        }
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onPause(context);
            }
        }
    }

    @Override
    public void onPageStart(String pageName) {
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onPageStart(pageName);
            }
        }
    }

    @Override
    public void onPageEnd(String pageName) {
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).onPageEnd(pageName);
            }
        }
    }

    @Override
    public void initTraceException(boolean isTrace) {
        if(mStatiticsSwitch.isOpenStatistics()) {
            for (int i = 0; i < mReportImpl.size(); i++) {
                mReportImpl.get(i).initTraceException(isTrace);
            }
        }
    }

}