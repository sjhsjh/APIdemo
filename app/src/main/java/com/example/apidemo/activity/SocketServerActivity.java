package com.example.apidemo.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.apidemo.BaseActivity;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;
import com.example.apidemo.socket.SocketClient;
import com.example.apidemo.socket.SocketServer;
import com.example.apidemo.utils.AndroidUtils;


public class SocketServerActivity extends BaseActivity {
    private static final boolean DEBUG = false;
    private Handler mHandler;
    private SocketServer server = new SocketServer(this, 8888);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);


        final TextView textView = (TextView) findViewById(R.id.textView1);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj != null) {
                    textView.setText(msg.obj.toString());
                }

            }
        };
        server.setHandler(mHandler);


        ((Button) findViewById(R.id.button1)).setText("beginListen");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // socket服务端开始监听
                server.beginListen();
            }
        });

        final EditText editText1 = (EditText) findViewById(R.id.edittext);
        editText1.setHint("请输入要返回的数据");
        ((Button) findViewById(R.id.button2)).setText("sendMessage");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // socket发送数据
                server.sendMessage(editText1.getText().toString());
            }
        });
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("本机服务端ip = " + AndroidUtils.getLocalIP(this));

        ((Button) findViewById(R.id.button3)).setText("closeSocket");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                server.closeSocket();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.destory();
    }

}
