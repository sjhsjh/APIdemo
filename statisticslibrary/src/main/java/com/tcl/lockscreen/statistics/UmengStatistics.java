package com.tcl.lockscreen.statistics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * <br>
 * Created by jinhui.shao on 2016/12/26.
 */
public class UmengStatistics extends AbstractStatistics{
    @Override
    public void init(Context context) {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onEvent(Context context, String eventName) {
        MobclickAgent.onEvent(context, eventName);
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventName, map);
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map, int value) {
        MobclickAgent.onEventValue(context, eventName, map, value);
    }

    @Override
    public void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    @Override
    public void onPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    @Override
    public void onPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }
}