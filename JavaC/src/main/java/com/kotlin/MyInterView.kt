package com.kotlin

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * 字节一面：
现有3个线程（命名为T1,T2,T3,后续内容以此描述进行），线程1能打印输出字母A;线程2能打印输出字母B;线程1能打印输出字母C;
要求，循环10次，使3个线程按顺序，每次打印输出ABC
使最终效果如：ABCABCABCABCABC..
 *
 * 易错点：
 * 1、kotlin里 Any对象没有wait和notify方法，需要使用Object对象才有。
 *
 *
 */
fun main(args: Array<String>) {
    val lock = Object()

    var a: Thread? = null
    var b: Thread? = null
    var c: Thread? = null

    var notNeedPrintA = false
    var notNeedPrintB = true
    var notNeedPrintC = true
    var count = 0
    val times = 4

    val runnableA = kotlinx.coroutines.Runnable {
        // 注意退出循环条件：必须减一。 count为012时正常打印，
        // runnableC打印最后一次时的notifyAll不应唤醒A和B，因此A和B在（times - 1）时无需再进入wait了
        while (count < times - 1) {
            synchronized(lock) {
                while (notNeedPrintA) {
                    try {
                        println("  a wait begin count=$count ")
                        lock.wait()
                        println("  a wait finish  count=$count ")
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                println("1")
                notNeedPrintA = true
                notNeedPrintB = false
                lock.notifyAll()
            }
        }
    }

    val runnableB = kotlinx.coroutines.Runnable {
        // 注意退出循环条件：必须减一。 count为012时正常打印，
        // runnableC打印最后一次时的notifyAll不应唤醒A和B，因此A和B在（times - 1）时无需再进入wait了
        while (count < times - 1) {
            synchronized(lock) {
                while (notNeedPrintB) {
                    try {
                        println("  b wait begin count=$count ")
                        lock.wait()
                        println("  b wait finish count=$count ")
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                println("2")
                notNeedPrintB = true

                notNeedPrintC = false
                lock.notifyAll()
            }
        }
    }

    val runnableC = kotlinx.coroutines.Runnable {
        while (count < times) {
            synchronized(lock) {
                while (notNeedPrintC) {
                    try {
                        println("  c wait begin count=$count ")
                        lock.wait()
                        println("  c wait finish count=$count ")
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                println("3")

                notNeedPrintC = true
                notNeedPrintA = false

                count++
                lock.notifyAll()
            }
        }
    }




    b = Thread(runnableB)
    b.start()
    c = Thread(runnableC)
    c.start()

    a = Thread(runnableA)
    a.start()

    val singleExecutor = Executors.newSingleThreadExecutor()
    singleExecutor.execute(c);
    val countDownLatch1 = CountDownLatch(1)
//    Thread.sleep(2000)



// 面试时 写错的 代码：

//        for(i in 0 until 10){
//            if(i % 3 == 0){
//                if(a == null){
//                    a = Thread(runnableA)
//                    a.start()
//                }else{
//                    synchronized(lock){
//                        lock.notifyAll()
//                    }
//                }
//            }else  if(i % 3 == 1){
//                if(b == null){
//                    b = Thread(runnableB)
//                    b.start()
//                }else{
//                    synchronized(lock){
//                        lock.notifyAll()
//                    }
//                }
//            }else{
//                if(c == null){
//                    c = Thread(runnableC)
//                    c.start()
//                }else{
//                    synchronized(lock){
//                        lock.notifyAll()
//                    }
//                }
//            }
//        }


}