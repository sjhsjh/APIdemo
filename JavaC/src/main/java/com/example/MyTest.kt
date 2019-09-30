package com.example

import java.lang.StringBuilder
import java.util.Random

//object Mytest {

//    @JvmStatic
fun main(args: Array<String>) {
    println("Hello,world!" + Utils.TAG_CONST + Utils.invokeStaticMethod())

    var a:Int = 5_2
    var obj = MyObjectA(8)
    println(a)
    println("==== ${a - 2}")
    println("==== ${MyObjectA(8)}")
    println(obj)

     var b:Byte = 114 ?:2
     var c = 4f
    var d = 5.0

    println(b.toString() +"   "+ obj.javaClass)


    var str: String = String(StringBuilder("xyz"))
    var str2 = "xyz"
    println(str == str2)    // equal
    println(str === str2)   // 比较内存地址

    for (i in 4 downTo 10) print(i)
    for (i in 12 until 4) print(i)

    ">100"
    var str3: String = if (b > 100) {
        ">100"
        "111"
        var aa = "222"
        aa
    } else if (b > 50) ">50"
    else "<50"
    println(str3)

    Utils.TAG_CONST
    Utils.TAG_NOT_CONST

    ObjectClass.print()


    val r1 = Random()
    for (i in 0..9) {
        //  System.out.println("===" + r1.nextInt(7));
    }
//    //字符串转list<String>
//    val str2 = "asdfghjkl"
//    val lis = Arrays.asList(*str2.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
//    for (string in lis) {
//        //   System.out.println(string);
//    }
//    //list<String>转字符串，list的内容以指定符号进行拼接
//    println(lis.joinToString(""))






}

//}

