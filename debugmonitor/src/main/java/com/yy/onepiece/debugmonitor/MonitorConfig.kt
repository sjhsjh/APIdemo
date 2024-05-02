package com.yy.onepiece.debugmonitor

import com.tencent.matrix.resource.analyzer.onepiece.util.BasicConfig
import java.io.File

/**
 *
 * @author shaojinhui@yy.com
 * @date 2020/8/12
 */
object MonitorConfig {
    val memoryMonitorDir =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator + "MemoryManager"
    val gcInfoFile =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator + "MemoryManager" +
            File.separator + "gc_info_exception.txt"

    val threadMonitorDir =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator + "ThreadMonitor"
    val imageMonitorDir =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator + "ImageMonitor"
    val setImageHookFile =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator + "SetImageHook" +
            File.separator + "set_over_large_bitmap.txt"
    val createImageHookFile =
        BasicConfig.cashDirPath + File.separator + "Memory" + File.separator +
            "CreateImageHook" + File.separator + "create_over_large_bitmap.txt"
}
