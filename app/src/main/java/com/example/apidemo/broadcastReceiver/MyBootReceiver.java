package com.example.apidemo.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.apidemo.activity.TestServiceActivity;
import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2020/3/9.
 */
public class MyBootReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NLog.w(TAG, "MyBootReceiver onReceive = " + intent);

        if (intent != null) {
            String action = intent.getAction();
            NLog.i(TAG, "MyBootReceiver onReceive = " + action);
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {           // 进程启动后约3s收到开机广播
                // context.startService(new Intent(context, TestService.class));
                Intent intent2 = new Intent(context, TestServiceActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {     // 界面解锁
                // 进程尚在，检查服务是否仍活动？
            }
        }
    }
}
