package com.tencent.matrix.resource.analyzer

import android.os.Build
import android.os.Debug
import android.util.Log
import com.tencent.matrix.resource.analyzer.model.AndroidExcludedBmpRefs
import com.tencent.matrix.resource.analyzer.model.HeapSnapshot
import com.tencent.matrix.resource.analyzer.onepiece.util.BasicConfig
import com.tencent.matrix.resource.analyzer.onepiece.util.FileUtils
import com.yy.onepiece.debugmonitor.Utils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Bitmap 对象重复检测，只支持 8.0 以下
 * 一件当前重复 Bitmap 对象 ：进度条占位图、头像占位图等等
 *  R.drawable.default_avatar、R.drawable.pic_default_middle
 * @author shaojinhui@yy.com
 * @date 2020/9/14
 */
object BitmapAnalyzer {
    private val TAG = "BitmapAnalyzer"
    private val hprofFileDirPath = BasicConfig.cashDirPath + File.separator + "Hprof"
    private val mMinBmpLeakSize = 100000      // 忽略大小低于此值的bitmap对象

    fun analyzeSnapshot() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.e(TAG,
//                "\n ! SDK version of target device is larger or equal to 26, which is not supported by DuplicatedBitmapAnalyzer.")
//            return
//        }

        Log.d("sjh8", "==begin analyzeSnapshot==")
        // just try gc
        triggerGc()
        val hprofFile = dumpHprof()

        // begin analyze
        val heapSnapshot: HeapSnapshot
        try {
            heapSnapshot = HeapSnapshot(hprofFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        val excludedBmps = AndroidExcludedBmpRefs.createDefaults().build()
        var duplicatedBmpResult = DuplicatedBitmapAnalyzer(mMinBmpLeakSize, excludedBmps).analyze(heapSnapshot)


        val jsonObject = JSONObject()
        try {
            duplicatedBmpResult.encodeToJSON(jsonObject)
            Utils.log("sjh8", "==duplicated bitmap==$jsonObject")

            // {"targetFound":false,"analyzeDurationMs":1149,"mFailure":"java.lang.IllegalArgumentException: Field mBuffer does not exists","duplicatedBitmapEntries":[]}
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * may not gc.
     */
    private fun triggerGc() {
        Runtime.getRuntime().gc()
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            throw AssertionError()
        }
        System.runFinalization()
    }

    private fun dumpHprof(): File {
        FileUtils.createOrExistsDir(hprofFileDirPath)
        // /storage/emulated/0/Android/data/com.example.apidemo/files/apidemo-dir/Hprof/bitmap.hprof
        // 28 MB
        val file = File(hprofFileDirPath + File.separator + "bitmap.hprof")
        if (file.exists()) {
            file.delete()
        }
        try {
            Debug.dumpHprofData(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}