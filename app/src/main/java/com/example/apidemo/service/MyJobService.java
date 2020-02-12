package com.example.apidemo.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import com.example.apidemo.activity.GaussActivity;
import com.example.apidemo.utils.NLog;

/**
 *
 * Created by Administrator on 2020/2/11.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        NLog.d(TAG, "MyJobService onCreate " + this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NLog.d(TAG, "MyJobService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 由主线程调用，需要自行开启异步任务
     * @param params
     * @return 任务是否 未执行完
     */
    @Override
    public boolean onStartJob(final JobParameters params) {
        long delay = params.getExtras().getLong("key_delay");
        // NLog.v(TAG, "onStartJob params extra delay =  " + delay);
        NLog.e(TAG, "============onStartJob======== jobID = " + params.getJobId());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MyJobService.this, GaussActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // MyJobService.this与onCreate的this不是同一对象？？
                NLog.d(TAG, "jobFinished: jobID = " + params.getJobId() + ". " + this);
                // wantsReschedule为true代表符合条件时再次触发onStartJob！
                jobFinished(params, true);     // 结束该jobID的任务！！注意覆盖jobID的情况。
            }
        }, 10 * 1000);


        // Return true as there's more work to be done with this job.
        return true;
    }

    /**
     * 不再符合job的条件时由系统触发调用。如断电后。
     * ps：onStartJob 返回false时onStopJob不会再调用
     *
     * @param params
     * @return true: job会重试; false:结束job！！！
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        NLog.d(TAG, "onStopJob: jobID = " + params.getJobId());

        // mHandler.removeCallbacksAndMessages(null);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NLog.d(TAG, "MyJobService onDestroy. " + this);
    }
}