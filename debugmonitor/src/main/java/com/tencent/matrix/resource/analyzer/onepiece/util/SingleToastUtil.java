package com.tencent.matrix.resource.analyzer.onepiece.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by qinbo on 2014/8/12.
 */
public class SingleToastUtil {
    private static final String TAG = "SingleToastUtil";
    protected static Toast toast = null;

    public static void showToast(CharSequence s) {
        showToast(Utils2.getContext(), s);
    }

    public static void showToast(Context context, CharSequence s) {
        try {
            if (context == null) {
                return;
            }
            if (toast != null) {
                toast.cancel();
            }
            toast = ToastCompat.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Throwable t) {
            // MLog.error(TAG, "showToast error!" + t);
        }
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showCenterToast(CharSequence s) {
        try {
            Context context = Utils2.getContext();
            if (context == null) {
                return;
            }
            if (toast != null) {
                toast.cancel();
            }
            toast = ToastCompat.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Throwable t) {
            // MLog.error(TAG, "showCenterToast error!" + t);
        }
    }

    public static void showCenterToast(CharSequence s, int yOffset) {
        try {
            Context context = Utils2.getContext();
            if (context == null) {
                return;
            }
            if (toast != null) {
                toast.cancel();
            }
            toast = ToastCompat.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, yOffset);
            toast.show();
        } catch (Throwable t) {
            // MLog.error(TAG, "showCenterToast yOffset  error!" + t);
        }
    }
}