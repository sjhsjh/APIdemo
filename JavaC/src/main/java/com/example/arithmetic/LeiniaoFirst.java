package com.example.arithmetic;

import java.util.ArrayList;
import java.util.List;

// 雷鸟一面算法
public class LeiniaoFirst {

    // 设计一个对象池,支持对象的获取和回收，支持多线程访问，比如Message.obtain()
    // class A{}
    //
    //
    // public class ObjPool{
    //     private static ArrayList list = new ArrayList<A>();
    //
    //
    //     public static A obtain(){
    //
    //         sychronized(ObjPool.class){
    //             if(){
    //
    //             }
    //
    //         }
    //
    //     }
    //
    //     public static A release(){
    //
    //
    //     }
    //
    //
    // }

    /**
     * 通用对象池实现
     * 支持对象的获取和回收，线程安全
     * 使用List集合存储对象
     *
     * @param <T> 池中对象的类型
     */
    public class ObjectPool<T extends ObjectPool.Recyclable> {

        // 对象池的最大容量
        private static final int MAX_POOL_SIZE = 50;

        // 池中对象的集合
        private final List<T> pool;

        // 对象工厂，用于创建新对象
        private final ObjectFactory<T> factory;

        // 锁对象，用于同步
        private final Object lock = new Object();

        /**
         * 对象工厂接口
         */
        public interface ObjectFactory<T> {
            T create();
        }

        /**
         * 可回收对象接口
         */
        public interface Recyclable {
            void reset();
        }

        /**
         * 构造对象池
         *
         * @param factory 对象工厂
         */
        public ObjectPool(ObjectFactory<T> factory) {
            this.factory = factory;
            this.pool = new ArrayList<>(MAX_POOL_SIZE);
        }

        /**
         * 从对象池中获取对象
         * 如果池中有可用对象则返回，否则创建新对象
         *
         * @return 可用的对象
         */
        public T obtain() {
            synchronized (lock) {
                if (!pool.isEmpty()) {
                    // 从列表末尾取出对象（效率更高）
                    return pool.remove(pool.size() - 1);
                }
            }
            // 在锁外创建新对象，避免长时间持有锁
            return factory.create();
        }

        /**
         * 回收对象到对象池
         *
         * @param obj 要回收的对象
         * @return true表示回收成功，false表示池已满
         */
        public boolean recycle(T obj) {
            if (obj == null) {
                throw new IllegalArgumentException("Cannot recycle null object");
            }

            synchronized (lock) {
                if (pool.size() < MAX_POOL_SIZE) {
                    // 重置对象状态
                    obj.reset();
                    // 将对象添加到列表末尾
                    pool.add(obj);
                    return true;
                }
            }
            // 池已满，不回收
            return false;
        }

        /**
         * 获取当前池中对象的数量
         *
         * @return 池中对象数量
         */
        public int getPoolSize() {
            synchronized (lock) {
                return pool.size();
            }
        }

        /**
         * 清空对象池
         */
        public void clear() {
            synchronized (lock) {
                pool.clear();
            }
        }
    }



}


// # Java 对象池实现
//
//         这是一个线程安全的对象池实现，类似于 Android 中的 `Message.obtain()` 模式。
//
//         ## 📋 核心特性
//
// - ✅ **线程安全**：使用 `synchronized` 保证多线程并发访问安全
// - ✅ **对象复用**：减少对象创建和GC压力
// - ✅ **List集合结构**：使用ArrayList存储回收的对象，简单高效
// - ✅ **容量限制**：默认最大池容量为50，防止内存泄漏
// - ✅ **泛型设计**：支持任意类型的对象池
// - ✅ **简单易用**：API设计简洁，使用方便
//
// ## 🏗️ 架构设计
//
// ### 1. ObjectPool - 对象池核心类
//
// ```java
//         public class ObjectPool<T extends ObjectPool.Recyclable> {
//             // 对象获取
//             public T obtain();
//
//             // 对象回收
//             public boolean recycle(T obj);
//
//             // 获取池大小
//             public int getPoolSize();
//
//             // 清空对象池
//             public void clear();
//         }
// ```
//
//         ### 2. Recyclable - 可回收对象接口
//
// ```java
//         public interface Recyclable {
//             void reset();                          // 重置对象状态
//         }
// ```
//
//         ### 3. ObjectFactory - 对象工厂接口
//
// ```java
//         public interface ObjectFactory<T> {
//             T create();                            // 创建新对象
//         }
// ```
//
//         ## 🚀 使用方法
//
// ### 基本使用 - Message示例
//
// ```java
//         // 1. 获取对象
//         Message msg = Message.obtain();
//         msg.what = 1;
//         msg.data = "Hello World";
//
// // 2. 使用对象
//         System.out.println(msg);
//
// // 3. 回收对象
// msg.recycle();
// ```
//
//         ### 带参数的获取方法
//
// ```java
//         // 方式1：设置what
//         Message msg1 = Message.obtain(100);
//
//         // 方式2：设置what和obj
//         Message msg2 = Message.obtain(200, "data");
//
//         // 方式3：设置所有参数
//         Message msg3 = Message.obtain(300, 1, 2, "data");
// ```
//
//         ### 自定义对象池
//
// ```java
//         // 1. 实现Recyclable接口
//         class Task implements ObjectPool.Recyclable {
//             private static final ObjectPool<Task> sPool =
//                     new ObjectPool<>(Task::new);
//
//             public String name;
//             public int priority;
//
//             // 2. 提供obtain方法
//             public static Task obtain(String name, int priority) {
//                 Task task = sPool.obtain();
//                 task.name = name;
//                 task.priority = priority;
//                 return task;
//             }
//
//             // 3. 提供recycle方法
//             public void recycle() {
//                 sPool.recycle(this);
//             }
//
//             // 4. 实现reset方法
//             @Override
//             public void reset() {
//                 name = null;
//                 priority = 0;
//             }
//         }
//
//         // 使用自定义对象池
//         Task task = Task.obtain("My Task", 10);
// task.recycle();
// ```
//
//         ## 🧪 运行测试
//
// ```bash
// # 编译
//         javac ObjectPool.java Message.java ObjectPoolDemo.java
//
// # 运行测试
//         java ObjectPoolDemo
// ```
//
//         ## 📊 测试结果示例
//
// ```
//         ========== 对象池演示 ==========
//
//         1. 基本使用示例：
//         初始池大小: 0
//         获取msg1后池大小: 0
//         msg1: Message{what=1, arg1=0, arg2=0, obj=null, data='Hello'}
//         获取msg2后池大小: 0
//         msg2: Message{what=2, arg1=0, arg2=0, obj=World, data='null'}
//         回收msg1后池大小: 1
//         回收msg2后池大小: 2
//
//         2. 对象复用验证：
//         原始对象: Message{what=100, arg1=0, arg2=0, obj=Original, data='null'}, hashCode=123456
//         复用对象: Message{what=0, arg1=0, arg2=0, obj=null, data='null'}, hashCode=123456
//         ✓ 验证通过：对象被成功复用
//
// 3. 多线程并发测试：
//         线程数: 10
//         每个线程操作次数: 1000
//         总操作次数: 10000
//         耗时: 1234ms
//         最终池大小: 50
//         ✓ 多线程测试完成，无异常
// ```
//
//         ## 🔧 实现原理
//
// ### 1. List集合结构
//         对象池使用ArrayList存储回收的对象：
//         ```
//         pool = [obj1, obj2, obj3, ...]
//         ```
//
//         ### 2. 获取对象流程
// ```
//         obtain()
//   └─> synchronized(lock)
//         └─> 如果池中有对象
//           └─> 从列表末尾取出（remove最后一个元素）
//         └─> 返回对象
//       └─> 如果池为空
//           └─> 创建新对象
// ```
//
//         ### 3. 回收对象流程
// ```
//         recycle(obj)
//   └─> synchronized(lock)
//         └─> 如果池未满
//           └─> reset对象状态
//           └─> 添加到列表末尾
//           └─> 返回true
//       └─> 如果池已满
//           └─> 返回false（对象被丢弃）
//         ```
//
//         ### 4. 线程安全机制
// - 使用 `synchronized` 锁保护共享资源
// - 对象创建在锁外进行，避免长时间持有锁
// - 所有对池状态的访问都在同步块内
//
// ## ⚡ 性能优化
//
// 1. **在锁外创建对象**：避免创建新对象时长时间持有锁
// 2. **列表末尾操作**：在ArrayList末尾添加和删除都是O(1)时间复杂度
// 3. **容量限制**：防止内存无限增长
// 4. **对象复用**：减少 GC 压力，提升性能
// 5. **简化结构**：相比链表更简单，无需维护next指针
//
// ## 📝 最佳实践
//
// 1. **及时回收**：使用完对象后立即调用 `recycle()`
//         2. **避免重复回收**：不要多次回收同一个对象
// 3. **状态重置**：在 `reset()` 方法中彻底清理对象状态，避免数据污染
// 4. **防止内存泄漏**：确保回收对象时清除对其他对象的引用
// 5. **适度使用**：只在频繁创建对象的场景使用对象池
// 6. **简化设计**：使用List集合比链表更简单，无需维护指针关系
//
// ## ⚠️ 注意事项
//
// - 回收后的对象不应该再被使用
// - 对象池大小有上限（默认50），超出不会回收
// - 必须实现 `reset()` 方法来清理对象状态
// - 不要在对象回收后仍持有其引用
//
// ## 📚 适用场景
//
// - 频繁创建和销毁的临时对象
// - 消息队列中的消息对象
// - 数据库连接池
// - 线程池中的任务对象
// - 游戏中的子弹、特效等对象
//
// ## 🔗 参考
//
//         本实现参考了 Android Framework 中 Message 的对象池设计：
//         - `android.os.Message`
//         - `android.os.Handler`
//
