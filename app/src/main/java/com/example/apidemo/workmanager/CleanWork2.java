package com.example.apidemo.workmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.example.apidemo.utils.NLog;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @date 2020/3/4
 */

public class CleanWork2 extends Worker {

    public CleanWork2(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NLog.i("sjh3", "CleanWork2 doWork");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.SUCCESS;
    }
}