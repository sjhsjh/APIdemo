package com.pkgname;

/**
     配置ndk.dir
     app的build.gradle + CMakeLists.txt 得到so名称
     写java类（声明native方法）
     写.h头文件
     写cpp类

     .externalNativeBuild文件夹：用于存放cmake编译好的文件
 */

//
// Created by sjh on 2024/12/8.
//
public class Java2CJNI {

    //加载so库
    static {
        System.loadLibrary("Java2C");   // 后面CMakeLists和JNI中是需要和这个名称对应的。
    }

    //native方法
    public native String javaMethod();

    public static native int javaIncrease(int num);


    public native String javaMethodPlus();

    public static native int javaIncreasePlus(int num);
}

