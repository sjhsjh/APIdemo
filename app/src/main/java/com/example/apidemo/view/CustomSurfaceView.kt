package com.example.apidemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView

/**
 * SurfaceView 绘制一个移动矩形
 */
class CustomSurfaceView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var drawingThread: Thread? = null
    private var isRunning = false

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private var positionX = 0
    private val speedX = 5

    init {
        holder.addCallback(this)

        TextureView(context)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i("sjh7", "surfaceCreated: ")

        isRunning = true
        drawingThread = Thread {
            while (isRunning) {
                val canvas: Canvas? = holder.lockCanvas()
                if (canvas != null) {
                    synchronized(holder) {
                        drawSomething(canvas)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        drawingThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.i("sjh7", "surfaceChanged: ")
        // Handle surface changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i("sjh7", "surfaceDestroyed: ")
        isRunning = false
        drawingThread?.join()
    }



    private fun drawSomething(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawRect(positionX.toFloat(), 100f, (positionX + 100).toFloat(), 200f, paint)
        positionX += speedX
        if (positionX > width) positionX = 0
    }
}