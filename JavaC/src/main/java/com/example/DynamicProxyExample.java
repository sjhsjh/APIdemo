package com.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @date 2024/3/12
 */
class DynamicProxyExample {

    public static void main(String[] args) {
        // 创建目标对象
        IUserService userService = new UserServiceImpl();

        // 2、创建InvocationHandler实例
        MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);

        // 3、生成 动态代理对象
        IUserService proxy = (IUserService) Proxy.newProxyInstance(
                userService.getClass().getClassLoader(),
                userService.getClass().getInterfaces(),
                invocationHandler
        );

        // 通过代理对象调用方法
        proxy.addUser("addUser Alice");
        proxy.delUser("delUser Alice");
        // 动态代理前置操作
        // 添加用户：Alice
        // 动态代理后置操作



        // 静态代理！！！
        // IUserService staticProxy = new StaticAgent();
        // staticProxy.addUser("真正实现");
        // begin
        // 添加用户：真正实现
        // end
    }
}


// 1、实现InvocationHandler接口。（最终被调用的地方）
class MyInvocationHandler implements InvocationHandler {
    private Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 注意这是所有接口方法的总入口，要用method形参来判断是什么方法调用的。
        // 动态代理 前置操作  method=public abstract void com.example.IUserService.addUser(java.lang.String)
        // 动态代理 前置操作  method=public abstract void com.example.IUserService.delUser(java.lang.String)


        System.out.println("动态代理 前置操作  method=" + method);
        Object result = method.invoke(target, args);
        System.out.println("动态代理 后置操作");
        return result;
    }
}


// 定义接口
interface IUserService {
    void addUser(String username);
    void delUser(String username);
}

// 实现接口的具体类
class UserServiceImpl implements IUserService {

    public void addUser(String username) {
        System.out.println("添加用户：" + username);
    }

    public void delUser(String username) {
        System.out.println("刪除用户：" + username);
    }
}



//////////////////////////////////////////////////////////////////////////////////////////////////
class StaticAgent implements IUserService {

    @Override
    public void addUser(String username) {
        System.out.println("begin");

        // 业务功能必须由目标对象亲自实现——>唱歌业务
        UserServiceImpl actual = new UserServiceImpl();
        actual.addUser(username);

        System.out.println("end");
    }

    @Override
    public void delUser(String username) {
        System.out.println("begin");

        UserServiceImpl actual = new UserServiceImpl();
        actual.delUser(username);

        System.out.println("end");
    }
}
