package com.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    runBlocking {

    }
    backpressure()
}

/**
 * 普通flow，冷流
 */
fun flowTest() = runBlocking {
    // ①
    (1..3).asFlow().collect { value ->
        println(value)
        println( "///")
    }
    // ②
    flowOf(1, 2, 3).collect { }

    // ③
    flow {
        // flow{ ... } 内部可以调用 suspend 函数；
        delay(1000)
        emit(11)
        delay(1000)
        emit(22)
        delay(1000)
        emit(33)
    }.collect {
        delay(3000)
        println( "result hi-----")
        println(it)
    }

    flowOf(1, 2, 3)
    // 等价于 ↓
    flow {
        emit(1)
        emit(2)
        emit(3)
        println( "result =fuck============= ")
    }

    println( "result = " + flowOf(1, 2, 3).first())

    // 默认情况下flow内 和collect内是串行执行 且都在同一线程！！！
//	flow {
//		println("post 1")
//		emit(11)    // emit后立刻挂起！然后执行collect结构体耗时代码，再往下执行！！!
//		println("post 2")
//		emit(22)
//		println("post 3")
//
//	}.collect {
//		println("collect start")
//		delay(5000)
//		println(it)
//		println("collect over")
//	}

    println("single:等待flow结束，且只允许发射一个值，否则抛异常 " + flowOf(1).single())

    flowOf(1, 2, 3).launchIn(CoroutineScope(Dispatchers.IO))


    Dispatchers.setMain(newSingleThreadContext("MyMain"))   // java程序setMain之后Dispatchers.Main才能用！！！
    withContext(Dispatchers.IO) {
        (1..3).asFlow().collect { value ->
            println(value)
            println("IO " + Thread.currentThread().name)
        }
        println("finish  collect 会挂起协程直到 flow 完成!!!")
    }
    withContext(Dispatchers.Main) {
        (1..3).asFlow().collect { value ->
            println(value)
            println("Main " + Thread.currentThread().name)
        }
    }

//    flow {
//        emit(1)
//        delay(1000)
//        println("test exception")
//        throw Exception("test exception")
//        emit(2)  // Unreachable
//    }.collect {
//        println(" exception: ${it}")
//    }

    flowOf(1, 2, 3).onStart {
        delay(1000)
        println("onStart. 在耗时操作时可以用来做 loading。")
    }.onEach {
        // 此操作符返回流，onEach后面还可接其他操作符
        delay(1000)
        println("onEach " + it)
    }.onCompletion { cause ->
        //  onCompletion 只能判断是否出现了异常，并不能捕获异常！！！
        if (cause != null) {
            println("flow completed exception")
        } else {
            println("onCompletion")
        }
    }.catch { ex ->
        println("catch exception: ${ex.message}")
    }.collect {}


    // 串行有序 耗时任务，不定时发射结果
//	val intSequence = sequence<Int> {
//		// Sequence 是同步调用，是阻塞的，无法调用其它的挂起函数,如delay！！
//		// delay(1000) xxx
//		Thread.sleep(1000) // 模拟耗时任务1
//		println("a")
//		yield(1)
//		println("b")
//
//		Thread.sleep(1000) // 模拟耗时任务2
//		yield(2)
//		Thread.sleep(1000) // 模拟耗时任务3
//		yield(3)
//	}
//
//	intSequence.forEach {
//		println(it)
//	}


//	val sum = (1..5).asFlow().reduce { a, b ->
//		println("a = " + a) // a是上一次的运算结果
//		println("b = " + b) // b是元素遍历
//		a + b
//	}
//	println("sum = " + sum)
//
//	val sum = (1..5).asFlow().fold(100) { a, b ->
//		a + b
//	}
//	println("sum = " + sum) // 115


}

fun hotFlow() = runBlocking {
    val mutableStateFlow = MutableStateFlow<Int>(0)
//    // 不想处理默认值的方法：
//    mutableStateFlow.drop(1).collect {
//        println("collect")
//    }


    //提前订阅的协程
    val stateJob = launch {
        mutableStateFlow.collect {
            println("collect before $it")
        }
        // 无法执行。 StateFlow 的收集者调用 collect 会挂起当前协程，而且永远不会结束。只能用Job.cancel()来终止collect！
        println("collect over")
    }
//	stateJob.cancel() // 取消状态流收集


    //修改StateFlow的值,分别修改为0-100
    launch {
        for (i in 0..10) {
            if(i == 0) 		println(" first  ")
            // 连续的setVlaue可能会丢失中途设置的值！！除非两次setVlaue之间加上delay(1)
            // 直接给value属性赋值是同步的，它会立即更新状态值，并通知所有的观察者
            mutableStateFlow.value = i
            println(" send  $i")
//			delay(1)
        }
    }

//	稍后订阅的协程
    launch {
        mutableStateFlow.collect {
            println("collect after $it")
        }
    }
    println("bottom")


//	val f = flow {
//		for (i in 0..10) {
//			if (i == 0) println(" first  ")
//			emit(i)
//			println(" send  $i")
//		}
//	}
//	//提前订阅的协程
//	launch {
//		f.collect {
//			println("collect before $it")
//		}
//		println("普通flow collect over")
//	}
//
//	//修改StateFlow的值,分别修改为0-100
//	launch {
//		for (i in 0..10) {
//			if(i == 0) 		println(" first  ")
//			emit(i)
//			println(" send  $i")
//		}
//	}
//
//	//稍后订阅的协程
//	launch {
//		f.collect {
//			println("collect after $it")
//		}
//	}

}


fun backpressure () = runBlocking {
    val time = measureTimeMillis {
        flow {
            (1..5).forEach {
                delay(200)
                println("emit: $it, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
                emit(it)
            }
        }
//       .flowOn(Dispatchers.Default)   // 效果约等于 .buffer()
        .buffer()       // 生产者和消费者在 同一线程，不同协程
//       .conflate()    // 生产者和消费者在 同一线程，不同协程,collect读数据只从“未消费队列”读取最新数据
        .collect {
            // 消费效率较低
            println("Collect $it in, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
            delay(500)
            println("Collect $it out, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
        }
    }
    println("time: $time")


//    val time = measureTimeMillis {
//        flow {
//            (1..5).forEach {
//                delay(200)
//                println("emit: $it, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
//                emit(it)
//            }
//        }.collectLatest {
//            // 消费效率较低
//            println("Collect $it, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
//            delay(500)
//            println("Collect $it done, ${System.currentTimeMillis()}, ${Thread.currentThread().name}")
//        }
//    }
//    println("time: $time")



}