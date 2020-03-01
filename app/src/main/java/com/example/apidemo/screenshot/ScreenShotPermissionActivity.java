package com.example.apidemo.screenshot;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * 申请截屏权限的空白透明activity
 */
public class ScreenShotPermissionActivity extends FragmentActivity {
    public static final int REQUEST_MEDIA_PROJECTION = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCapturePermission();
    }

    public void requestCapturePermission() {
        // 5.0 之后才允许使用屏幕截图
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    FloatWindowsService.setResultData(data);
                    startService(new Intent(getApplicationContext(), FloatWindowsService.class));
                } else {
                    Toast.makeText(this, "截屏失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        finish();
    }

}
