package com.example.apidemo.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.NLog;

/**
 * 自动点击
 */
public class ClickAccessibilityService extends BaseAccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        }
        if ("com.example.apidemo".equals(event.getPackageName().toString())) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                // rootNode的className都是FrameLayout，全屏幕的，viewIdResName也为null；但是它的儿子却不包含状态栏和导航栏，而是应用区域里的一些可交互view（实测）。
                // 这些交互view覆盖了应用区域。
                // 各节点也只能获得一部分的child，并且它们的viewIdResName可能null可能非null。
                // getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();

                Rect rect = new Rect();
                rootNode.getBoundsInScreen(rect);
                NLog.i("sjh2", "==rect : " + rect);
                // NLog.i("sjh2", "==getViewIdResourceName : " + rootNode.getChild(0).getViewIdResourceName());
            }
        }

        // // 一旦回到桌面就打开电话
        // if (event.getPackageName().toString().contains("googlequicksearchbox") ||
        //         event.getPackageName().toString().contains("miui.home")) {
        //     if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        //         AccessibilityNodeInfo settingNode = findViewByText("电话");
        //         if (settingNode != null) {
        //             performViewClick(settingNode);
        //         }
        //     }
        // }

        // 自动滚动view
        // if ("com.example.apidemo".equals(event.getPackageName().toString())) {
        //     if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        //         AccessibilityNodeInfo node = findViewByText("Activity");
        //         performScrollForward(node);
        //     }
        // }

        // 自动下拉通知栏，贼牛逼！
        // performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);


    }
}
