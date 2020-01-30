package com.example.apidemo.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.apidemo.activity.AutoClickActivity;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.NLog;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "action_alarm";
    private AlarmManager alarmManager;
    private boolean isRepeat;
    private long intervalMs;
    private AutoClickActivity.TimeUpCallback timeUpCallback;
    private PendingIntent pendingIntent;

    /**
     * @param timeUpCallback 尽量不要引用Activity等对象
     */
    public AlarmBroadcastReceiver(AlarmManager alarmManager, boolean isRepeat, long intervalMs, AutoClickActivity.TimeUpCallback timeUpCallback, PendingIntent pendingIntent) {
        this.isRepeat = isRepeat;
        this.intervalMs = intervalMs;
        this.timeUpCallback = timeUpCallback;
        this.alarmManager = alarmManager;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NLog.w("sjh5", "--AlarmBroadcastReceiver onReceive----");
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_ALARM.equals(action)) {
                if (timeUpCallback != null) {
                    timeUpCallback.timeUp();
                }
                // 重复定时任务
                if (isRepeat) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMs, pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMs, pendingIntent);
                    }
                } else {
                    AndroidUtils.destoryBroadcast(context);
                }
            }

        }
    }
}