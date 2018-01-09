package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.utils.NLog;
import java.lang.reflect.Method;


/**
 * Created by Administrator on 2018/1/10 0010.
 */

public class SDActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NLog.i("sjh7", getFilesDir().getAbsolutePath()  //  /data/data/com.example.apidemo/files
                 + " " + getFilesDir().getPath()        //  /data/data/com.example.apidemo/files
                        + " 1: " + getCacheDir().getPath() //  /data/data/com.example.apidemo/cache

                        + " 2: " + getExternalFilesDir(null).getPath() //  /storage/emulated/0/Android/data/com.example.apidemo/files
                        + " 3: " + getExternalCacheDir().getPath()     //  /storage/emulated/0/Android/data/com.example.apidemo/cache
                        + " 4: " + Environment.getExternalStorageDirectory().getAbsolutePath()//   /storage/emulated/0

                        + " 5: " + Environment.getRootDirectory().getPath()     //  /system
                        + " 6: " + Environment.getDataDirectory().getPath()     //  /data
                        + " 7: " + getPrimaryStoragePath()                      //  /storage/emulated/0/
                        + " 8: " + getSecondaryStoragePath()                    //  /storage/sdcard1 ???

        );
    }

    // 获取主存储卡路径
    public String getPrimaryStoragePath() {
        try {
            StorageManager sm = (StorageManager) getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm);
            // first element in paths[] is primary storage path
            return paths[0];
        } catch (Exception e) {
            NLog.e("sjh7", "getPrimaryStoragePath() failed", e);
        }
        return null;
    }

    // 获取次存储卡路径,一般就是外置 TF 卡了. 不过也有可能是 USB OTG 设备...
    // 其实只要判断第二章卡在挂载状态,就可以用了.
    public String getSecondaryStoragePath() {
        try {
            StorageManager sm = (StorageManager) getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm);
            // second element in paths[] is secondary storage path
            return paths.length <= 1 ? null : paths[1];
        } catch (Exception e) {
            NLog.e("sjh7", "getSecondaryStoragePath() failed", e);
        }
        return null;
    }

}
