package com.example

class SonA(var x: Int) : MyObjectA(x) {

//    constructor(i: Int) : super(i)

    override var memberUse = x + 1000  // 属性也能被覆盖！！

    override fun plus() {
        super.plus()
    }
}