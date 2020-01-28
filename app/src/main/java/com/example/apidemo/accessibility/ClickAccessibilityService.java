package com.example.apidemo.accessibility;

import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.APIDemoApplication;
import com.example.apidemo.utils.HardWareUtils;
import com.example.apidemo.activity.AutoClickActivity;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.NLog;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动点击
 * 可增加位置偏移来反检测
 */
public class ClickAccessibilityService extends BaseAccessibilityService {
    public static final String SERVICE_ID = "com.example.apidemo/.accessibility.ClickAccessibilityService";
    private static final String PKG_WEIXIN = "com.tencent.mm";
    private static final String PKG_QQ = "com.tencent.mobileqq";
    private static final String PKG_API_DEMO = "com.example.apidemo";
    private static final String PKG_BROWSER = "com.android.browser";
    private static final String PKG_MI_LAUNCHER = "com.miui.home";

    /**
     * @param event
     * EventType: TYPE_WINDOW_STATE_CHANGED; EventTime: 9763348; PackageName: com.example.apidemo; MovementGranularity: 0;
     * Action: 0 [ ClassName: com.example.apidemo.activity.AutoClickActivity; Text: [API-DEMO]; ContentDescription: null;
     * ItemCount: -1; CurrentItemIndex: -1; IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true; Scrollable: false;
     * BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX: -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1;
     * RemovedCount: -1; ParcelableData: null ]; recordCount: 0
     *
     * TYPE_WINDOW_STATE_CHANGED的className一般是activity的class，但也有LinearLayout的；
     * 而TYPE_WINDOW_CONTENT_CHANGED的className则全部是FrameLayout等；
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        }
        // if (PKG_WEIXIN.equals(event.getPackageName().toString())) {     // for test
        //     NLog.v("sjh2", "==receive==event==" + event);
        // }

        // 无论是否锁屏，收到qq特定内容的消息就开启自动点击
        // if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
        //     if (PKG_QQ.equals(event.getPackageName().toString())) {
        //         NLog.v("sjh2", "==receive notification==event==" + event);
        //         // if(checkEventText(event, "qqq")){
        //         //
        //         // }
        //         // clickNotification(event);
        //         HardWareUtils.getInstance().wakeUpAndDisableKeyguard(APIDemoApplication.getContext());
        //         new Handler().postDelayed(new Runnable() {
        //             @Override
        //             public void run() {
        //                 performGlobalAction(GLOBAL_ACTION_HOME);    // 第一次点击home对屏幕重新上锁，没回到桌面。
        //                 try {
        //                     Thread.sleep(500);
        //                 } catch (InterruptedException e) {
        //                     e.printStackTrace();
        //                 }
        //                 performGlobalAction(GLOBAL_ACTION_HOME);
        //                 startClickOrMove(130, 1685, true);   // 左下角。MI: 170, 2135
        //             }
        //         }, 2000);   // 解锁需要时间
        //     }
        // }

        if (AutoClickActivity.enableAutoClick && PKG_API_DEMO.equals(event.getPackageName().toString()) // PKG_BROWSER、PKG_MI_LAUNCHER
                && event.getClassName() != null && AutoClickActivity.CLASSNAME.equals(event.getClassName().toString())) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                AutoClickActivity.enableAutoClick = false;
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

        // 查看node节点的Rect坐标
        // if (PKG_API_DEMO.equals(event.getPackageName().toString())) {
        //     if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        //         // rootNode的className都是FrameLayout，全屏幕的，viewIdResName也为null；但是它的儿子却不包含状态栏和导航栏，而是应用区域里的一些可交互view（实测）。
        //         // 这些交互view覆盖了应用区域。
        //         // 各节点也只能获得一部分的child，并且它们的viewIdResName可能null可能非null。
        //         // getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        //         AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        //
        //         Rect rect = new Rect();
        //         rootNode.getBoundsInScreen(rect);
        //         NLog.i("sjh2", "==rect : " + rect);
        //         // NLog.i("sjh2", "==getViewIdResourceName : " + rootNode.getChild(0).getViewIdResourceName());
        //     }
        // }

        // 最简单的抢红包
        // if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        //     if (PKG_WEIXIN.equals(event.getPackageName().toString())
        //             && event.getClassName() != null && "com.tencent.mm.ui.LauncherUI".equals(event.getClassName().toString()))  {
        //         NLog.v("sjh2", "==red packet==event==" + event);
        //         startClickOrMove(500, 1800, true);
        //         try {
        //             Thread.sleep(500);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         startClickOrMove(485, 1450, true);
        // }

        // 一旦回到桌面就打开电话
        // if (event.getPackageName().toString().contains("googlequicksearchbox") ||
        //         PKG_MI_LAUNCHER.equals(event.getPackageName().toString())) {
        //     if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        //         AccessibilityNodeInfo settingNode = findViewByText("电话");
        //         if (settingNode != null) {
        //             performViewClick(settingNode);
        //         }
        //     }
        // }

        // 自动滚动view
        // if (PKG_API_DEMO.equals(event.getPackageName().toString())) {
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
    public static boolean enable = true;
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

    /**
     * 检查通知里是否有指定内容
     */
    private boolean checkEventText(AccessibilityEvent event, String beCheckText) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                if (!TextUtils.isEmpty(content) && content.contains(beCheckText)) {
                    NLog.i("sjh2", "==notification content==" + content); // 君临天下: 2
                    return true;
                }
            }
        }
        return false;
    }

    private void clickNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            if (notification.tickerText != null) {
                String content = notification.tickerText.toString();
                NLog.d("sjh2", "==tickerText content==" + content);   // 君临天下: 8
            }
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NLog.v("sjh2", "==onStartCommand==flags==" + flags);    // 没调用
        return super.onStartCommand(intent, flags, startId);
    }
}
