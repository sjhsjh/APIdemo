package com.example.apidemo.accessibility;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;

/**
 * 免Root自动安装
 * Created by xuyisheng on 16/12/10.
 */

public class AutoInstallAccessibilityService extends BaseAccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        if(event.getPackageName() == null){
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                (event.getPackageName().equals("com.android.packageinstaller")
                || event.getPackageName().equals("com.google.android.packageinstaller"))) {
            AccessibilityNodeInfo nodeInfo = findViewByText("安装", true);    // 注意多语言
            if (nodeInfo != null) {
                performViewClick(nodeInfo);
            }
        }
    }
}
