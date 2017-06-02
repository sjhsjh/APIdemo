package com.example.apidemo.activity;

import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.FlashLightManager;
import com.example.apidemo.utils.NLog;

/**
 * <br>
 * Created by jinhui.shao on 2017/6/1.
 */
public class HardWareActivity extends BaseActivity{
    private FlashLightManager mFlashLightManager;
    private Button button;
    private boolean mIsFlahsLightOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        button = ((Button)findViewById(R.id.button1));

        button.setText(R.string.open_flashlight);
        button.setBackgroundColor(getResources().getColor(R.color.white));

        mFlashLightManager = new FlashLightManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFlashLightManager.registerFlashLightCallBack(new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeChanged(String cameraId, boolean enabled) {  // 注册的时候就每个摄像头会被调用，因此可用于获取闪光灯的当前开启状态.
                    super.onTorchModeChanged(cameraId, enabled);
                    NLog.i("onTorchModeChanged enabled = " + enabled + " cameraId = " + cameraId);// 后置cameraId为0，前置cameraId为1.
                    statusChange(enabled);
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = mFlashLightManager.setFlashLightStatus(!mIsFlahsLightOpen);
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M && success){
                    statusChange(!mIsFlahsLightOpen);
                }

            }
        });

    }

    private void statusChange(boolean enabled){
        mIsFlahsLightOpen = enabled;
        if(enabled){
            button.setText(R.string.close_flashlight);
            button.setBackgroundColor(getResources().getColor(R.color.flashlight_color));

        }
        else {
            button.setText(R.string.open_flashlight);
            button.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlashLightManager.unRegisterFlashLightCallBack();
    }


}