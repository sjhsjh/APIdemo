package com.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import java.lang.StringBuilder

fun main(args: Array<String>) {


    coroutines()

//    collectionApi()
//    demo2()
//    println("\n\n\n")
//    var sonA = SonA(8)
//    println("\n")
//    println(sonA.memberUse)
//    sonA.InnerClass().printInnerClass()
//    println("==== ${MyObjectA(8)}")
}

fun coroutines() {
    // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test
    Dispatchers.setMain(newSingleThreadContext("MyMain"))   // java程序setMain之后Dispatchers.Main才能用！！！

//    val job: Job = GlobalScope.launch {
//        // 在后台启动一个新的协程并继续
//        println(
//            "==launch 1==" + Thread.currentThread().name + "————" + coroutineContext[Job])  // DefaultDispatcher-worker-1
//        delay(1000L)
//        launch(Dispatchers.Main) {
//            println("==launch 4==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//        }
//        println(
//            "==launch 2==" + Thread.currentThread().name + "————" + coroutineContext[Job])  // DefaultDispatcher-worker-4
//        // withContext的返回值是lambda表达式内最后一条表达式的值！
//        var x = withContext(Dispatchers.Main) {
//            println("==launch 3==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//            5
//        }
//    }
////    job.join()    // 作用类似Thread.join()
////    job.cancel()

//    val deferred = GlobalScope.async(CoroutineName("myCoroutineName")) {
//        delay(2000L)
//        println(
//            "==async==" + Thread.currentThread().name + "————" + coroutineContext[Job])  // DefaultDispatcher-worker-3
//        5 * 5
//    }
//    GlobalScope.launch(Dispatchers.Main) {
//        println("launch main await = " + deferred.await())
//    }
//    runBlocking {
//        // await:返回协程执行的结果。只能在协程内使用。await返回值是async的返回值. await会等待对应的async执行完毕
//        println("await = " + deferred.await() + "————" + Thread.currentThread().name)
//    }

//    // 协程里再开协程，子协程也是并发的
//    GlobalScope.launch(Dispatchers.IO) {
//
//        GlobalScope.launch(Dispatchers.Main) {
//            delay(2000L)
//            println("子协程 launch main " + "————" + Thread.currentThread().name)
//        }
//        println("launch IO begin " + "————" + Thread.currentThread().name)
//    }

//    runBlocking {
//        launch(Dispatchers.Main) {
//            // 加了Dispatchers.Main就是MyMain，不加打印就是main
//            println("==main runBlocking 1±0.5 :  ${Thread.currentThread().name + "————" + coroutineContext[Job]}")
//        }
//
//        println("==runBlocking 1 ==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//        delay(2000L)  // runBlocking里加delay()可以阻塞当前的线程，等价于Thread.sleep()
//        // 挂起当前协程
//        withContext(Dispatchers.IO) {
//            println("==withContext IO  1.8  :  ${Thread.currentThread().name + "————" + coroutineContext[Job]}")
//        }
//        println("==runBlocking 2 ==" + Thread.currentThread().name + "————" + coroutineContext[Job])  // main!!!
//    }

    // runBlocking可以阻塞等待它内部的所有协程完成。
//    runBlocking {
//        println("==runBlocking 11 " + Thread.currentThread().name + "————" + coroutineContext[Job])
//        launch {
//            // 在 runBlocking 作用域中启动一个新协程。runBlocking内使用launch类似 handler.postDelay.
//            delay(1000L)
//            println("==runBlocking 33 " + Thread.currentThread().name + "————" + coroutineContext[Job]) // main
//        }
//        println("==runBlocking 22 " + Thread.currentThread().name + "————" + coroutineContext[Job])
//    }
//    println("Hello====1====\n\n")

//    CoroutineScope(Dispatchers.Main).launch {
//        launch {
//            delay(500L)
//            println("33 Task from nested launch " + Thread.currentThread().name + "————" + coroutineContext[Job])
//        }
//        delay(100L)
//        println(
//            "11 Task from coroutine scope " + Thread.currentThread().name + "————" + coroutineContext[Job]) // 这一行会在内嵌 launch 之前输出
//    }
//    println("CoroutineScope is over")

//    GlobalScope.launch {
//        launch {
//            delay(200L)
//            println("2、Task from runBlocking  " + Thread.currentThread().name + "————" + coroutineContext[Job])
//        }
//
//        coroutineScope {
//            // 创建新的协程作用范围
//            launch {
//                delay(500L)
//                println("3、Task from nested launch")
//            }
//
//            delay(100L)
//            println("1、Task from coroutine scope") // 在嵌套的 launch 之前, 这一行会打印
//        }
//        println("4、Coroutine scope is over") // 直到嵌套的 launch 运行结束后, 这一行才会打印
//    }

//    runBlocking {
//        // 启动一个协程来处理某种传入请求（request）
//        val jobRequest = launch {
//            repeat(3) { i ->
//                // 启动少量的子作业
//                launch {
//                    delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒的时间
//                    println("Coroutine $i is done")
//                }
//            }
//            println("11  request: I'm done and I don't explicitly join my children that are still active")
//        }
//        jobRequest.join() // 阻塞等待请求的完成，包括其所有子协程
//        println("33 Now processing of the request is complete")
//    }
//    println("Hello====2====\n\n")


    // delay挂起前后使用不同的线程！！！相同的协程！！
//    for (i in 1..1_00L)
//        GlobalScope.launch {
//            val c = Thread.currentThread().name + "————" + coroutineContext[Job]
//            delay(1000L)
//            val d = Thread.currentThread().name + "————" + coroutineContext[Job]
//            if (!c.equals(d)) {
//                println("=====!c.equals(d)=======c : " + c + "          d : " + d)
//            }
//        }


    // 网络请求，并return请求结果
//    GlobalScope.launch(Dispatchers.Main) {
//        // 两个withContext(Dispatchers.IO)实现异步任务串行!!!!!!!
//        val result1 = withContext(Dispatchers.IO) {
//            delay(2000L)
//            println("result 1")
//        }
//        val result2 = withContext(Dispatchers.IO) {
//            delay(100L)
//            println("result 2")
//        }
//        println("world======updateUI(result)======")
//    }

    // 两个都运行在主线程内的协程,父协程通常优先执行
//    GlobalScope.launch(Dispatchers.Main) {
//        println("==launch Main 0.5==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//        runBlocking {
//            println("==launch Main 0.6==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//        }
//        var x = 0
//        launch(Dispatchers.Main) {
//            //            delay(1000L)
//            println("==launch Main 2== x = $x  " + Thread.currentThread().name + "————" + coroutineContext[Job])
//        }
//
////        var xx = withContext(Dispatchers.Main) {
////            println("==withContext Main==" + Thread.currentThread().name + "————" + coroutineContext[Job])
////            5
////        }
////        delay(1000L)
//        for (i in 0..100_0000L) {
//            x++
//        }
//        println("==launch Main 1==" + Thread.currentThread().name + "————" + coroutineContext[Job])
//    }

    // 协程挂起完成后，要等待线程空闲时才能继续执行
//    suspend fun getResponse(): String {
////        println("2、协程 开始执行，时间:  ${System.currentTimeMillis()}  " + Thread.currentThread().name)
//        delay(1)
//        println(" end. getResponse 开始执行，时间:  ${System.currentTimeMillis()}  " + Thread.currentThread().name)
//        return "response"
//    }
//
//    GlobalScope.launch(Dispatchers.Unconfined) {
//        println("1、协程 开始执行，时间:  ${System.currentTimeMillis()}  " + Thread.currentThread().name)
//        val response = getResponse()
//    }
//
//    for (i in 1..1000) {
//        if (i == 1 || i == 1000) {
//            println("主线程打印第$i 次，时间:  ${System.currentTimeMillis()}")
//        }
//    }



    println("Hello===3=====\n\n")


//    val mainScope = MainScope()
//    mainScope.launch {  }
//    mainScope.cancel()

    Thread.sleep(3000)
    println("Stop")
}


private fun collectionApi() {
    // 过滤。kotlin集合的filter方法，将集合的item用lambda表达式进行过滤，只留下执行lambda表达式返回true的元素
    fun isOdd(x: Int) = x % 2 != 0

    val numbers = listOf(1, 2, 3, 4, 5)
    println(numbers.filter(::isOdd))   // [1, 3, 5]

    val matches: (Regex, CharSequence) -> Boolean = Regex::matches
    val numberRegex = "\\d+".toRegex()
    val list = listOf("abc", "124", "a70")
    println(list.filter(numberRegex::matches))  // 某个对象的方法
    println(list.filter({
        numberRegex.matches(it)     // 注意不需要return！！
    }))

    // 映射。kotlin集合的map方法，将集合的item批量处理成另一种类型的值的list
    val strs = listOf("a", "bc", "def")
    println(strs.map(String::length))   // [1, 2, 3]

    // kotlin集合的forEach方法,对集合的每个item都进行某些处理
    strs.forEach {
        println(it)
    }
    val list2 = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    // count
    val count = list2.map { it + 2 }.count { it % 2 == 0 }
    println("count = " + count)

    // first方法找不到会抛异常
    list2.firstOrNull()
    println(list2.firstOrNull {
        it % 5 == 0
    })

    // asSequence 懒处理. map filter等返回Sequence的都是中间操作！！！都存在遍历操作。asSequence可以将它们合并起来,只存在一次遍历,提升性能
    val list3 = list2.asSequence().map { it + 2 }.filter { it % 2 == 0 }.toList()
    println(list3)
}

private fun demo2() {
    // object
    ObjectClass.print()
    // companion object
    Utils.TAG_CONST
    Utils.TAG_NOT_CONST
    // kotlin文件中“Utils”实际上引用的是Utils.comObj对象！！MyClass 等于 MyClass.Companion
    Utils.comObj.invokeNonStaticMethod()
    Utils.invokeNonStaticMethod()
    val companionObj1 = Utils.comObj
    val companionObj2 = Utils
    println("Utils.comObj === " + companionObj1 + "\nUtils === " + companionObj2)

    // 函数形参默认值
    defaultParam(a = 1, b = 2, normal = true) // 使用命名参数(形参变量名)来调用函数！！！
    defaultParam(1) // 11
    defaultParam(1, 2)  // 3
    defaultParam(1, 2, true)    // 3
    B().foo()   // BBB i = 10!!!

    // 命名参数
    // 所有位置参数都要放在第一个命名参数之前。调用方法时所有非命名参数(隐式声明)必须全部在所有命名参数(显式声明)前面！使用默认值后面的参数都要显式声明。
    // 理解：如果不使用默认值，则直接传入参数(隐式声明)；一旦有一个参数使用默认值，则参数的位置发生变化，则该参数后面的参数都必须使用命名参数(显式声明)
    namedParameter("", aa = false, bb = false, cc = false, word = 'e')

    optionalParam(1, 2, 3)
    intAndInteger()

    var result = { base: Int ->
        var a = base * 10
        a      // lambda表达式的返回值
    }(8)    // 这样result 就得到lambda表达式的返回值。
    println(result)


    // lambda 表达式使用
    val sum: (q: Int, w: Int) -> Int = { x, y: Int -> x + y }
    val sum2: (Int) -> Int = { y: Int -> 3 * y }
//    ints.filter { it > 0 } // 这个字面值是“(it: Int) -> Boolean”类型的
//    ints.filter { (it: Int) -> it > 0 } // 这个字面值是“(it: Int) -> Boolean”类型的
    lambdaFunction { y -> 2 * y }
    lambdaFunction { it: Int -> it * 3 }
    lambdaFunction { it -> it * 4 }
    lambdaFunction { it * 5 }

    // 匿名函数与lambda表达式，两者类型都是Function2<java.lang.Integer, java.lang.Integer, java.lang.Integer>
    var anoFunction = fun(x: Int, y: Int): Int {
        return x + y
    }
    var lambdaFunction = { x: Int, y: Int -> x + y }
    println("anoFunction = " + anoFunction(2, 4))
    println("lambdaFunction = " + lambdaFunction(3, 5))


    // 每个lambda表达式or匿名函数都持有同一闭包下所有变量的一份副本！！！
    fun makeList(str: String): () -> List<String> {
        var list = mutableListOf<String>()
        fun add(): List<String> {
            list.add(str)
            return list
        }
        return ::add
    }

    val add1 = makeList("111")  // 同一个函数实现访问同一份变量副本
    println(add1())             // [111]
    println(add1())             // [111, 111]

    val add2 = makeList("111")
    println(add2())             // [111]
    println(add2())             // [111, 111]


    // 函数声明与函数实现，与接口很相似！！
    var test: (Int) -> Int = ::fun1
    var test2 = ::fun1  // 函数引用
    var test3: (Int) -> Int = { x -> x * 2 }
    println(fun1(5))
    println(test(5))
    println(test3(5))
    println(test.javaClass)

    val items = listOf(1, 2, 3, 4, 5)
    // Lambda 表达式是花括号括起来的代码块。
    items.fold(0, { acc: Int, i: Int ->

        print("acc = $acc, i = $i, ")
        val result = acc + i
        println("result = $result")
        // lambda 表达式中的最后一个表达式是返回值：
        result
//        return@fold
    })
    // lambda 表达式的参数类型是可选的，如果能够推断出来的话：
    val joinedToString = items.fold("Elements:", { acc, i -> acc + " " + i })
    // 函数引用也可以用于高阶函数调用：
    val product = items.fold(1, Int::times)


    // 将类的方法赋值给变量
    val plusFunction0 = MyObjectB::plus
    val plusFunction: (MyObjectB, Int) -> Int = MyObjectB::plus  // javaClass = class com.example.MyTestKt$main$plus$1
    println(plusFunction(MyObjectB(100), 5))

    // componentN方法与解构。解构作用：将对象的成员变量快速定义为N个变量。
    // entry可被解构为(key, value)。data class成员也被自动解构。
    var objB = MyObjectB(6)
    var (num, name) = objB     // 将objB对象解构给2个变量
    var (_, name2) = objB      //  不需要解构的变量使用“_”来占位
    println("num = " + num)
    println("name = " + name)

    var pair = Pair<String, Int>("one", 1)  // 任意两种类型组合成了一种新类型
    var (fir, sec) = pair                   // " (fir, sec)"是一个它们所属的对象
    println("fir = " + fir + " sec = " + sec)
    val triple = Triple<String, Int, Boolean>("two", 2, true)
}

private fun demo() {
    var sonA0 = SonA(100, "sona") // 查看父类子类的初始化顺序

    // 基本类型
    var a: Int = 5_2
    println("a = " + a)
    println("a-2 = ${a - 2}")

    var b: Byte = 114 ?: 2
    println(b.toString())
    var c = 4f
    var d = 5.0

    var str3: String = if (b > 100) {
        var aa = ">100"     // 这条表达式返回值是Unit！
        aa
    } else if (b > 50) ">50"
    else "<50"
    println(str3)

    // enum class
    var color: Color = Color.RED
    println(color.rgb)
    color.getColor()
    Color.BLUE.getColor()

    println(Color.BLUE.ordinal) // 2
    println(Color.BLUE > Color.RED)

    var array: Array<Color> = Color.values()
    println(array[0])

    var col: Color = Color.valueOf("GREEN")  // 大小写必须匹配
    println(col)

    // “==”与“===”
    var str1: String = String(StringBuilder("xyz"))
    var str2 = "xyz"
    println(str1 == str2)    // equal。true
    println(str1 === str2)   // 比较内存地址。false

    // 遍历
    print(".. ")
    for (i in 1..3) print(i)        // 全闭区间
    print("\nuntil ")
    for (i in 1 until 3) print(i)   // 左闭右开
    print("\ndownTo ")
    for (i in 3 downTo 1) print(i)  // 全闭区间
    println("")

    for (i in 4 downTo 10) print(i) // 不报错但不执行循环体
    for (i in 12 until 4) print(i)  // 不报错但不执行循环体

    // data class
    var bean1 = UserBean("sjh1", 11)
    var bean2 = UserBean("sjh1", 11)
    println(bean1)
    println(bean2)
    println(bean1.equals(bean2))
    println((bean1 == bean2))
    var (name, age) = bean1
    println("name = " + name + " age = " + age)

    // 构造函数
    var myObjectA = MyObjectA(8, 9)
    println("\n" + myObjectA)
    println(myObjectA.javaClass)   // class com.example.MyObjectA

    // get and set
    var myObjectB = MyObjectB(9)
    println("str ==== " + myObjectB.str)
    println("bar.javaClass === " + myObjectB.bar.javaClass)
    println("bar ===" + myObjectB.bar)
    println("foo ===" + myObjectB.foo)

    // 扩展函数 or 扩展属性
    var sonA = SonA(8)
    println("扩展函数 : " + sonA.toString2())
    sonA.result = "i am result."
    println("扩展属性 : " + sonA.result)

    // 对象表达式
    var count = 0
    val anonymous = object {    // 相当于new Object() 并重写对应的属性和方法。变量的父类是Any
        var x: Int = 0
        override fun toString(): String {
            return "我是匿名对象"
        }

        fun getIncreaseCount(): Int {
//            sonA = SonA(99)     // kotlin匿名内部类中访问局部变量时无需加final修饰，变量能改变值。java需要！！
            return ++count
        }
    }
    println("getIncreaseCount = " + anonymous.getIncreaseCount() + " count = " + count)
    println(anonymous)


    val ab = object : MyObjectB(1), ILog {
        override fun printB() {
            super.printB()
        }

        override fun log() {
        }
    }
    println("匿名内部类的javaClass = " + ab) // com.example.MyTestKt$main$ab$1@6e0be858。文件名$方法名$变量名$1

    (ab as java.lang.Object).wait()    // 一定要在kotlin内使用wait方法时就这样强转
}

/**
 * fun defaultParam(a: Int = 10, b: Int)
 * defaultParam(b = 1)  // 使用默认值 a = 10
 */
// 默认值通过类型后面的 = 及给出的值来定义。这可以减少重载数量。
fun defaultParam(a: Int, b: Int = 10, normal: Boolean = true) {
    println(a + b)
}

// 可变数量的参数vararg
// IntArray类型(即int[3])或Array <Any>类型，不是Array<Int> 类型
fun optionalParam(vararg intArray: Int) {
    println(intArray[0])
    println(intArray.javaClass)   // class [I
}

fun namedParameter(
    str: String,
    aa: Boolean = true,
    bb: Boolean = true,
    cc: Boolean = false,
    word: Char
) {/*……*/
}

// kotlin内的int 与 Integer：
fun intAndInteger(): Int {
    var a = 1               // unbox
    var b: Int? = 2         // box
    println(a.javaClass)    // int
    println(b?.javaClass)   // int  （没装箱吧？）

    var objArray = Array(3, { 111 })
    println(
        "=====objArray[0].javaClass=======" + objArray[0].javaClass)  // 1、坑:Array<Int>把元素取出来就成了基本类型！！get和set操作的都是基本类型。
    println("=====objArray.javaClass=======" + objArray.javaClass)        // class [Ljava.lang.Integer;

    var intArray = IntArray(5, { it + 10 })
    println(intArray.javaClass)   // class [I
    for (x in intArray) {
        objArray[0] = x
    }

    var objArray2: Array<Any>
    objArray2 = arrayOf(A(), 11, "22")   // Object[3]
    objArray2[1] = 99                    // Array<Any>数组元素赋值时也会自动装箱

//    val one: Integer = 1 // error: "The integer literal does not conform to the expected type Integer"
//    val one2: Int = Int(1) // error: "Cannot access '<init>': it is private in 'Int'
//    val two2: Integer = Integer.valueOf(22)   // 2、坑:提示valueOf返回 “Int！”？？
    val two: Integer = Integer(2)
    println(two.javaClass)      // Integer!!!   kotlin获取对象的class的方法①
    println(two::class.java)    // kotlin获取对象的class的方法②
    println(Int::class.java)

    val three: Any = 3         // Any被赋值成基本类型时会自动装箱
    println(three.javaClass)   // Integer!!!

    // kotlin的class对象KClass：class com.example.MyClass (Kotlin reflection is not available)
    println("====" + MyClass::class)
    println("====" + MyClass()::class)

    val c = MyClass::class
    println("====" + c.java)         // java的class对象：class com.example.MyClass
    println("====" + c.java.kotlin)  // kClass.java获取java的class对象，javaClass.kotlin 获取kotlin的KClass对象！


    when (three) {
        is Int -> println("is Int")            // true    3、坑:实际对象为Integer的Any对象的is Int和is Integer都为true！！
        is Integer -> println("is Integer")    // true
        else -> println("is other")
    }

    return a
}

fun lambdaFunction(sum2: (Int) -> Int) {
    println(sum2(7))   // Function1<java.lang.Integer, java.lang.Integer>
}

// 单表达式函数不写返回类型时，其返回类型是单表达式的返回类型，并不一定都是返回void！ 当返回值类型可由编译器推断时，显式声明返回类型是可选的。
fun fun1(x: Int) = x * 2 // 省略 :Int

fun fun2(x: Int) = println()  // 省略 :Unit

interface ILog {
    fun log()
}

open class A {
    open fun foo(i: Int = 10) {
        println("AAA  i = " + i)
    }
}

class B : A() {
    // 重写的方法的所有参数都不能有默认值。重写方法的形参的默认参数与基类变量默认值相同！！覆盖方法总是使用与基类型方法相同的默认参数值。
    override fun foo(i: Int) {
        println("BBB i = " + i)
    }
}
//class Ss:Sjh(){   // 密封类的直接子类必须跟密封类在同一文件中
//    override fun asd() {
//    }
//}

fun <T, R> Collection<T>.fold(
    initial: R,
    combine: (acc: R, nextElement: T) -> R
): R {
    var accumulator: R = initial
    for (element: T in this) {
//        accumulator = combine(accumulator, element)
        combine.invoke(accumulator, element)
    }
    return accumulator
}