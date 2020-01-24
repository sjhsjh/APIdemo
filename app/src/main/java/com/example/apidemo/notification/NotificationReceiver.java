package com.example.apidemo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.example.apidemo.MainActivity;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.Constant;
import com.example.apidemo.utils.NLog;


public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_CLEAR = Constant.PKG + ".action_clear";
    public static final String ACTION_KIND_GENERAL = Constant.PKG + ".action_general";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();

            if (!TextUtils.isEmpty(action)) {
                NotificationControl.getInstance().cancelNotification();
                AndroidUtils.closeStatusBar(context);

                switch (action) {
                    case ACTION_KIND_GENERAL:
                        NLog.i("sjh3", "onReceive: KIND_GENERAL");
                        goMainActivity(context);
                        break;
                    case ACTION_CLEAR:
                        // 通知被划掉时回调
                        NLog.i("sjh3", "onReceive: notification clear");
                        break;
                }
            }
        }
    }

    public static void goMainActivity(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(NotificationControl.FROM_NOTIFICATION, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
