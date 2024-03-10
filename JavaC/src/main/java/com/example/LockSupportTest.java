package com.example;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {

    /**
     * 使用方法：
     * park() + unpark(thread) 成对使用；
     * unpark(thread) +  park()直接返回；
     *
     *
     * 注意：LockSupport.park() 不释放锁！！！
     *    synchronized(lock){
     *       LockSupport.park();
     *    }
     *
     * LockSupport 的park和Object的wait一样也能响应中断。但park不会抛出中断异常。
     * https://www.jianshu.com/p/da76b6ab56be
     */
    public static void main(String[] args) {
        new Thread() {

            @Override
            public void run() {
                System.out.println("begin unpark 111");
                LockSupport.unpark(this);   // 发放“立刻被消耗”的证书 or 发放“待使用”的证书！！！！
                LockSupport.unpark(this);   // 可能被立刻消耗掉，也可能增加到一张，一张证书封顶。

                System.out.println("begin park 222");
                LockSupport.park();                // 无证则阻塞，有证则消耗掉 并 直接返回

                System.out.println("begin park 333");
                LockSupport.park();

                System.out.println("finish");
            }
        }.start();
    }

}
