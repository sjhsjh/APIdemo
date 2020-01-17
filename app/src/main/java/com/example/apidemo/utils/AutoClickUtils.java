package com.example.apidemo.utils;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * 自动模拟点击
 * @date 2020/1/17
 */
public class AutoClickUtils {

    /**
     * 需要root，会弹窗询问用户授权root，能在后台执行。
     * 执行ADB命令： input tap 500 500
     */
    public static void execShellCmd(String cmd) {
        try {
            // ③ su获取root权限 + cmd
            Process process = Runtime.getRuntime().exec("su");

            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // ① ProcessBuilder完全无效
        // int x = 500, y = 500;
        // String[] order = {"input", "tap", " ", x + "", y + ""};
        // try {
        //     new ProcessBuilder(order).start();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // ② 无root执行adb命令仅可在当前应用在前台时有效执行
        // Process process = null;
        // try {
        //     process = Runtime.getRuntime().exec("input tap 500 500"); // 只能用于当前应用的activity在前台
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // } finally {
        //     // if (process != null) {
        //     //     process.destroy();
        //     // }
        // }
    }


}
