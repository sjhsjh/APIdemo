package com.example.apidemo.utils;

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
    private static final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println("WifiState : " + mWifiManager.getWifiState() + "    isWifiEnabled : " + mWifiManager.isWifiEnabled());
            System.out.println("gps enabled ?   =" +  mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

        }
    };


    /**
     * GPS，蓝牙，数据漫游的开启关闭，会修改系统的数据表，通过监听数据表中数据变化来判断打开，关闭操作。
     * @param context
     */
    public static void registerGPSListener(Context context){
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        context.getContentResolver().registerContentObserver(
            Settings.Secure.getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),false, mGpsMonitor);
    }


    public static void unRegisterGPSListener(Context context){
        mLocationManager = null;
        context.getContentResolver().unregisterContentObserver(mGpsMonitor);

    }

    public static void registerWifiListener(Context context){
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.WIFI_ON  ),false, mGpsMonitor);// .System.WIFI_ON
    }


    public static void unRegisterWifiListener(Context context){
        mWifiManager = null;
        context.getContentResolver().unregisterContentObserver(mGpsMonitor);

    }


}