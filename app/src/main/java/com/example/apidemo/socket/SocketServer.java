package com.example.apidemo.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.NLog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sjh on 2018/12/10.
 * serverSocket.accept();的时候只要socket还没关闭,仍然可以通过向输出流写数据从而向客户端多次发送数据。
 * serverSocket要在accept状态下才能被客户端连接！！
 */
public class SocketServer {
    private Context context;
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream in;
    private Handler mHandler;
    private Thread thread;
    private int port;
    private boolean mIsRunning = false;

    public SocketServer(Context context, int port) {
        this.context = context;
        this.port = port;
        mIsRunning = true;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * socket监听数据
     */
    public void beginListen() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!AndroidUtils.hasNetwork(context)){
                        updateUI("No Network");
                        return;
                    }
                    mIsRunning = true;
                    if (serverSocket == null) {
                        // 连续单击重复执行new ServerSocket(port)，则报错 Address already in use
                        // 不杀进程退出activity重新进入报错：Address already in use？？因为没有关闭端口：serverSocket.close();
                        serverSocket = new ServerSocket(port);  // 只能在线程中初始化。否则报错NetworkOnMainThreadException
                    }
                    NLog.i("sjh9", "AbsServer服务器已经启动");
                    updateUI("AbsServer服务器已经启动");
                    socket = serverSocket.accept();
                    // waiting......


                    NLog.w("sjh9", "Server accept() over!!");
                    updateUI("Server accept() over!!");
                    // socket.setSoTimeout(5 * 1000);  // 设置连接后客户端N秒内要收到客户端发送的数据过来,即read()的等待时长。
                    // 1.接收并解析请求数据
                    receiveRequest();

                } catch (IOException e) {
                    e.printStackTrace();
                    NLog.e("sjh9", e.getMessage() + "  AbsServer通信失败");
                    updateUI(e.getMessage() + "  AbsServer通信失败");
                }
                NLog.e("sjh9", "服务器线程结束，服务器已经停止");
                updateUI("服务器线程结束，服务器已经停止");
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 返回请求要的信息(开线程)
     */
    public void sendMessage(final String result) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        if (!socket.isClosed()) {
                            // 方法1
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(result.getBytes());
                            outputStream.flush();
                            NLog.i("sjh9", "AbsServer返回给客户端数据=" + result);
                            updateUI("AbsServer返回给客户端数据=" + result);
//                            // 方法2
//                            PrintWriter out = new PrintWriter(socket.getOutputStream());
//                            out.print(result);
//                            out.flush();
                        } else {
                            NLog.e("sjh9", "socket is closed!!");
                            updateUI("socket is closed!!");
                        }
                    } else {
                        NLog.e("sjh9", "socket null");
                        updateUI("socket null");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    NLog.e("sjh9", e.getMessage());
                    updateUI(e.getMessage());
                }
            }
        });
        thread.start();
    }

    /**
     * 接收并解析请求信息
     */
    public void receiveRequest() {
        String data = "";
        int len = 0;
        while (mIsRunning && !thread.isInterrupted()) {
            try {
                in = socket.getInputStream();
                byte[] buf = new byte[4096];
                len = in.read(buf); // read返回-1代表已到达流的结尾，客户端关闭socket就会令服务端的输入流到达结尾！
                // waiting......

                NLog.w("sjh9", "len = " + len);
                if(len == -1){
                    closeSocket();
                    return;
                }
                data = new String(buf, "UTF-8").trim();
                NLog.w("sjh9", "AbsServer接收到客户端发来的数据=" + data);
                updateUI("AbsServer接收到客户端发来的数据=" + data);

            } catch (IOException e) {
                e.printStackTrace();
                NLog.e("sjh9", "receiveRequest IOException: " + e.getMessage());
                updateUI("receiveRequest IOException: " + e.getMessage());
                closeSocket();
            }
        }

    }

    private void updateUI(String data){
        if (mHandler != null){
            Message msg = new Message();
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 断开连接
     */
    public void closeSocket() {
        mIsRunning = false;
        try {
            if (socket != null) {
                socket.close();
                socket.shutdownOutput();
//                socket.shutdownInput();
            }
            if (serverSocket != null) {
                // 已被客户端连接过，只要服务端进程尚在，即使socket和serverSocket都已关闭，客户端仍能连上？
                serverSocket.close();   // 释放端口！！
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void destory() {
        if (thread != null) {
            thread.interrupt();
        }
        closeSocket();
    }

}