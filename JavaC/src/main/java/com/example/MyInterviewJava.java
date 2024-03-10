package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Java多线程---顺序打印ABC打印10次的实现-三种实现
 * https://www.jianshu.com/p/b036dda3f5c8
 * 规定：
 * 1、只有3个线程对象。
 * 2、不允许使用线程池的复用功能
 */
public class MyInterviewJava {

    // private static final Object lock = new Object();
    public static void main(String[] args) {
        // 方法1、使用 wait 和 notify
        // logByWait();

        // 方法2、使用 ReentrantLock  （基于AQS实现）
        // logByReentrantLock();

        // 方法3、使用 信号量Semaphore  （加强版synchronized）（基于AQS实现）
        // 只有信号量的api是semaphore.acquire()和 semaphore.release();其他都是await +（notify OR 空 OR countDown）
        // logBySemaphore();

        // 方法4、使用 CyclicBarrier (有2种方法)（基于Condition实现）
        // logByBarrier();
        // logByBarrierSecond();

        // 方法5、使用 CountDownLatch （基于AQS实现）
        // logByCountDownLatch();

        // 方法6、使用 LockSupport （基于Unsafe的native实现, 比其他方式更底层）
        logByLockSupport();

        // onlyNotify();
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
     * 不await 也可以直接 signal！！(即可以 signal + await + signal)
     */
    private static void onlyNotify() {
        // Object lock0 = new Object();
        // synchronized (lock0) {
        //     lock0.notify();
        // }


        Condition conditionA = lock.newCondition();
        lock.lock();

        System.out.println("before signal");
        conditionA.signal();
        System.out.println("after signal");


        new Thread() {
            @Override
            public void run() {
                lock.lock();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.out.println("begin await");
                try {
                    conditionA.await();
                } catch (InterruptedException e) {
                    System.out.println("bb error =  " + e);
                    e.printStackTrace();
                }

                lock.unlock();
                System.out.println("after await==========");
            }
        }.start();


        new Thread() {
            @Override
            public void run() {
                lock.lock();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                conditionA.signal();
                System.out.println(" conditionA.signal();");

                lock.unlock();
            }
        }.start();


        System.out.println("finish ");
        lock.unlock();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * （线程同步是通过 nextCondition.signal() 来唤醒下一个线程！）
     * conditionA可以在 B线程中await，然后在 c线程 中signal！！！！ 即能唤醒指定线程！！
     * 若B、C线程先执行，则它们先进入await并等待A来唤醒；    若A线程先执行，则A线打印并且执行不影响结果的conditionB.signal()；
     */
    private static void logByReentrantLock() {
        // ExecutorService poolService = Executors.newFixedThreadPool(3);
        Integer count = 5;

        new Thread(new Worker("A", count, 1, lock, conditionA, conditionB)).start();
        // poolService.execute(new Worker("A", count, 1, lock, conditionA, conditionB));

        // 可以任意调整执行顺序，结果都是对的！
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Worker("B", count, 2, lock, conditionB, conditionC)).start();
        new Thread(new Worker("C", count, 3, lock, conditionC, conditionA)).start();

        // 使用线程池，出现了 执行conditionB.signal() + conditionC.signal() + conditionB.await()时，
        // await内  if (Thread.interrupted()) throw new InterruptedException();

        // poolService.execute(new Worker("B", count, 2, lock, conditionB, conditionC));
        // poolService.execute(new Worker("C", count, 3, lock, conditionC, conditionA));
        //
        //
        // poolService.shutdownNow();
    }

    private static ReentrantLock lock = new ReentrantLock();
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
                    // System.out.println(i + " begin " + key);
                    // System.out.println(i + " currentState " + currentState);
                    // System.out.println(i + " targetState " + targetState);
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
                System.out.println(key + " error ");
                e.printStackTrace();
            } finally {
                this.lock.unlock();
            }
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * （线程同步是通过 限流为0达到wait的效果，然后nextSemaphore.release()来唤醒下一个线程！）
     * 当计数器为0时，acquire()将阻塞线程直到其他线程调用release()。
     */
    private static void logBySemaphore() {
        Semaphore a = new Semaphore(1);
        Semaphore b = new Semaphore(0);
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
        private Semaphore currentSemaphore;
        private Semaphore nextSemaphore;
        private Integer count;

        public Worker2(Semaphore currentSemaphore, Semaphore next, String key, Integer count) {
            this.currentSemaphore = currentSemaphore;
            this.nextSemaphore = next;
            this.key = key;
            this.count = count;
        }

        public void run() {
            for (int i = 0; i < count; i++) {
                try {
                    // 获取当前的锁
                    currentSemaphore.acquire(); // 请求成功才 current - 1，  请求失败则wait
                    System.out.println(i + ",  " + key);
                    // 释放next的锁
                    nextSemaphore.release();    // next + 1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 线程同步是通过 barrier.await()来改变getNumberWaiting的值，从而改变
     * 下一个线程的 循环条件 来唤醒下一个线程！
     */
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
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            System.out.println("===单个循环结束===");
        });

        new ThreadBarrier(barrier, 0).start();
        new ThreadBarrier(barrier, 1).start();
        new ThreadBarrier(barrier, 2).start();

    }



///////////////////////////////////////////////////////////////////////////////////////////////////
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    private static CyclicBarrier cyclicBarrier2 = new CyclicBarrier(3);
    private static CyclicBarrier cyclicBarrier3 = new CyclicBarrier(3);

    /**
     * 这方法没有用到线程同步。A 、B、C 三条线程允许以任意顺序执行。
     * 利用冲破栅栏时，唤醒所有线程，然后对应线程打印对应内容。
     */
    private static void logByBarrierSecond() {
        new Thread(new PrintABCUsingCyclicBarrier(5, "A")).start();
        new Thread(new PrintABCUsingCyclicBarrier(5, "B")).start();
        new Thread(new PrintABCUsingCyclicBarrier(5, "C")).start();

    }

    private static class PrintABCUsingCyclicBarrier implements Runnable {
        private int times;
        private String word;

        public PrintABCUsingCyclicBarrier(int times, String word) {
            this.times = times;
            this.word = word;
        }

        @Override
        public void run() {
            try {
                // 每打印一个字母，都要3个线程一起冲破一个屏障
                for (int i = 0; i < times; i++) {
                    cyclicBarrier.await();
                    if ("A".equals(word)) {
                        System.out.println(word);
                    }
                    cyclicBarrier2.await();
                    if ("B".equals(word)) {
                        System.out.println(word);
                    }
                    cyclicBarrier3.await();
                    if ("C".equals(word)) {
                        System.out.println(word);
                    }
                }
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 作用：等待多个线程都完成之后，自己才继续执行！
     * 当计数器的值为0时，countDownLatch.await被喚醒；
     * 使用一次性；
     * countDownLatch.await()前执行 countDownLatch.countDown()无效 且 无影响；
     */

     /**
     * 首先调用所依赖的latch的await()方法，如果所依赖的latch的count为0，
     * 则重置所依赖的latch并打印需要输出的，最后将自身的count减去。
      *
      * 一开始，3个countDownLatch都在await，直到手动执行latchC.countDown 来唤醒A。
      * A                   B                   C
      * latchC.await        latchA.await        latchB.await
      * latchA.countDown    latchB.countDown    latchC.countDown
      *
      * （线程同步是通过 countDownLatchA.countDown()来唤醒 countDownLatchA.await   ）
     */
    private static void logByCountDownLatch() {
        String latchA = "A";
        String latchB = "B";
        String latchC = "C";

        countDownLatchMap.put("A", new CountDownLatch(1));
        countDownLatchMap.put("B", new CountDownLatch(1));
        countDownLatchMap.put("C", new CountDownLatch(1));

        //创建三个线程，但是此时由于三个CountDownLatch都不为0，所以三个线程都处于阻塞状态
        Thread threadA = new Thread(new PrintABCUsingCountDownLatch2("C", "A"));
        Thread threadB = new Thread(new PrintABCUsingCountDownLatch2("A", "B"));
        Thread threadC = new Thread(new PrintABCUsingCountDownLatch2("B", "C"));
        threadA.start();
        threadB.start();
        threadC.start();

        //latchC 阻塞了 latchA；调用latchC的countDown()方法，先让latchC为0，使latchA先运行
        countDownLatchMap.get("C").countDown();
    }

    private static Map<String, CountDownLatch> countDownLatchMap = new HashMap<>();

    private static class PrintABCUsingCountDownLatch2 implements Runnable {

        private String dependLatch;
        private String selfLatch;

        private PrintABCUsingCountDownLatch2(String dependLatch, String selfLatch) {
            this.dependLatch = dependLatch;
            this.selfLatch = selfLatch;
        }

        @Override
        public void run() {
            // 可以试试：
            // current.wait
            // next.countdown
            for (int i = 0; i < 5; i++) {
                try {
                    countDownLatchMap.get(dependLatch).await();
                    countDownLatchMap.put(dependLatch, new CountDownLatch(1));

                    System.out.println(Thread.currentThread().getName() + ":  " + selfLatch);

                    countDownLatchMap.get(selfLatch).countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int times2 = 5;
    private static Thread t1, t2, t3;

    /**
     * 另一种写法：
     * 3个线程都先 LockSupport.park()，然后LockSupport.unpark(t1) 开启循环
     */
    private static void logByLockSupport() {
        t1 = new Thread(() -> {
            for (int i = 0; i < times2; i++) {
                System.out.println("   begin park a 1");
                LockSupport.park();

                System.out.println("A");

                // System.out.println("begin park a 2");
                // LockSupport.park();
                System.out.println("   a finish park");

                LockSupport.unpark(t2);
            }
        });
        t2 = new Thread(() -> {
            for (int i = 0; i < times2; i++) {
                LockSupport.park();
                System.out.println("B");

                LockSupport.unpark(t3);
            }
        });
        t3 = new Thread(() -> {
            for (int i = 0; i < times2; i++) {
                System.out.println("   c begin unpark  t1  1");
                LockSupport.unpark(t1);

                // System.out.println("c begin unpark  t1  2");
                // LockSupport.unpark(t1);

                System.out.println("   begin park c ");
                LockSupport.park();

                System.out.println("C");
            }
        });

        t1.start();
        t2.start();
        // sleep
        t3.start();

    }


}

