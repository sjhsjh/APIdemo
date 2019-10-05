package com.example

class Utils() {

    object asd {
        val TAG_NOT_CONST = "tag_not_const"
        const val TAG_CONST = "tag_const"

        @JvmStatic
        fun invokeStaticMethod(): String {
            return "invokeStaticMethod"
        }

        fun invokeNonStaticMethod(): String {
            return "invokeNonStaticMethod"
        }
    }
}