package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyInterviewJava {

    // private static final Object lock = new Object();

    static int count = 1;
    static Object monitor = new Object();

    static class PrintSequenceRunnable implements Runnable {

        int numOfThreads;

        public PrintSequenceRunnable(int nubOfThreads) {
            this.numOfThreads = nubOfThreads;
        }

        @Override
        public void run() {
            print();
        }

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

    public static void main(String[] args) {
        logByWait();
        // logByReentrantLock();

    }

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
    private static Condition conditionA = lock.newCondition();
    private static Condition conditionB = lock.newCondition();
    private static Condition conditionC = lock.newCondition();
    private static volatile int state = 1;


    public static class Worker implements Runnable {
        private String key;
        private Integer count;
        private Lock lock;
        private Condition current;
        private Condition next;
        private int targetState;

        public Worker(String key, Integer count, int targetState, Lock lock, Condition cur, Condition next) {
            this.key = key;
            this.count = count;
            this.lock = lock;
            this.current = cur;
            this.next = next;
            this.targetState = targetState;
        }

        public void run() {
            this.lock.lock();
            try {
                for (int i = 0; i < count; i++) {
                    while (state != targetState) {
                        current.await();
                    }
                    System.out.println(i + ",  " + key);
                    state++;
                    if (state > 3) {
                        state = 1;
                    }
                    next.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.lock.unlock();
            }
        }
    }


}

