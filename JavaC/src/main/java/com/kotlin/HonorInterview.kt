package com.kotlin

import java.util.Scanner

class HonorInterview {
}

fun main(args: Array<String>) {
//    5
//    my/2019-01-01T09:00:01
//    my/2019-01-01T09:00:01
//    abc/2018-12-24T08:00:00/test/you
//    1/2018-12-24T08:00:00/test/Test1
//    123/2018-12-24T08:00:09/test/me

    val read = Scanner(System.`in`)
    val len = read.nextInt()
    val set = linkedSetOf<String>()
    for (i in 1..len) {
        set.add(read.next())
    }
    println(set)

    val list = arrayListOf<String>()
    list.addAll(set)

    for (i in 0 until set.size) {
        for (j in 0 until set.size - 1) {
            if (!compareStr(list.get(j), list.get(j + 1))) {
                val temp = list.get(j)
                list[j] = list.get(j + 1)
                list[j + 1] = temp
            }
        }
    }
    for (i in 0 until list.size) {
        println(list[i])
    }
}

// 小于 则 返回true
fun compareStr(str1: String, str2: String): Boolean {
    val startIndex1 = str1.indexOf("/")
    val startIndex2 = str2.indexOf("/")

    val year1 = str1.substring(startIndex1 + 1, startIndex1 + 1 + 4).toInt()
    val year2 = str2.substring(startIndex2 + 1, startIndex2 + 1 + 4).toInt()
    if(year1 < year2) return true
    if(year1 > year2) return false

    val month1 = str1.substring(startIndex1 + 6, startIndex1 + 6 + 2).toInt()
    val month2 = str2.substring(startIndex2 + 6, startIndex2 + 6 + 2).toInt()
    if(month1 < month2) return true
    if(month1 > month2) return false

    val day1 = str1.substring(startIndex1 + 9, startIndex1 + 9 + 2).toInt()
    val day2 = str2.substring(startIndex2 + 9, startIndex2 + 9 + 2).toInt()
    if(day1 < day2) return true
    if(day1 > day2) return false

    val shi1 = str1.substring(startIndex1 + 12, startIndex1 + 12 + 2).toInt()
    val shi2 = str2.substring(startIndex2 + 12, startIndex2 + 12 + 2).toInt()
    if(shi1 < shi2) return true
    if(shi1 > shi2) return false

    val fen1 = str1.substring(startIndex1 + 15, startIndex1 + 15 + 2).toInt()
    val fen2 = str2.substring(startIndex2 + 15, startIndex2 + 15 + 2).toInt()
    if(fen1 < fen2) return true
    if(fen1 > fen2) return false

    val miao1 = str1.substring(startIndex1 + 18, startIndex1 + 18 + 2).toInt()
    val miao2 = str2.substring(startIndex2 + 18, startIndex2 + 18 + 2).toInt()
    if(miao1 < miao2) return true
    if(miao1 > miao2) return false

    if (str1.length != str2.length) {
        return str1.length < str2.length
    }
    for (i in 0 until str1.length) {
        return str1[i] < str2[i]
    }

    return true
}