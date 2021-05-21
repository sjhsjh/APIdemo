package com.example.apidemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.accessibility.ClickAccessibilityService;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.HardWareUtils;
import com.example.apidemo.utils.KeepCompactUtil;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.utils.TimeUtils;
import java.util.Calendar;
import java.util.Random;

/**
 * @see ClickAccessibilityService
 * @date 2020/1/21
 */
public class AutoClickActivity extends BaseActivity {
    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    public static final String CLASSNAME = "com.example.apidemo.activity.AutoClickActivity";
    public static final String TRIGGER_WINDOW_CHANGE = "trigger_window_change";
    private EditText edittext;
    private EditText edittext2;
    private Switch switchBtn;
    public static boolean enableAutoClickTest = false;
    public static boolean enableAutoClickPhone = false;
    public static boolean isMonitorOpen = false;

    private boolean checkIsEnable() {
        if (!BaseAccessibilityService.getInstance().checkAccessibilityEnabled(ClickAccessibilityService.SERVICE_ID)) {
            Toast.makeText(this, "辅助服务未开启", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        BaseAccessibilityService.getInstance().init(this);
        checkIsEnable();

        switchBtn = ((Switch) findViewById(R.id.switchBtn));
        switchBtn.setVisibility(View.VISIBLE);
        switchBtn.setText("开启监控某app");
        switchBtn.setChecked(isMonitorOpen);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // NLog.v("sjh5", "---isChecked--" + isChecked);
                isMonitorOpen = isChecked;
            }
        });


        edittext = findViewById(R.id.edittext);
        edittext.setTextSize(12);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setHint("9:00 + x.x hours 之前");
        edittext.setEnabled(!AndroidUtils.isAlarmRunning());

        edittext2 = findViewById(R.id.edittext2);
        edittext2.setTextSize(12);
        edittext2.setInputType(InputType.TYPE_CLASS_NUMBER);
        edittext2.setHint("N hours later from now");
        edittext.setEnabled(!AndroidUtils.isAlarmRunning());

        ((Button) findViewById(R.id.button1)).setText("跳转辅助服务");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.goAccessibilityServiceSettings(AutoClickActivity.this);
            }
        });
        ((Button) findViewById(R.id.button2)).setText("模拟点击Test");
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
                if (!checkIsEnable()) {
                    return;
                }
                //设定时间为 2016年12月16日11点50分0秒
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, 2020);
                calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 8);   //
                calendar.set(Calendar.MINUTE, 54);       //
                calendar.set(Calendar.SECOND, 0);

                long beginMs = calendar.getTimeInMillis();
                long now = System.currentTimeMillis();
                long inteval = ONE_DAY;
                while (beginMs < now) {
                    beginMs += inteval;
                }
                // NLog.v("sjh5", "---deta time---" + (beginMs - now));

                edittext.setEnabled(false);
                edittext2.setEnabled(false);
                double skipHours = 0.0;

                if (!TextUtils.isEmpty(edittext.getText().toString())) {
                    skipHours = Double.parseDouble(edittext.getText().toString());
                } else if (!TextUtils.isEmpty(edittext2.getText().toString())) {
                    int skipHour = Integer.parseInt(edittext2.getText().toString());
                    beginMs = now + skipHour * ONE_HOUR;
                    NLog.i("sjh5", "---edittext2---beginMs = " + beginMs);
                }
                NLog.v("sjh5", "---skipHours---" + skipHours);
                beginMs += skipHours * ONE_HOUR;
                NLog.v("sjh5", "---skipHours * ONE_HOUR---" + skipHours * ONE_HOUR);

                // 随机增加最多5min
                Random r1 = new Random();
                int offset = r1.nextInt(300) * 1000;
                NLog.v("sjh5", "---offset---" + offset);
                beginMs += offset;

                // final excute time
                ((TextView) findViewById(R.id.textView1)).setText("Excute time:\n" + TimeUtils.getTimeStringFromMillis(beginMs));


                // 若beginMs为过去的时刻，则闹钟约3~5s后触发；若beginMs太靠近当前时间的话，则第一次执行的时刻不准！！偏移10s就正常。
                // 因此beginMs永远需要大于当前时刻才正确，最好大于10s以上
                AndroidUtils.openAlarm(AutoClickActivity.this, true,    // System.currentTimeMillis() + 10000, 15000
                        beginMs, inteval, new TimeUpCallback() {
                            @Override
                            public void timeUp() {
                                NLog.d("sjh5", "---time up run---" + AutoClickActivity.this);
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

                                        // finish();
                                        // overridePendingTransition(0, 0);
                                        Intent intent = new Intent(AutoClickActivity.this, AutoClickActivity.class);
                                        // intent.setAction(TRIGGER_WINDOW_CHANGE);
                                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                        // Toast.makeText(AutoClickActivity.this, "start AutoClick Activity", Toast.LENGTH_SHORT).show();
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
                edittext.setEnabled(true);
                edittext2.setEnabled(true);
                edittext.requestFocus();
                ((TextView) findViewById(R.id.textView1)).setText("textView1");
            }
        });
        ((Button) findViewById(R.id.button5)).setText("恢复锁屏 reenableKeyguard");
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HardWareUtils.getInstance().reenableKeyguard();
            }
        });

        ((Button) findViewById(R.id.button6)).setText("前往设置 允许应用不休眠");
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean hasPage = KeepCompactUtil.INSTANCE.goNoSleepSettings(AutoClickActivity.this);
                if (!hasPage) {
                    Toast.makeText(AutoClickActivity.this, "找不到防休眠的设置页面", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ll_button7).setVisibility(View.VISIBLE);
        findViewById(R.id.button8).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.button7_1)).setText("前往 电池优化界面");
        findViewById(R.id.button7_1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AndroidUtils.goBatteryOptimizationSettings(AutoClickActivity.this);
            }
        });
        ((Button) findViewById(R.id.button7_2)).setText("检查是否在电池优化白名单");
        findViewById(R.id.button7_2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isWhite = AndroidUtils.isInBatteryOptimizationWhiteList(AutoClickActivity.this);
                Toast.makeText(AutoClickActivity.this, isWhite ? "是" : "否", Toast.LENGTH_SHORT).show();
            }
        });
        ((Button) findViewById(R.id.button8)).setText("前往设置 允许应用自启动");
        findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean hasPage = KeepCompactUtil.INSTANCE.goAutoStartSettings(AutoClickActivity.this);
                if (!hasPage) {
                    Toast.makeText(AutoClickActivity.this, "找不到自启动的设置页面", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // NLog.v("sjh5", "AutoClickActivity onResume :" + getIntent());
        if (enableAutoClickPhone) {         // 可增加action判断
            switchBtn.post(new Runnable() { // 重要！！onResume直接showDialog会导致dialog显示后还未能获取焦点（尚未绘制完），触发不了AccessibilityEvent
                @Override
                public void run() {
                    showFakeDialog();
                }
            });
        }
    }

    private void showFakeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AutoClickActivity.this)
                .setTitle(TRIGGER_WINDOW_CHANGE)// .setMessage("msg")
                .setNegativeButton("确定", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog2) {
                NLog.i("sjh5", "-------dialog show--------");  // dialog.getContext() : ContextThemeWrapper
                Toast.makeText(AutoClickActivity.this, "dialog已触发显示", Toast.LENGTH_SHORT).show();

                dialog2.dismiss();   // 立刻消失
            }
        });
        dialog.show();
    }

    public interface TimeUpCallback {
        void timeUp();
    }
}