package com.example

import java.lang.StringBuilder

fun main(args: Array<String>) {

    var a: Int = 5_2
    var obj = MyObjectA(8, 9)
    var obj2 = SonA(8)
    println(obj2.memberUse)
    println(obj2.toString2())

    obj2.result = "i am result."
    println(obj2.result)

//    SonA.Filler()

//    println("=====str:======" + obj.str)
//    println("===========" + obj.bar.javaClass)
//    println("======bar:=====" + obj.bar)
//    println("======foo:=====" + obj.foo)
    println("\n\n\n")


    println(a)
    println("==== ${a - 2}")
    println("==== ${MyObjectA(8)}")
    println(obj)

    var b: Byte = 114 ?: 2
    var c = 4f
    var d = 5.0

    println(b.toString() + "   " + obj.javaClass)


    var str1: String = String(StringBuilder("xyz"))
    var str2 = "xyz"
    println(str1 == str2)    // equal
    println(str1 === str2)   // 比较内存地址

    print(".. ")
    for (i in 1..3) print(i)        // 全闭区间
    print("\nuntil ")
    for (i in 1 until 3) print(i)   // 左闭右开
    print("\ndownTo ")
    for (i in 3 downTo 1) print(i)  // 全闭区间
    println("")

    for (i in 4 downTo 10) print(i) // 不报错但不执行循环体
    for (i in 12 until 4) print(i)  // 不报错但不执行循环体


    var str3: String = if (b > 100) {
        ">100"
        "111"
        var aa = "222"
        aa
    } else if (b > 50) ">50"
    else "<50"
    println(str3)

    Utils.TAG_CONST
    Utils.TAG_NOT_CONST
    Utils.invokeNonStaticMethod()

    ObjectClass.print()

    defaultParam(1)
    defaultParam(1, 2)
    defaultParam(1, 2, true)
}


/**
 * fun defaultParam(a: Int = 10, b: Int)
 * defaultParam(b = 1)  // 使用默认值 a = 10
 */
// 默认值通过类型后面的 = 及给出的值来定义。这可以减少重载数量。
fun defaultParam(a: Int, b: Int = 10, normal: Boolean = true) {
    println(a + b)
}

// 可变数量的参数args, 是数组
fun optionParam(vararg args: Int){
    println(args[0])
}


