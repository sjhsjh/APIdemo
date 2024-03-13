package com.example;

/**
 * @date 2024/3/13
 */
class B {
    public static B t1 = new B();   // 注意：静态的new B()不会触发 其他同为静态的  静态代码块！！！
    public static B t2 = new B();

            {
                System.out.println("构造块");
            }

    static {
        System.out.println("静态块");
    }


    public static void main(String[] args) {
        B t = new B();
        // 构造块
        // 构造块
        // 静态块
        // 构造块
    }
}


class JvmTest {

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
        System.out.println("Singleton1 value1:" + singleton.value1);    // 1
        System.out.println("Singleton1 value2:" + singleton.value2);    // 0

        Singleton2 singleton2 = Singleton2.getInstance2();
        System.out.println("Singleton2 value1:" + singleton2.value1);   // 1
        System.out.println("Singleton2 value2:" + singleton2.value2);   // 1
    }

}


class Singleton {
    private static Singleton singleton = new Singleton();
    public static int value1;
    public static int value2 = 0;

    private Singleton() {
        value1++;
        value2++;
    }

    public static Singleton getInstance() {
        return singleton;
    }

}

class Singleton2 {
    public static int value1;
    public static int value2 = 0;
    private static Singleton2 singleton2 = new Singleton2();

    private Singleton2() {
        value1++;
        value2++;
    }

    public static Singleton2 getInstance2() {
        return singleton2;
    }

}
