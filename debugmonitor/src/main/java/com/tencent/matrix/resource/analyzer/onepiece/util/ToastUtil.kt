package com.tencent.matrix.resource.analyzer.onepiece.util

import android.os.Looper
import android.util.Log
import android.widget.Toast

/**
 *
 * @author shaojinhui@yy.com
 * @date 2020/8/13
 */
object ToastUtil {

    @JvmStatic
    fun toastAnyThread(s: CharSequence) {
        Log.i("sjh1", "toastAnyThread  content=$s")

        if (Looper.myLooper() == Looper.getMainLooper()) {
            SingleToastUtil.showToast(s)
        } else {
            s.toast()

//            GlobalScope.launch(Dispatchers.Main) {
//                SingleToastUtil.showToast(s)
//            }
        }
    }

}

fun CharSequence?.toast(default: CharSequence = "") {
    val text = if (isNullOrEmpty()) default else this
    ToastCompat.makeText(Utils2.getContext(), text, Toast.LENGTH_SHORT).show()
}