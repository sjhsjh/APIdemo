package com.example.arithmetic;

import java.util.HashMap;

/**
 * 企业微信算法——生产与消费
 */
public class QiYeWechatThread {

    // 题目（多线程）：阅读下面代码，在2线程环境下，设计一个方案，判断无限数据流(Stream)每个正整数是否素数，越快越好
    interface Stream {
        long get(); // 获取下一个需判断的整数
        void put(boolean result); // 返回整数是否素数的结果

        static boolean isPrimeNumber(long num) { // 判断一个整数是否素数
            if (num < 2) return false;
            for (long i = 2, s = (long) Math.sqrt(num); i <= s; i++) {
                if (num % i == 0) return false;
            }
            return true;
        }

        static Stream getInstance() {
            try {
                return (Stream) Class.forName("StreamImpl").newInstance(); // 运行环境保证不会异常
            } catch (Exception e) {
                return null;
            }
        }
    }
    /**
    比如：Stream={1,2,3,4,...}, Result={false,true,true,false,...}，注意输出顺序。

    public class Problem {
        private Stream stream = Stream.getInstance();
        private Object monitor = new Object();

        public static void main(String[] args) {
            Problem instance = new Problem();

            new Thread(() -> {
                while (true) {
                    try {
                        instance.thread1();
                    } catch (InterruptedException e) {

                    }
                }
            }, "thread1").start();

            new Thread(() -> {
                while (true) {
                    try {
                        instance.thread2();
                    } catch (InterruptedException e) {

                    }
                }
            }, "thread2").start();
        }

        public void thread1() throws InterruptedException {
            // TODO 请完成实现部分


            // 效率略低的解法
            synchronized (monitor) {
                int num = stream.get();
                boolean res = stream.isPrimeNumber(num);
                stream.put(res);
            }


        }
        public void thread2() throws InterruptedException {
            // TODO 请完成实现部分

            // 效率略低的解法
            synchronized (monitor) {
                int num = stream.get();
                boolean res = stream.isPrimeNumber(num);
                stream.put(res);
            }
        }

 */
    private Stream stream = Stream.getInstance();

    private Object monitor = new Object();
    private volatile int inputIndex = 0;
    private volatile int outputIndex = 0;
    // 记录不连续的结果数据。
    private volatile HashMap<Integer, Boolean> map = new  HashMap<Integer, Boolean>();

    public void thread1() throws InterruptedException {
        // TODO 请完成实现部分
        int resIndex = -1;
        long num = -1;

        synchronized (monitor) {
            // 输出的代码
            // 以outputIndex为指针，从map中取出数据来打印，取不到指定索引的数据则留给下次再打印。
            while (true) {
                Boolean res = map.get(outputIndex);
                if (res == null) {
                    break;
                } else {
                    stream.put(res);
                    outputIndex++;
                }
            }



            num = stream.get();
            resIndex = inputIndex;
            inputIndex++;
        }

        boolean res = Stream.isPrimeNumber(num);

        synchronized (monitor) {
            map.put(resIndex, res);
        }

    }

    public void thread2() throws InterruptedException {
        // TODO 请完成实现部分
        // 同 thread1()

        int resIndex = -1;
        long num = -1;

        synchronized (monitor) {
            // 输出的代码
            // 以outputIndex为指针，从map中取出数据来打印，取不到指定索引的数据则留给下次再打印。
            while (true) {
                Boolean res = map.get(outputIndex);
                if (res == null) {
                    break;
                } else {
                    stream.put(res);
                    outputIndex++;
                }
            }



            num = stream.get();
            resIndex = inputIndex;
            inputIndex++;
        }

        boolean res = Stream.isPrimeNumber(num);

        synchronized (monitor) {
            map.put(resIndex, res);
        }
    }


    public static void main(String[] args) {




    }


}
