package com.example;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @date 2024/3/8
 */
class SemaphoreTest {
    public static boolean result = true;

    // permits是初始"许可证数量"，表示可以"同时访问的线程数"。
    // 当permits = 0 时，acquire 和 release 的使用等价于 object.wait 和 object.notify。
    // 构造函数的入参 permits 允许为负数！！！直到执行N次release且 "许可证数量"大于0 时，acquire才允许继续执行。

    public static void main(String[] args) {
        //4表示4张桌子
        Semaphore semaphore = new Semaphore(-1);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    if (result) {
                        result = false;
                        semaphore.release();
                        semaphore.release();
                    }


                    //1.被服务员叫号  分发许可证
                    semaphore.acquire();
                    //2.开始吃饭
                    System.out.println(Thread.currentThread().getName() + "被服务员叫号后，准备上桌吃饭");
                    //3.吃饭中  模拟吃饭2s
                    TimeUnit.SECONDS.sleep(5);
                    //4.吃完饭,喊服务员结算
                    semaphore.release();

                    System.out.println(Thread.currentThread().getName() + "喊服务员结算!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "客人" + index).start();
        }

    }
}
