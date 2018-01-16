package com.example.apidemo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/10 0010.
 */

public class SDActivity extends BaseActivity {
    private String beforeText;
    private int cursorPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

//        01-16 23:31:08.077 16107-16107/com.example.apidemo I/sjh8: source = a start=0 end=1 dest= dstart=0 dend=0
//        01-16 23:31:13.057 16107-16107/com.example.apidemo I/sjh8: source = b start=0 end=1 dest=a dstart=1 dend=1
//        01-16 23:31:19.927 16107-16107/com.example.apidemo I/sjh8: source = c start=0 end=1 dest=ab dstart=2 dend=2
//        01-16 23:33:26.767 16107-16107/com.example.apidemo I/sjh8: source = c start=0 end=1 dest=abc dstart=3 dend=3
//        01-16 23:33:44.287 16107-16107/com.example.apidemo I/sjh8: source = 1 start=0 end=1 dest=abcc dstart=4 dend=4
//        01-16 23:33:56.697 16107-16107/com.example.apidemo I/sjh8: source = 2 start=0 end=1 dest=abcc dstart=4 dend=4
        // 设置InputFilter可以对editText中输入的每个字符进行限制处理
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                NLog.d("sjh8", "source = " + source + " start=" + start + " end=" + end + " dest=" + dest + " dstart=" + dstart + " dend=" + dend);
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        ((EditText)findViewById(R.id.edittext)).setFilters(new InputFilter[]{filter}); //这个显然是可以设置多个filter
        ((EditText)findViewById(R.id.edittext)).setHint("只能输入大小写字母");

        final EditText editText2 = ((EditText)findViewById(R.id.edittext2));
        editText2.setHint("只能输入汉字、大小写字母、数字");
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();
                cursorPos = editText2.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String afterText = s.toString();
                NLog.d("sjh8", "onTextChanged =" + afterText);

                if (!TextUtils.isEmpty(afterText)) {    // 清空输入框后肯定不需要再匹配正则
                    Pattern p = Pattern.compile("^[\u4e00-\u9fa5_a-zA-Z0-9]{1,6}$");
                    Matcher m = p.matcher(afterText);
                    boolean isValid = m.matches();
                    if (!isValid) {
                        // int differ = afterText.length() - beforeText.length();   // 新增的字符数
      // ==原来焦点在edittext上时edittext.setText()会令光标移到最前方！！
      //      cursorPos = editText2.getSelectionStart() - 1;
                        final int a = cursorPos;
                        int b = a;
                        b++;
                        // 如果用户的输入不符合规范，则显示之前输入的文本
                        editText2.setText(beforeText);
                        // 光标移动到输入错误字符前的位置，因为用户可以在字符串中间输入字符！
                        editText2.setSelection(a);   // 光标移动到文本末尾: afterText.length() - differ
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            });



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
