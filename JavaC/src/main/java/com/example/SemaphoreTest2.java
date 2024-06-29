package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信算法 临场发挥
 * 最小粒度加锁 + 线程同步控制 并发顺序
 * 能保证按顺序从list中get出元素，同时能 保持原list的顺序 输出到listResult！！
 *
 *   模版代码：
 *   currentSemaphore.acquire(); // permit - 1 或 wait！！
 *   // 被锁部分
 *   nextSemaphore.release();    // next + 1
 *
 *
 * @date 2024/6/28
 */
class SemaphoreTest2 {
    private static ArrayList<Integer> listStream = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    private static ArrayList<Integer> listResult = new ArrayList<Integer>();

    private static int getFirst() {
        if (!listStream.isEmpty()) {
            int x = listStream.get(0);
            listStream.remove(0);
            return x;
        }
        return -1;
    }

    private static Semaphore sem1 = new Semaphore(1);
    private static Semaphore sem2 = new Semaphore(0);

    // 按下列1 2 3 4 的顺序执行
    public static void thread1() throws InterruptedException {

        // 1
        sem1.acquire();          // permit - 1 或 wait！！
        int num = getFirst();
        System.out.println("thread1         get " + num);
        sem2.release();

        try {
            System.out.println(Thread.currentThread().getName() + " sleep!");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3
        sem1.acquire();         // 一定跟在 sem1.release() 后面执行！
        int square = num * num;
        listResult.add(square);
        System.out.println("thread1 put square num " + num);
        sem2.release();
    }

    public static void thread2() throws InterruptedException {
        sem2.acquire();         // 一定跟在 sem2.release() 后面执行！
        int num = getFirst();
        System.out.println("thread2         get " + num);
        sem1.release();

        try {
            System.out.println(Thread.currentThread().getName() + " sleep!");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 4
        sem2.acquire();
        int square = num * num;
        listResult.add(square);
        System.out.println("thread2 put square num " + num);
        sem1.release();
    }

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    thread1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 打印 结果
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("listResult = " + listResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }, "thread1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    thread2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // System.out.println("listResult = " + listResult);
        }, "thread2").start();


    }

}
