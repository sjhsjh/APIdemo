package com.example;


public class MyVolatile {

    public static void main(String[] args) {
        //由于多线程情况下未必会试出重排序的结论,所以多试一些次
        for (int i = 0; i < 10000; i++) {
            ThreadA threadA = new ThreadA();
            ThreadB threadB = new ThreadB();
            threadA.start();
            threadB.start();
            //这里等待线程结束后,重置共享变量,以使验证结果的工作变得简单些.
            try {
                threadA.join();
                threadB.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a = 2;
            flag = false;
        }
    }

    private static int a = 0;
    private static boolean flag = false;

    static class ThreadA extends Thread {
        public void run() {
            a = 1;
            flag = true;
        }
    }
// a先执行得3；b先执行得2；
    static class ThreadB extends Thread {
        public void run() {
            if (flag) {
                a = a * 3;
            }
            if (a == 6) {
                System.out.println("a=================6");
            } else if (a == -3) {
                System.out.println("a= -3");
            } else if (a == 1) {
                System.out.println("a==== 1");
            }
        }
    }


}
