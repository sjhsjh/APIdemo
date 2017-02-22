package com.example.apidemo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.apidemo.Book;
import com.example.apidemo.IMyAidlInterface;
import com.example.apidemo.R;
import com.example.apidemo.activity.TestServiceActivity;
import com.example.apidemo.utils.AndroidUtils;

/**
 * <br> AIDL的服务端代码
 * Created by jinhui.shao on 2017/1/17.
 */
public class TestService extends Service{
    private final String TAG = this.getClass().getSimpleName();
    private MyBinder mMyBinder = new MyBinder();
    private Handler mHandler;
    private Thread mThread;
    private Runnable mRunnable = new Runnable() {   // 开线程定时循环工作！
        @Override
        public void run() {
            Log.d(TAG, "mRunnable time = " + AndroidUtils.debugLog(System.currentTimeMillis()));
            if(Looper.myLooper() == null){
                Looper.prepare();   // 循环的时候避免重复prepare
            }
            mHandler = new Handler();
            mHandler.postDelayed(this, 2000);
            Looper.loop();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TestService. process id is" + Process.myPid() + "  thread id is " + Thread.currentThread().getId());
        // 只在service创建的时候调用一次，可以在此进行一些一次性的初始化操作
    }


    /**
     * 每次其他组件调用startService()方法时，此方法将会被调用.在这里进行这个service主要的操作
     * @param intent
     * @param flags 默认情况下是0，对应的常量名为START_STICKY_COMPATIBILITY
     * @param startId 在service未destory前提下，多次startService()呈现0,1,2....递增
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand. flags = " + flags + " startId = " + startId);

        boolean isHead = intent.getBooleanExtra(TestServiceActivity.ISHEAD, false);
        if(isHead){
            Intent notificationIntent = new Intent(this, TestServiceActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("title")
                    .setContentText("text")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            startForeground(1, notification);
        }

//        new Thread(){   // 开线程定时循环工作！
//            @Override
//            public void run() {
//                super.run();
//                Log.d(TAG, "onStartCommand time = " + AndroidUtils.debugLog(System.currentTimeMillis()));
//                if(Looper.myLooper() == null){
//                    Looper.prepare();   // 循环的时候避免重复prepare
//                }
//                mHandler = new Handler();
//                mHandler.postDelayed(this, 2000);
//                Looper.loop();
//            }
//        }.start();
        if(mThread == null || !mThread.isAlive()){  // 防止开启多个线程.
            mThread = new Thread(mRunnable);
            mThread.start();
        }
        // new Thread(mRunnable).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // 当其他组件调用bindService()方法时，此方法将会被调用.返回一个IBinder对象，它是用来支撑其他组件与service之间的通信。如果不想让这个service被绑定，在此返回null即可

        /*  TAG: 返回本地binder */
        // return mMyBinder;
        /*  TAG: 启动AIDL远程binder ，返回AIDL文件的内部类Binder对象！ */
        return mAIDLBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {    // 很少重载该方法
        Log.d(TAG, "onUnbind ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy. 服务停止！");
        // service调用的最后一个方法.在此进行资源的回收
        if(mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }

    /*  TAG: 本地binder */
    public class MyBinder extends Binder{
        public void binderLog(){
            Log.d(TAG, "binderLog");
        }
    }

    /*  TAG: AIDL远程binder ，AIDL文件的内部类Binder对象！ */
    private AIDLBinder mAIDLBinder = new AIDLBinder();
    public class AIDLBinder extends IMyAidlInterface.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public int plus(Book book1, Book book2) throws RemoteException {
            return book1.getNumber() + book2.getNumber();
        }

    }

}