package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MyClass {

    public static void main(String[] args){

        Random r1 = new Random();
        for(int i = 0; i < 10; i++){
          //  System.out.println("===" + r1.nextInt(7));
        }

        //字符串转list<String>
        String str2 = "asdfghjkl";
        List<String> lis = Arrays.asList(str2.split(""));
        for (String string : lis) {
         //   System.out.println(string);
        }
        //list<String>转字符串，list的内容以指定符号进行拼接
        System.out.println(String.join("", lis));





    }


    public static class AS{
        public String a = "a";
    }

}
