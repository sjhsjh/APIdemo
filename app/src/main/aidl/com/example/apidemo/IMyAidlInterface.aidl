// IMyAidlInterface.aidl
package com.example.apidemo;

// Declare any non-default types here with import statements

//导入所需要使用的非默认支持数据类型的包!!!
import com.example.apidemo.Book;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    // 传参时除了Java基本类型以及String，CharSequence之外的类型都需要在前面加上定向tag!!
    int plus(in Book book1, in Book book2);

}
