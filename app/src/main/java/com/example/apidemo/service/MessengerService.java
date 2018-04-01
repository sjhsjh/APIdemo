package com.example.apidemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2018/4/1 .
 * 使用Messenger的服务端
 */
public class MessengerService extends Service{
    public static final String CLIENT_TO_SERVER = "client_to_server";
    public static final String SERVER_TO_CLIENT = "server_to_client";
    public static final int CLIENT_WHAT = 1;
    public static final int SERVER_WHAT = 2;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            NLog.d("sjh8", "server handler: " + msg.getData().getString(CLIENT_TO_SERVER));

            // 向客户端回信息
            Messenger clientMessenger = msg.replyTo;
            Message message = Message.obtain(null, SERVER_WHAT);
            Bundle bundle = new Bundle();
            bundle.putString(MessengerService.SERVER_TO_CLIENT, "server receive. ");
            message.setData(bundle);
            try {
                clientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    };
    private Messenger messenger = new Messenger(mHandler);  // handler.getIMessenger()：handler里有IMessenger，IMessenger内又有Stub对象。

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }



}
