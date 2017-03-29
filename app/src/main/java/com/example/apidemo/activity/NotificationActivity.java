package com.example.apidemo.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;

public class NotificationActivity extends BaseActivity {
    private static final boolean DEBUG = false;
    private int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent fullScreenintent = new Intent(this, ResolveInfoActivity.class);
        final PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenintent, PendingIntent.FLAG_UPDATE_CURRENT);

        ((Button)findViewById(R.id.button1)).setText("send notification");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Notification.Builder builder = new Notification.Builder(NotificationActivity.this)
                .setContentTitle("title " + ++number)
                .setContentText("text " + number)
                .setSmallIcon(R.drawable.ic_launcher) //tmd,不设置图标还发不了通知！！！这3个属性是必须设置的！
//                .setTicker("ticker")   // 通知到来。通知首次出现在通知栏，带上升动画效果的
                .setAutoCancel(true)    // 点击后是否自动消失
                // 5.0之前设置了FullScreenIntent，通知会在显示的时候自动弹出那个可跳转的activity！！
                // 5.0之后则使用新增的悬挂式Notification! 此时该fullScreenPendingIntent无用了，setFullScreenIntent仅声明使用悬挂通知。
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setContentIntent(pendingIntent);

                manager.notify(1000 + number, builder.build());    // id相同的通知会被后来的通知覆盖；不同id的通知会显示多条；
                // 展开时的视图:notification.bigContentView(RemoteViews).
                // 指定视图Notification 正常状态下的视图:notification.contentView(RemoteViews).
            }
        });

        ((Button)findViewById(R.id.button2)).setText("remove newest notification");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(number > 0){
                    manager.cancel(1000 + number--);
                    // manager.cancelAll();
                }
            }
        });
    }


}
