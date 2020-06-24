package com.example.apidemo.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.LogPrinter

/**
 * 单线程执行多个Looper.loop()
 * 主线程可以执行多个Looper.loop()，相当于mainMessageQueue.wait()后唤醒队列再mainMessageQueue.wait()。注意是同一个对象的wait。
 * 永远是最新的Looper.loop()在wait；只有最新的Looper.loop()因抛异常退出loop循环时才回到上一个Looper.loop()的wait！！！
 * Looper.myLooper().quit()不行，会退出所有loop循环。
 * @date 2020/6/10
 */
object LooperTest {
    val TAG = "LoperTest"
    private var handler: Handler? = null

    private val thread: Thread = Thread(object : Runnable {
        override fun run() {
            NLog.w(TAG, "======= begin loop 1 ======" + this)

            Looper.prepare()
            handler = Handler(Looper.myLooper())
            Looper.myLooper()!!.setMessageLogging(LogPrinter(Log.DEBUG, "sjh6"))

            try {
                Looper.loop()
            } catch (e: Throwable) {
                NLog.w(TAG, "======= finish loop 1 ======" + this)
            }
        }
    })

    fun startThread() {
        thread.start()
    }

    fun sendMsg() {
        handler?.post {
            NLog.e(TAG, "======= begin loop 2 ======" + this)
            try {
                Looper.loop()
            } catch (e: Throwable) {
                NLog.e(TAG, "======= finish loop 2 ======" + this)
            }
        }

        handler?.postDelayed({
            throw Exception("finish newest loop!!!")

        }, 3000)

        handler?.postDelayed({
            throw Exception("finish newest loop again!!!")

        }, 7000)
    }
}