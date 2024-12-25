//
// Created by sjh on 2024/12/8.
//

#include <jni.h>
#include "com_pkgname_Java2CJNI.h"
#include<android/log.h>

/**
 cpp文件名无要求
*/
JNIEXPORT jstring JNICALL Java_com_pkgname_Java2CJNI_javaMethod(JNIEnv* env, jobject instance)
{
    return (env)->NewStringUTF("I am From Native C22");
}

/**
    c代码中：JNIEnv是指向JNINativeInterface结构的指针:
   jstring javaStr = (*env)->NewStringUTF(env,"Hello from JNI");

    c++代码中：JNIEnv是c++类实例，这两种方式调用函数的方式是不一样的:
    env->NewStringUTF("Hello from JNI");
*/

/**
    基本数据类型：(除了void之外都是加个“j”前缀！！！)
    | JNI类型 | Java类型 |
    | ------ | ------ |
    | jboolean | boolean |
    | jbyte | byte |
    | jchar | char |
    | jshort | short |
    | jint | int |
    | jlong | long |
    | jfloat | float |
    | jdouble | double |
    | void | void |

    引用类型：
    | JNI类型 | Java类型 |
    | ------ | ------ |
    | jobject | Object |
    | jclass | Class |
    | jstring | String |
    | jobjectArray | Object[] |
    | jbooleanArray | boolean[] |
    | jbyteArray | char[] |
    | jshortArray | short[] |
    | jintArray | int[] |
    | jlongArray | long[] |
    | jfloatArray | float[] |
    | jdoubleArray | double[] |
    | jthrowable | Throwable |
 */


JNIEXPORT jint JNICALL Java_com_pkgname_Java2CJNI_javaIncrease
(JNIEnv *env, jclass jcs, jint num){
//	printf("来自java的int数值为%ld", num);

	//因为jint是long类型所以直接赋值
	long new_int = num;

	new_int++;  // 加1后返回

	return new_int;
}
