package com.yy.onepiece.debugmonitor

import android.util.Log

/**
 *
 * @author shaojinhui@yy.com
 * @date 2020/9/14
 */
object Utils {
    /**
     * 超长日志打印，max：4096
     */
    fun log(tag: String, msg: String) {
        val strLength = msg.length
        var start = 0
        var end = 4000
        for (i in 0..99) {
            // 剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(tag + i, msg.substring(start, end))
                start = end
                end = end + 4000
            } else {
                Log.d(tag, msg.substring(start, strLength))
                break
            }
        }
    }
}