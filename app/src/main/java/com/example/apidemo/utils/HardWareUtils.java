package com.example.apidemo.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * <br>
 *  2016/10/27.
 */
public class HardWareUtils {
    private static LocationManager mLocationManager;
    private static WifiManager mWifiManager;
    private static Context mContext;
    private static final ContentObserver hardWareObserver = new ContentObserver(null) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if(mWifiManager != null)
               NLog.i("sjh1", "WifiState : " + mWifiManager.getWifiState() + " isWifiEnabled : " + mWifiManager.isWifiEnabled());
            if(mLocationManager != null)
               NLog.i("sjh1", "gps enabled ?  " +  mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            BluetoothAdapter bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null){
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


    /**
     * GPS，蓝牙，数据漫游的开启关闭，会修改系统的数据表，通过监听数据表中数据变化来判断打开，关闭操作。
     * @param context
     */
    public static void registerGPSListener(Context context){
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        context.getContentResolver().registerContentObserver(
            Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED), false, hardWareObserver);
    }


    public static void unRegisterGPSListener(Context context){
        mLocationManager = null;
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerBluetoothListener(Context context){
        mContext = context.getApplicationContext();
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.BLUETOOTH_ON), false, hardWareObserver);
    }


    public static void unRegisterBluetoothListener(Context context){
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerWifiListener(Context context){
        mContext = context.getApplicationContext();
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.System.WIFI_ON  ), false, hardWareObserver);// .System.WIFI_ON
    }


    public static void unRegisterWifiListener(Context context){
        mWifiManager = null;
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }

    public static void registerAirplaneListener(Context context){
        mContext = context.getApplicationContext();
        context.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.AIRPLANE_MODE_ON), false, hardWareObserver);// 不能用Settings.System！
    }

    public static void unRegisterAirplaneListener(Context context){
        context.getContentResolver().unregisterContentObserver(hardWareObserver);

    }


}