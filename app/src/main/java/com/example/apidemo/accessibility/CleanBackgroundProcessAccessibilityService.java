package com.example.apidemo.accessibility;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;

/**
 * 自动点击确定"强行停止"
 */
public class CleanBackgroundProcessAccessibilityService extends BaseAccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getPackageName() == null){
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.android.settings")) {
            CharSequence className = event.getClassName();
            if (className.equals("com.android.settings.applications.InstalledAppDetailsTop")) { // 设置里“应用信息”详情页面
                AccessibilityNodeInfo info = findViewByText("强行停止", true);
                if (info == null) {
                    info = findViewByText("确定");
                }
                if (info != null && info.isEnabled()) {
                    performViewClick(info);
                } else {
                    performBackClick();
                }
            }
            if (className.equals("android.app.AlertDialog")) {
                clickTextViewByText("确定");
                performBackClick();
            }
        }
    }
}
