package com.example.apidemo.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.service.MyJobService;
import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2020/2/11.
 */
public class JobServiceActivity extends BaseActivity {
    private static final String TAG = "MyJobService";
    private int mJobId = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        ((Button) findViewById(R.id.button1)).setText("scheduleJob");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scheduleJob();
            }
        });

        ((Button) findViewById(R.id.button2)).setText("cancelAllJobs");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelAllJobs();
                Toast.makeText(JobServiceActivity.this, "cancelAllJobs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleJob() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        // schedule相同jobID的任务时，若任务仍未触发，则会覆盖旧的JobInfo；若任务正在执行，则会onStopJob--onDestroy--onCreate--onStartJob
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++,
                new ComponentName(getPackageName(), MyJobService.class.getName()));

        // 设置任务的延迟执行时间，相当于post delay。
        // builder.setMinimumLatency(10 * 1000);

        //设置任务最晚的延迟时间。如果到了规定的时间时其他条件还未满足，你的任务也会被启动。
        // builder.setOverrideDeadline(30 * 1000);

        // 设置任务运行的周期（每X毫秒运行一次）。
        // setMinimumLatency和setOverrideDeadline任一个都不能同时与setPeriodic(long time)同时设置，也就是说，在设置延迟和最终期限时间时是不能设置重复周期时间的。
        builder.setPeriodic(60 * 1000);

        // 让你这个任务只有在满足指定的网络条件时才会被执行
        // NETWORK_TYPE_ANY : 网络状态联网就行
        // NETWORK_TYPE_UNMETERED : 设备不是蜂窝网络( 比如在WIFI连接时 )时任务才会被执行
        // NETWORK_TYPE_NOT_ROAMING : 非漫游网络。非漫游4g + wifi可以。
        // NETWORK_TYPE_NONE : 默认值。不管是否有网络这个任务都会被执行。即无网络限制
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);

        // 你的任务只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务。设备处于屏幕关闭或dreaming状态（类似window的休眠动画状态）71分钟后，执行工作。
        builder.setRequiresDeviceIdle(true);
        // 当设备在充电时这个任务才会被执行。这个也并非只是插入充电器，而且还要在电池处于健康状态的情况下才会触发，一般来说是手机电量>15%
        // builder.setRequiresCharging(true);

        // setBackoffCriteria 与 setRequiresDeviceIdle互斥，不明原因？？An idle mode job will not respect any back-off policy。
        // 若idle触发的任务执行失败，则仍有默认的重试策略的，不可使用setBackoffCriteria自定义重试策略。
        // setBackoffCriteria默认的失败重试策略是{30 seconds, Exponential}。最长重试5h：JobInfo.MAX_BACKOFF_DELAY_MILLIS.
        // builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);

        // Extras, work duration.
        PersistableBundle extras = new PersistableBundle();
        extras.putLong("key_delay", 123 * 1000);
        builder.setExtras(extras);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        // 这里就将开始在service里边处理我们配置好的job
        // schedule失败时会返回一个小于0的错误码；成功会返回该任务的id，这里可以使用这个id来判断哪些任务成功了。
        int requestResult = jobScheduler.schedule(builder.build());
        NLog.d(TAG, "Scheduling job requestResult = " + requestResult);  // requestResult每次都是1
    }

    private void cancelAllJobs() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

}
