package com.example.apidemo.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.example.apidemo.utils.AndroidUtils;
import com.example.apidemo.utils.NLog;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by sjh on 2018/12/18.
 */
public class SocketClient {
    private Context context;
    private Socket socket;
    private int port;
    private String site;
    private Thread thread;
    private Handler mHandler;
    private boolean mIsRunning = false;
    private InputStream in;

    /**
     *
     * @param context
     * @param site IP
     * @param port 端口
     */
    public SocketClient(Context context, String site, int port) {
        this.context = context;
        this.site = site;
        this.port = port;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 客户端开启线程与服务端建立连接
     */
    public void tryConnectServer() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!AndroidUtils.hasNetwork(context)){
                        updateUI("No Network");
                        return;
                    }
                    // 1.连接服务器
                    String localHostIP = InetAddress.getLocalHost().getHostAddress();// 服务器IP地址.127.0.0.1 xx
                    NLog.i("sjh9", "localHostIP = " + localHostIP);
                    NLog.i("sjh9", "本机 ip = " + AndroidUtils.getLocalIP(context));//192.168.1.116 ok


                    NLog.i("sjh9","AbsClient开始连接服务器");
                    // IP地址错误就报错java.net.NoRouteToHostException: No route to host
                    // 端口错误就报错：java.net.ConnectException: Connection refused
                    // 连续两次new Socket，第二次客户端能发送数据但是服务端却收不到，估计是两个客户端socket都能连上服务端，
                    // 但是第一个socket的输出流是关联服务端的输入流的，因此对第二个socket的输出流写数据，服务端收不到。
                    // 需要服务端再次处于accept状态，客户端再发起连接才正确。
                    // 开始连接服务器，此处会一直处于阻塞，直到连接成功
                    socket = new Socket(site, port);    // connect() 服务端尚未启动new socket报错吧？，但是必须已连上局域网才能new socket！！
                    // socket.setSoTimeout(5000); // 设置超时时间,这样就不能长时间等待服务器返回数据了

                    NLog.w("sjh9","AbsClient与服务器成功建立连接！！");
                    updateUI("AbsClient与服务器成功建立连接！！");

                    if (socket != null) {
                        mIsRunning = true;
                        receiveResponse();
                    } else {
                        mIsRunning = false;
                        NLog.e("sjh9", "连接失败" + "site=" + site + " ,port=" + port);
                        updateUI("连接失败" + "site=" + site + " ,port=" + port);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    NLog.e("sjh9", e.getMessage());
                    updateUI(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    NLog.e("sjh9", e.getMessage());
                    updateUI(e.getMessage());
                }
                NLog.e("sjh9", "客户端线程结束，客户端已经停止");
                updateUI("客户端线程结束，客户端已经停止");
            }
        });
        thread.start();
    }


    /**
     * 接收并解析响应数据
     */
    public void receiveResponse() {
        String data = "";
        int len = 0;
        while (mIsRunning && !thread.isInterrupted()) {
            try {
                in = socket.getInputStream();
                // 得到的是16进制数，需要进行解析
                byte[] buf = new byte[50];
                len = in.read(buf);
                // waiting......

                NLog.w("sjh9", "len = " + len);
                if(len == -1){
                    closeSocket();
                    return;
                }
                data = new String(buf, "UTF-8").trim();
                if (TextUtils.isEmpty(data)) {
                    NLog.e("sjh9", "服务器并无返回数据");
                } else {
                    NLog.w("sjh9", "AbsClient收到服务器返回结果：" + data);
                    updateUI("AbsClient收到服务器返回结果：" + data);
                }

            } catch (IOException e) {
                e.printStackTrace();
                NLog.e("sjh9", "Client receive IOException: " + e.getMessage());
                updateUI(e.getMessage());
                closeSocket();
            }
        }

    }


    /**
     * 生成并发送请求数据
     */
    public void sendRequest(final String params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        socket.getOutputStream().write(params.getBytes());
                        socket.getOutputStream().flush();
                        NLog.i("sjh9", "AbsClient向服务器发送请求数据：" + params);
                    } catch (IOException e) {
                        e.printStackTrace();
                        NLog.e("sjh9", e.getMessage());
                        updateUI(e.getMessage());
                    }
                } else {
                    mIsRunning = false;
                    NLog.e("sjh9", "socket null");
                    updateUI("socket null");
                }
            }
        }).start();

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
                socket.close();             // 不会直接影响已连接的服务端的socket
                socket.shutdownOutput();    // 会令服务端中的输入流直接到达结尾！！！！
//                socket.shutdownInput();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destory() {
        closeSocket();
        if (thread != null) {
            thread.interrupt();
        }
    }

}
