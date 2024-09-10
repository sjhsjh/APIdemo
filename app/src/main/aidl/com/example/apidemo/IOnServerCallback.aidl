// IOnServerCallback.aidl
package com.example.apidemo;

// Declare any non-default types here with import statements

//导入所需要使用的非默认支持数据类型的包!!!
import com.example.apidemo.Book;

/**
* 新建IOnServerCallback.aidl用于 设置回调函数，从而实现服务端向客户端传递数据。
*/
interface IOnServerCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    // 服务端 调 客户端
    void onBookReceived(in Book book);

}