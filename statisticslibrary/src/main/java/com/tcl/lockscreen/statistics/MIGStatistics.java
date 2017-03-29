package com.tcl.lockscreen.statistics;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.statisticsdk.agent.StatisticsAgent;
import com.tcl.statisticsdk.util.Unit;

import java.util.HashMap;

/**
 * <br>
 * Created by jinhui.shao on 2016/12/26.
 */
public class MIGStatistics extends AbstractStatistics{

    @Override
    public void init(Context context) {
        StatisticsAgent.init(context);
        StatisticsAgent.setAutoTraceActivity(false); // 此接口实现是记录各个页面的信息（比如activity ，fragment）
        StatisticsAgent.setWifiOnly(context, true);
        // StatisticsAgent.setPeriod(1, Unit.DAY);      // 4.设置上传频率，正式出货版本为一周上传一次
        StatisticsAgent.setDebugMode(true);         // TUDO
        StatisticsAgent.setSessionTimeOut(context, 2000);
        boolean isDiagnostic = Settings.Global.getInt(context.getContentResolver(), "def.diagnostic.on", -1) != -1 ? true : false;  // 5.根据Diagnostics总开关决定是否上传数据
        Log.e("sjh1", "MIGStatistics init. isDiagnostic = " + isDiagnostic);
        if (isDiagnostic) {  // Diagnostics开关存在
            StatisticsAgent.experienceImprove(context, true);   // 使SDK读取Diagnostics开关;根据Diagnostics开关动态决定是否上传
        } else {
            StatisticsAgent.enableReport(context, false);
        }
    }

    /**
     * 4.1.7	自定义事件Key命名规则
     Key Name : app _function _type(type sample:计数NUM，状态STA，时长DUR,其他OTH)
     */
    @Override
    public void onEvent(Context context, String eventName) {
        if(!TextUtils.isEmpty(eventName)){
            Log.i("sjh1", "eventName = " + eventName);
            StatisticsAgent.onEvent(context, eventName);
        }
    }

    @Override
    public void onEvent(Context context, String eventName, String value) {
        super.onEvent(context, eventName, value);
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map) {
        if(!TextUtils.isEmpty(eventName) && map != null && map.size() > 0){
            Log.i("sjh1", "eventName = " + eventName + "map = " + map.toString());
            StatisticsAgent.onEvent(context, eventName, map);
        }
    }

    @Override
    public void onEvent(Context context, String eventName, HashMap<String, String> map, int value) {
        if(!TextUtils.isEmpty(eventName) && map != null && map.size() > 0){
            StatisticsAgent.onEvent(context, eventName, map, value);
        }
    }

    @Override
    public void onResume(Context context) {
        Log.i("sjh1", "onResume " );
        StatisticsAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        Log.i("sjh1", "onPause " );
        StatisticsAgent.onPause(context);
    }


    @Override
    public void onPageStart(String pageName) {
        Log.i("sjh1", "onPageStart pageName = " + pageName );
        StatisticsAgent.onPageStart(pageName);
    }

    @Override
    public void onPageEnd(String pageName) {
        Log.i("sjh1", "onPageEnd pageName = " + pageName );
        StatisticsAgent.onPageEnd(pageName);
    }

}