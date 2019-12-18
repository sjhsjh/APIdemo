package com.example

class SonA(var x: Int = 60.also { println("3、SonA 主构造.") }) : MyObjectA(110, "myobjecta") {

    //    constructor(i: Int) : super(i)
    companion object {
        val sonStatic: String = "2、SonA static".also(::println)
    }

    override var memberUse = x + 1000  // 属性也能被覆盖！！相同类型才能覆盖

    init {
        println("10、父类的“初始化块和属性初始化”全部执行完才到子类的")
    }

    val sonATemp = "11、父类的“初始化块和属性初始化”全部执行完才到子类的".also(::println)

    constructor(x: Int, str: String) : this() {
        println("12、SonA 次级构造 ")
    }

    override fun plus() {
        super.plus()
        println("SonA plus.")
    }

    /************************************* 嵌套类与内部类 ********************************/
    class NestClass private constructor(a: Int) {

    }

    inner class InnerClass {
        fun printInnerClass() {
            println("printInnerClass")
//            plus()
            super@SonA.plus() // 调用外部类的父类plus的实现
        }
    }

    /************************************* 嵌套类与内部类 ********************************/


    // 扩展函数的调用是以表达式的类型决定的!
    fun testExtendFunction() {
        printClassName(Rectangle())
    }

    // 扩展属性
//    var result = "result"

    fun printStr(str: String) {
        println("test run. str = " + str)
    }
}


/******************************************* 扩展属性 ***********************************************/
public var SonA.result: String
    get() = "result get = " + toString2()
    set(v) = printStr("set run. " + v)

/******************************************* 扩展函数 ***********************************************/
// 扩展函数。定义在任意class外部！！这里可以是xxx.toString2()
fun SonA?.toString2(): String { // 用“?.”定义为可空类型扩展方法！对象为null时仍然会执行函数体！
    // this 关键字在扩展函数内部对应到接收者对象（即传过来的在点符号前的对象）
    if (this == null) return "my null"
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
sealed class Sjh {
    abstract fun asd()
}

open class SjhSon : Sjh() {
    override fun asd() {
    }
}