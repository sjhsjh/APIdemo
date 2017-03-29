package com.tcl.lockscreen.statistics;

import android.content.Context;

import java.util.HashMap;

/**
 * @author htoall
 * @Description:
 * @date 2016/12/28 下午4:28
 * @copyright TCL-MIE
 */
class AbstractStatistics implements IStatistics{

    @Override
    public void init(Context context) {

    }

    @Override
    public void onEvent(Context context, String eventName) {

    }

    @Override
    public void onEvent(Context context, String eventName, String value) {

    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map) {

    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map, int value) {

    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onPageStart(String pageName) {

    }

    @Override
    public void onPageEnd(String pageName) {

    }

    @Override
    public void initTraceException(boolean isTrace) {

    }
}
