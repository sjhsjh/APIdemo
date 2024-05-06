package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.tencent.matrix.resource.analyzer.BitmapAnalyzer;
import com.yy.onepiece.debugmonitor.thread.ThreadHookManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 性能监控
 * Created by jinhui.shao on 2024/5/3.
 */
public class APMActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);


        ((Button) findViewById(R.id.button1)).setText("dumpHprof+DuplicatedBitmapAnalyzer");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitmapAnalyzer.INSTANCE.analyzeSnapshot();

            }
        });

        ((Button) findViewById(R.id.button2)).setText("beginHook   thread");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ThreadHookManager.getInstance().beginHook();

            }
        });

        ((Button) findViewById(R.id.button3)).setText("new Thread().start(); ");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("sjh2", "=========thread finish============");
                    }
                }).start();

            }
        });

        ((Button) findViewById(R.id.button4)).setText("mHandlerThread.start(); ");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final HandlerThread mHandlerThread = new HandlerThread("handlerThread");
                mHandlerThread.start();
            }
        });

        ((Button) findViewById(R.id.button5)).setText("executor.submit(new Runnable())");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newFixedThreadPool(3);

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("sjh2", "=========run finish============");
                    }
                });
            }
        });

        ((Button) findViewById(R.id.button6)).setText("executor.submit(new Runnable())");
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 测试线程的内存占用，100条约占用2M，即一条占用20KB左右的native空间
                while(true){
                    for (int i = 0; i < 100; i++) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(Integer.MAX_VALUE);
                                } catch (InterruptedException e) {

                                }
                            }
                        }).start();
                    }
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {

                    }
                }
                
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}