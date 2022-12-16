package com.example.apidemo.accessibility;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;

/**
 * 进入到设置应用详情页面就自动返回，防卸载
 * Created by xuyisheng on 16/12/10
 */

public class SelfDefenseAccessibilityService extends BaseAccessibilityService {

    private String mDefenseName = "微信";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        if(event.getPackageName() == null){
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.android.settings")) {
            CharSequence className = event.getClassName();
            if (className.equals("com.android.settings.SubSettings")) {
                AccessibilityNodeInfo nodeInfo = findViewByText("应用程序信息");  // nexeus 应用程信息
                if (nodeInfo != null && findViewByText(mDefenseName) != null) {
                    performBackClick();
                }
            }
        }
    }
}
