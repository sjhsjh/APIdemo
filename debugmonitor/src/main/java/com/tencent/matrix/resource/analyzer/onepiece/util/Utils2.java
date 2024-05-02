package com.tencent.matrix.resource.analyzer.onepiece.util;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Utils初始化相关
 * from https://github.com/Blankj/AndroidUtilCode
 */
public final class Utils2 {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils2() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils2.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("u should init first");
    }


}
