package com.tencent.matrix.resource.analyzer.onepiece.util;

import android.content.Context;
import android.os.SystemClock;
import java.io.File;

public class BaseUtils {

    public static File getCacheDir(Context ctx) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        long start = SystemClock.elapsedRealtime();
        final File cacheDir = getNoPermissionExternalFilesDir(ctx);

        return new File(cacheDir, "apidemo-dir");
    }

    private static File getNoPermissionExternalFilesDir(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) {
            dir = ctx.getFilesDir();
        }
        return dir;
    }
}
