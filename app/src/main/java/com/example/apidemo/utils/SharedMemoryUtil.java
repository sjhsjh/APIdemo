package com.example.apidemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.MemoryFile;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/**
 * 1、进程1创建MemoryFile并写入数据
 * 2、通过Binder将MemoryFile的“文件描述符”传递到进程2
 * 3、进程2通过获取到的文件描述符进行数据的读写
 *
 * 两个文件描述符并不是同一个，只不过他们都指向了内核中的同一个文件!!!
 *
 * MemoryFile是对SharedMemory的包装，官方推荐直接使用SharedMemory（更灵活）。
 *
 * @date 2024/11/6
 */
public class SharedMemoryUtil {
    private static final int SHARED_MEMORY_SIZE = 1024;
    private static final String TAG = "Shared";

    public static Pair<Integer, Integer> writeSharedMemory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return null;

        try {
            File jpegFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera.jpg");
            // 1、使用文件流把文件读入到内存
            FileInputStream inputStream = new FileInputStream(jpegFile);
            byte[] bytes = new byte[(int) jpegFile.length()];
            inputStream.read(bytes);

            // 2、创建sharedMemory跨进程传输
            SharedMemory sharedMemory = SharedMemory.create("MySharedMemory", bytes.length); // api 27
            // 3、mapReadWrite获取ByteBuffer
            ByteBuffer buffer = sharedMemory.mapReadWrite();
            // 4、put数据
            buffer.put(bytes);
            // 5、把sharedMemory权限设置为只读，create默认是读写权限都有，这里可以避免客户端更改数据
            sharedMemory.setProtect(OsConstants.PROT_READ);

                    Method method = SharedMemory.class.getDeclaredMethod("getFileDescriptor");
                    FileDescriptor des = (FileDescriptor) method.invoke(sharedMemory);
                    ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(des);

                    // 传递 共享内存文件描述符
                    int sharedMemoryFd = pfd.detachFd();
                    Log.d(TAG, "sharedMemory pfd.detachFd() = " + sharedMemoryFd);

            //////////////////////////////////////////////
            // aidl 传递parcel状态的 FileDescriptor ！！！
            Parcel parcel = Parcel.obtain();
            parcel.writeFileDescriptor(des);
            //////////////////////////////////////////////



            // 6、使用完需要unmap
            SharedMemory.unmap(buffer);

            return new Pair<>(sharedMemoryFd, (int) jpegFile.length());

                // byte[] data2 = new byte[(int) jpegFile.length()];
                // // 根据文件描述符 打开共享内存
                // ParcelFileDescriptor pfd2 = ParcelFileDescriptor.adoptFd(sharedMemoryFd);
                //
                // FileDescriptor descriptor = pfd2.getFileDescriptor();
                // FileInputStream fileInputStream = new FileInputStream(descriptor);
                // fileInputStream.read(data2);
                // Bitmap bitmap = BitmapFactory.decodeByteArray(data2, 0, data2.length);
                //
                // return bitmap;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Pair<Integer, Integer> writeMemoryFile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return null;
        try {
            File jpegFile = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera.jpg");
            // 1、使用文件流把文件读入到内存
            FileInputStream inputStream = new FileInputStream(jpegFile);
            byte[] data = new byte[(int) jpegFile.length()];
            inputStream.read(data);

            MemoryFile memoryFile = new MemoryFile("shared_sjh", data.length);
            memoryFile.getOutputStream().write(data);

            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            FileDescriptor des = (FileDescriptor) method.invoke(memoryFile);
            ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(des);

            // 传递 共享内存文件描述符
            int sharedMemoryFd = pfd.detachFd();
            Log.d(TAG, "MemoryFile pfd.detachFd() = " + sharedMemoryFd);


            //////////////////////////////////////////////
            // aidl 传递parcel状态的 FileDescriptor ！！！
            Parcel parcel = Parcel.obtain();
            parcel.writeFileDescriptor(des);
            //////////////////////////////////////////////

            return new Pair<>(sharedMemoryFd, (int) jpegFile.length());


                // byte[] data2 = new byte[(int) jpegFile.length()];
                // // 根据文件描述符 打开共享内存
                // ParcelFileDescriptor pfd2 = ParcelFileDescriptor.adoptFd(sharedMemoryFd);
                //
                // FileDescriptor descriptor = pfd2.getFileDescriptor();
                // FileInputStream fileInputStream = new FileInputStream(descriptor);
                // fileInputStream.read(data2);
                // Bitmap bitmap = BitmapFactory.decodeByteArray(data2, 0, data2.length);
                //
                // return bitmap;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap readSharedMemory(int sharedMemoryFd, int dataSize) {
        try {
            byte[] data2 = new byte[dataSize];
            // 根据文件描述符 打开共享内存
            ParcelFileDescriptor pfd2 = ParcelFileDescriptor.adoptFd(sharedMemoryFd);

            FileDescriptor descriptor = pfd2.getFileDescriptor();
            FileInputStream fileInputStream = new FileInputStream(descriptor);
            fileInputStream.read(data2);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data2, 0, data2.length);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String readSharedMemory(SharedMemory sharedMemory, int size) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            return null;
        }
//     // 1、通过aidl拿到SharedMemory
//     SharedMemory sharedMemory = iService.takeScreenshot();
//     if (sharedMemory == null) {
//         return;
//     }
        ByteBuffer byteBuffer = null;
        byte[] bytes = null;
        String data = null;
        try {
            // 2、mapReadOnly获取到存了数据的ByteBuffer
            byteBuffer = sharedMemory.mapReadOnly();
            int dataSize = byteBuffer.limit() - byteBuffer.position();
            bytes = new byte[dataSize];
            // 3、借助byteBuffer获取到数据
            byteBuffer.get(bytes);
            // 4.1、把字节转换成bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // 4.2
            data = new String(bytes).substring(0, size);

            SharedMemory.unmap(byteBuffer);
            sharedMemory.close();
        } catch (ErrnoException e) {
            e.printStackTrace();
        }


        return data;
    }



}
