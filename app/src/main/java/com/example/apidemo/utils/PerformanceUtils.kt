package com.example.apidemo.utils

import android.os.Build
import android.os.Process
import android.system.ErrnoException
import android.system.Os
import android.util.Log
import com.blankj.utilcode.util.FileIOUtils
import java.io.File

/**
 *
 * @date 2024/3/20
 */
object PerformanceUtils {

    fun getFdInfo() {
        // 在/proc/pid/limits描述着Linux系统对对应进程的限制，其中Max open files就代表可创建FD的最大数目。
        val fdFile2 = File("/proc/" + Process.myPid() + "/limits")
        val limitsStr = FileIOUtils.readFile2List(fdFile2)
        Log.i("sjh3", "=FD limits==$limitsStr")

        // FD数量 需要监控
        val fdFile = File("/proc/" + Process.myPid() + "/fd")
        val files: Array<File>? = fdFile.listFiles()
        val fdNums = files?.size ?: 0               // 即进程中的fd数量。205
        Log.e("sjh3", "=FD num==$fdNums")


        // FD的用途主要有打开文件、创建socket连接、创建handlerThread等。 需要对以下内容进行 类型归并
        for (i in 0 until fdNums) {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    val str = Os.readlink(files?.get(i)!!.getAbsolutePath()) //得到软链接实际指向的文件
                    Log.w("sjh3", "=str=  $str")
                } catch (e: ErrnoException) {
                    Log.e("sjh3", "=error==$e")
                }
            } else {
                // 6.0以下系统可以通过执行readlink命令去得到软连接实际指向文件，但是耗时较久
            }
        }
    }


}