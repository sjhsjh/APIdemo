package com.example

open class MyObjectA(var member: Int) {  // 1、参数默认public；主构造函数的参数可以设置默认值
    open var memberUse = member  // 可以使用主构造函数的变量
    /******************************************************************************************/
    // 实例初始化顺序：主构造函数 -> 初始化块和属性初始化 -> 次级构造函数
    // 在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起。
    // 基类的属性初始化、初始化块和构造函数中，不能使用“可被重写的open变量”和“可被重写的方法”和“this”，因为派生类还没有初始化！！！
    val firstField = "2、First field: $member. memberUse=$memberUse".also(::println)

    init {
        println("3、First initializer block ${member}")
    }

    val secondField = "4、Second field: ${member}".also(::println)

    init {
        println("5、Second initializer block  ${member}")
    }

    // 主构造函数内的参数必须初始化，因此次级构造函数必须加上this委托给主构造函数 or 通过别的次构造函数间接委托
    // 否则报错Primary constructor call expected.
    constructor(x: Int, y: Int) : this(100) {
        println("6、Secondary constructor execute. ")
    }

    constructor(x: Int, y: Int, z: Int) : this(1, 2) { // : super(x)
        println("xyz constructor. ")
    }
    /******************************************************************************************/

    override fun toString(): String {
        return super.toString() + "member = " + member
    }

    open fun plus(){

    }

}