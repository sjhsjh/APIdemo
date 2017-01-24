package com.example.apidemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 * Created by jinhui.shao on 2017/1/19.
 */
public class AndroidUtils {

    public static String debugLog(long receiveTime){
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(receiveTime);
        return simpledate.format(date);
    }



}