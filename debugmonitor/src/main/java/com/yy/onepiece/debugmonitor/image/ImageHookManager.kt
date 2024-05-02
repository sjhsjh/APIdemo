package com.yy.onepiece.debugmonitor.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.ImageView
import de.robv.android.xposed.DexposedBridge
import de.robv.android.xposed.XC_MethodHook

/**
 *
 * @author shaojinhui@yy.com
 * @date 2020/8/11
 */
class ImageHookManager {
    companion object {
        private const val TAG = "ImageHookManager"
        var hookSwitch = true   // 是否开启hook ImageView
        @JvmStatic
        val instance: ImageHookManager by lazy { ImageHookManager() }
    }

    private var unHookList = ArrayList<XC_MethodHook.Unhook>()
    private var hookCallbackList = ArrayList<XC_MethodHook>()

    fun beginHook() {
        if (!hookSwitch) {
            return
        }
        hookSetImage()
        hookCreateImage()
    }

    fun unhookAll() {
        for (i in 0 until unHookList.size) {
            unHookList[i]?.let {
                DexposedBridge.unhookMethod(it.getHookedMethod(), hookCallbackList[i])
            }
        }

        unHookCreateImage()
        clearData()
    }

    private fun clearData() {
        unHookList.clear()
        hookCallbackList.clear()
        map.clear()
    }

    private fun hookSetImage() {
        val imageHook1 = SetImageHook()
        val unHook1 = DexposedBridge.findAndHookMethod(ImageView::class.java, "setImageBitmap",
            Bitmap::class.java, imageHook1)
        saveToList(unHook1, imageHook1)

        val imageHook2 = SetImageHook()
        val unHook2 = DexposedBridge.findAndHookMethod(ImageView::class.java, "setImageDrawable",
            Drawable::class.java, imageHook2)
        saveToList(unHook2, imageHook2)

        val imageHook3 = SetImageHook()
        val unHook3 = DexposedBridge.findAndHookMethod(ImageView::class.java, "setImageResource",
            Int::class.java, imageHook3)
        saveToList(unHook3, imageHook3)
    }

    private fun saveToList(unHook: XC_MethodHook.Unhook, methodHook: XC_MethodHook) {
        unHook?.let {
            unHookList.add(it)
        }
        hookCallbackList.add(methodHook)
    }

    private var map = HashMap<XC_MethodHook, Set<XC_MethodHook.Unhook>>()

    fun hookCreateImage() {
        hookCreateBitmap()
        hookBitmapFactory()
    }

    fun unHookCreateImage() {
        for ((key, value) in map) {
            for (unhook in value) {
                DexposedBridge.unhookMethod(unhook.getHookedMethod(), key)
            }
        }
    }

    private fun hookCreateBitmap() {
        val imageCreateHook = CreateImageHook()
        val unHookSet = DexposedBridge.hookAllMethods(Bitmap::class.java, "createBitmap", imageCreateHook)
        map.put(imageCreateHook, unHookSet)
    }

    private val methodNameArray = arrayOf("decodeResource", "decodeFile", "decodeStream",
        "decodeByteArray", "decodeFileDescriptor", "decodeResourceStream")

    private fun hookBitmapFactory() {
        for (methodName in methodNameArray) {
            val imageCreateHook = CreateImageHook()
            val unHookSet = DexposedBridge.hookAllMethods(BitmapFactory::class.java, methodName, imageCreateHook)
            map.put(imageCreateHook, unHookSet)
        }
    }
}