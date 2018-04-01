package com.example.apidemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.service.MessengerService;
import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2018/4/1 .
 * 使用Messenger的客户端
 */
public class MessengerActivity extends BaseActivity {
    private final String TAG = "MessengerActivity";
    private boolean mIsBind = false;
    private Messenger messenger;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NLog.d(TAG, "onServiceConnected.  ComponentName = " + name.toString() + "IBinder = " + service.toString());
            messenger = new Messenger(service);
            Message message = Message.obtain(null, MessengerService.CLIENT_WHAT);
            Bundle bundle = new Bundle();
            bundle.putString(MessengerService.CLIENT_TO_SERVER, "client send to server success. ");
            message.setData(bundle);

            message.replyTo = clientMessenger;  // 传一个clientMessenger给服务端!!
            try {
                // 可以理解成，因为由IBinder得到messenger，messenger内获取到IMessenger，服务端中含有相同IMessenger的messenger即可收到消息。
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // 用于服务端向客户端回发信息
    private Handler clientHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            NLog.d("sjh8", "clientHandler:" + msg.getData().getString(MessengerService.SERVER_TO_CLIENT));
        }
    };
    private Messenger clientMessenger = new Messenger(clientHandler);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        ((Button)findViewById(R.id.button1)).setText("messenger bind service");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIsBind = true;
                Intent intent = new Intent(MessengerActivity.this, MessengerService.class);
                bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            }
        });

        ((Button)findViewById(R.id.button2)).setText("unbind service");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                unBindAllService();
            }
        });


    }
    private void unBindAllService(){
        if(mIsBind){
            mIsBind = false;
            unbindService(mServiceConnection);  // 不能多次unBind
        }
    }

}
