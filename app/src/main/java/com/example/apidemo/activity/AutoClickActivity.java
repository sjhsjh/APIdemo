package com.example.apidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;
import com.example.apidemo.accessibility.ClickAccessibilityService;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.HardWareUtils;
import com.example.apidemo.utils.NLog;
import java.util.Calendar;
import java.util.Random;

/**
 * @see ClickAccessibilityService
 * @date 2020/1/21
 */
public class AutoClickActivity extends BaseActivity {
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
    private static final long ONE_WEEK = 7 * ONE_DAY;
    public static final String CLASSNAME = "com.example.apidemo.activity.AutoClickActivity";
    private Switch switchBtn;
    public static boolean enableAutoClickTest = false;
    public static boolean enableAutoClickPhone = false;
    public static boolean isMonitorOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        BaseAccessibilityService.getInstance().init(this);
        if (!BaseAccessibilityService.getInstance().checkAccessibilityEnabled(ClickAccessibilityService.SERVICE_ID)) {
            Toast.makeText(this, "辅助服务未开启", Toast.LENGTH_SHORT).show();
        }

        switchBtn = ((Switch) findViewById(R.id.switchBtn));
        switchBtn.setVisibility(View.VISIBLE);
        switchBtn.setText("开启监控某app");
        switchBtn.setChecked(isMonitorOpen);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NLog.d("sjh5", "---isChecked--" + isChecked);
                isMonitorOpen = isChecked;
            }
        });

        ((Button) findViewById(R.id.button1)).setText("跳转辅助服务");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.goAccessibilityServiceSettings(AutoClickActivity.this);
            }
        });
        ((Button) findViewById(R.id.button2)).setText("开启模拟点击");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 模拟界面变化触发页面监控并进行自动点击
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableAutoClickTest = true;

                        finish();
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(AutoClickActivity.this, AutoClickActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Toast.makeText(AutoClickActivity.this, "AutoClick Activity已重启", Toast.LENGTH_SHORT).show();
                    }
                }, 500);
            }
        });

        ((Button) findViewById(R.id.button3)).setText("开启 AlarmManager");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //设定时间为 2016年12月16日11点50分0秒
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, 2020);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 29);
                calendar.set(Calendar.HOUR_OF_DAY, 11);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Random r1 = new Random();
                int offset = r1.nextInt(120) * 1000;
                NLog.v("sjh5", "---offset---" + offset);

                // beginMs太靠近当前时间的话，第一次执行的时刻不准！！偏移10s就正常。
                AndroidUtils.openAlarm(AutoClickActivity.this,true,
                        calendar.getTimeInMillis(), 5 * 60 * 1000, new Runnable() {
                    @Override
                    public void run() {
                        NLog.d("sjh5", "---times up run---");
                        // Intent intent = new Intent(AutoClickActivity.this, MainActivity.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // startActivity(intent);

                        HardWareUtils.getInstance().wakeUpAndDisableKeyguard(AutoClickActivity.this.getApplicationContext());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                enableAutoClickPhone = true;
                                isMonitorOpen = true;
                                switchBtn.setChecked(isMonitorOpen);
                                finish();
                                overridePendingTransition(0, 0);
                                Intent intent = new Intent(AutoClickActivity.this, AutoClickActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                Toast.makeText(AutoClickActivity.this, "AutoClick Activity已重启", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);   // 解锁需要时间

                    }
                });
            }
        });
        ((Button) findViewById(R.id.button4)).setText("取消 AlarmManager");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.cancelAlarmAndBroadcast(AutoClickActivity.this);
                isMonitorOpen = false;
                switchBtn.setChecked(isMonitorOpen);
            }
        });
        ((Button) findViewById(R.id.button5)).setText("恢复锁屏 reenableKeyguard");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HardWareUtils.getInstance().reenableKeyguard();
            }
        });
    }

}