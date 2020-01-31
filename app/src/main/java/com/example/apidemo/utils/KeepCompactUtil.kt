package com.example.apidemo.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils

/**
 * 手机厂商的自启动和防睡眠开关页面
 * from: http://lastwarmth.win/2019/09/06/keepalive/
 */
object KeepCompactUtil {
    private val TAG = "KeepCompactUtil"

    private val AUTO_START_INTENTS = arrayOf(
        // 小米
        Intent().setComponent(ComponentName("com.miui.securitycenter",
            "com.miui.permcenter.autostart.AutoStartManagementActivity")),

        Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),

        // 华为
        Intent().setComponent(ComponentName
            .unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity")),

        // 三星
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.autorun.ui.AutoRunActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.ram.AutoRunActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.appmanagement.AppManagementActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.autorun.ui.AutoRunActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.ram.AutoRunActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.appmanagement.AppManagementActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity")),
        Intent().setComponent(ComponentName.unflattenFromString(
            "com.samsung.android.sm_cn/.app.dashboard.SmartManagerDashBoardActivity")),
        Intent().setComponent(ComponentName.unflattenFromString(
            "com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity")),

        // oppo
        Intent().setComponent(ComponentName
            .unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.coloros.safecenter/.permission.startupapp.StartupAppListActivity")),
        Intent().setComponent(ComponentName("com.coloros.safecenter",
            "com.coloros.privacypermissionsentry.PermissionTopActivity")),
        Intent().setComponent(
            ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity")),

        // vivo
        Intent().setComponent(ComponentName
            .unflattenFromString("com.vivo.permissionmanager/.activity.BgStartUpManagerActivity")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.iqoo.secure/.phoneoptimize.BgStartUpManager")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.vivo.permissionmanager/.activity.PurviewTabActivity")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.SoftwareManagerActivity")),

        // 魅族
        Intent().setComponent(ComponentName.unflattenFromString("com.meizu.safe/.SecurityCenterActivity")),

        // 一加
        Intent().setComponent(ComponentName
            .unflattenFromString("com.oneplus.security/.chainlaunch.view.ChainLaunchAppListActivity")),

        // 乐视
        Intent().setComponent(
            ComponentName.unflattenFromString("com.letv.android.letvsafe/.AutobootManageActivity")),

        // HTC
        Intent().setComponent(
            ComponentName.unflattenFromString("com.htc.pitroad/.landingpage.activity.LandingPageActivity"))
    )

    private val BATTERY_INTENTS = arrayOf(
        // 小米
        Intent().setComponent(ComponentName
            .unflattenFromString("com.miui.powerkeeper/.ui.HiddenAppsContainerManagementActivity")),

        Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST").addCategory(Intent.CATEGORY_DEFAULT),

        // 华为
        Intent().setComponent(ComponentName
            .unflattenFromString("com.huawei.systemmanager/.power.ui.HwPowerManagerActivity")),

        // 三星
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.battery.AppSleepListActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.battery.BatteryActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.battery.AppSleepListActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.battery.BatteryActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.lool",
            "com.samsung.android.sm.battery.ui.BatteryActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.lool",
            "com.samsung.android.sm.ui.battery.BatteryActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm",
            "com.samsung.android.sm.ui.battery.BatteryActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.sm_cn",
            "com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity")),

        // oppo
        Intent().setComponent(ComponentName
            .unflattenFromString("com.coloros.safecenter/.appfrozen.activity.AppFrozenSettingsActivity")),
        Intent().setComponent(ComponentName("com.coloros.oppoguardelf",
            "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
        Intent().setComponent(ComponentName("com.coloros.oppoguardelf",
            "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
        Intent().setComponent(ComponentName("com.coloros.oppoguardelf",
            "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
        Intent().setComponent(ComponentName
            .unflattenFromString("com.oppo.safe/.SecureSafeMainActivity")),

        // vivo
        Intent().setComponent(ComponentName("com.vivo.abe",
            "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity")),
        Intent().setComponent(ComponentName.unflattenFromString("com.iqoo.powersaving/.PowerSavingManagerActivity")),

        // 魅族
        Intent().setComponent(ComponentName
            .unflattenFromString("com.meizu.safe/.SecurityCenterActivity"))
    )

   var brandAliveEnumList: List<BrandAliveEnum> = object : ArrayList<BrandAliveEnum>() {
        init {
            add(BrandAliveEnum.Huawei)
            add(BrandAliveEnum.Xiaomi)
            add(BrandAliveEnum.Oppo)
            add(BrandAliveEnum.Vivo)
            add(BrandAliveEnum.Samsung)
            add(BrandAliveEnum.Meizu)
            add(BrandAliveEnum.LeEco)
            add(BrandAliveEnum.Smartisan)
            add(BrandAliveEnum.Lenovo)
            add(BrandAliveEnum.NONE)
        }
    }

    /**
     * @return 是否为三星s9 型号的手机
     */
    val isSamsungS9: Boolean
        get() = ("samsung".equals(Build.BRAND, ignoreCase = true) && !TextUtils.isEmpty(Build.MODEL)
            && Build.MODEL.startsWith("SM-G9"))

    val deviceEnum: BrandAliveEnum
        get() {
            if ("Huawei".equals(Build.BRAND, ignoreCase = true) || "HONOR".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Huawei
            }
            if ("vivo".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Vivo
            }
            if ("OPPO".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Oppo
            }
            if ("Xiaomi".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Xiaomi
            }
            if ("Meizu".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Meizu
            }
            if ("samsung".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Samsung
            }
            if ("smartisan".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Smartisan
            }
            if ("LeEco".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.LeEco
            }
            if ("Lenovo".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Lenovo
            }
            if ("oneplus".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.Yijia
            }
            if ("Sony".equals(Build.MANUFACTURER, ignoreCase = true)) {
                return BrandAliveEnum.Sony
            }
            if ("LG".equals(Build.MANUFACTURER, ignoreCase = true)) {
                return BrandAliveEnum.LG
            }
            if ("Coolpad".equals(Build.BRAND, ignoreCase = true)) {
                return BrandAliveEnum.NONE
            }
            return if ("ZTE".equals(Build.BRAND, ignoreCase = true)) {
                BrandAliveEnum.NONE
            } else BrandAliveEnum.NONE
        }

    // 防睡眠(应用实际界面一般关于耗电)
    fun goNoSleepSettings(activity: Activity): Boolean {
        for (intent in BATTERY_INTENTS) {
            if (activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                NLog.w(TAG, "goNoSleepSettings intent-----" + intent)
                try {
                    activity.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    NLog.e(TAG, e.toString())
                    continue
                }
            }
        }
        return false
    }

    /**
     * 自启动。包含开机自启动和接收系统广播等方式启动，但是由A应用启动B应用则不受限制。
     */
    fun goAutoStartSettings(activity: Activity): Boolean {
        for (intent in AUTO_START_INTENTS) {
            if (activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                NLog.w(TAG, "goAutoStartSettings intent-----" + intent)
                try {
                    activity.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    NLog.e(TAG, e.toString())
                    continue
                }
            }
        }
        return false
    }

    enum class BrandAliveEnum() {
        NONE,
        Huawei,
        Vivo,
        Oppo,
        Xiaomi,
        Meizu,
        Samsung,
        Smartisan,
        LeEco,
        Lenovo,
        Yijia,
        Sony,
        LG;
    }
}