//
// Created by sjh on 2024/12/28.
//

#include <jni.h>
#include "com_pkgname_Java2CJNI.h"
#include<android/log.h>
#include <stdio.h>


/**
 * c函数与cpp函数不一样, 且头文件的extern "C" 可以不一样
*/


JNIEXPORT jstring JNICALL Java_com_pkgname_Java2CJNI_javaMethod(JNIEnv* env, jobject instance)
{
    //Java字符串转换成C字符串
    const jbyte *str;
    jboolean isCopy;
    jstring javaString = (*env)->NewStringUTF(env, "new-javaString");

    str = (*env)->GetStringUTFChars(env, javaString, &isCopy);
    // GetStringChars函数 需要释放字符串
    (*env)->ReleaseStringUTFChars(env, javaString, str);


    jintArray javaArray = (*env)->NewIntArray(env, 10);

    //////////////////////////////////////////////////


//    c代码中：JNIEnv是指向JNINativeInterface结构的指针:
    jstring javaStr = (*env)->NewStringUTF(env, "I am From Native C");
//
//    c++代码中：JNIEnv是c++类实例，这两种方式调用函数的方式是不一样的:
//    env->NewStringUTF("Hello from JNI");

//    return (env)->NewStringUTF("I am From Native C++");

    return javaStr;
}



JNIEXPORT jint JNICALL Java_com_pkgname_Java2CJNI_javaIncrease
        (JNIEnv *env, jclass jcs, jint num){
	printf("<JavaToC.c>。来自java的int数值为%ld", num);    // 来自stdio.h。 实际无打印



    //因为jint是long类型所以直接赋值
    long new_int = num;

    new_int = new_int + 10;

    return new_int;
}

