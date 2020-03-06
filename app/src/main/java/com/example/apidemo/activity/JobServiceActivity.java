package com.example.apidemo.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.apidemo.R;
import com.example.apidemo.service.MyJobService;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.workmanager.CleanWork;
import com.example.apidemo.workmanager.CleanWork2;
import com.example.apidemo.workmanager.CleanWork3;
import java.util.concurrent.TimeUnit;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import java.util.ArrayList;
import java.util.List;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkRequest;

/**
 * Created by Administrator on 2020/2/11.
 */
public class JobServiceActivity extends FragmentActivity {
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
        ((Button) findViewById(R.id.button3)).setText("startWorkManagerJob");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startWorkManagerJob();
            }
        });
        ((Button) findViewById(R.id.button4)).setText("stopWorkManagerJob");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopWorkManagerJob();
            }
        });
    }

    /**
     * JobScheduler：满足条件后，在指定的粗略时间范围内触发任务。
     * 时间范围：延迟、重复、deadline。
     * 缺点：可选触发条件有限、触发时间很粗略
     *
     * ps:JobScheduler是在API21加入的，但在API21&22有N个系统Bug（如重启后setMinimumLatency的值大大增加，
     * 这就意味着它只能用在API23及以上的版本，因此WorkManager都在api23+才使用JobScheduler。
     */
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

    /**
     * WorkManager相比JobScheduler的不同点：
     * 1、没有deadline；
     * 2、使用WorkContinuation进行链式请求，非常灵活地控制各任务的执行顺序；
     * 3、增加了对任务的状态监听
     * 4、WorkManager结合了Room，将数据保存在数据库中。
     *
     *
     * 1、WorkManager目的是统一Android背景任务调度。用于如发送应用程序日志，同步应用程序数据，备份用户数据等场景。
     * 2、发出延时N秒的任务，当进程被杀后不会拉起进程，而是在进程重启后重新发出延时N秒的任务！！！
     * 而小米要将省电策略改成无限制，应用重启后才能触发WorkManager的任务。
     */
    private void startWorkManagerJob() {
        NLog.d("sjh3", "startWorkManagerJob()  ->  " + System.currentTimeMillis());

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                // .setRequiresDeviceIdle(true)
                // .setRequiresCharging(true)
                // .setRequiresBatteryNotLow(true)
                // .setRequiresStorageNotLow(true)
                .build();
        Data inputData = new Data.Builder().putString("key1", "value1").build();
        // 没有设置deadline的方法
        OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(CleanWork.class)
                .setInitialDelay(20, TimeUnit.SECONDS)     // 符合触发条件后，延迟10秒执行(联网后3s触发)
                .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)                       // 设置指数退避算法
                .setConstraints(constraints).setInputData(inputData).build();
        OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(CleanWork2.class)
                .setConstraints(constraints).setInputData(inputData).build();
        OneTimeWorkRequest request3 = new OneTimeWorkRequest.Builder(CleanWork3.class)
                .setConstraints(constraints).setInputData(inputData).build();
        OneTimeWorkRequest request4 = new OneTimeWorkRequest.Builder(CleanWork.class)
                .setConstraints(constraints).setInputData(inputData).build();

        // 使用链式请求的是WorkContinuation，enqueue返回值是Operation
        // WorkManager.getInstance().beginWith(request1).then(request2).enqueue();   // 串行

        // List<WorkRequest> requests = new ArrayList<>();
        // requests.add(request1);
        // requests.add(request2);
        // WorkManager.getInstance().enqueue(requests);                  // 并行

        // 链式请求不支持PeriodicWorkRequest，因此链式请求内不能含有PeriodicWorkRequest。
        // List<OneTimeWorkRequest> requests = new ArrayList<>();
        // requests.add(request1);
        // requests.add(request2);
        // WorkManager.getInstance().beginWith(requests).enqueue();      // 并行(带链式请求)

        // beginWith 和 then(List<OneTimeWorkRequest> work)具有并行功能
        // then默认使用ExistingWorkPolicy.KEEP，因此then传入已有的request会一个任务也不执行。maybe
        // WorkManager.getInstance().beginWith(request1).then(requests).then(request4).enqueue();

        // combine可以将两条串行分支并行
        // WorkContinuation workContinuation1 = WorkManager.getInstance().beginWith(request1);     // .then(workC);
        // WorkContinuation workContinuation2 = WorkManager.getInstance().beginWith(request2);     // .then(workD);
        // WorkContinuation.combine(workContinuation1, workContinuation2).then(request3).enqueue();  // 先串后并

        // 定时循环任务。周期性任务的间隔时间不能小于15分钟。
        // PeriodicWorkRequest periodWork = new PeriodicWorkRequest.Builder(CleanWork.class, 16, TimeUnit.MINUTES)
        //         .addTag("tag_period")
        //         .setConstraints(constraints).build();
        // WorkManager.getInstance().enqueue(periodWork);


        WorkManager.getInstance().beginWith(request2).enqueue();

        WorkManager.getInstance().getWorkInfoByIdLiveData(request2.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        NLog.d("sjh3", "onChanged : " + "workInfo:" + workInfo);
                    }
                });
    }

    private void stopWorkManagerJob() {
        // WorkManager.getInstance().cancelWorkById(request2.getId());
        WorkManager.getInstance().cancelAllWork();
    }
}
