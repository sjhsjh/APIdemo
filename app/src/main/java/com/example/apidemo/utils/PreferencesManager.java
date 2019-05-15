package com.example.apidemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.junit.Assert;

/**
 * @date 2016/6/30
 */
public class PreferencesManager {
    /**配置文件名称 */
    public static final String DEFAULT_PREFERENCE_NAME = "apidemo_sp";
    /** preference配置对象 */
    private SharedPreferences mPreference = null;
    /** Editor */
    private Editor mEditor = null;

    private static PreferencesManager sInstance;

    public static PreferencesManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (PreferencesManager.class) {
                if (null == sInstance) {
                    sInstance = new PreferencesManager(context);
                }
            }
        }
        return sInstance;
    }

    private PreferencesManager(Context context) {
        mPreference = context.getSharedPreferences(DEFAULT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Assert.assertNotNull(mPreference);

        mEditor = mPreference.edit();
        Assert.assertNotNull(mEditor);
    }

    public String getString(String key, String defValue) {
        return mPreference.getString(key, defValue);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreference.getBoolean(key, defValue);
    }

    /**
     * 存储 Boolean 类型的值到 SharedPreferences 文件里面
     * @param key
     * @param value
     * @return
     */
    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    /**
     * 获取Int类型的值
     * @param key
     * @param defValue
     * @return 是否成功
     */
    public int getInt(String key, int defValue) {
        return mPreference.getInt(key, defValue);
    }

    /**
     * 存储 Int 类型的值到 SharedPreferences 文件里面
     * @param key
     * @param value
     * @return 是否成功
     */
    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    /**
     * 获取 Long 类型的值到 SharedPreferences 文件里面
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(String key, long defValue) {
        return mPreference.getLong(key, defValue);
    }

    /**
     * 存储 Long 类型的值到 SharedPreferences 文件里面
     * @param key
     * @param value
     * @return 是否成功
     */
    public void putLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    /**
     * 清除SharedPreferences中保存的内容
     * @param key
     * @return 是否成功
     */
    public void removeKey(String key) {
        mEditor.remove(key);
        mEditor.apply();
    }

}
