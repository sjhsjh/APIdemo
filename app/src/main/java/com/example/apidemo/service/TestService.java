package com.example.apidemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.apidemo.utils.AndroidUtils;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/17.
 */
public class TestService extends Service{
    private final String TAG = this.getClass().getSimpleName();
    private MyBinder mMyBinder = new MyBinder();
    private Handler mHandler;
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
        Log.d(TAG, "onCreate. thread id is " + Thread.currentThread().getId());
        // 只在service创建的时候调用一次，可以在此进行一些一次性的初始化操作
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // 当其他组件调用startService()方法时，此方法将会被调用.在这里进行这个service主要的操作

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

        new Thread(mRunnable).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // 当其他组件调用bindService()方法时，此方法将会被调用.返回一个IBinder对象，它是用来支撑其他组件与service之间的通信。如果不想让这个service被绑定，在此返回null即可

        return mMyBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // service调用的最后一个方法.在此进行资源的回收
        if(mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder{
        public void binderLog(){
            Log.d(TAG, "binderLog");
        }
    }

}