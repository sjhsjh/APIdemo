package com.example.apidemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import com.tcl.lockscreen.statistics.StatisticsWrapper;

/**
 * Created  on 2017/3/29.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsWrapper.getInstance().onResume(this);
        StatisticsWrapper.getInstance().onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatisticsWrapper.getInstance().onPageEnd(this.getClass().getSimpleName());
        StatisticsWrapper.getInstance().onPause(this);
    }

    /**
     * 效果是该activity显示在锁屏之上。若跳转其他acitivty则会弹出锁屏。
     * 注意：要在setContentView之前设置，否则不生效。
     */
    protected void setDismissKeyguard(boolean isDismissKeyguard) {
        if (isDismissKeyguard) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
    }
}