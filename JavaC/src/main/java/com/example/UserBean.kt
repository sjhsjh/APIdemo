package com.example

data class UserBean(
    val name: String,
    val age: Int
){


// 仍能重写toString等方法，并以现有函数为准。
//    override fun toString(): String {
//        return super.toString()
//    }
}