package com.example.apidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;
import com.example.apidemo.accessibility.ClickAccessibilityService;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.NLog;
import java.util.Calendar;

/**
 * @see ClickAccessibilityService
 * @date 2020/1/21
 */
public class AutoClickActivity extends BaseActivity {
    public static final String CLASSNAME = "com.example.apidemo.activity.AutoClickActivity";
    public static boolean enableAutoClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        BaseAccessibilityService.getInstance().init(this);
        if (!BaseAccessibilityService.getInstance().checkAccessibilityEnabled(ClickAccessibilityService.SERVICE_ID)) {
            Toast.makeText(this, "辅助服务未开启", Toast.LENGTH_SHORT).show();
        }

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
                        enableAutoClick = true;

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

        ((Button) findViewById(R.id.button3)).setText("AlarmManager");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //设定时间为 2016年12月16日11点50分0秒
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, 2020);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 28);
                calendar.set(Calendar.HOUR_OF_DAY, 16);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);

                AndroidUtils.openAlarm(AutoClickActivity.this,false,
                        System.currentTimeMillis(), 7000, new Runnable() {
                    @Override
                    public void run() {
                        NLog.d("sjh5", "---times up run---");
                        Intent intent = new Intent(AutoClickActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

            }
        });
    }

}