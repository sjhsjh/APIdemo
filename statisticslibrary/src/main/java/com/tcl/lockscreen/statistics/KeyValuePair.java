package com.tcl.lockscreen.statistics;

import android.text.TextUtils;

/**
 * <br> 生产map所需要的键值对.
 * Created by jinhui.shao on 2016/12/28.
 */
public class KeyValuePair {
    private String mKey;
    private String mValue;

    private KeyValuePair(String key, String value){
        mKey = key;
        mValue = value;
    }

    public static KeyValuePair produce(String key, String value){
        if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
            return  new KeyValuePair(key, value);
        }
        else {
            return null;
        }
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}