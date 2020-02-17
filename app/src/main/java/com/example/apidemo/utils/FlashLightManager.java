package com.example.apidemo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.widget.Toast;

/**
 * <br> 6.0之前的手机，Camera对象是否为空代表闪光灯的是否开启.因此不采用静态的方法来调用。
 * Created by jinhui.shao on 2017/5/18.
 */
public class FlashLightManager {
    private Context mContext;
    private Camera mCamera;
    @RequiresApi(api = Build.VERSION_CODES.M)
    private CameraManager.TorchCallback mTorchCallback;
    private boolean mHasFlashLight;

    public FlashLightManager(Context context){
        mContext = context;
        mHasFlashLight = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);    // FEATURE_CAMERA
    }

    /**
     * 注册闪光灯开关改变的监听. 6.0以上手机才执行.
     */
    // @RequiresApi(api = Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.M)
    public void registerFlashLightCallBack(CameraManager.TorchCallback torchCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && torchCallback != null) {
            mTorchCallback = torchCallback;
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            cameraManager.registerTorchCallback(mTorchCallback, null);
        }
    }

    /**
     * 反注册闪光灯开关改变的监听. 6.0以上手机才执行.
     */
    // @RequiresApi(api = Build.VERSION_CODES.M)
    public void unRegisterFlashLightCallBack(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(mTorchCallback == null){
                return ;
            }
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            cameraManager.unregisterTorchCallback(mTorchCallback);
            mTorchCallback = null;
        }
    }

    /**
     *
     * @param open 是否打开闪光灯
     * @return 操作是否成功
     */
    @SuppressLint("NewApi")
    public boolean setFlashLightStatus(boolean open){
        if(!mHasFlashLight){
            return false;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraManager.setTorchMode("0", open);  // 6.0以后打开闪光灯不需要相机等权限
            } catch (CameraAccessException e) {
                e.printStackTrace();
                // android7.0 com.ibox.flashlight_060206.apk和com.devuni.flashlight_111217.apk可以占用相机，其他应用打开相机则报错：
                // android.hardware.camera2.CameraAccessException: CAMERA_IN_USE (4): setTorchMode:1400:
                // Torch for camera "0" is not available due to an existing camera user.  原因未明
                return false;
            }
        }
        else {
            if (open) {
                // 7.0运行该段代码无异常但不能打开闪光灯
                PackageManager packageManager = mContext.getPackageManager();
                FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
                if (featureInfos != null) {
                    for (FeatureInfo featureInfo : featureInfos) {
                        if (PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) { // 判断设备是否支持闪光灯
                            try {
                                if (mCamera == null) {
                                    // 没有相机权限or打开camera未释放就再次open都会报错Fail to connect to camera service。
                                    // (对我4.4手机的手电筒CP_FlashLight，其他应用占用着执行Camera.open()无crash，且打开已打开的闪光灯就是重启闪光灯，原因未明)
                                    mCamera = Camera.open();
                                }
                                Camera.Parameters parameters = mCamera.getParameters();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                mCamera.setParameters(parameters);
                                mCamera.startPreview();
                            } catch (Exception e) {
                                e.printStackTrace();
                                NLog.e("====setFlashLightStatus=====" + e.getMessage()); // 360限制权限后调用Camera.open()会ANR和Method called after release().
                                Toast.makeText(mContext,"Camera may be occupied by other application. Cause:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        }
                    }
                }

            }
            else {
                if(mCamera != null){
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }

            }
        }

        return true;
    }



}