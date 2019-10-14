package com.example

import java.lang.StringBuilder

fun main(args: Array<String>) {

    var sonA = SonA(8)
    println("\n")
//    println(sonA.memberUse)
//    sonA.InnerClass().printInnerClass()
//    println("==== ${MyObjectA(8)}")




    println("\n\n\n")
    var a: Int = 5_2
    var b: Byte = 114 ?: 2

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

    // 函数形参默认值
    defaultParam(1) // 11
    defaultParam(1, 2)  // 3
    defaultParam(1, 2, true)    // 3
}


private fun demo() {
    // 基本类型
    var a: Int = 5_2
    println("a = " + a)
    println("a-2 = ${a - 2}")

    var b: Byte = 114 ?: 2
    println(b.toString())
    var c = 4f
    var d = 5.0

    // enum class
    var color: Color = Color.RED
    println(color.rgb)
    color.getColor()
    Color.BLUE.getColor()

    println(Color.BLUE.ordinal) // 2
    println(Color.BLUE > Color.RED)

    var array:Array<Color> = Color.values()
    println(array[0])

    var col: Color  = Color.valueOf("GREEN")  // 大小写必须匹配
    println(col)

    // “==”与“===”
    var str1: String = String(StringBuilder("xyz"))
    var str2 = "xyz"
    println(str1 == str2)    // equal。true
    println(str1 === str2)   // 比较内存地址。false

    // 遍历
    print(".. ")
    for (i in 1..3) print(i)        // 全闭区间
    print("\nuntil ")
    for (i in 1 until 3) print(i)   // 左闭右开
    print("\ndownTo ")
    for (i in 3 downTo 1) print(i)  // 全闭区间
    println("")

    for (i in 4 downTo 10) print(i) // 不报错但不执行循环体
    for (i in 12 until 4) print(i)  // 不报错但不执行循环体

    // data class
    var bean1 = UserBean("sjh1", 11)
    var bean2 = UserBean("sjh1", 11)
    println(bean1)
    println(bean2)
    println(bean1.equals(bean2))
    println((bean1 == bean2))

    // 构造函数
    var myObjectA = MyObjectA(8, 9)
    println("\n" + myObjectA)
    println(myObjectA.javaClass)   // class com.example.MyObjectA

    // get and set
    var myObjectB = MyObjectB(9)
    println("str ==== " + myObjectB.str)
    println("bar.javaClass === " + myObjectB.bar.javaClass)
    println("bar ===" + myObjectB.bar)
    println("foo ===" + myObjectB.foo)

    // 扩展函数 or 扩展属性
    var sonA = SonA(8)
    println("扩展函数 : " + sonA.toString2())
    sonA.result = "i am result."
    println("扩展属性 : " + sonA.result)
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
fun optionParam(vararg args: Int) {
    println(args[0])
}


