package com.tcl.lockscreen.statistics;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by jinhui.shao on 2016/12/26.
 */

public interface IStatistics {

    void init(Context context);
    void onEvent(Context context, String eventName);
    void onEvent(Context context, String eventName, String value);
    void onEvent(Context context, String eventName, HashMap<String, String> map);   // 统计发生次数
    void onEvent(Context context, String eventName, HashMap<String, String> map, int value); // 计算事件
    void onResume(Context context);
    void onPause(Context context);
    void onPageStart(String pageName);
    void onPageEnd(String pageName);
    void initTraceException(boolean isTrace);
}
