package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MyClass {

    public static void main(String[] args) {

        // usefulApi();
        kotlinStaticTest();
    }

    private static void kotlinStaticTest() {
        // Utils.TAG_CONST
        // Utils.TAG_NOT_CONST
        // Utils.invokeStaticMethod();
        // Utils.invokeNonStaticMethod();  // with @JvmStatic. 加上@JvmStatic修饰静态方法可以让java文件调用时省去Companion
        //
        // Utils.comObj.TAG_CONST
        Utils.comObj.getTAG_NOT_CONST();
        Utils.comObj.getQqq();
        Utils.comObj.invokeStaticMethod();
        Utils.comObj.invokeNonStaticMethod();      // without @JvmStatic


        // ObjectClass.print();  // with @JvmStatic.
        ObjectClass.INSTANCE.print(); // without @JvmStatic
        System.out.println(ObjectClass.INSTANCE);

        System.out.println(ObjectClass.User);
        System.out.println(ObjectClass.User2);
    }

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


        // kotlin文件内调用扩展函数：对象.扩展方法()。sonA.toString2()
        // java文件内调用扩展函数：“文件名+Kt”.扩展方法(对象)。SonAKt.toString2(sonA)
        SonA sonA = new SonA(99);
        System.out.println("\n" + SonAKt.toString2(sonA));
    }

    private static void collectionDelete() {
        // 不要在 foreach 循环里进行元素的 remove/add 操作。
        // 倒序遍历删除优点：删除元素后不影响待遍历的元素的位置。
        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(1);
        list2.add(2);
        list2.add(2);
        list2.add(2);
        list2.add(2);
        for (int i = 0; i < list2.size(); i++) {     // list.size()是变化的，用它是对的.若使用固定值则会数组越界
            if (list2.get(i) == 2) {
                list2.remove(i);
            }
        }
        System.out.println(list2); // [1, 2, 2] 错在删除后第一个左移的元素没有遍历到。


        List<Integer> list3 = new ArrayList<Integer>();
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3.add(2);
        list3.add(2);
        Iterator<Integer> iterator = list3.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 3) {
                // 使用Iterator遍历集合，若要remove 元素请使用 iterator.remove();而不是 list.remove(i);因为iterator内记录集合大小必须与list大小一直相等。
                // list3.remove(integer);
                iterator.remove();
            }
        }
        System.out.println(list3);
    }
}

abstract class Base {
    String str = this.toString();
    int code = cal();
    abstract int cal();
}

class Son extends Base {
    @Override
    int cal() {
        return 110;
    }
    // System.out.println(new Son().code); // java ok, kotlin incorrect.
}

abstract class Grandson extends Son {
    abstract int cal();
}