package com.yy.onepiece.debugmonitor


/**
 *
 * @author shaojinhui@yy.com
 * @date 2020/8/11
 */
abstract class AbstractMonitor {
    private var monitorSwitch = true                         // 是否开启监控

    fun setMonitorSwitch(monitorSwitch: Boolean) {
        this.monitorSwitch = monitorSwitch
    }

    fun isSwitchOpen() = monitorSwitch
}