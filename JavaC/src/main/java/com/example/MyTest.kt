package com.example

import java.lang.StringBuilder

fun main(args: Array<String>) {

    var sonA = SonA(8)
    println("\n")
//    println(sonA.memberUse)
//    sonA.InnerClass().printInnerClass()
//    println("==== ${MyObjectA(8)}")




    // object
    ObjectClass.print()
    // companion object
    Utils.TAG_CONST
    Utils.TAG_NOT_CONST
    // kotlin文件中“Utils”实际上引用的是Utils.comObj对象！！MyClass 等于 MyClass.Companion
    Utils.comObj.invokeNonStaticMethod()
    Utils.invokeNonStaticMethod()
    val companionObj1 = Utils.comObj
    val companionObj2 = Utils
    println("Utils.comObj === " + companionObj1 + "\nUtils === " + companionObj2)


//    println("\n\n\n")
//    var a: Int = 5_2
//    var b: Byte = 114 ?: 2
//
//    var str3: String = if (b > 100) {
//        ">100"
//        "111"
//        var aa = "222"
//        aa
//    } else if (b > 50) ">50"
//    else "<50"
//    println(str3)


    // 函数形参默认值
//    defaultParam(a = 1, b = 2, normal = true) // 使用命名参数(形参变量名)来调用函数！！！
//    defaultParam(1) // 11
//    defaultParam(1, 2)  // 3
//    defaultParam(1, 2, true)    // 3
//    B().foo()   // BBB i = 10!!!

    // 命名参数
    // 所有位置参数都要放在第一个命名参数之前。调用方法时所有非命名参数(隐式声明)必须全部在所有命名参数(显式声明)前面！使用默认值后面的参数都要显式声明。
    // 理解：如果不使用默认值，则直接传入参数(隐式声明)；一旦有一个参数使用默认值，则参数的位置发生变化，则该参数后面的参数都必须使用命名参数(显式声明)
    namedParameter("", aa = false, bb = false, cc = false, word='e')

    optionalParam(1, 2, 3)
    intAndInteger()
}


fun namedParameter(
    str: String,
    aa: Boolean = true,
    bb: Boolean = true,
    cc: Boolean = false,
    word: Char
) {/*……*/}

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

    // 对象表达式
    var count = 0
    val anonymous = object {    // 相当于new Object() 并重写对应的属性和方法。变量的父类是Any
        var x: Int = 0
        override fun toString(): String {
            return "我是匿名对象"
        }

        fun getIncreaseCount(): Int {
//            sonA = SonA(99)     // kotlin匿名内部类中访问局部变量时无需加final修饰，变量能改变值。java需要！！
            return ++count
        }
    }
    println("getIncreaseCount = " + anonymous.getIncreaseCount() + " count = " + count)
    println(anonymous)


    val ab = object : MyObjectB(1), ILog {
        override fun printB() {
            super.printB()
        }

        override fun log() {
        }
    }
    println("匿名内部类的javaClass = " + ab) // com.example.MyTestKt$main$ab$1@6e0be858。文件名$方法名$变量名$1

}

/**
 * fun defaultParam(a: Int = 10, b: Int)
 * defaultParam(b = 1)  // 使用默认值 a = 10
 */
// 默认值通过类型后面的 = 及给出的值来定义。这可以减少重载数量。
fun defaultParam(a: Int, b: Int = 10, normal: Boolean = true) {
    println(a + b)
}

// 可变数量的参数vararg
// IntArray类型(即int[3])或Array <Any>类型，不是Array<Int> 类型
fun optionalParam(vararg intArray: Int) {
    println(intArray[0])
    println(intArray.javaClass)   // class [I
}

// kotlin内的int 与 Integer：
fun intAndInteger(): Int {
    var a = 1               // unbox
    var b: Int? = 2         // box
    println(a.javaClass)    // int
    println(b?.javaClass)   // int  （没装箱吧？）

    var objArray = Array(3, { 111 })
    println(
        "=====objArray[0].javaClass=======" + objArray[0].javaClass)  // 1、坑:Array<Int>把元素取出来就成了基本类型！！get和set操作的都是基本类型。
    println("=====objArray.javaClass=======" + objArray.javaClass)        // class [Ljava.lang.Integer;

    var intArray = IntArray(5, { it + 10 })
    println(intArray.javaClass)   // class [I
    for (x in intArray) {
        objArray[0] = x
    }

    var objArray2: Array<Any>
    objArray2 = arrayOf(A(), 11, "22")   // Object[3]
    objArray2[1] = 99                    // Array<Any>数组元素赋值时也会自动装箱

//    val one: Integer = 1 // error: "The integer literal does not conform to the expected type Integer"
//    val one2: Int = Int(1) // error: "Cannot access '<init>': it is private in 'Int'
//    val two2: Integer = Integer.valueOf(22)   // 2、坑:提示valueOf返回 “Int！”？？
    val two: Integer = Integer(2)
    println(two.javaClass)   // Integer!!!

    val three: Any = 3         // Any被赋值成基本类型时会自动装箱
    println(three.javaClass)   // Integer!!!

    when (three) {
        is Int -> println("is Int")            // true    3、坑:实际对象为Integer的Any对象的is Int和is Integer都为true！！
        is Integer -> println("is Integer")    // true
        else -> println("is other")
    }

    return a
}

// 单表达式函数不写返回类型时，其返回类型是单表达式的返回类型，并不一定都是返回void！ 当返回值类型可由编译器推断时，显式声明返回类型是可选的。
fun fun1(x: Int) = x * 2 // 省略 :Int
fun fun2(x: Int) = println()  // 省略 :Unit

interface ILog {
    fun log()
}


open class A {
    open fun foo(i: Int = 10) {
        println("AAA  i = " + i)
    }
}

class B : A() {
    // 重写的方法的所有参数都不能有默认值。重写方法的形参的默认参数与基类变量默认值相同！！覆盖方法总是使用与基类型方法相同的默认参数值。
    override fun foo(i: Int) {
        println("BBB i = " + i)
    }
}
