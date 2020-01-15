package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.View;

import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * @date 2020/1/15
 */
public class AppBarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_appbar);


        findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(android.view.View v) {
                NLog.v("sjh9", "==appbar getHeight===" + findViewById(R.id.appbar).getHeight());
                NLog.w("sjh9", "===appbar getElevation==" + findViewById(R.id.appbar).getElevation());
                // findViewById(R.id.appbar).setElevation(0);
            }
        });
    }
}
