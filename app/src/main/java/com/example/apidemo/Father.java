package com.example.apidemo;

import com.example.apidemo.utils.NLog;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
public class Father implements IGSON{

    @Override
    public void add() {
        NLog.d("sjh0", "Father");
    }

}
