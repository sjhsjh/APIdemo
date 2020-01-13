package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;

/**
 * @date 2020/1/9
 */
public class NestScrollActivity extends BaseActivity {
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.layout_nested_demo);
        setContentView(R.layout.layout_coordinator);

        final Button button = ((Button) findViewById(R.id.button1));
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(android.view.View v) {
                    if (times == 4) {
                        times = 0;
                    }
                    button.setTranslationY(100 * ++times);
                }
            });
        }

    }

}