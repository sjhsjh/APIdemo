import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField

// 定义注解
annotation class Anno

// 使用3个注解修饰该类
@Deprecated("该类已经不推荐使用")
@Anno
@Suppress("UNCHECKED_CAST")
class ClassTest(age: Int) {
     var name: String = "KotlinVar"

    // 为该类定义一个构造器
    constructor() : this(20) {
    }

    // 定义一个有参数的构造器
    constructor(name: String) : this(15) {
        println("执行有参数的构造器:${name}")
    }

    // 定义一个无参数的info方法
    fun info() {
        println("执行无参数的info方法")
    }

    // 定义一个有参数的info方法
    fun info(str: String) {
        println("执行有参数的info方法，其str参数值：" + str)
    }

    // 定义一个测试用的嵌套类
    class Inner
}

// 为ClassTest定义扩展方法
fun ClassTest.bar() {
    println("扩展的bar方法 " + name)
}

// 为ClassTest定义扩展属性
val ClassTest.foo: Double
    get() = 2.4


fun main(args: Array<String>) {





    // 下面代码可以获取ClassTest对应的KClass
    val clazz = ClassTest::class
    // 通过constructors属性获取KClass对象所对应类的全部构造器
    val ctors = clazz.constructors;
    println("ClassTest的全部构造器如下：")
    ctors.forEach {
        println(it)
    }

    println("ClassTest的主构造器如下：")
    println(clazz.primaryConstructor)

    // 通过functions属性获取该KClass对象所对应类的全部方法(包含系统添加的equal等方法！！)
    var funs = clazz.functions
    println("ClassTest的全部方法如下：")
    funs.forEach {
        println(it)
    }

    /*
    通过declaredFunctions属性获取该KClass对象
    本身所声明的全部方法（不包括继承的方法） */
    var funs2 = clazz.declaredFunctions
    println("ClassTest本身声明的全部方法如下：")
    funs2.forEach {
        println(it)
    }

    /*
    通过declaredMemberFunctions属性获取该KClass对象
    本身所声明的全部成员方法（不包括继承的方法） */
    var memberFunctions = clazz.declaredMemberFunctions
    println("ClassTest本身声明的成员方法如下：")
    memberFunctions.forEach {
        println(it)
    }
    /*
    通过memberExtensionFunctions属性获取该KClass对象
    所代表类的全部扩展方法（不包括继承的方法） */
    var extensionFunctions = clazz.memberExtensionFunctions
    println("ClassTest的扩展方法如下：")
    extensionFunctions.forEach {
        println(it)
    }

    /*
    通过declaredMemberProperties属性获取该KClass对象
    本身所声明的全部成员属性（不包括继承的属性） */
    var memberProperties = clazz.declaredMemberProperties
    println("ClassTest本身声明的成员属性如下：")
    memberProperties.forEach {
        println(it)
    }

    /*
    通过memberExtensionProperties属性获取该KClass对象
    所代表类的全部扩展属性（不包括继承的属性） */
    var extensionProperties = clazz.memberExtensionProperties
    println("ClassTest的扩展属性如下：")
    extensionProperties.forEach {
        println(it)
    }

    // 通过annotations属性获取该KClass对象所对应类的全部注解
    val anns = clazz.annotations;
    println("ClassTest的全部注解如下：")
    anns.forEach {
        println(it)
    }

    println("该KClass元素上的@Anno注解为：" + clazz.findAnnotation<Anno>())

    // 通过nestedClasses属性获取该KClass对象所对应类的全部嵌套类（包括内部类和嵌套类）
    val inners = clazz.nestedClasses
    System.out.println("ClassTest的全部内部类如下：")
    inners.forEach {
        println(it)
    }
    // 通过supertypes属性获取该类的所有父类型（包括父类和父接口）
    println("ClassTest的父类型为：" + clazz.supertypes)

    reflect()
}

private fun reflect() {
    val instance = ClassTest::class.createInstance()    // 调用无参构造函数
    val kFunctionImpl = ClassTest::class.constructors.last()
    val instance2 = kFunctionImpl.call(99)      // 调用参数为Int的构造函数
    System.out.println("\nClassTest = " + instance2)


    var memberProperties = ClassTest::class.declaredMemberProperties
    memberProperties.forEach {
        println(it)
        System.out.println("it.get(instance) = " + it.get(instance))    // 属性不能为private？
    }

    val kProperty = ClassTest::name    // 属性引用KProperty1。属性不能为private？
    kProperty.set(instance2, "reflectName") // 需要传入需要反射的对象instance2
    println("\nkProperty = " + kProperty)
    println(kProperty.get(instance2))

    val kProperty2 = instance2::name   // 使用对象::获取KProperty时无需再传入反射对象！！！
    kProperty2.set("reflectName2")
    println(kProperty2.get())

    //“::函数名”代表特定函数的引用，效果跟lambda表达式相同。
    val f: (String) -> Unit = instance2::info
    println("\n  f(\"temp\") = " + f("temp"))

}