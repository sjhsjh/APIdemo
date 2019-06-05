package com.example.apidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.apidemo.utils.NLog;


public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        NLog.i("sjh2", "surfaceCreated holder=" + holder);
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        NLog.i("sjh2", "surfaceChanged holder=" + holder + " format = " + format + " width = " + width + " height = " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        NLog.i("sjh2", "surfaceDestroyed holder=" + holder);
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawSomething();
        }
    }

    //绘图逻辑
    private void drawSomething() {
        try {
            // 锁定画布并获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();  // 获得的Canvas对象仍然是上次绘制的对象
            //绘制背景
            mCanvas.drawColor(Color.YELLOW);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {    // 当画布内容不为空时才提交显示
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);    // 释放canvas对象并提交画布
            }
        }
    }

}