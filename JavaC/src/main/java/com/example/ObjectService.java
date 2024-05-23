package com.example;

public class ObjectService {

    public synchronized static void methodA(){
        try {
            System.out.println("static methodA begin 线程名称:"+Thread.currentThread().getName()+" " +
                    "times:"+System.currentTimeMillis()%100000);
            Thread.sleep(3000);
            System.out.println("static methodA end   线程名称:"+Thread.currentThread().getName()+" " +
                    "times:"+System.currentTimeMillis()%100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void methodB(){
        System.out.println("static methodB begin 线程名称:"+Thread.currentThread().getName()+" times" +
                ":"+System.currentTimeMillis()%100000);
        System.out.println("static methodB end   线程名称:"+Thread.currentThread().getName()+" times" +
                ":"+System.currentTimeMillis()%100000);
    }

    public void methodC() {
        synchronized (ObjectService.class) {
            System.out.println("static methodC begin 线程名称:" + Thread.currentThread().getName() + " times:" +  System.currentTimeMillis()%1000);
            System.out.println( "static methodC end   线程名称:" + Thread.currentThread().getName() + " times:" +  System.currentTimeMillis()%1000);
        }
    }

    /**
     * 1.Synchronized修饰非静态方法，实际上是对调用该方法的对象加锁，俗称“对象锁”。
     * 修饰非静态方法(锁class的同一实例的所有Synchronized方法)
     * <p>
     * ！！同一个对象的所有非静态Synchronized方法   +   synchronized (objA)代码块
     * ！!非静态方法的锁是 某一个对象；
     */
    public synchronized void methodD() {

    }

    public void methodD2() {
        synchronized (this) {

        }
    }

    /**
     2.Synchronized修饰静态方法，实际上是对该类对象加锁，俗称“类锁”。
     修饰静态方法(锁class的所有实例的所有Synchronized方法)
     ！！该类所有的synchronized static方法 +        synchronized (ObjectService.class)代码块
     ！！静态方法的锁是 class对象；
     */
    public synchronized static void methodE(){

    }

    public void methodE2() {
        synchronized (this.getClass()) {

        }
    }

    /**
     * 3.修饰代码块(锁定特定的对象)
     */
}
