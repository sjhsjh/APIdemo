package com.example.apidemo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.fragment.BottomFragment;
import com.example.apidemo.fragment.TopFragment;

/**
 * Created by Administrator on 2018/9/4 0004.
 */

public class ViewModelActivity extends FragmentActivity {   // ViewModelProviders内使用了FragmentActivity和v4的fragment，因此此处只能用FragmentActivity。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewmodel);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new TopFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, new BottomFragment()).commit();

    }


}
