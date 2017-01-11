package com.tcl.lockscreen.statistics;

import java.util.HashMap;

/**
 * <br> 生产统计api所需的形参参数map.
 * Created by jinhui.shao on 2016/12/28.
 */
public class MapFactory {

    public static HashMap<String, String> produce(KeyValuePair ... keyValuePairs){
        HashMap map = new HashMap<String, String>();
        for(KeyValuePair keyValuePair : keyValuePairs){
            if(keyValuePair != null){
                map.put(keyValuePair.getKey(), keyValuePair.getValue());
            }
        }
        return map;
    }

}