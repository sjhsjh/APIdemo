package com.example

class MyObjectA(private var member: Int) {

    override fun toString(): String {
        return super.toString() + "member = " + member
    }
}