package com.example.apidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.Constant;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.utils.PreferencesManager;

/**
 *  activity进出动画、禁止截屏功能
 * Created by Administrator on 2018/8/5 0005.
 */

public class FunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        NLog.w("sjh1", "FunctionActivity onCreate ");

        CheckBox checkBox = ((CheckBox) findViewById(R.id.checkbox));
        checkBox.setVisibility(View.VISIBLE);
        if (PreferencesManager.getInstance(this).getBoolean(Constant.ALLOW_SCREENSHOT, true)) {
            checkBox.setChecked(true);
        } else {
            // 禁止截屏,包括系统截屏和adb命令截屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            checkBox.setChecked(false);
        }


        ((Button)findViewById(R.id.button1)).setText("无动画退出activity");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finishWithoutAnimation();

            }
        });

        ((Button)findViewById(R.id.button2)).setText("重启当前activity");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finishWithoutAnimation();
                Intent intent = new Intent(FunctionActivity.this, FunctionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);    // 等效于overridePendingTransition(0, 0);这flag仅适用于进入activity
                startActivity(intent);
                Toast.makeText(FunctionActivity.this, "当前activity已重启", Toast.LENGTH_SHORT).show();

            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
                PreferencesManager.getInstance(FunctionActivity.this).putBoolean(Constant.ALLOW_SCREENSHOT, isChecked);
            }
        });

    }

    /**
     * 无动画退出activity
     */
    public void finishWithoutAnimation() {
        finish();
        // overridePendingTransition必需紧挨着startActivity()或者finish()函数之后调用
        overridePendingTransition(0, 0);
    }


    @Override
    public void onBackPressed() {
        finish();
        // enterAnim: 即将要显示的activity的进入动画；exitAnim: 即将要消失的activity的离开动画
        overridePendingTransition(R.animator.slide_left_in, R.animator.slide_right_out);
    }


}
