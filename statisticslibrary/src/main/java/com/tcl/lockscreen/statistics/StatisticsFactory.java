package com.tcl.lockscreen.statistics;

/**
 * @author htoall
 * @Description:
 * @date 2016/12/28 下午4:27
 * @copyright TCL-MIE
 */
public class StatisticsFactory {
    protected static IStatistics produce(StatisticsKind statisticsKind) {
        switch (statisticsKind) {
            case STATISTICS_MIG:
                return new MIGStatistics();
            case STATISTICS_UMENG:
                return new UmengStatistics();
            default:
                return null;
        }
    }
}
