package com.example

class Utils() {

    // 是非静态内部类（普通内部类）。但外部类拥有同名的public static对象。
    // 引用外部类时就生成该内部类对象并用静态变量保存。
    // companion object内定义的所有变量和常量默认都会定义在外部类中定义成private static变量！！
    companion object comObj {
        const val TAG_CONST = "tag_const"
        val TAG_NOT_CONST = "tag_not_const"
        var qqq = "qqq"

        @JvmStatic //  companion object 内的方法加上@JvmStatic就会在外部类中定义public static的同名方法
        fun invokeStaticMethod(): String {
            return "invokeStaticMethod" + invokeNonStaticMethod()
        }

        fun invokeNonStaticMethod(): String {
            return "invokeNonStaticMethod"
        }
    }

    fun test() {
        TAG_NOT_CONST
        // kotlin文件调用companion object的非@JvmStatic时可以省略“comObj.”
        comObj.invokeNonStaticMethod()
    }
}