package com.shopline.pos2.checkout;

import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 用ProcessBuilder 来执行cmd命令，但是无输出？原因未明
 * @date 2024/3/21
 */
public class RealTimeProcess {

    private String TAG = "RealTimeProcess";

    private ProcessBuilder mProcessBuilder;
    private Process recordProcess;
    private BufferedReader readStdout;
    private BufferedReader readStderr;
    private RealtimeProcessListener realtimeProcessListener; // 回调用到的接口
    private String tmp1;
    private String tmp2;

    public RealTimeProcess(RealtimeProcessListener realtimeProcessListener) {
        this.realtimeProcessListener = realtimeProcessListener;
    }

    public void start(List<String> commands) {
        try {
            mProcessBuilder = new ProcessBuilder(commands).redirectErrorStream(true);
            recordProcess = mProcessBuilder.start();
            exec(recordProcess);
        } catch (Exception e) {
            Log.e(TAG, "start error = " + e);
        }

    }

// 16:05:18.357 RealTimeProcess         com.xx                E  tmp1= --------- beginning of main
// 16:05:18.363 RealTimeProcess         com.xx                 E  tmp1= --------- beginning of system
    private void exec(final Process process) {
        readStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        readStderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread execThread = new Thread() {
            public void run() {
                try {
                    while ((tmp1 = readStdout.readLine()) != null ||
                            (tmp2 = readStderr.readLine()) != null) {
                        if (tmp1 != null) {
                            Log.w(TAG, "tmp1= " + tmp1);
                            realtimeProcessListener.onNewStdoutListener(tmp1);
                        }
                        if (tmp2 != null) {
                            realtimeProcessListener.onNewStderrListener(tmp2);
                            Log.w(TAG, "tmp2= " + tmp2);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "exec error = " + e);
                    realtimeProcessListener.onProcessFinish();
                }
            }
        };

        execThread.start();
    }


    public void destroyRealtimeProcess() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (recordProcess.isAlive()) {
                recordProcess.destroyForcibly();
            }
        }
    }


}