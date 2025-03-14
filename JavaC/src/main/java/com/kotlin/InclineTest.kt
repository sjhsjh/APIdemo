package com.kotlin

private interface MFunction {
    fun invoke()
}

// crossinline
private inline fun testIncline(block: (Int, String) -> String) {
    val start = 0
    val end = "end"

    println("000 ")
    block(start, end)
    println("incline end ")  // 被 block内的lambda的return跳过了

//    val function = object : MFunction {
//        override fun invoke() {
////            3、内联高阶函数的匿名类对象中使用lambda表达式的时候，lambda表达式不允许 裸return
////            4、让内联函数里的lambda表达式内的“函数类型的参数” 可以使用，可以将该“函数类型的参数” 形参加上crossinline，
////            代价是不能在Lambda表达式里使用裸return。
//            block(start, end)
//        }
//    }
}

fun main(args: Array<String>) {
    testIncline { i, s ->
        // 0、带标签的局部返回应该是任何时候都可用
        // return@testIncline ""

        println("in lambda ")
//            一、lambda表达式是否可以使用return
//            1、非内联函数的lambda表达式中使用裸return是不被允许（即普通lambda可使用标签return，不能用裸return）
//            2、内联函数是允许使用return，它会从调用该lambda的函数中返回
        return
    }

    println("main end ")
}