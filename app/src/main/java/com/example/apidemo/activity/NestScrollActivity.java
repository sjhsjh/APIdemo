package com.example.apidemo.activity;

import android.os.Bundle;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;

/**
 * @date 2020/1/9
 */
public class NestScrollActivity  extends BaseActivity {
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nested_demo);
        mButton = ((Button)findViewById(R.id.button1));
    }

}