package com.example

open class MyObjectB(var member: Int) {
    internal var internalVar = 6    // 相同模块内可见

    override fun toString(): String {
        return super.toString() + " member = " + member
    }

    open fun printB(){
        println("MyObjectB printB.")
    }



    /************************************* Getters 与 Setters ********************************/
    var str: String = "init"
        get() = field
        set(value) {
            field = "my value is " + value
        }

    val eee
        get() = 3
    var ddd = 1
        get() = 3 + field


    private var prix = 3
    var pubx = 4


    val foo = calcValue("foo")         // 对象创建时调用一次calcValue()
    val bar
        get() = calcValue("bar")   // 每次引用时都调用calcValue()。get()的返回相当于定义了变量的类型。

    private fun calcValue(name: String): Int {
        println("Calculating $name")
        return 222
    }
    /************************************* Getters 与 Setters ********************************/

}