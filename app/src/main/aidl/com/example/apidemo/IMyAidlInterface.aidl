// IMyAidlInterface.aidl
package com.example.apidemo;

// Declare any non-default types here with import statements

//导入所需要使用的非默认支持数据类型的包!!!
import com.example.apidemo.Book;
import com.example.apidemo.IOnServerCallback;


interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    // 这个接口里的方法的定义顺序会 “在对应的java文件里 按顺序生成枚举值”
    int minus(in Book book1, in Book book2);

    // 传参时除了Java基本类型以及String，CharSequence之外的类型都需要在前面加上定向tag!!
    int plus(in Book book1, in Book book2);


    void registerListener(IOnServerCallback callback);

    void unRegisterListener(IOnServerCallback callback);

}
