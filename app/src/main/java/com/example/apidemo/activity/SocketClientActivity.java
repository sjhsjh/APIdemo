package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.socket.SocketClient;
import com.example.apidemo.utils.Constant;
import com.example.apidemo.utils.PreferencesManager;

public class SocketClientActivity extends BaseActivity {
    private static final boolean DEBUG = false;
    private SocketClient socketClient;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        String defaultIP = "192.168.0.102";      // 172.20.10.2

        String lastIp = PreferencesManager.getInstance(SocketClientActivity.this)
                .getString(Constant.SERVER_IP, "");
        if (!lastIp.isEmpty()) {
            defaultIP = lastIp;
        }

        ((EditText) findViewById(R.id.edittext)).setText(defaultIP);

        final TextView textView = (TextView) findViewById(R.id.textView1);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null){
                    textView.setText(msg.obj.toString());
                }

            }
        };

        ((Button) findViewById(R.id.button1)).setText("tryConnectServer");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ((EditText) findViewById(R.id.edittext)).getText().toString();
                PreferencesManager.getInstance(SocketClientActivity.this)
                        .putString(Constant.SERVER_IP, ip);

                if (!TextUtils.isEmpty(ip)) {
                    socketClient = new SocketClient(getApplicationContext(), ip, 8888); // 服务端的IP地址和端口号
                    socketClient.setHandler(mHandler);
                    socketClient.tryConnectServer();
                } else {
                    Toast.makeText(SocketClientActivity.this, "Input empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final EditText editText2 = (EditText) findViewById(R.id.edittext2);
        editText2.setHint("请输入要发送的数据");
        ((Button) findViewById(R.id.button2)).setText("sendRequest");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (socketClient != null) {
                    socketClient.sendRequest(editText2.getText().toString());
                }
            }
        });

        ((Button) findViewById(R.id.button3)).setText("closeSocket");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (socketClient != null) {
                    socketClient.closeSocket();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketClient != null) {
            socketClient.destory();
        }
    }


}
