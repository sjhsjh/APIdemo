package com.example

data class UserBean(
    val name: String,
    val age: Int
) {

// 仍能重写toString等方法，并以现有函数为准。
//    override fun toString(): String {
//        return super.toString()
//    }
}

/**
 * 定义时比java多了关键字class
 * 枚举类可以实现接口（但不能从类继承）
 */
enum class Color(val rgb: Int) : IColor {
    RED(0xFF0000) {
        override fun getColor() {
            println("red")
        }
    },
    GREEN(0x00FF00) {
        override fun getColor() {
            println("green")
        }
    },
    BLUE(0x0000FF) {
        override fun getColor() {
            println("blue")
        }
    }
}

interface IColor {
    fun getColor()
}