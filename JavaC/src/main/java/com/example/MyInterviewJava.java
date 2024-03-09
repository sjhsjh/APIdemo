package com.example;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Java多线程---顺序打印ABC打印10次的实现-三种实现
 * https://www.jianshu.com/p/b036dda3f5c8
 */
public class MyInterviewJava {

    // private static final Object lock = new Object();
    public static void main(String[] args) {
        // 方法1、使用 wait 和 notify
        // logByWait();

        // 方法2、使用 ReentrantLock
        // logByReentrantLock();

        // 方法3、使用 信号量Semaphore
        // logBySemaphore();

        // 方法4、使用 CyclicBarrier
        logByBarrier();

        // 方法5、使用 CountDownLatch
        logByCountDownLatch();

    }

///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void logByWait() {
        PrintSequenceRunnable runnable1 = new PrintSequenceRunnable(1);
        PrintSequenceRunnable runnable2 = new PrintSequenceRunnable(2);
        PrintSequenceRunnable runnable3 = new PrintSequenceRunnable(0);

        Thread t1 = new Thread(runnable1, "T1");
        Thread t2 = new Thread(runnable2, "T2");
        Thread t3 = new Thread(runnable3, "T3");

        t1.start();
        t2.start();
        t3.start();
    }

    private static int count = 1;
    private static Object monitor = new Object();

    private static class PrintSequenceRunnable implements Runnable {
        // 第几条Thread
        int numOfThreads;

        public PrintSequenceRunnable(int nubOfThreads) {
            this.numOfThreads = nubOfThreads;
        }

        @Override
        public void run() {
            print();
        }

        /**
         * 伪代码：
         * synchronized（lock）{
         *     while(条件){
         *         lock.wait()
         *     }
         *     // 逻辑
         *
         *     lock.notifyAll()
         * }
         */
        private void print() {
            // 1~10 打印10次，然后最后一次（第10次）会notify另外两条线程分别打印一次，即一共10 + 2次！
            while (count < 11) {
                synchronized (monitor) {
                    while (count % 3 != numOfThreads) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    int result = numOfThreads;
                    if (result == 0) {
                        result = 3;
                    }
                    System.out.println("ThreadId [" + numOfThreads + "] " +
                            "-->count=" + count + "-->result=" + result);
                    count++;
                    monitor.notifyAll();
                }


            }

        }

    }

///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 不wait  可以直接 notify！！
     * 不await 也可以直接 signal！！
     */
    private static void onlyNotify() {
        Object lock0 = new Object();
        synchronized (lock0) {
            lock0.notify();
        }


        Condition conditionA = lock.newCondition();
        lock.lock();
        conditionA.signal();
        lock.unlock();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    private static void logByReentrantLock() {
        ExecutorService poolService = Executors.newFixedThreadPool(3);
        Integer count = 10;
        poolService.execute(new Worker("A", count, 1, lock, conditionA, conditionB));
        poolService.execute(new Worker("B", count, 2, lock, conditionB, conditionC));
        poolService.execute(new Worker("C", count, 3, lock, conditionC, conditionA));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        poolService.shutdownNow();
    }


    private static ReentrantLock lock = new ReentrantLock();
    // conditionA可以在 B线程中await，然后在 c线程 中signal！！！！ 即能唤醒指定线程！！
    private static Condition conditionA = lock.newCondition();
    private static Condition conditionB = lock.newCondition();
    private static Condition conditionC = lock.newCondition();
    private static volatile int currentState = 1;


    public static class Worker implements Runnable {
        private String key;
        private Integer count;
        private Lock lock;
        private Condition currentCondition;
        private Condition nextCondition;
        private int targetState;

        public Worker(String key, Integer count, int targetState, Lock lock, Condition cur, Condition next) {
            this.key = key;
            this.count = count;
            this.lock = lock;
            this.currentCondition = cur;
            this.nextCondition = next;
            this.targetState = targetState;
        }

        public void run() {
            this.lock.lock();
            try {
                // for循环放在lock外也可, 放lock里面更好，因为这样就不用多次lock  和 unlock
                for (int i = 0; i < count; i++) {
                    // while内条件为false者 先执行打印。即 ThreadA 和 ThreadB 要符合循环条件进入wait
                    while (currentState != targetState) {
                        currentCondition.await();
                    }
                    System.out.println(i + ",  " + key);

                    currentState++;
                    if (currentState > 3) {
                        currentState = 1;
                    }
                    nextCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.lock.unlock();
            }
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void logBySemaphore() {
        Semaphore a = new Semaphore(1);
        Semaphore b  = new Semaphore(0);
        Semaphore c = new Semaphore(0);

        ExecutorService poolService = Executors.newFixedThreadPool(3);
        Integer count = 10;
        poolService.execute(new Worker2(a, b, "A", count));
        poolService.execute(new Worker2(b, c, "B", count));
        poolService.execute(new Worker2(c, a, "C", count));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        poolService.shutdownNow();
    }

    public static class Worker2 implements Runnable {
        private String key;
        private Semaphore current;
        private Semaphore next;
        private Integer count;

        public Worker2(Semaphore current, Semaphore next, String key, Integer count) {
            this.current = current;
            this.next = next;
            this.key = key;
            this.count = count;
        }

        public void run() {
            for (int i = 0; i < count; i++) {
                try {
                    // 获取当前的锁
                    current.acquire(); // 请求成功才 current - 1，  请求失败则wait
                    System.out.println(i + ",  " + key);
                    // 释放next的锁
                    next.release();    // next + 1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////
    private static class ThreadBarrier extends Thread {
        private CyclicBarrier barrier;
        private int index;

        public ThreadBarrier(CyclicBarrier barrier0, int index0) {
            this.barrier = barrier0;
            this.index = index0;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                /**
                 * 用的是
                 *  while(true){
                 *      Thread.sleep(100)   // 会占着CPU不工作.
                 *  }
                 * 来实现 线程等待!!!!!!!!!
                 */
                while (barrier.getNumberWaiting() != index) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // 打印 并且
                System.out.println("==" + charArray[index]);
                try {
                    // getNumberWaiting的值加1 & 等待!!!
                    barrier.await();

                    System.out.println("=====" + charArray[index] + "==finish wait=="
                            + "getNumberWaiting=" + barrier.getNumberWaiting());
                    // C==finish wait==getNumberWaiting=0
                    // 栅栏被冲破后getNumberWaiting变回0！！

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static char[] charArray = {'A', 'B', 'C'};
    private static final int times = 5;

    private static void logByBarrier() {
        CyclicBarrier barrier = new CyclicBarrier(3,() -> {
            System.out.println("===单个循环结束===" );
        });

        new ThreadBarrier(barrier, 0).start();
        new ThreadBarrier(barrier, 1).start();
        new ThreadBarrier(barrier, 2).start();

    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void logByCountDownLatch() {

    }


}

