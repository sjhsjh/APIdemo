package com.example.apidemo.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;
import com.example.apidemo.notification.NotificationControl;
import com.example.apidemo.utils.AndroidUtils;


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
                // NotificationCompat.Builder builder = NotificationControl.createBuilder(NotificationActivity.this, "channelId_x")

                .setContentTitle("title " + ++number)
                .setContentText("text " + number)
                .setSmallIcon(R.drawable.ic_launcher) //tmd,不设置图标还发不了通知！！！这3个属性是必须设置的！
//                .setTicker("ticker")   // 通知到来。通知首次出现在通知栏，带上升动画效果的
                .setAutoCancel(true)    // 点击后是否自动消失
                // 5.0之前设置了FullScreenIntent，通知会在显示的时候自动弹出那个可跳转的activity！！
                // 5.0之后则使用新增的悬挂式Notification! 此时该fullScreenPendingIntent无用了，setFullScreenIntent仅声明使用悬挂通知。xx
                // 用于立即执行的intent如来电。Notification.Builder不再发送通知；而NotificationCompat.Builder则既执行fullScreenIntent也发送通知
                // .setFullScreenIntent(fullScreenPendingIntent, true)
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

        final EditText editText2 = (EditText) findViewById(R.id.edittext2);
        ((Button)findViewById(R.id.button3)).setText("copy edittext text ");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.copyText(NotificationActivity.this, editText2.getText().toString());
            }
        });

        ((Button) findViewById(R.id.button4)).setText("open and close StatusBar ");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.openStatusBar(NotificationActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtils.closeStatusBar(NotificationActivity.this);
                    }
                }, 2000);
            }
        });
        ((Button) findViewById(R.id.button5)).setText("send specific notification");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NotificationControl.getInstance().show(null);
            }
        });
        ((Button) findViewById(R.id.button6)).setText("remove specific notification");
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NotificationControl.getInstance().cancelNotification();
            }
        });
    }



}
