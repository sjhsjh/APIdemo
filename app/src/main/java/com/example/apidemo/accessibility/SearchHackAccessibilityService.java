package com.example.apidemo.accessibility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.example.apidemo.accessibility.base.BaseAccessibilityService;
import com.example.apidemo.utils.NLog;
import java.util.HashMap;
import java.util.Map;

/**
 * 浏览器劫持，监控浏览器一旦打开就输入指定的网址，或者自动输入内容并搜索
 */
public class SearchHackAccessibilityService extends BaseAccessibilityService {
    private String mPackageName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getPackageName() == null){
            return;
        }
        mPackageName = event.getPackageName().toString();
        if ("com.android.chrome".equals(mPackageName)) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                // AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                // goThrough(rootNode);

                autoSearch();
            }
        }
    }

    private void autoSearch() {
        AccessibilityNodeInfo editTextNode = findViewByID("com.android.chrome:id/url_bar");
        NLog.d("sjh2", "editTextNode : " + editTextNode);
        if (editTextNode != null && "android.widget.EditText".equals(editTextNode.getClassName())) {
            AccessibilityNodeInfo parent = editTextNode;
            while (parent != null) {
                if (parent.isClickable()) {

                    // editText获取焦点并输入内容
                    requestFocusAndInputText(parent, "omg");
                    NLog.d("sjh2", "====requestFocusAndInputText===");

                    // 点击联想结果item
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clickSpecificView();
                        }
                    }, 1000);   // 先等待请求联想结果

                    break;
                }
                parent = parent.getParent();
            }
        }
    }

    private void clickSpecificView() {
        HashMap<Rect, AccessibilityNodeInfo> map = findViewByTextForRect("omg");
        NLog.d("sjh2", "match map : " + map);

        if (map != null && !map.isEmpty()) {
            Rect minRect = null;
            for (Map.Entry<Rect, AccessibilityNodeInfo> entry : map.entrySet()) {
                if ("android.widget.TextView".equals(entry.getValue().getClassName())) {
                    if (minRect == null || entry.getKey().top < minRect.top) {
                        minRect = entry.getKey();
                    }
                }
            }
            if (minRect != null) {
                AccessibilityNodeInfo clickNode = map.get(minRect);
                // clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);   // 该TextView的clickable为false！
                performViewClick(clickNode);
                NLog.w("sjh2", "clickNode :" + clickNode);
            }
        }
    }

    /**
     *  遍历每一个叶节点
     * @param info 根节点！！
     * @return
     */
    private boolean goThrough(AccessibilityNodeInfo info) {
        if (info == null) {
            return false;
        }
        if (info.getChildCount() == 0) {
            // 对每个叶节点的处理：
            if (info.getText() != null && info.getText().toString().contains("搜索")) {
                if ("在沪江网校中搜索".equals(info.getText().toString()) && "android.widget.TextView".equals(info.getClassName())) {
                    AccessibilityNodeInfo parent = info;
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                } else if ("输入关键字搜索".equals(info.getText().toString()) && "android.widget.EditText".equals(info.getClassName())) {
                    // 需要EditText先click获取焦点，否则无效
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("paste", "雅思英语");
                    clipboardManager.setPrimaryClip(clipData);
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } else if ("搜索".equals(info.getText().toString()) && "android.widget.TextView".equals(info.getClassName())) {
                    AccessibilityNodeInfo parent = info;
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                    return true;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    goThrough(info.getChild(i));
                }
            }
        }
        return false;
    }
}
