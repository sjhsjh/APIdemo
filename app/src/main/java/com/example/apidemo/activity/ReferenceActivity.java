package com.example.apidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.utils.NLog;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/1/7 0007.
 * 弱引用理解
 */

public class ReferenceActivity extends BaseActivity {
    private static final String KEY = "key";
    private static ReferenceActivity.InnerClass i;
    private static WeakReference wr;
    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            NLog.d("sjh7", "onCreate(). savedInstanceState data = " + savedInstanceState.getCharSequence(KEY));
        }

        wr = new WeakReference(this);
        activity = this;

        i = new InnerClass(this);

        MyThreadR mt1 = new MyThreadR("线程A ") ;    // 实例化对象
        MyThreadR mt2 = new MyThreadR("线程B ") ;    // 实例化对象
        Thread t1 = new Thread(mt1,"thread_name") ;       // 实例化Thread类对象
        Thread t2 = new Thread(mt2) ;       // 实例化Thread类对象
        t1.start() ;    // 启动多线程
        t2.start() ;    // 启动多线程

        MyThread mt11 = new MyThread("线程A ") ;    // 实例化对象
        MyThread mt22 = new MyThread("线程B ") ;    // 实例化对象
        mt11.start() ;   // 调用线程主体
        mt22.start() ;   // 调用线程主体
        Thread.currentThread().getName();
        try{
            Thread.sleep(2000) ;    // 线程休眠2秒
        }catch(InterruptedException e){
            System.out.println("3、休眠被终止00000000000") ;
        }
    }

    class MyThreadR implements Runnable{ // 实现Runnable接口，作为线程的实现类
        private String name ;       // 表示线程的名称
        public MyThreadR(String name){
            this.name = name ;      // 通过构造方法配置name属性
        }
        public void run(){  // 覆写run()方法，作为线程 的操作主体
            for(int i=0;i<10;i++){
                System.out.println(name + "运行，i = " + i) ;
            }
        }
    };

    class MyThread extends Thread{  // 继承Thread类，作为线程的实现类
        private String name ;       // 表示线程的名称
        public MyThread(String name){
            super();
            this.name = name ;      // 通过构造方法配置name属性
        }
        public void run(){  // 覆写run()方法，作为线程 的操作主体
            for(int i=0;i<10;i++){
                System.out.println(name + "运行，i = " + i) ;
            }
        }
    };

    public static class InnerClass{
        WeakReference wr;
        public InnerClass(Context context){
            wr = new WeakReference(context);
        }
    }


    /**
     * @param outState 保存activity内所有view的信息.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        NLog.d("sjh7", "onSaveInstanceState(). ");
        outState.putCharSequence(KEY, "onSaveInstanceState value");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // onRestoreInstanceState的savedInstanceState一定不为空.
        NLog.d("sjh7", "onRestoreInstanceState(). data = " + savedInstanceState.getCharSequence(KEY));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NLog.d("sjh7", "onNewIntent");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        NLog.d("sjh7", "newConfig = " + newConfig.toString());
    }

}
