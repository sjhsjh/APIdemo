package com.example

class SonA(var x: Int) : MyObjectA(x) {

//    constructor(i: Int) : super(i)

    override var memberUse = x + 1000  // 属性也能被覆盖！！

    override fun plus() {
        super.plus()
        println("SonA plus.")
    }

    inner class Filler {
        fun drawAndFill() {
            println("drawAndFill")
            super@SonA.plus() // 调用父类的plus实现
        }
    }

    // 扩展函数的调用是以表达式的类型决定的!
    fun testExtendFunction() {
        printClassName(Rectangle())
    }

//    var result = "result"

    fun printStr(str:String) {
        println("test run. str = " + str)
    }

}

/******************************************* 扩展属性 ***********************************************/
public var SonA.result: String
    get() = "result get = " + toString2()
    set(v) = printStr("set run. " + v)

/******************************************* 扩展函数 ***********************************************/
// 扩展函数。定义在任意class外部！！这里可以是xxx.toString2()
fun SonA?.toString2(): String {
    // this 关键字在扩展函数内部对应到接收者对象（即传过来的在点符号前的对象）
    if (this == null) return "null"
    // 空检测之后，“this”会自动转换为非空类型，所以下面的 toString() 解析为 Any 类的成员函数
    return "扩展函数 " + toString()
}


/******************************************* 扩展函数 ***********************************************/
open class Shape

class Rectangle : Shape()

fun Shape.getName() = "Shape"
fun Rectangle.getName() = "Rectangle"

fun printClassName(s: Shape) {
    println(s.getName())        // Shape!!
}
/****************************************************************************************************/
