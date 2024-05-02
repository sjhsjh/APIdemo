package com.yy.onepiece.debugmonitor.image

import android.graphics.Bitmap
import com.tencent.matrix.resource.analyzer.onepiece.util.FileIOUtils
import com.tencent.matrix.resource.analyzer.onepiece.util.TimeUtils
import com.yy.onepiece.debugmonitor.AbstractMonitor
import com.yy.onepiece.debugmonitor.MonitorConfig
import com.yy.onepiece.debugmonitor.Utils
import java.io.File
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentSkipListMap

/**
 *  记录所有bitmap的弱引用和创建堆栈，并按内存占用大小排序
 * @author shaojinhui@yy.com
 * @date 2020/8/24
 */
class ImageMonitor : AbstractMonitor() {
    companion object {
        private const val TAG = "ImageMonitor"
        private const val LARGEST_BITMAP_COUNT = 10                       // 最大的 N 张bitmap
        private const val TOPEST_BITMAP_OCCUPY_LIMIT = 50 * 1024 * 1024   // 50M。最大的 N 张bitmap占用超过该值就警告
        @JvmStatic
        val instance: ImageMonitor by lazy { ImageMonitor() }
    }

    val bitmapMap = ConcurrentSkipListMap<WeakReference<Bitmap>, String>(
        Comparator { lhs, rhs ->
            (rhs!!.get()?.allocationByteCount ?: 0) - (lhs!!.get()?.allocationByteCount ?: 0)
        })

    /**
     * 遍历删除Bitmap被回收的键值对 + 去重
     */
    fun tryAddBitmap(bitmap: Bitmap?, stackStr: String) {
        bitmap?.let {

            val iterator = bitmapMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.get() == null) {
                    iterator.remove()
                } else {
                    if (entry.key.get() === bitmap) {
                        return
                    }
                }
            }
            bitmapMap.put(WeakReference<Bitmap>(bitmap), stackStr)
        }
    }

    fun dumpBitmapInfoNow(writeSD: Boolean) {
        val sb = StringBuilder()
        sb.append("Bitmap count : ${bitmapMap.size}\n")
        sb.append("Bitmap size rank : \n")

        val summarySb = StringBuilder()
        summarySb.append("Bitmap count : ${bitmapMap.size}\n")
        summarySb.append("Bitmap size rank : \n")

        var topTenSize = 0L
        var num = 0
        run loop@{
            for (entry in bitmapMap.entries) {
                entry.key.get()?.let {
                    if (num == LARGEST_BITMAP_COUNT) {
                        return@loop     // same as break
                    }
                    sb.append("$it Bitmap info : " + it.width + " x " + it.height)
                    sb.append("  Bitmap size: " + it.allocationByteCount + " B")
                    sb.append("\n call stack trace: " + entry.value + "\n")
                    sb.append("\n")

                    summarySb.append("Bitmap info : " + it.width + " x " + it.height)
                    summarySb.append("  Bitmap size: " + it.allocationByteCount + " B")
                    summarySb.append("\n")

                    topTenSize += it.allocationByteCount
                    num++
                }
            }
        }
        Utils.log(TAG, "--dumpBitmapInfoNow  start--\n$sb---end---")

        if (topTenSize >= TOPEST_BITMAP_OCCUPY_LIMIT) {
//            ToastUtil.toastAnyThread("最大的10个bitmap内存占用已超 50M! 已占用${topTenSize / 1024 / 1024} M" +
//                " 详情请查看日志 " + MonitorConfig.imageMonitorDir)
            writeSD(sb.toString())
        } else if (writeSD) {
//            ToastUtil.toastAnyThread(summarySb)
            writeSD(sb.toString())
        }
    }

    private fun writeSD(content: String) {
        val timeString = TimeUtils.getFormatTimeString(System.currentTimeMillis(), "year-mon-day_hour:min:sec")
        val filePath = MonitorConfig.imageMonitorDir + File.separator + timeString + ".txt"
        FileIOUtils.writeFileFromString(filePath, content)
    }
}

