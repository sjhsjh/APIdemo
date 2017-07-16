package com.example.apidemo;

import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
public class Son implements IGSON{
    private int a = 5;
    @Override
    public void add() {
        a++;
        NLog.d("sjh0", "Son");
    }

}
