package com.example.apidemo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.Book;
import com.example.apidemo.IMyAidlInterface;
import com.example.apidemo.R;
import com.example.apidemo.service.TestService;
import com.example.apidemo.utils.NLog;

/**
 * <br> AIDL的客户端代码
 * Created by jinhui.shao on 2017/1/17.
 */
public class TestServiceActivity extends BaseActivity {
    private final String TAG = "TestServiceActivity";
    public final static String ISHEAD = "isHead";
    private boolean mIsBind = false;
    private TestService.MyBinder mConnectedBinder;
    private IMyAidlInterface mMyAidlInterface;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {   // onBind返回非null之后调用. Component是TestService, IBinder是TestService的binder.
            NLog.d("TestService", "onServiceConnected.  ComponentName = " + name.toString() + "IBinder = " + service.toString());
            /*  TAG: 返回本地binder */
            mConnectedBinder = (TestService.MyBinder)service;
            mConnectedBinder.binderLog();
            /*  TAG: 启动AIDL远程binder */
     //       mMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        /**
         * 正常unbind肯定是不会调用这的，只有在Service和Activity在非正常情况下失去连接，才会调用该方法。例如当服务崩溃 (crash)了或者被杀死 (kill)了。
         * 长时间黑屏可能会断开.
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            NLog.d("TestService", "onServiceDisconnected.  ComponentName = " + name.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        NLog.d("TestService", "TestServiceActivity. process id is" + Process.myPid() + "  thread id is " + Thread.currentThread().getId());

        ((Button)findViewById(R.id.button1)).setText("start service");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestServiceActivity.this, TestService.class);
                startService(intent);
            }
        });

        ((Button)findViewById(R.id.button2)).setText("stop service");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestServiceActivity.this, TestService.class);
                stopService(intent);
            }
        });

        ((Button)findViewById(R.id.button3)).setText("bind service");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestServiceActivity.this, TestService.class);
                // BIND_AUTO_CREATE表示在Activity和Service建立关联后自动创建Service，这会使得MyService中的onCreate()方法得到执行，但onStartCommand()方法不会执行。
                mIsBind = true;
                bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            }
        });

        ((Button)findViewById(R.id.button4)).setText("unbind service");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                unBindAllService();
            }
        });

        ((Button)findViewById(R.id.button5)).setText("start Head service ");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestServiceActivity.this, TestService.class);
                intent.putExtra(ISHEAD, true);
                startService(intent);
            }
        });

        ((TextView)findViewById(R.id.button6)).setText("excute aidl method ");
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                   NLog.d("TestService", TAG + " mMyAidlInterface = " + mMyAidlInterface);
                    if(mMyAidlInterface != null){
                       NLog.d("TestService", TAG + " aidl:" + mMyAidlInterface.plus(new Book(5), new Book(8)));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void unBindAllService(){
        if(mIsBind){
            mIsBind = false;
            unbindService(mServiceConnection);  // 不能多次unBind
        }
    }

    @Override
    protected void onDestroy() {
        NLog.d("TestService", TAG + " onDestroy");
        unBindAllService(); // 若activity绑定了service，它destory的时候要解绑service，否则报错ServiceConnectionLeaked。

        super.onDestroy();
    }



}