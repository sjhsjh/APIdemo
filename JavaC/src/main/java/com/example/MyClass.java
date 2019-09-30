package com.example;

import java.util.Arrays;
import java.util.List;

public class MyClass {

    public static void main(String[] args){

        // Random r1 = new Random();
        // for(int i = 0; i < 10; i++){
        //    System.out.println("===" + r1.nextInt(7));
        // }

        //字符串转list<String>
        String str2 = "asdfghjkl";
        List<String> lis = Arrays.asList(str2.split(""));
        for (String string : lis) {
         //   System.out.println(string);
        }
        //list<String>转字符串，list的内容以指定符号进行拼接
        System.out.println(String.join("", lis));

        Utils.TAG_CONST
        Utils.TAG_NOT_CONST
        Utils.Companion.getTAG_NOT_CONST();
        Utils.Companion.invokeNonStaticMethod();   // without @JvmStatic
        Utils.invokeNonStaticMethod();             // with @JvmStatic. 加上@JvmStatic修饰静态方法可以让java文件调用时省去Companion

        ObjectClass.INSTANCE.print();
        System.out.println(ObjectClass.INSTANCE);
        System.out.println(ObjectClass.INSTANCE);

        Utils.asd.INSTANCE.non_static()
        Utils.asd.invokeStaticMethod();

        System.out.println(ObjectClass.User);
        System.out.println(ObjectClass.User2);


    }

    
}
