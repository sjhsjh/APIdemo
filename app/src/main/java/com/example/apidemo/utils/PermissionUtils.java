package com.example.apidemo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * <br>
 * Created by jinhui.shao on 2019/1/26.
 */
public class PermissionUtils {
    public static String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 申请读权限--始终允许--申请写权限--仍然弹框询问权限。
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    /**
     * 各种情况下都能准确判断是否有权限的api
     */
    public static boolean hasPermission(Context context, String permission) {
        // 6.0以下手机总是有权限，但是gz sdk对SDK_INT 是21和22的设备用单元测试api来判断是否有权限，奇怪。
        NLog.w("sjh7", "  " //   + context.checkSelfPermission(permission)     // 需要api 23
                + ContextCompat.checkSelfPermission(context, permission)  // 6.0以下的只要在Manifests文件中定义有uses-permission的都是true，而6.0或以上的授权之后才是true.
                + context.getPackageManager().checkPermission(permission, context.getPackageName())
                + PermissionChecker.checkSelfPermission(context, permission)    // best
        );

        // For Android < Android M, self permissions are always granted.
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            int targetSdkVersion = 0;
            try {
                final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                targetSdkVersion = info.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            boolean result = true;
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

                NLog.i("sjh7", " context.checkSelfPermission = " + context.checkSelfPermission(permission));
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)     // 内部使用AppOpsManager实现！！
                        == PermissionChecker.PERMISSION_GRANTED;

                NLog.i("sjh7", " PermissionChecker.checkSelfPermission = " + PermissionChecker.checkSelfPermission(context, permission));
            }
            return result;
        }

    }

    /**
     * 各种targetAPI都适用，底层判断是否有权限的api
     */
    public static boolean hasPermissionByOP(@NonNull Context context, @NonNull String permission) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            int result = context.checkPermission(permission, Process.myPid(), Process.myUid());
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String op = AppOpsManager.permissionToOp(permission);   // 需要api 23
            if (TextUtils.isEmpty(op)) {
                return true;
            }

            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);   // 方法①    // Context.APP_OPS_SERVICE
            result = appOpsManager.noteProxyOp(op, context.getPackageName());   // 0 、1
            NLog.d("sjh7", " noteProxyOp ： " + (result == AppOpsManager.MODE_ALLOWED));

            int checkOp = appOpsManager.checkOp(op, Process.myUid(), context.getPackageName());  // 方法②
            NLog.d("sjh7", " checkOp ： " + (checkOp == AppOpsManager.MODE_ALLOWED));

            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 根据所需的权限数组去请求权限(区分了是“用户点击拒绝权限”还是“上一次弹框点击了不再询问的拒绝权限”)
     * 1、已有全部权限，则执行init初始化代码
     * 2、有N个没有权限，但是都是没有选择不再询问的，就去一次性申请N个权限
     * 3、至少有一个权限被选择了不再询问，则可进入引导进入设置or退出页面（即强制所有权限都被授权时才进入下一步的功能）
     */
    public static void requestForPermissions(Activity activity, String[] permissions) {
        List<String> denyPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {

            if (!hasPermissionByOP(activity, permissions[i])) {
                if (PreferencesManager.getInstance(activity).getBoolean(Constant.FIRST_SHOW_PERMISSION_DIALOG + permissions[i], true)) {
                    denyPermissionList.add(permissions[i]);
                    continue;
                } else {
                    boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);  // 为true代表在上次权限弹框点击了禁止（但并没有勾选“下次不再询问”
                    if (isTip) {    // 表明用户没有彻底禁止弹出权限请求
                        denyPermissionList.add(permissions[i]);
                    } else {    // 表明用户已经彻底禁止弹出权限请求
                        // 提示用户进入权限设置界面or退出页面
                        goIntentSetting(activity);
                        return;
                    }
                }

            }
        }

        if (denyPermissionList.size() > 0) {
//            requestPermissions((String[])denyPermissionList.toArray(new String[0]), 999);
            // api低于23直接回调onRequestPermissionsResult
            ActivityCompat.requestPermissions(activity, (String[]) denyPermissionList.toArray(new String[0]), 999);
            for (int i = 0; i < denyPermissionList.size(); i++) {
                if (PreferencesManager.getInstance(activity).getBoolean(Constant.FIRST_SHOW_PERMISSION_DIALOG + denyPermissionList.get(i), true)) {
                    PreferencesManager.getInstance(activity).putBoolean(Constant.FIRST_SHOW_PERMISSION_DIALOG + denyPermissionList.get(i), false);
                }
            }
        } else {    // 所有权限已被授权
            // init
        }

    }

    /**
     * 需要适配不同手机厂商
     */
    public static void goIntentSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

