package com.example

object ObjectClass {
    const val User = "user"     // public static final
    @JvmField
    var User2 = "user2"        // public static

    fun print() {
        println("ObjectClass print")
    }
}