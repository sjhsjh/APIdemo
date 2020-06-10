package com.example.apidemo.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.LogPrinter

/**
 * 单线程执行多个Looper.loop()
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