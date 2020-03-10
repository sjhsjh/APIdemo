package com.example

open class MyObjectA(var member: Int = 70.also { println("4、父类 MyObjectA 主构造.") }) {  // 1、参数默认public；主构造函数的参数可以设置默认值
    open var memberUse = member  // 可以使用主构造函数的变量

    companion object {
        val faStatic: String = "1、父类 MyObjectA static".also(::println)
    }

    /****************************************** Constructor ************************************/
    // 父类静态成员 -> 子类静态成员 -> 子类主构造 -> 父类主构造
    // 父类“初始化块和属性初始化” -> 父类次级构造函数 -> 子类“初始化块和属性初始化” -> 子类次级构造函数
    // 概括：kotlin对比java就多了主构造函数的初始化
    // 单个类的实例初始化顺序：主构造函数 -> 初始化块和属性初始化 -> 次级构造函数
    // 在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起。
    // 基类的属性初始化、初始化块和构造函数中，不能使用“可被重写的open变量”和“可被重写的方法”和“this”，因为派生类还没有初始化！！！
    val firstField = "5、First field: $member. memberUse=$memberUse".also(::println)

    init {
        println("6、First initializer block ${member}")
    }

    val secondField = "7、Second field: ${member}".also(::println)

    init {
        println("8、Second initializer block  ${member}")
    }

    constructor(x: Int, str: String) : this() {
        println("9、父类 MyObjectA 次级构造 ")
    }

    // 主构造函数是new该对象必须调用的。主构造函数内的参数当然也必须初始化，因此次级构造函数必须加上this委托给主构造函数 or 通过别的次构造函数间接委托
    // 否则报错Primary constructor call expected.
    constructor(x: Int, y: Int) : this(100) {
        println("6、Secondary constructor execute. ")
    }

    constructor(x: Int, y: Int, z: Int) : this(1, 2) { // : super(x)
        println("xyz constructor. ")
    }

    /****************************************** Constructor ************************************/

    override fun toString(): String {
        return super.toString() + " member = " + member
    }

    open fun plus() {
        println("MyObjectA plus.")
    }

}