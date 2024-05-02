package com.tencent.matrix.resource.analyzer.onepiece.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Ping on 2018/10/24.
 * 防止Toast崩溃
 */

public final class ToastCompat {
    private static final String TAG = "ToastCompat";

    public static Toast makeText(CharSequence text) {
        return makeText(Utils2.getContext(), text, Toast.LENGTH_SHORT);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        return tryToHook(Toast.makeText(context, text, duration));
    }

    @SuppressWarnings("ConstantConditions")
    private static Toast tryToHook(Toast toast) {
        try {
            Object mTN = ReflectWrapper.findField(toast, "mTN").get(toast);
            Object mHandler = ReflectWrapper.findField(mTN, "mHandler").get(mTN);
            ReflectWrapper.findField(mHandler, "mCallback")
                    .set(mHandler, new SafeHandlerCallback((Handler) mHandler));
        } catch (Throwable throwable) {
            // MLog.error(TAG, "tryToHook error! " + throwable);
        }
        return toast;
    }

    private static class SafeHandlerCallback implements Handler.Callback {
        private static final String TAG = "SafeHandlerCallback";
        private Handler mHandler;

        SafeHandlerCallback(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            try {
                //                MLog.debug(TAG, "handleMessage " + msg);
                mHandler.handleMessage(msg);
            } catch (Throwable throwable) {
                // MLog.error(TAG, "handleMessage error!", throwable);
            }
            return true;
        }
    }
}
