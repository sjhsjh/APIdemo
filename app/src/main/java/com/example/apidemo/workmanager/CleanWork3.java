package com.example.apidemo.workmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.example.apidemo.utils.NLog;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @date 2020/3/4
 */

public class CleanWork3 extends Worker {

    public CleanWork3(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NLog.i("sjh3", "CleanWork3 doWork");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.SUCCESS;
    }
}