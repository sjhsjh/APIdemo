package com.example.apidemo.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.example.apidemo.APIDemoApplication;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;

public class NotificationControl {
    private static final String CHANNEL_ID = "apidemo_channel_id";
    private static final int NOTIFICATION_ID = 10000;
    public static final String FROM_NOTIFICATION = "from_notification";
    private NotificationManager mNotificationManager;

    private static volatile NotificationControl instance;

    public static NotificationControl getInstance() {
        if (instance == null) {
            synchronized (NotificationControl.class) {
                if (instance == null) {
                    instance = new NotificationControl();
                }
            }
        }
        return instance;
    }

    public void show(RemoteViews remoteView) {
        Context context = APIDemoApplication.getContext();
        NotificationCompat.Builder builder = createBuilder(context, CHANNEL_ID);

        // 点击通知跳转
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(FROM_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 通知被消除时发广播
        Intent clearIntent = new Intent(context, NotificationReceiver.class);
        clearIntent.setAction(NotificationReceiver.ACTION_CLEAR);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0, clearIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setSmallIcon(R.drawable.earth)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deleteIntent);

        if (remoteView == null) {
            builder.setContentTitle("title " + System.currentTimeMillis())
                    .setContentText("text");
        } else {
            builder.setCustomContentView(remoteView);
        }
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setOnlyAlertOnce(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);


        mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());  // 相同NotificationID则覆盖内容。
    }


    public void cancelNotification() {
        if (mNotificationManager != null)
            mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public RemoteViews createRemoteView(int layoutId) {
        Context context = APIDemoApplication.getContext();
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), layoutId);
        // RemoteViews expandView = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        // remoteView.setOnClickPendingIntent(R.id.noti_general, createJumpIntentByBroadcast(NotificationReceiver.ACTION_KIND_GENERAL, context));

        return remoteView;
    }

    /**
     * PendingIntent使用广播的优点是可以执行跳转之外的代码。
     * tips: 点击notification时，如果它是发送广播的点击事件，通知栏就不会自动收起。我们需要通过代码手动的让通知栏收起。
     */
    private PendingIntent createJumpIntentByBroadcast(String action, Context context) {
        Intent clickIntent = new Intent(context, NotificationReceiver.class);
        clickIntent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    public static NotificationCompat.Builder createBuilder(Context context, String channelId) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT < 26) {
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            NotificationChannel channel = new NotificationChannel(channelId, "name_" + channelId, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, channelId);
        }
        builder.setOnlyAlertOnce(true);
        return builder;
    }
}
