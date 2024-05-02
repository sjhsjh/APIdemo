package com.tencent.matrix.resource.analyzer.onepiece.util;

import com.ironz.unsafe.UnsafeAndroid;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

// import androidx.annotation.Keep;     // 跟support冲突   android.useAndroidX=true

/**
 * @author lizhangqu
 * @version V1.0
 * @since 2018-06-30 12:30
 */
// @Keep
public class ReflectWrapper {

    //just for finding the java.lang.Class classLoader field's offset
    // @Keep
    private Object classLoaderOffsetHelper;

    static {
        try {
            Class<?> versionClass = Class.forName("android.os.Build$VERSION");
            Field sdkIntField = versionClass.getDeclaredField("SDK_INT");
            sdkIntField.setAccessible(true);
            int sdkInt = sdkIntField.getInt(null);
            if (sdkInt >= 28) {
                Field classLoader = ReflectWrapper.class.getDeclaredField("classLoaderOffsetHelper");
                UnsafeAndroid unsafe = new UnsafeAndroid();
                long classLoaderOffset = unsafe.objectFieldOffset(classLoader);
                if (unsafe.getObject(ReflectWrapper.class, classLoaderOffset) instanceof ClassLoader) {
                    Object originalClassLoader = unsafe.getAndSetObject(ReflectWrapper.class, classLoaderOffset, null);
                } else {
                    throw new RuntimeException("not support");
                }
            }
        } catch (Exception e) {
            // MLog.error("ReflectWrapper", "static initializer error! " + e);
        }
    }

    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the field into.
     * @param name     field name
     * @return a field object
     * @throws NoSuchFieldException if the field cannot be located
     */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    public static Field findField(Class<?> originClazz, String name) throws NoSuchFieldException {
        for (Class<?> clazz = originClazz; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + originClazz);
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param instance       an object to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param clazz          a class to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + clazz);
    }

    public static Constructor<?> findConstructor(Class<?> originClazz, Class<?>... parameterTypes) throws
            NoSuchMethodException {
        for (Class<?> clazz = originClazz; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor(parameterTypes);

                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }

                return ctor;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Constructor"
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + originClazz);
    }

    /**
     * Locates a given constructor anywhere in the class inheritance hierarchy.
     *
     * @param instance       an object to search the constructor into.
     * @param parameterTypes constructor parameter types
     * @return a constructor object
     * @throws NoSuchMethodException if the constructor cannot be located
     */
    public static Constructor<?> findConstructor(Object instance, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor(parameterTypes);

                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }

                return ctor;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Constructor"
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }
}