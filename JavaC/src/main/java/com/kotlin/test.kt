import com.example.MyObjectB
import java.util.Arrays

fun main(args: Array<String>) {
	MyObjectB(1).internalVar = 22


	out@ for (x in 0..10) {
		for (y in 1..9) {
			if (y == 2) {
				println(x)
				break@out
			}
		}
	}

//	when {
//		false -> {
//			println(11)
//		}
//		true -> println(22)
//	}

	printArray()
	printCollection()
//	printFunction()

	// 数组转List:
	val array1 = arrayOf("a", "b", "c")
	val list1 = Arrays.asList(*array1)    // 注意Arrays.asList(array1)得到的list第一个元素是array1
	// list转数组
	var list2 = listOf(1, 3, 5)
	var array2: Array<Int> = list2.toTypedArray()    // Integer[3] ,比java方便,java的数组需要自己定义

}

fun printArray() {
	println("====================printArray====================")
	// 所有数组类型：Array<Any>、Array<Int>、IntArray
	var objArray:Array<Any>

	var obj: MyClass2 = MyClass2()
	// 静态初始化数组
	var arr1 = arrayOf(obj, obj, obj)
	var arr2 = arrayOf("ss", "22", "11")	// ② 穷举初始化,自动推测类型和大小
	var arr3 = intArrayOf(1, 2, 10)			// 注意是小写开头。booleanArrayOf()、charArrayOf()、floatArrayOf()等

	// 指定类型和数组长度
	var arr4 = arrayOfNulls<String>(5) // ①声明object类型数组Object[5] : var arr4 = arrayOfNulls<Any>(5)
	var arr5 = emptyArray<String>()    		// 长度为0的数组 : String[0]
	// 高端用法
	var arr6 = Array(3, { "ax" })     // ③指定size和初始值！！
	var arr66 = Array(3, { obj })
	var arr666 = Array(3, { it.toString() })    // Integer[3]:0, 1, 2。lambda表达式，it代表数组的索引值！根据下标生成对应位置的元素。it需要放在大括号中。
	var arr7 = IntArray(5, { it + 10 })         // 10~14
	var arr77 = IntArray(3)            // 确定元素类型是int之后可不使用lambda表达式初始化
	var arr8 = BooleanArray(5, { true })
//	var arr11 = StringArray(5, 6, false) 	// 非固定大小array
//	arr11.add("ss")

	// 使用arrayOf和Array生成的都是对象类型（如Integer）！！intArrayOf和IntArray！！！！！！！！！！！！！
	println(Arrays.toString(arr3))
	println(Arrays.toString(arr5))
	println(Arrays.toString(arr7))
	println(arr7.contentToString())
	println(arr1.javaClass)
	println(arr7.indices)    // 返回数组索引区间对象！！如0..4，常用于遍历
	println(arr7.lastIndex)    // 返回数组最后的索引，一般是arr7.size - 1

	// 数组常用api
	println(arr7.find({ it > 12 })) // 查找第一个符合条件的元素，13
	println(arr8.distinct())        // 去重！
	var arr9 = arr7.copyOf(3)    // 复制出一个新数组，copyOf(N)可复制前N个元素
	println(Arrays.toString(arr9))
	println("arr9 = " + arr9.asList())            // 数组转List！！

	println("arr3 = " + Arrays.toString(arr3))
	var arr10 = arr3 + arr9        // 数组可以直接相加、不可以相减！！
	println(Arrays.toString(arr10))

}

fun printCollection() {
	println("====================printCollection====================")
	// 创建list
	var list1 = listOf(1, 3, 5, 7)            // 不可变集合(Arrays$ArrayList)
	var list2 = listOfNotNull(2, 4, 6, null)  // 不可变集合(ArrayList),允许传入null成员，但是null元素会被去除。
	var list3 = mutableListOf(1, 2, 3)        // 可变集合(ArrayList),不使用？
	var list4 = arrayListOf(3, 4, 5)          // ② 可变集合(ArrayList),与mutableListOf区别？目前arrayListOf与mutableListOf的源码实现完全相同。
	var list6 = mutableListOf(null)

	var list44 = ArrayList<String>()	  // ① good
	var list444 = arrayListOf<String>()

	// add、remove、removeAt、clear
	list4[0] = 33                // 可以使用list[i]来访问元素！！
	// list、set和map都能相加减（因为具有operator修饰的plus和minus方法）！！而数组只能相加不能相减！！（相加是所有元素合并（元素可重复），相减是减去公共元素）
	var list5 = list4 + list1


	println(list1.subList(0, 2))    // 返回指定区间的子list，左闭右开
	println(list2)
	println(list3)
	println(list4)
	println("list5 = " + list5 + "\n")

	// 创建set
	var set1 = setOf(1, 2)           // 不可变集合(Collections$SingletonSet)
	var set2 = mutableSetOf(2, 3)    // 可变集合(LinkedHashSet)
	var set3 = hashSetOf(3)          // ②可变集合(HashSet,无序)
	var set4 = linkedSetOf(4)        // 可变集合(LinkedHashSet)
	var set5 = sortedSetOf(5)        // 可变集合(TreeSet,从小到大排列)

	var set33 = HashSet<String>()	 // ① good
	var set333 = hashSetOf<String>()

	var set6 = set1 intersect set2   // 取交集！！
	println(set6)
	var set7 = set1 union set2    // 取并集！！等价于set1 + set2
	println(set7)
	println(set5.javaClass.toString() + "\n")

	// 创建map
	var map1 = mapOf(1 to "first", 2 to "second")            	// x不可变集合 LinkedHashMap
	var map2 = mutableMapOf(11 to "first", 22 to "second")   	// x可变集合 LinkedHashMap
	var map3 = hashMapOf(111 to "first", 222 to "second")       // ②可变集合 HashMap
	var map4 = linkedMapOf(1111 to "first", 2222 to "second")   // 可变集合 LinkedHashMap
	var map5 = sortedMapOf(11111 to "first", 22222 to "second") // 可变集合 TreeMap

	var map33 = HashMap<String, String>()     // ① good
	var map333 = hashMapOf<String, String>()

	println(11 in map2)	// 因为含有contains方法
	// map添加元素or覆盖元素
	map3.put(333, "value3")
	map3[444] = "value4"	// 等价于put
	println(map3)
	// map遍历
	for(entry in map1.entries){
		println("key = " + entry.key + " value = " + entry.value)
	}
	for ((key, value) in map1) {   // 解构。Map.Entry内含componentN方法
	}
	
}

fun printFunction() {
	println("====================printCoFunction====================")
	
}



public class MyClass2 {
	open var bufferingTimes: Int = 10

	fun plus():Unit {
		var bb: Int = 2999999
		val bb2: String

	}


}