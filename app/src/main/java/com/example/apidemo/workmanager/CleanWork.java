package com.example.apidemo.workmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.example.apidemo.utils.NLog;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @date 2020/3/4
 */

public class CleanWork extends Worker {

    public CleanWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NLog.i("sjh3", "CleanWork1 doWork " + Thread.currentThread().getName());    // 子线程
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 返回 FAILURE 时，链式请求的后续请求都变成失败
        // 返回 RETRY 时，setBackoffCriteria有效
        return Result.SUCCESS;
    }
}