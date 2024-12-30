//
// Created by sjh on 2024/12/8.
//

#include <jni.h>
#include "com_pkgname_Java2CJNIplus.h"
#include<android/log.h>
#include "MyLog.h"

/**
 cpp文件名无要求
*/
JNIEXPORT jstring JNICALL
Java_com_pkgname_Java2CJNI_javaMethodPlus(JNIEnv *env, jobject instance,
                                          jstring string, jboolean isOk,
                                          jobject listObj, jint len) {
    /*************************  jstring 与 char* 互转： ******************************/
//    Java 层的字符串：Java 默认使用 Unicode 编码。
//    JNI ：    	 jstring 类型的，但 jstring 指向的是 JVM 内部的一个字符串。
//    C/C++：char* ： 默认使用 UTF 编码。

    jstring returnValue = env->NewStringUTF("I am From Native C++");

    // 把jstring类型的字符串转换为C风格的字符串，会额外申请内存
    // isCopy如果为JNI_TRUE 则返回拷贝，并且要为产生的字符串拷贝分配内存空间；
    // 如果为JNI_FALSE就直接返回了JVM 源字符串的指针，
    const char *str = env->GetStringUTFChars(string, 0);


    // 调用完GetStringUTFChars 方法要做检查判断.
    // 因为 JVM 需要为产生的新字符串分配内存空间，如果分配失败就会返回 NULL，并且会抛出 OutOfMemoryError 异常，
    if (str == NULL) {
        return NULL;
    }
    LOGD("str = %s", str);

    //释放申请的内存
    env->ReleaseStringUTFChars(string,str);

    /*************************** jni的基本类型能直接被c使用 ******************************/
    bool flag = false;
    flag = isOk;
    LOGD("isOk = %d", isOk);
    LOGD("flag = %d", flag);

    /*************************** native层接收对象 ******************************/
    const char *fieldName = "booleanData";          // 调用该jni方法的java对象的成员变量
    jclass cdata = env->GetObjectClass(instance);
    jfieldID booleanDataID = env->GetFieldID(cdata, fieldName, "Z");
    jboolean cbooleanData = env->GetBooleanField(instance, booleanDataID);

    //注意JAVA 对象的私有属性此处也可以获取到
    jfieldID intDataID = env->GetFieldID(cdata, "intData", "I");
    jint cintData = env->GetIntField(instance, intDataID);

    LOGI("cbooleanData = %d", cbooleanData);
    LOGI("cintData = %d", cintData);

    /*************************** native层接收ArrayList *************************/
    // 获取传入对象的java类型，也就是ArrayList
    jclass datalistcls = env->GetObjectClass(listObj);

    /*
     * 执行 javap -s java.util.ArrayList 查看ArrayList的函数签名:
     * public E get(int);
     * descriptor: (I)Ljava/lang/Object;
     */
    // 从ArrayList对象中拿到其get方法的方法ID
    jmethodID getMethodID = env->GetMethodID(datalistcls, "get", "(I)Ljava/lang/Object;");
    //调用get方法，拿到list中存储的第一个Integer 对象
    jobject intObj = env->CallObjectMethod(listObj, getMethodID, 0);

    /*
     * javap -s java/lang/Integer
     * public int intValue();
     * descriptor: ()I
     */
    jclass datacls = env->GetObjectClass(intObj);
    jmethodID intValueMethodID = env->GetMethodID(datacls, "intValue", "()I");
    //将Integer 对象的int值取出
    int data0_int = env->CallIntMethod(intObj, intValueMethodID);

    LOGW("setList buf[0] = %d", data0_int);



    return returnValue;
//    return (env)->NewStringUTF("I am From Native C++");
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


JNIEXPORT jint JNICALL Java_com_pkgname_Java2CJNI_javaIncreasePlus
(JNIEnv *env, jclass jcs, jint num){
//	printf("来自java的int数值为%ld", num);

	//因为jint是long类型所以直接赋值
	long new_int = num;

	new_int++;  // 加1后返回

	return new_int;
}

/**
 * native向java传递java对象
 * native中生成一个java对象，并且对其属性赋值，最终将构造好的对象直接返回给java层。
 */
JNIEXPORT jobject JNICALL Java_com_pkgname_Java2CJNI_nativeNewObj
        (JNIEnv *env, jclass jcs) {

    // 获取类对象 这个class文件存在于dex中，可用apk工具查看
    jclass packagecls = env->FindClass("com/pkgname/Java2CJNI");

    // 获取这个类的构造方法的方法id 以及这个方法的函数签名
    jmethodID construcMethodID = env->GetMethodID(packagecls, "<init>", "()V");
    // 创建这个java对象
    jobject packageobj = env->NewObject(packagecls, construcMethodID);

    // 操作对象的属性
    jfieldID jfieldId = env->GetFieldID(packagecls, "longData", "J");
    env->SetLongField(packageobj, jfieldId, (jlong) 109);

    return packageobj;
}