package com.example.apidemo.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import java.io.InputStream;
import java.io.OutputStream;
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

        ((Button) findViewById(R.id.button1)).setText("open SAF to read file");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSAF(true);
            }
        });
        ((Button) findViewById(R.id.button2)).setText("open SAF to write file");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSAF(false);
            }
        });

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

            }

            @Override
            public void afterTextChanged(Editable s) {
                NLog.d("sjh8", "afterTextChanged =" + s);

                if (!TextUtils.isEmpty(s)) {    // 清空输入框后肯定不需要再匹配正则
                    Pattern p = Pattern.compile("^[\u4e00-\u9fa5_a-zA-Z0-9]{1,6}$");
                    Matcher m = p.matcher(s);
                    boolean isValid = m.matches();
                    // 等价于 boolean isValid = afterText.matches("^[\u4e00-\u9fa5_a-zA-Z0-9]{1,6}$");

                    if (!isValid) {
                        // int differ = afterText.length() - beforeText.length();   // 新增的字符数

                        // 方法① final不能少，代表仅取得cursorPos的值，correctPos的值不跟随cursorPos的值变化。
                        final int correctPos = cursorPos;
                        // 方法②
                        // int a[] = new int[1];
                        // a[0] = correctPos;
                        // 方法③ beforeTextChanged时不处理cursorPos，
                        // previousCursor = editText2.getSelectionStart() - differ;

                        // 如果用户的输入不符合规范，则显示之前输入的文本。注意setText会执行beforeTextChanged！还触发了anr。editText2.setText(beforeText);
                        s.replace(0, s.length(), beforeText);
                        // 光标移动到输入错误字符前的位置，因为用户可以在字符串中间输入字符！
                        editText2.setSelection(correctPos);   // 光标移动到文本末尾: afterText.length() - differ
                    }
                }

            }

            });


        NLog.i("sjh7",
            // 三星和vivo是（android6.0开始有多用户，/data/user/0目录会指向/data/data）：/data/user/0/com.example.apidemo/files
          "1: " + getFilesDir().getAbsolutePath()       //  /data/data/com.example.apidemo/files
            + "\n2: " + getFilesDir().getPath()              //  /data/data/com.example.apidemo/files
            + "\n3: " + getCacheDir().getPath()              //  /data/data/com.example.apidemo/cache   当设备的内部存储空间不足时，Android可能会删除这些缓存文件以回收空间。
            // 在您的内部存储空间内创建（或打开现有的）目录。默认不存在,且getDir后才创建app_dir的文件夹，带读写权限。
            + "\n4: " + getDir("sjh", Context.MODE_PRIVATE).getAbsolutePath()   //  /data/data/com.example.apidemo/app_sjh
            //  默认不存在,且databases文件夹和dbname文件都不会自动创建，带读写权限。
            + "\n5: " + getDatabasePath("dbName")                               //  /data/data/com.example.apidemo/databases/dbname

            + "\n11: " + getExternalFilesDir(null).getPath()     //  /storage/emulated/0/Android/data/com.example.apidemo/files
            + "\n12: " + getExternalCacheDir().getPath()              //  /storage/emulated/0/Android/data/com.example.apidemo/cache

            ////////////////////////////////////////////////////////////////////////
            + "\n21: " + Environment.getExternalStorageDirectory().getAbsolutePath()//   /storage/emulated/0   无SD卡权限能获取路径，但是需要声明写SD卡权限才能写SD卡。
            + "\n22: " + Environment.getRootDirectory().getAbsolutePath()           //  /system 不可读写
            + "\n23: " + Environment.getDataDirectory().getAbsolutePath()           //  /data
            + "\n24: " + Environment.getDownloadCacheDirectory().getAbsolutePath()  //  /cache  需声明权限or声明权限+申请权限
            + "\n25: " + getPrimaryStoragePath()                                    //  /storage/emulated/0/
            + "\n26: " + getSecondaryStoragePath()                                  //  /storage/sdcard1  三星和vivo无外置TF卡所以为null。

            + "\n31: " + getPackageCodePath()       // /data/app/com.example.apidemo-2/base.apk  默认存在，apk包路径.
            + "\n32: " + getPackageResourcePath()   // /data/app/com.example.apidemo-2/base.apk  默认存在，apk包路径.


        );


        NLog.w("sjh8",   "checkSelfPermission = " + ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
         + ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE));
        // != PackageManager.PERMISSION_GRANTED  0 与 -1
        //   NLog.i("sjh8", "=====" + ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
    }

    /**
     * 获取主存储卡路径
     * @return
     */
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

    /**
     * 获取次存储卡路径,一般就是外置TF卡了. 不过也有可能是USB OTG设备
     * 其实只要判断第二章卡在挂载状态,就可以用了.
     * @return
     */
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


    private static int READ_CODE = 100;
    private static int WRITE_CODE = 101;

    /**
     * 打开 com.google.android.documentsui文件管理器 选择文件
     */
    private void startSAF(boolean read) {
        // 调用者只需要指定想要读写的文件类型，比如文本类型、图片类型、视频类型等，选择器就会过滤出相应文件以供选择
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //指定选择文本类型的文件
        intent.setType("text/plain");
        if (read) {
            startActivityForResult(intent, READ_CODE);
        } else {
            startActivityForResult(intent, WRITE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_CODE) {
            //选中返回的文件信息封装在Uri里
            Uri uri = data.getData();
            openUriForRead(uri);
        } else if (requestCode == WRITE_CODE) {
            Uri uri = data.getData();
            openUriForWrite(uri);
        }
    }

    private void openUriForRead(Uri uri) {
        if (uri == null)
            return;
        try {
            //获取输入流
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] readContent = new byte[1024];
            int len = 0;
            do {
                //读文件
                len = inputStream.read(readContent);
                if (len != -1) {
                    Log.d("sjh7", "read content: " + new String(readContent).substring(0, len));
                }
            } while (len != -1);
            inputStream.close();
        } catch (Exception e) {
            Log.d("sjh7", e.getLocalizedMessage());
        }
    }

    private void openUriForWrite(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            //从uri构造输出流
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            //待写入的内容
            String content = "hello world I'm from SAF\n";
            //写入文件
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e("sjh7", e.getLocalizedMessage());
        }
    }



}
