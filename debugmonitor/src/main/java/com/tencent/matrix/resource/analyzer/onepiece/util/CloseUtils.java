package com.tencent.matrix.resource.analyzer.onepiece.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭相关工具类
 * from https://github.com/Blankj/AndroidUtilCode
 */
public final class CloseUtils {

    public static final String TAG = "CloseUtils";

    private CloseUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 关闭IO
     *
     * @param closeables closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                    // MLog.error(TAG, "closeIOQuietly:" + ignored.getMessage());
                }
            }
        }
    }
}
