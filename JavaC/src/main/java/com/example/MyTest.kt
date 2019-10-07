package com.example

import java.lang.StringBuilder
import java.util.Random


fun main(args: Array<String>) {

    var a: Int = 5_2
    var obj = MyObjectA(8, 9)
    obj.member
    println(a)
    println("==== ${a - 2}")
//    println("==== ${MyObjectA(8)}")
    println(obj)

    var b: Byte = 114 ?: 2
    var c = 4f
    var d = 5.0

    println(b.toString() + "   " + obj.javaClass)


    var str: String = String(StringBuilder("xyz"))
    var str2 = "xyz"
    println(str == str2)    // equal
    println(str === str2)   // 比较内存地址

    for (i in 4 downTo 10) print(i)
    for (i in 12 until 4) print(i)

    ">100"
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


