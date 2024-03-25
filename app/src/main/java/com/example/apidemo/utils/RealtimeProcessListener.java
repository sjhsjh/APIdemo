package com.shopline.pos2.checkout;

/**
 * @date 2024/3/21
 */
interface RealtimeProcessListener {

    void onNewStdoutListener(String str);

    void onNewStderrListener(String str);

    void onProcessFinish();

}
