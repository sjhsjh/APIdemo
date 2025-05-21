package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class JavaApi {

    public static void main(String[] args) {


        int a = 5; // 二进制表示为 101
        int b = 3; // 二进制表示为 011

        int result = a ^ b; // 执行异或运算
        System.out.println("异或结果: " + result); // 输出 6，因为 101 ^ 011 = 110


        stringApi();



        // 判断两数相除是否整数，3.0 与 3相等。     用取余也可。
        boolean aa = 9 / 3 == (((double) 9) / 3);
        System.out.println("  aa=" + aa);

        int max = Math.max(4, 3);
        System.out.println(" max: " + max);
        int abs = Math.abs(-1);
        System.out.println(" abs: " + abs);

        double pow = Math.pow(2, 3);
        System.out.println(" pow: " + (int)pow);
        int max1 = (int)Math.pow(2, 31);    // 2^31是2,147,483,648，这里用int会溢出！！
        long max2 = (long)Math.pow(2, 31);  // 用long才对
        System.out.println(" pow: " + (Math.pow(2, 31)-1));
        System.out.println(" pow: " + max2);


        // 空数组的定义！
        int[] arrEmpty = new int[0];

        int[] arr = new int[5];
        int[] arr2 = {1, 2, 3, 4, 5};
        int[] arr3 = new int[arr2.length];
        int l = arr.length;




        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Collections.reverse(list);
        System.out.println("Reversed List: " + list);

    }


    // 关键点：keySet、Integer、containsKey、containsValue
    private static void mapAndSetApi() {
        HashMap<Integer, Boolean> map0 = new HashMap<Integer, Boolean>();
        // map.put(0, true);
        // boolean res = map.get(0);   // xx。为null就报错
        Boolean res = map0.get(0);
        System.out.println(res);

        //  hashmap遍历
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.containsKey("one");
        map.containsValue(2);
        int a = map.get("");        // 装箱和拆箱问题
        Integer a2 = map.get("");

        for (String key : map.keySet()) {
            System.out.println("Key = " + key + ", Value = " + map.get(key));
        }

        // hashset遍历
        HashSet<String> set = new HashSet<>();
        set.add("one");
        set.add("two");

        for (String element : set) {
            System.out.println(element);
        }
    }

    /**
     * java 算法面试的常见api
     *   int[] javaArray = new int[5];
     *
     *   List list1 = Arrays.asList(array1);
     *
     *   String[] array0 = new String[list0.size()];
     *   list0.toArray(array0);  // !!
     */
    private static void usefulApi() {
        Random r1 = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println("===" + r1.nextInt(7));   // 0~6
        }

        // 字符串转list<String>!!!!
        String str2 = "asdfghjk";
        List<String> lis = Arrays.asList(str2.split(""));
        for (String string : lis) {
            System.out.println(string);
        }
        // list<String>转字符串，list的内容以指定符号进行拼接!!
        System.out.println(String.join("", lis));

        // List转换为Array:
        ArrayList<String> list0 = new ArrayList<String>();
        list0.add("aa");
        list0.add("bb");
        String[] array0 = new String[list0.size()];
        list0.toArray(array0);  // !!
        // String[] array2 = (String[]) (list0.toArray());     // xx. Object[] 数组不能强转成String[]数组;
        // 数组转List:
        String[] array1 = {"a", "b", "c"};
        List list1 = Arrays.asList(array1);

        ArrayList<Integer> list = new ArrayList<Integer>();
        int[] nums2 = new int[2];


        // kotlin文件内调用扩展函数：对象.扩展方法()。sonA.toString2()
        // java文件内调用扩展函数：“文件名+Kt”.扩展方法(对象)。SonAKt.toString2(sonA)
        SonA sonA = new SonA(99);
        System.out.println("\n" + SonAKt.toString2(sonA));
    }

    private static void stringApi() {
        // 字符串反转
        String reversed = new StringBuilder("xyz").reverse().toString();

        String s1="0a";
        String q1 = s1 += (s1.charAt(0));       // "0"    字符串拼接char
        String q2 = s1 += (int)(s1.charAt(0));  // "48"  字符串拼接int
        // char和int 都能自动转String！！！
        System.out.println( "q1="+q1 + " q2=" + q2);

        boolean w = (s1.charAt(1) == 'a');
        boolean ww = (s1.charAt(1) == 97);
        System.out.println( "w="+w + " ww=" + ww + (char)(s1.charAt(1)-32));

        StringBuilder sb = new StringBuilder("originalString");
        sb.length();
        sb.setCharAt(1, ' ');   // 改字符串里的字符！



        //判断两个字符串相等记得用 equals， 而不是==

        "ss".indexOf("sda");
        "ss".indexOf('s');
        "ss".substring(0, 2);        // 全小写！！！


        char ff = 'a';      // c小写！！
        Character fff = 'a';
        // 自动装箱和拆箱
        Character chObj = ff; // 自动装箱
        char chBack = chObj;  // 自动拆箱
        char chBack2 = chObj.charValue();

        Character ffff = 'a';
        Character hh = 48+1;

        String s = " as";
        s.length();
        s.isEmpty();


        // 由于 int 是一个较大的数据类型（32位），而 char 是一个较小的数据类型（16位，
        // 所以在算术运算中，char 会被自动提升为 int。
        char c = 'a';
        int i = 10;
        int sum = c + i; // 这里'a'会被自动提升为int类型的ASCII码值97，然后与10相加
        System.out.println( " (c + i)=" + (c + i));     // 107。 char会被类型提升为int


        boolean a = (s.charAt(1) == 'a');
        System.out.println(a + "//" + (char)(s.charAt(1)-32));
        System.out.println((int) s.charAt(1));
        System.out.println((char) 65);
        System.out.println(s.indexOf("a")); // ok. 1
        System.out.println(s.indexOf('a')); // ok. 1


        String str= "asd";
        System.out.println("===" + str.contains("s")); // 对
        // System.out.println("===" + a.contains('s')); // 错
        System.out.println("===" + str.contains(str.charAt(1) + ""));  //  char转string!! 强转不行。
    }




}
