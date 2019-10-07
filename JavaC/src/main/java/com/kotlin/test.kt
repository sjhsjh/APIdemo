import java.lang.StringBuilder
import java.util.Arrays

fun main(args: Array<String>) {

//	val ages2 = age?.toInt() ?: -1
//	println("Hello World!" + ages2)


	var map = hashMapOf<String, Int>()//1 to 33, 2 to 44)

	var hashMap0 = hashMapOf(1 to "coder", 2 to "pig")

	var hashMap = hashMapOf<Int, Boolean>()
	if (hashMap.get(7) == null || hashMap.get(7) == false) {
		hashMap.put(7, true)

	}


	hashMap.put(6, false)
	hashMap.get(6)


	println(map)
	println(hashMap)

	var obj: MyClass2 = MyClass2()

	var a = "qwe"
	var c: String = "qwe"// new String("qwe")	// ????

	var d: Int = 6
	var b: Int = 62

	out@ for (x in 0..10) {
		for (y in 1..9) {
			if (y == 2) {
				println(x)
				break@out
			}
		}
	}


	when {
		false -> {
			println(11)
		}
		true -> println(22)
	}

//	printArray()
//	printCollection()
	printFunction()

}

fun printArray() {
	println("====================printArray==========")
	var obj: MyClass2 = MyClass2()
	// 静态初始化数组
	var arr1 = arrayOf(obj, obj, obj)
	var arr2 = arrayOf("ss", "22", "11")
	var arr3 = intArrayOf(1, 2, 10) // 注意是小写开头

	// 指定类型和数组长度
	var arr4 = arrayOfNulls<String>(5)    // 声明object类型？？
	var arr5 = emptyArray<String>()    // 长度为0的数组
	// 高端用法
	var arr6 = Array(3, { "ax" })
	var arr7 = IntArray(5, { it + 10 })    // it 分别是0到5，即it会从0开始遍历数组的索引！！那非数字类型it代表什么？？BooleanArray？？it需要放在大括号中
	var arr8 = BooleanArray(5, { true })


	println(Arrays.toString(arr8))
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
	println("====================printCollection==========")
	// 创建list
	var list1 = listOf(1, 3, 5, 7)            // 不可变集合(Arrays$ArrayList)
	var list2 = listOfNotNull(2, 4, 6, null)  // 不可变集合(ArrayList),去除为null的元素
	var list3 = mutableListOf(1, 2, 3)        // x可变集合(ArrayList)
	var list4 = arrayListOf(3, 4, 5)          // 可变集合(ArrayList),与mutableListOf区别？？
	var list6 = mutableListOf(null)
	
	// add、remove、removeAt、clear
	list4[0] = 33                // 可以使用list[i]来访问元素！！
	var list5 = list4 - list1    // list、set和amp都能相减（因为具有operator修饰的plus和minus方法）！！而数组只能相加不能相减！！（相加是并集，相减是减去公共元素）

	println(list1.subList(0, 2))    // 返回指定区间的子list，左闭右开
	println(list2)
	println(list3)
	println(list4)
	println("list5 = " + list5)

	// 创建set
	var set1 = setOf(1, 2)            // 不可变集合(Collections$SingletonSet)
	var set2 = mutableSetOf(2, 3)   // 可变集合(LinkedHashSet)
	var set3 = hashSetOf(3)        // 可变集合(HashSet,无序)
	var set4 = linkedSetOf(4)        // 可变集合(LinkedHashSet)
	var set5 = sortedSetOf(5)        // 可变集合(TreeSet,从小到大排列)

	var set6 = set1 intersect set2   // 取交集！！
	println(set6)
	var set7 = set1 union set2    // 取并集！！等价于set1 + set2
	println(set7)
	println(set5.javaClass)

	// 创建map
	var map1 = mapOf(1 to "first", 2 to "second")            	// x不可变集合 LinkedHashMap
	var map2 = mutableMapOf(11 to "first", 22 to "second")   	// x可变集合 LinkedHashMap
	var map3 = hashMapOf(111 to "first", 222 to "second")       // 可变集合 HashMap
	var map4 = linkedMapOf(1111 to "first", 2222 to "second")   // 可变集合 LinkedHashMap
	var map5 = sortedMapOf(11111 to "first", 22222 to "second") // 可变集合 TreeMap

	println(11 in map2)	// 因为含有contains方法
	// map添加元素or覆盖元素
	map3.put(333, "new3")
	map3[444] = "new4"	// 等价于put
	println(map3)
	// map遍历
	for(entry in map1.entries){
		println("key = " + entry.key + " value = " + entry.value)
	}
	
	
}

fun printFunction() {
	println("====================printCoFunction==========")
	
}



public class MyClass2 {
	open var bufferingTimes: Int = 10 // 缓冲次数

	fun qplus():Unit {	// :Unit可省略，Unit相当于void
		var bb: Int = 2999999
		val bb2: String

	}


}