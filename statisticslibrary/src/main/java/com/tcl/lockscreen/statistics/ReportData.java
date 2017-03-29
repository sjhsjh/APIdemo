package com.tcl.lockscreen.statistics;

/**
 * 记录所有统计事件的字段
 * 命名规则：
 * 1、键：以"KEY_"开头
 * 2、值：以"VALUE_"开头
 * 3、事件：以"EVENT_"开头
 * 4、页面：以"PAGE_"开头
 * Created by jinhui.shao on 2016/12/27.
 */
public class ReportData {
    public static String KEY_ID_UNLOCK = "Unlock";

    // 记录用户每天亮屏次数
    public static String KEY_EVENT_SCREENON = "ScrrenOn";

    // 使用锁屏的总停留
    public static String PAGE_LOCKSCREEN = "page_lockscreen";

    // 记录每天单次使用锁屏的时间及用户分布
    public static String EVENT_KEYGUARD_INTERVAL = "event_keyguard_interval";
    public static String KEY_KEYGUARD_INTERVAL = "keyguard_interval_value";

    // 记录锁屏状态下充电
    public static String EVENT_LOCK_CHARGING = "lock_charging";


    public static String EVENT_UNLOCK = "event_unlock";
    public static String KEY_UNLOCK_TYPE = "key_unlock_type";
    public static String VALUE_UNLOCK_NONE = "value_unlock_none";
    public static String VALUE_UNLOCK_SLIDE = "value_unlock_slide";
    public static String VALUE_UNLOCK_PATTERN = "value_unlock_pattern";
    public static String VALUE_UNLOCK_PIN = "value_unlock_pin";
    public static String VALUE_UNLOCK_PASSWORD = "value_unlock_password";
    public static String VALUE_UNLOCK_FINGERPRINTS = "value_unlock_fingerprints";

    public static String EVENT_BLOCK_NOTIFICATIONS = "event_block_notifications";
    public static String EVENT_CLEAR_ALL_NOTIFICATIONS = "event_clear_all_notifications";

    public static String EVENT_KEYGUARD_ENTER_SHADE_LOCK = "event_keyguard_enter_shade_lock";
    public static String EVENT_KEYGUARD_DROP_DOWN_QS = "event_keyguard_drop_down_qs";
    public static String EVENT_KEYGUARD_DROP_DOWN_NOTIFICATION_ALL = "event_keyguard_drop_down_notification_all";
    public static String EVENT_ALL_DROP_DOWN_NOTIFICATION = "event_all_drop_down_notification";
    public static String PAGE_QS_DROP_DOWN_TIME = "page_qs_drop_down_time";
    public static String PAGE_ENTER_SHADE_LOCK_TIME = "page_enter_shade_lock_time";


}