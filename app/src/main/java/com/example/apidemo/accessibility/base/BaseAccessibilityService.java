package com.example.apidemo.accessibility.base;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.utils.NLog;
import java.util.HashMap;
import java.util.List;


public class BaseAccessibilityService extends AccessibilityService {
    private AccessibilityManager mAccessibilityManager;
    private Context mContext;
    private static BaseAccessibilityService mInstance;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static BaseAccessibilityService getInstance() {
        if (mInstance == null) {
            mInstance = new BaseAccessibilityService();
        }
        return mInstance;
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    private boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模拟点击事件(遍历该view的可点击父view)
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                NLog.i("sjh2", "performViewClick ok");
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }

    /**
     * 模拟返回操作
     */
    public void performBackClick() {
        try {
            Thread.sleep(500);      // todo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
        NLog.i("sjh2", "performBackClick ok");
    }

    /**
     * 模拟下滑操作(滚动单屏距离)
     * @param scrollNode 处于滚动view中的节点
     */
    public void performScrollBackward(AccessibilityNodeInfo scrollNode) {
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);

        while (scrollNode != null) {
            if (scrollNode.isScrollable()) {
                scrollNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                NLog.i("sjh2", "performScrollBackward ok");
                break;
            }
            scrollNode = scrollNode.getParent();
        }
    }

    /**
     * 模拟上滑操作(滚动单屏距离)
     * @param scrollNode 处于滚动view中的节点
     */
    public void performScrollForward(AccessibilityNodeInfo scrollNode) {
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

        while (scrollNode != null) {
            if (scrollNode.isScrollable()) {
                scrollNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                NLog.i("sjh2", "performScrollForward ok");
                break;
            }
            scrollNode = scrollNode.getParent();
        }
    }

    /**
     * 查找对应文本的View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        // node的text为null，contentDescription含指定内容也会返回
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return AccessibilityNodeInfo
     */
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 返回带坐标的AccessibilityNodeInfo集合用于判断获取哪个view
     */
    public HashMap<Rect, AccessibilityNodeInfo> findViewByTextForRect(String text) {
        HashMap<Rect, AccessibilityNodeInfo> map = new HashMap<Rect, AccessibilityNodeInfo>();

        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    Rect rect = new Rect();
                    nodeInfo.getBoundsInScreen(rect);
                    map.put(rect, nodeInfo);
                }
            }
        }
        return map;
    }

    /**
     * 查找对应ID的View
     * @param viewID viewID 需要完整的ID，形如： com.android.chrome:id/url_bar
     * @return AccessibilityNodeInfo
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String viewID) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(viewID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 返回带坐标的AccessibilityNodeInfo集合用于判断获取哪个view
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public HashMap<Rect, AccessibilityNodeInfo> findViewByIDForRect(String viewID) {
        HashMap<Rect, AccessibilityNodeInfo> map = new HashMap<Rect, AccessibilityNodeInfo>();

        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(viewID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                map.put(rect, nodeInfo);
            }
        }
        return map;
    }

    public void clickTextViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickTextViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 模拟输入。
     * 以下两种方法都可以。
     * @param nodeInfo nodeInfo
     * @param text text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {                 // 会将EditText在不获取焦点时也直接setText
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {   // EditText下能自动获取焦点并粘贴，Best！！
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    public void requestFocusAndInputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
