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
        // MobclickAgent.setSessionContinueMillis(2000);   // 默认30s
    }

    @Override
    public void onEvent(Context context, String eventName) {
        MobclickAgent.onEvent(context, eventName);
    }

    @Override
    public void onEvent(Context context, String eventName, String value) {
        MobclickAgent.onEvent(context, eventName, value);
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventName, map);
    }

    /**
     * @param context
     * @param eventName
     * @param map 可为null
     * @param value
     */
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

    /**
     * 捕获程序崩溃日志，并在程序下次启动时发送到服务器
     * @param isTrace
     */
    @Override
    public void initTraceException(boolean isTrace) {
        MobclickAgent.setCatchUncaughtExceptions(isTrace);
    }
}