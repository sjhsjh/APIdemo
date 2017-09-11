package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.view.MyView;

/**
 * <br>
 * Created by jinhui.shao on 2017/9/8.
 */
public class ScrollActivity extends BaseActivity{
    private Button mButton;
    private MyView mMyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);
        mMyView = ((MyView)findViewById(R.id.my_view));
        mButton = ((Button)findViewById(R.id.button1));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyView.smoothScrollBy(-200, 0);
            }
        });
    }

}