package com.example;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;


public class MyClass {

    public static void main(String[] args) {
        // genericity();
        // longPlusFloat();

        // usefulApi();
        // kotlinStaticTest();
        fanxingTest();

        synchronizedTest();
    }
    
    ////////////////////////////////////////////////////////////////
    public static void synchronizedTest() {
        ThreadA a = new ThreadA();
        a.setName("A");
        a.start();
        ThreadB b = new ThreadB();
        b.setName("B");
        b.start();
    }

    ////////////////////////////////////////////////////////////////
    interface I<T> {
        T funC(T a);
    }

    class IImpl implements I<Integer> {
        @Override
        public Integer funC(Integer a) {
            return a;
        }
        // ↓ 编译器自动生成
        // @Override
        // public Object funC(Object o) {
        //     return funC((Integer)o);
        // }
    }

    /**
     * 泛型擦除后，编译器会自动生成一个桥接方法，方法参数都是Object，然后会强转成对应类型再使用。
     * 该方法将类型参数替换为具体的类型，以确保多态性的正确性。
     */
    private static void fanxingTest() {
        Class<IImpl> ii = IImpl.class;
        Method[] methods = ii.getDeclaredMethods();
        for (Method method : methods) {
            // funC:Integer
            // funC:Object
            System.out.println(method.getName() + "  :" + method.getReturnType().getSimpleName());
        }
    }
    ////////////////////////////////////////////////////////////////

    /**
     * 很大的long与float相加 就会得到一个很大误差的值。将float改成double就正确。
     * skipHours * ONE_HOUR = 0.0
     * 1234567890000
     * 1234567954432
     * 3.00000128E8
     */
    private static void longPlusFloat() {
        long ONE_HOUR = 60 * 60 * 1000;
        long beginMs = 1234567890000L;
        float skipHours = 0f;
        System.out.println("skipHours * ONE_HOUR = " + skipHours * ONE_HOUR);
        System.out.println(beginMs);
        beginMs = (long) (beginMs + skipHours);  // 很大的long与float相加
        // beginMs += (beginMs+ skipHours * ONE_HOUR);
        System.out.println(beginMs);

        long a = 300000000L;
        float b = 1.23f;
        long c = 123;
        b = a + b + c;
        System.out.println(b);  // 3.00000128E8
    }

    private static void kotlinStaticTest() {
        String str = Utils.TAG_CONST;  // Utils public static 对象
        // Utils.TAG_NOT_CONST
        Utils.invokeStaticMethod();
        // Utils.invokeNonStaticMethod();  // with @JvmStatic. 加上@JvmStatic修饰静态方法可以让java文件调用时省去Companion

        Utils.comObj.getTAG_NOT_CONST();
        Utils.comObj.getQqq();
        Utils.comObj.invokeStaticMethod();
        Utils.comObj.invokeNonStaticMethod();      // without @JvmStatic

        // ObjectClass.print();  // with @JvmStatic.
        ObjectClass.INSTANCE.print(); // without @JvmStatic
        System.out.println(ObjectClass.INSTANCE);

        System.out.println(ObjectClass.User);
        System.out.println(ObjectClass.User2);

        // java中获取kotlin中的KClass
        KClass kClass = JvmClassMappingKt.getKotlinClass(Utils.comObj.getQqq().getClass());

        // interface Source<T> {
        //     T nextT();
        // }
        // void demo(Source<? extends Object> strs) {
        //     Source<Object> objects = strs; // ！！！在 Java 中不允许
        // }
    }

    private static void javaApi() {
        String s = " as";
        s.length();
        s.isEmpty();

        int[] arr = new int[5];
        int l = arr.length;


        boolean a = (s.charAt(1) == 'a');
        System.out.println(a + "//" + s.charAt(1));
        System.out.println((int) s.charAt(1));
        System.out.println((char) 65);
        System.out.println(s.indexOf("a"));
        System.out.println(s.indexOf('a'));

        List list = new ArrayList<String>();
        list.size();

        //  hashmap遍历
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        
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

    //用动物，猫，加菲猫的继承关系说明extends与super在集合中的意义
    private static void genericity() {
        // List<Animal> animal = new ArrayList<Animal>();        //动物
        // List<Cat> cat = new ArrayList<Cat>();                 //猫
        // List<Garfield> garfield = new ArrayList<Garfield>();  //加菲猫
        //
        // animal.add(new Animal());
        // cat.add(new Cat());
        // garfield.add(new Garfield());
        //
        // //第二段：测试赋值操作
        // //下行编译出错。只能赋值Cat或Cat子类集合
        // List<? extends Cat> extendsCatFromAnimal = animal;
        // List<? extends Cat> extendsCatFromCat = cat;
        // List<? extends Cat> extendsCatFromGarfield = garfield;
        //
        // List<? super Cat> superCatFromAnimal = animal;
        // List<? super Cat> superCatFromCat = cat;
        // //下行编译出错。只能赋值Cat或着Cat父类集合
        // List<? super Cat> superCatFromGarfield = garfield;
        //
        // //第三段：测试add方法
        // //下面三行中所有的<? extends T>都无法进行add操作，编译出错
        // extendsCatFromCat.add(new Animal());
        // extendsCatFromCat.add(new Cat());// 就算传入上限父类型都有可能跟存储的子类型类型冲突!!!!
        // extendsCatFromCat.add(new Garfield());
        // extendsCatFromCat.add(null);    // 只能存入null
        //
        // //下行编译出错。只能添加Cat或者Cat的子类集合。
        // superCatFromCat.add(new Animal());
        // superCatFromCat.add(new Cat());
        // superCatFromCat.add(new Garfield());
        // superCatFromAnimal.add(new Garfield());
        //
        // //第四段：测试get方法
        // //以下extends操作能够返回元素
        // Animal catExtends3 = extendsCatFromCat.get(0);  // 能保证取出来是上限类型!!!!
        // Object catExtends2 = extendsCatFromCat.get(0);
        // Cat catExtends1 = extendsCatFromCat.get(0);
        // //下行编译错误。虽然Cat集合从Garfield赋值而来，但类型擦除后，是不知道的; 需要强转
        // Garfield cat2 = extendsCatFromGarfield.get(0);   // (Garfield) extendsCatFromGarfield.get(0); // ok
        //
        // //所有的super操作能够返回元素，但是泛型丢失，只能返回object对象
        // Object object1 = superCatFromCat.get(0);
        // Animal object = superCatFromCat.get(0);
        // Cat object3 = superCatFromCat.get(0);
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

class Animal {
    public Animal() {
        System.out.println("Animal construct");     // 1
    }

    public void aaa() {
    }

}

class Cat extends Animal {

    private Garfield xx = new Garfield();

    public Cat() {
        System.out.println("Cat construct");    // 3
    }

    public void ccc() {
    }

}

class Garfield extends Object {
    public Garfield() {
        System.out.println("Garfield construct");  // 2
    }

    public void ggg() {
    }
}