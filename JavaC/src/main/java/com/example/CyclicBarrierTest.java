package com.example;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    // 当打破屏障后，会自动重置，getNumberWaiting变回0，不需要手动调用它的reset方法

    public static void main(String[] args) {
        // 这种方法，线程A不是固定输出“A”的，是另一题面试题。
        logByBarrier();
    }


    private static int sharedCounter = 0;
    private static final int times = 5;
    // 打印的内容
    private static final String printString = "ABC";

    /**
     * CountDownLatch 基于 AQS 的共享模式的使用，  而 CyclicBarrier 基于 Condition 来实现的。因为 CyclicBarrier 的源码相对来说简单许多
     * CountDownLatch 是一次性的，        CyclicBarrier 是可循环利用的。
     * CountDownLatch 参与的线程的职责是不一样的，有的在倒计时，有的在等待倒计时结束。CyclicBarrier 参与的线程职责是一样的
     * CountDownLatch 是等待多个线程都完成之后，自己才继续执行！CyclicBarrier是等待多个线程都完成之后，大家一起继续执行！
     */
    private static void logByBarrier() {
        // 定义循环栅栏
        // parties 是参与线程的个数，即要执行 parties次《cyclicBarrier.await() 》
        // 第二个参数的意思是  最后一个线程到达栅栏时 执行！！（即brarrier. last await()）
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            // 单个循环 最后一次await内部
            System.out.println("===单个循环结束===" );
        });

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < times; i++) {
                    synchronized (this) {
                        sharedCounter = sharedCounter > 2 ? 0 : sharedCounter; // 循环打印. 值是0》1》2循环
                        System.out.println("===" + printString.toCharArray()[sharedCounter++]);
                    }
                    // 返回当前在屏障处等待的参与者数目！！！！！
                    // System.out.println("getNumberWaiting=" + cyclicBarrier.getNumberWaiting());

                    try {
                        // 等待 3 个线程都打印一遍之后，继续走下一轮的打印
                        // 代表当前线程 已经到达栅栏！！
                        cyclicBarrier.await();      // 即是 getNumberWaiting的值加1 & 等待!!!


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // 开启多个线程. 注意是不同的Thread  和 同一个runnable对象！！
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}


        // System.out.println("111");
        // cyclicBarrier.await();
        // cyclicBarrier.await();     // 需要在不同线程执行await才行。
        // cyclicBarrier.await();
        //
        // System.out.println("222");