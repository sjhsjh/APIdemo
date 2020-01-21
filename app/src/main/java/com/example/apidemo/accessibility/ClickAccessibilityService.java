package com.example.apidemo.accessibility;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.NLog;
import java.util.ArrayList;

/**
 * 自动点击
 * 可增加位置偏移来反检测
 */
public class ClickAccessibilityService extends BaseAccessibilityService {
    public static final String SERVICE_ID = "com.example.apidemo/.accessibility.ClickAccessibilityService";

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

        // if (event.getPackageName().toString().contains("miui.home")) {
        // if ("com.android.browser".equals(event.getPackageName().toString())) {
        if ("com.example.apidemo".equals(event.getPackageName().toString())) {
            if (enable && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                // reset();
                addSimulateAction(new Runnable() {
                    @Override
                    public void run() {
                        performGlobalAction(GLOBAL_ACTION_HOME);
                        execNext();
                    }
                }).addSimulateAction(new Runnable() {
                    @Override
                    public void run() {
                        startClickOrMove(1060, 500, false);
                    }
                }).addSimulateAction(new Runnable() {
                    @Override
                    public void run() {
                        startClickOrMove(500, 500, false);
                    }
                }).addSimulateAction(new Runnable() {
                    @Override
                    public void run() {
                        startClickOrMove(160, 268, true);   // 点击最左上角的应用图标
                    }
                }).start();

            }
        }

        // 一旦回到桌面就打开电话
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

    private Handler mHandler = new Handler();
    private ArrayList<Runnable> list = new ArrayList<Runnable>();
    private float currentX = 500;
    private float currentY = 500;
    public static boolean enable = false;
    private boolean isRunning = false;

    private ClickAccessibilityService addSimulateAction(Runnable runnable) {
        if(!isRunning){
            list.add(runnable);
        }
        return this;
    }

    private void start() {
        NLog.i("sjh2", "==list : " + list.size());
        if (enable && !isRunning && !list.isEmpty()) {
            isRunning = true;
            Runnable runnable = list.get(0);
            list.remove(0);
            runnable.run();
        } else {
            reset();
        }
    }

    private void reset() {
        isRunning = false;
        list.clear();
        currentX = 500;
        currentY = 500;
        enable = false;
    }

    private void execNext() {
        if (enable && !list.isEmpty()) {
            Runnable runnable = list.get(0);
            if (runnable != null) {
                list.remove(0);
                mHandler.post(runnable);
            }
        } else {
            reset();
        }
    }
    /**
     * dispatchGesture必须加上android:canPerformGestures="true"才生效
     * @param isClick true：点击；false：滑动
     */
    private void startClickOrMove(final float x, final float y, boolean isClick) {
        if (Build.VERSION.SDK_INT >= 24) {
            Path path = new Path();
            if (isClick) {
                path.moveTo(x, y);  // 只有moveTo就是点击！！
            } else {
                path.moveTo(currentX, currentY);  // 只有moveTo就是点击！！
                path.lineTo(x, y);
            }

            // move的duration在300~500ms的效果较好，太短会导致滑动不可用；click的duration别人用10ms
            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 300, 200)).build();
            // 能点击到状态栏和应用区域，不能点到导航栏！！
            dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    NLog.d("sjh2", "==GestureResultCallback onCompleted==");
                    currentX = x;
                    currentY = y;
                    execNext();
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    NLog.w("sjh2", "==GestureResultCallback onCancelled==");
                    reset();
                }
            }, mHandler);
        }
    }
}
