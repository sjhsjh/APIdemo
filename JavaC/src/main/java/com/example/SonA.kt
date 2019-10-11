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


}