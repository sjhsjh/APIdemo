package com.example

class MyObjectA(var member: Int) {  // 1、参数默认public
    var memberUse = member  // 可以使用主构造函数的变量
    /******************************************************************************************/
    // 在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起。
    val firstField = "2、First field: $member".also(::println)

    init {
        println("3、First initializer block ${member}")
    }

    val secondField = "4、Second field: ${member}".also(::println)

    init {
        println("5、Second initializer block  ${member}")
    }

    // 主构造函数的内的参数必须初始化，因此次级构造函数必须加上this委托给主构造函数 or 通过别的次构造函数间接委托
    // 否则报错Primary constructor call expected.
    constructor(x: Int, y: Int) : this(100) {
        println("6、Secondary constructor execute. ")
    }
    constructor(x: Int, y: Int, z: Int)  :this(1, 2){ // : super(x)
        println("xyz constructor. ")
    }
    /******************************************************************************************/

    override fun toString(): String {
        return super.toString() + "member = " + member
    }
}