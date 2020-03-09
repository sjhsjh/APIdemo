package com.example.apidemo.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.apidemo.activity.TestServiceActivity;
import com.example.apidemo.service.TestService;
import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2020/3/9.
 */
public class MyBootReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {         // 进程启动后约3s收到广播
        NLog.w(TAG, "MyBootReceiver onReceive = " + intent);

        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                // context.startService(new Intent(context, TestService.class));

                Intent intent2 = new Intent(context, TestServiceActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            }
        }
    }
}
