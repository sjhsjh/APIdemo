package com.example.apidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import com.didichuxing.doraemonkit.util.PermissionUtil;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.screenshot.FloatWindowsService;
import com.example.apidemo.screenshot.ScreenShotPermissionActivity;

/**
 * @date 2020/3/1
 */
public class ScreenShotActivity extends BaseActivity {
    private static final String TAG = "ScreenShotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        ((Button) findViewById(R.id.button1)).setText("截屏");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Settings.canDrawOverlays(ScreenShotActivity.this)) {
                    // 小米9 不行
                    Intent intent =
                            new Intent(ScreenShotActivity.this, ScreenShotPermissionActivity.class);
                    startActivity(intent);
                } else {
                    PermissionUtil.requestDrawOverlays(ScreenShotActivity.this);
                }

            }
        });
        ((Button) findViewById(R.id.button2)).setText("关闭截屏服务");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenShotActivity.this, FloatWindowsService.class);
                stopService(intent);
            }
        });
    }
}
