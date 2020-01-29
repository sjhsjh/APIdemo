package com.example.apidemo.utils;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/**
 * 2016/10/27.
 */
public class HardWareUtils {
    private static LocationManager mLocationManager;
    private static WifiManager mWifiManager;
    private static Context mContext;
    private static final ContentObserver hardWareObserver = new ContentObserver(null) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mWifiManager != null)
                NLog.i("sjh1", "WifiState : " + mWifiManager.getWifiState() + " isWifiEnabled : " + mWifiManager.isWifiEnabled());
            if (mLocationManager != null)
                NLog.i("sjh1", "gps enabled ?  " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                NLog.i("sjh1", "bluetooth enabled ? " + bluetoothAdapter.isEnabled());
            }
            try {
                int isAirplaneOpen = Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
                NLog.i("sjh1", "Airplane Open ? " + isAirplaneOpen);
            } catch (Settings.SettingNotFoundException e) {
                NLog.e("sjh1", e.getMessage());
            }
        }
    };

    private static HardWareUtils mInstance;
    public static HardWareUtils getInstance() {
        if (null == mInstance) {
            synchronized (HardWareUtils.class) {
                if (null == mInstance) {
                    mInstance = new HardWareUtils();
                }
            }
        }
        return mInstance;
    }
    /**
     * GPS，蓝牙，数据漫游的开启关闭，会修改系统的数据表，通过监听数据表中数据变化来判断打开，关闭操作。
     * @param context
     */
    public static void registerGPSListener(Context context) {
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED), false, hardWareObserver);
    }


    public static void unRegisterGPSListener(Context context) {
        mLocationManager = null;
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerBluetoothListener(Context context) {
        mContext = context.getApplicationContext();
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.BLUETOOTH_ON), false, hardWareObserver);
    }


    public static void unRegisterBluetoothListener(Context context) {
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerWifiListener(Context context) {
        mContext = context.getApplicationContext();
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.System.WIFI_ON), false, hardWareObserver);// .System.WIFI_ON
    }


    public static void unRegisterWifiListener(Context context) {
        mWifiManager = null;
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerAirplaneListener(Context context) {
        mContext = context.getApplicationContext();
        context.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.AIRPLANE_MODE_ON), false, hardWareObserver);// 不能用Settings.System！
    }

    public static void unRegisterAirplaneListener(Context context) {
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    /**
     * 获得设备硬件标识
     * imei + android_id + serial + 硬件uuid（自生成）
     * @return A89FFC4D6277BB4CF4FFFD3B2587E71079BF0AC5
     */
    public static String getDeviceId(Context context) {
        String deviceID = getDeviceID(context);
        NLog.d("sjh1", "deviceID = " + deviceID);

        String androidID = getAndroidId(context);
        NLog.d("sjh1", "androidID = " + androidID);

        String serial = getSERIAL();
        NLog.d("sjh1", "serial = " + serial);

        String uuid = getDeviceUUID().replace("-", "");
        NLog.d("sjh1", "uuid = " + uuid);


        StringBuilder sbDeviceId = new StringBuilder();
        // 追加imei
        if (deviceID != null && deviceID.length() > 0) {
            sbDeviceId.append(deviceID);
            sbDeviceId.append("|");
        }
        // 追加androidid
        if (androidID != null && androidID.length() > 0) {
            sbDeviceId.append(androidID);
            sbDeviceId.append("|");
        }
        // 追加serial
        if (serial != null && serial.length() > 0) {
            sbDeviceId.append(serial);
            sbDeviceId.append("|");
        }
        // 追加硬件uuid
        if (uuid != null && uuid.length() > 0) {
            sbDeviceId.append(uuid);
        }

        // 生成SHA1，统一DeviceId长度
        if (sbDeviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(sbDeviceId.toString());
                String sha1 = bytesToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    //返回最终的DeviceId
                    return sha1;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // 如果以上硬件标识数据均无法获得，则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 获得设备默认IMEI  (14位)
     * >=6.0需要获得READ_PHONE_STATE所在权限组的权限，否则返回null
     * @return 35362607681366
     */
    private static String getDeviceID(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // NLog.d("sjh1", "getImei = " + telephonyManager.getImei());   // nexus null; coolpad: 99000556822530

            return telephonyManager.getDeviceId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备的AndroidId（无需权限）(16位)
     * ps:极个别设备获取不到数据或得到错误数据
     * @return 4e9366b4167b1dc1
     */
    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备序列号（无需权限, 长度不定）（如：WTK7N16923005607）, 个别设备无法获
     * ps:极个别设备获取不到数据
     * @return 61efe0e1
     */
    private static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
     * 使用硬件信息，计算出一个随机数
     * @return 000000001f9e1db6ffffffffd0c5b084
     */
    private static String getDeviceUUID() {
        try {
            String dev = "3883756" +
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.HARDWARE.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.SERIAL.length() % 10;
            return new UUID(dev.hashCode(), Build.SERIAL.hashCode()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 取SHA1
     * @param data 数据
     * @return 对应的hash值
     */
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    /**
     * 转16进制字符串
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; n < data.length; n++) {
            stmp = (Integer.toHexString(data[n] & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

    private KeyguardManager.KeyguardLock currentKeyguardLock;
    /**
     * 熄屏下唤醒屏幕并解锁
     * 另附反射方案解锁屏幕：https://blog.csdn.net/behindeye/article/details/78178673
     * disableKeyguard注意事项：
     * 0、disableKeyguard之后按home键看似没反应，实质应该对屏幕重新上锁（系统原因）。
     * 此时要先将上一次的keyguardLock进行reenableKeyguard才能再用新keyguardLock来disableKeyguard。
     * 1、调用完disable这个方法后，除非应用进程被杀死，否则按电源键只是黑屏，无法锁住屏幕的。
     * 2、KeyguardLock对象必须是同一个才能在disable之后重新reenable，所以要使reenable生效的话要把调用disable的对象存起来便于再reenable，
     * 而且单纯的调用reenable方法是没有任何作用的，所以你锁不了其他程序打开的屏幕，有时候甚至锁不了自己曾经打开的锁（对象不是同一个的话）
     * 3、调用完disableKeyguard方法关闭系统锁屏服务后， 在适当的时候（即恢复系统锁屏服务时）调用reenableKeyguard方法，让他们成对调用。
     * （先理解成系统同一时刻只允许存在一对disableKeyguard与reenableKeyguard吧）。
     * 4、disableKeyguard并不是解锁屏幕，只是把锁屏功能禁掉了。这也导致了在某些系统上锁屏界面仍然存在而且并没有解锁，
     * 导致按Home键的时候Home的实际功能被锁屏界面拦截而无法进入主页。
     */
    public void wakeUpAndDisableKeyguard(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) {
            NLog.e("sjh2", "PowerManager null!!!");
            return;
        }
        // 获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        // SCREEN_BRIGHT_WAKE_LOCK是4个flag之一，决定CPU、Screen、Keyboard是否开启
        // ps:用户点击电源键时 SCREEN_BRIGHT_WAKE_LOCK 会被释放
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "apidemo:wakelock-flag-bright");
        //点亮屏幕
        wakelock.acquire(5000);

        reenableKeyguard();
        //得到键盘锁管理器对象，需要DISABLE_KEYGUARD权限
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        NLog.i("sjh2", "wakeUpAndDisableKeyguard isLocked = " + keyguardManager.inKeyguardRestrictedInputMode());
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("apidemo-keyguard");

        //解锁
        keyguardLock.disableKeyguard();
        currentKeyguardLock = keyguardLock;
        NLog.i("sjh2", "wakeUpAndDisableKeyguard isKeyguardLocked = " + keyguardManager.isKeyguardLocked());

        wakelock.release();
    }

    public void reenableKeyguard() {
        if (currentKeyguardLock != null) {
            currentKeyguardLock.reenableKeyguard();
        }
    }
}