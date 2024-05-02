package com.tencent.matrix.resource.analyzer.onepiece.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * Created by  on 14/6/5.
 */
public class TimeUtils {

    public static final long MONTHS_OF_YEAR = 12;
    public static final long DAYS_OF_YEAR = 365;
    public static final long DAYS_OF_MONTH = 30;
    public static final long HOURS_OF_DAY = 24;
    public static final long MINUTES_OF_HOUR = 60;
    public static final long SECONDS_OF_MINUTE = 60;
    public static final long MILLIS_OF_SECOND = 1000;

    public static class YEARS {
        public static long toMillis(long years) {
            return checkOverflow(years, DAYS_OF_YEAR * HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE *
                    MILLIS_OF_SECOND);
        }

        public static long toSeconds(long years) {
            return checkOverflow(years, DAYS_OF_YEAR * HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE);
        }

        public static long toMinutes(long years) {
            return checkOverflow(years, DAYS_OF_YEAR * HOURS_OF_DAY * MINUTES_OF_HOUR);
        }

        public static long toHours(long years) {
            return checkOverflow(years, DAYS_OF_YEAR * HOURS_OF_DAY);
        }

        public static long toDays(long years) {
            return checkOverflow(years, DAYS_OF_YEAR);
        }

        public static long toMonths(long years) {
            return checkOverflow(years, MONTHS_OF_YEAR);
        }
    }

    public static class MONTHS {
        public static long toMillis(long months) {
            return checkOverflow(months, DAYS_OF_MONTH * HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE *
                    MILLIS_OF_SECOND);
        }

        public static long toSeconds(long months) {
            return checkOverflow(months, DAYS_OF_MONTH * HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE);
        }

        public static long toMinutes(long months) {
            return checkOverflow(months, DAYS_OF_MONTH * HOURS_OF_DAY * MINUTES_OF_HOUR);
        }

        public static long toHours(long months) {
            return checkOverflow(months, DAYS_OF_MONTH * HOURS_OF_DAY);
        }

        public static long toDays(long months) {
            return checkOverflow(months, DAYS_OF_MONTH);
        }

        public static long toYears(long months) {
            return months / MONTHS_OF_YEAR;
        }
    }

    public static class DAYS {
        public static long toMillis(long days) {
            return checkOverflow(days, HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE * MILLIS_OF_SECOND);
        }

        public static long toSeconds(long days) {
            return checkOverflow(days, HOURS_OF_DAY * MINUTES_OF_HOUR * SECONDS_OF_MINUTE);
        }

        public static long toMinutes(long days) {
            return checkOverflow(days, HOURS_OF_DAY * MINUTES_OF_HOUR);
        }

        public static long toHours(long days) {
            return checkOverflow(days, HOURS_OF_DAY);
        }

        public static long toMonths(long days) {
            return days / DAYS_OF_MONTH;
        }

        public static long toYears(long days) {
            return days / DAYS_OF_YEAR;
        }
    }

    public static class HOURS {
        public static long toMillis(long hours) {
            return checkOverflow(hours, MINUTES_OF_HOUR * SECONDS_OF_MINUTE * MILLIS_OF_SECOND);
        }

        public static long toSeconds(long hours) {
            return checkOverflow(hours, MINUTES_OF_HOUR * SECONDS_OF_MINUTE);
        }

        public static long toMinutes(long hours) {
            return checkOverflow(hours, MINUTES_OF_HOUR);
        }

        public static long toDays(long hours) {
            return hours / HOURS_OF_DAY;
        }

        public static long toMonths(long hours) {
            return toDays(hours) / DAYS_OF_MONTH;
        }

        public static long toYears(long hours) {
            return toDays(hours) / DAYS_OF_YEAR;
        }
    }

    public static class MINUTES {
        public static long toMillis(long minutes) {
            return checkOverflow(minutes, SECONDS_OF_MINUTE * MILLIS_OF_SECOND);
        }

        public static long toSeconds(long minutes) {
            return checkOverflow(minutes, SECONDS_OF_MINUTE);
        }

        public static long toHours(long minutes) {
            return minutes / MINUTES_OF_HOUR;
        }

        public static long toDays(long minutes) {
            return toHours(minutes) / HOURS_OF_DAY;
        }

        public static long toMonths(long minutes) {
            return toDays(minutes) / DAYS_OF_MONTH;
        }

        public static long toYears(long minutes) {
            return toDays(minutes) / DAYS_OF_YEAR;
        }
    }

    public static class SECONDS {
        public static long toMillis(long seconds) {
            return checkOverflow(seconds, MILLIS_OF_SECOND);
        }

        public static long toMinutes(long seconds) {
            return seconds / SECONDS_OF_MINUTE;
        }

        public static long toHours(long seconds) {
            return toMinutes(seconds) / MINUTES_OF_HOUR;
        }

        public static long toDays(long seconds) {
            return toHours(seconds) / HOURS_OF_DAY;
        }

        public static long toMonths(long seconds) {
            return toDays(seconds) / DAYS_OF_MONTH;
        }

        public static long toYears(long seconds) {
            return toDays(seconds) / DAYS_OF_YEAR;
        }
    }

    public static class MILLIS {
        public static long toSeconds(long millis) {
            return millis / MILLIS_OF_SECOND;
        }

        public static long toMinutes(long millis) {
            return toSeconds(millis) / SECONDS_OF_MINUTE;
        }

        public static long toHours(long millis) {
            return toMinutes(millis) / MINUTES_OF_HOUR;
        }

        public static long toDays(long millis) {
            return toHours(millis) / HOURS_OF_DAY;
        }

        public static long toMonths(long millis) {
            return toDays(millis) / DAYS_OF_MONTH;
        }

        public static long toYears(long millis) {
            return toDays(millis) / DAYS_OF_YEAR;
        }
    }

    public static boolean isSameDay(long millis1, long millis2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(millis1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(millis2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameWeek(long millis1, long millis2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(millis1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(millis2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isSameYear(long millis1, long millis2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(millis1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(millis2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    public static boolean isSameMon(long mills1, long mills2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(mills1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(mills2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }

    public static int getYear(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.YEAR);
    }

    public static int getMonth(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.MONTH) + 1;
    }

    public static int getDayOfMonth(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * @param millis time in millis
     * @return hour of 12-hour clock
     */
    public static int getHourOf12HClock(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.HOUR);
    }

    /**
     * @param millis time in millis
     * @return hour of 24-hour clock
     */
    public static int getHourOf24HClock(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.MINUTE);
    }

    public static int getSecond(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.SECOND);
    }

    /**
     * Get expired dead time, in millis.
     *
     * @param millisToExpire millis from now to the dead time.
     * @return Expired dead time, in millis, it is in UTC(GMT).
     */
    public static long getExpireDeadTime(long millisToExpire) {
        return System.currentTimeMillis() + millisToExpire;
    }

    /**
     * @param timeMillis time in millis.
     * @return format time string , like "2014-06-06 16:19:15"
     */
    public static String getTimeStringFromMillis(long timeMillis) {
        String format = "%04d-%02d-%02d %02d:%02d:%2d";
        return getTimeStringFromMillis(timeMillis, format);
    }

    /**
     * @param timeMillis time in millis
     * @param format     format string, must contain 6 args: year, month, day, hour, minute, second
     * @return format time string
     */
    public static String getTimeStringFromMillis(long timeMillis, String format) {
        if (format == null || format.length() == 0) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        String timeString = null;
        try {
            timeString = String.format(format, year, month, day, hour, min, sec);
        } catch (IllegalFormatException e) {
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 将毫秒时间转换为指定的字符串格式,例如传入format“year-mon-day”,返回类似“2014-08-29”
     *
     * @param timeMillis
     * @param format     year,mon,day,hour,min,sec
     * @return 指定时间格式
     */
    public static String getFormatTimeString(long timeMillis, String format) {
        if (format == null || format.length() == 0) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        String timeString = null;
        try {
            //            timeString = String.format(format, year, month, day, hour, min, sec);
            timeString = format.replaceAll("year", String.valueOf(year))
                    .replaceAll("mon", month < 10 ? "0" + month : "" + month)
                    .replaceAll("day", day < 10 ? "0" + day : "" + day)
                    .replaceAll("hour", hour < 10 ? "0" + hour : "" + hour)
                    .replaceAll("min", min < 10 ? "0" + min : "" + min)
                    .replaceAll("sec", sec < 10 ? "0" + sec : "" + sec);
        } catch (Exception e) {
            // MLog.error("TimeUtils", "getFormatTimeString error! " + e.toString());
        }
        return timeString;
    }

    /**
     * t multiply scale, check overflow
     *
     * @param t     t
     * @param scale scale
     * @return t * scale if not overflow, else return Long.MAX_VALUE or Long.MIN_VALUE
     */
    private static long checkOverflow(long t, long scale) {
        if (t > Long.MAX_VALUE / scale) {
            return Long.MAX_VALUE;
        }
        if (t < Long.MIN_VALUE / scale) {
            return Long.MIN_VALUE;
        }
        return t * scale;
    }

    /**
     * 转换时间日期格式字串为long型
     *
     * @param time 格式为：yyyy-MM-dd HH:mm:ss的时间日期类型
     */
    public static Long convertTimeToLong(String time) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
            // MLog.error("TimeUtils", e);
            return 0L;
        }
    }

    /*
    获得某月天数
     */
    public static int getMaxDayInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getActualMaximum(Calendar.DATE);
    }

    /*
     *获得日期时间戳
     */
    public static long getDateTimeStamp(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, day);
        return cal.getTimeInMillis();
    }

    /**
     * 获得年月时间戳
     */
    public static long getMonthTimeStamp(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getTimeInMillis();
    }

    /**
     * 获得时间间隔文本
     */
    public static String getDurationString(long millis) {
        long second = millis / MILLIS_OF_SECOND % SECONDS_OF_MINUTE;
        long min = millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE % MINUTES_OF_HOUR;
        long hour = millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE / MINUTES_OF_HOUR;
        return (hour == 0 ? "" : (hour + "小时")) + (min == 0 ? "" : (min + "分"))
                + (second == 0 ? "" : (second + "秒"));
    }

    /**
     * 获得时间间隔 “xx:xx:xx”
     */
    public static String convertDurationString(long millis) {
        int second = (int) (millis / MILLIS_OF_SECOND % SECONDS_OF_MINUTE);
        int min = (int) (millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE % MINUTES_OF_HOUR);
        int hour = (int) (millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE / MINUTES_OF_HOUR);
        String hourString = hour + "";
        if (hour < 10) {
            hourString = "0" + hour;
        }
        return String.format(Locale.getDefault(), "%s:%02d:%02d", hourString, min, second);
    }

    /**
     * 获得时间长度 “xx:xx”
     */
    public static String convertDurationToText(long millis) {
        int second = (int) (millis / MILLIS_OF_SECOND % SECONDS_OF_MINUTE);
        int min = (int) (millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE);
        String minString = min + "";
        if (min < 10) {
            minString = "0" + min;
        }
        return String.format(Locale.getDefault(), "%s:%02d", minString, second);
    }

    public static String getFlowDurationString(long millis) {
        long second = millis / MILLIS_OF_SECOND % SECONDS_OF_MINUTE;
        long min = millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE % MINUTES_OF_HOUR;
        long hour = millis / MILLIS_OF_SECOND / SECONDS_OF_MINUTE / MINUTES_OF_HOUR;
        return (hour == 0 ? "" : (hour > 24 ? "24+小时" : hour + "小时")) + (min == 0 ? "" : (min + "分"))
                + (second == 0 ? "" : (second + "秒"));
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * from 开始时间
     * to 结束时间
     *
     * @return
     */
    public static long differentDaysByMillisecond(long from, long to) {
        int days = (int) ((to - from) / (1000 * 3600 * 24));
        return days;
    }

    /***
     *  判断两个时间相差分钟数
     * @param from
     * @param to
     * @return
     */
    public static long differentTimeByMillisecond(long from, long to) {
        return (to - from) / 1000 / 60 / 60;
    }
}
