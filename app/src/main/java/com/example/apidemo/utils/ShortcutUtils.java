package com.example.apidemo.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import com.example.apidemo.MainActivity;
import com.example.apidemo.R;
import com.example.apidemo.activity.PowerManagerActivity;
import java.util.Arrays;
import java.util.List;

public class ShortcutUtils {

    /**
     * api < 26时适用。
     * Intent actionIntent = new Intent(MainActivity.this, PowerManagerActivity.class); // 需要activity android:exported="true"
     * @param context
     * @param name
     * @param resourceId
     * @param actionIntent
     */
    public static void installShortCut(Context context, String name, int resourceId, Intent actionIntent) {
        //  actionIntent.setComponent(new ComponentName("com.example.some3", "com.example.some3.MainActivity"));  // 不能加？？

        if(TextUtils.isEmpty(actionIntent.getAction())){
            actionIntent.setAction(Intent.ACTION_MAIN); // 一定要有action，但注意别覆盖了了外部设置的action
        }
        // 坑爹属性，图标和名称都成为了activity清单文件中的label和icon。桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
//        actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   // 非必须
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);   // 非必须


        Intent createShortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        Parcelable parcelableIcon = Intent.ShortcutIconResource.fromContext(context, resourceId);
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, parcelableIcon);
        createShortCutIntent.putExtra("duplicate", false); // 不允许重复创建,不一定有效，实测无效
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        context.sendBroadcast(createShortCutIntent);

//        // 快捷方式的图标
//        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
//        createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

    }


    /**
     * 使用自定义图标创建快捷方式, 兼容8.0以上和以下版本
     * Intent actionIntent = new Intent(MainActivity.this, PowerManagerActivity.class); // 需要activity android:exported="true"
     * @param context
     * @param shortcutID 不能生成name不同但是shortcutID相同的快捷方式。
     * @param name
     * @param iconCompat
     * @param actionIntent ps：可以生成"name、图标、shortcutID都相同，但是actionIntent内的extra数据不同"的快捷方式
     */
    public void installShortCut(Context context, String shortcutID, String name, IconCompat iconCompat, Intent actionIntent) {
        NLog.w("sjh7", "isRequestPinShortcutSupported=" + ShortcutManagerCompat.isRequestPinShortcutSupported(context));
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            if(TextUtils.isEmpty(actionIntent.getAction())){
                actionIntent.setAction(Intent.ACTION_MAIN);
            }
            ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(context, shortcutID)    // 包含名字、图标、intent
                    .setShortLabel(name)
                    .setIcon(iconCompat)
                    .setIntent(actionIntent).build();
            Intent pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(context, pinShortcutInfo);// 包含名字、图标、intent

            PendingIntent successCallback = PendingIntent.getBroadcast(context, 0, pinnedShortcutCallbackIntent, 0);

            ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, successCallback.getIntentSender());
        }

//        // api >= 26
//        if (Build.VERSION.SDK_INT >= 26) {
//            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
//            Intent actionIntent2 = new Intent(MainActivity.this, PowerManagerActivity.class);
//            actionIntent2.setAction(Intent.ACTION_MAIN);
//
//            if (mShortcutManager.isRequestPinShortcutSupported()) {
//                // Assumes there's already a shortcut with the ID "my-shortcut".
//                // The shortcut must be enabled.
//                ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(context, "my-shortcut")
//                        .setShortLabel(name)
//                        .setIcon(Icon.createWithResource(context, R.drawable.ic_launcher))
//                        .setIntent(actionIntent2).build();
//
//                // Create the PendingIntent object only if your app needs to be notified
//                // that the user allowed the shortcut to be pinned. Note that, if the
//                // pinning operation fails, your app isn't notified. We assume here that the
//                // app has implemented a method called createShortcutResultIntent() that
//                // returns a broadcast intent.
//                Intent pinnedShortcutCallbackIntent = mShortcutManager.createShortcutResultIntent(pinShortcutInfo);
//
//                // Configure the intent so that your app's broadcast receiver getsthe callback successfully.
//                // 当添加快捷方式的确认弹框弹出来时，将被回调
//                PendingIntent successCallback = PendingIntent.getBroadcast(context, 0, pinnedShortcutCallbackIntent, 0);
//                mShortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
//            }
//        }


    }

    /**
     * 长安图标弹出的标语,需要 api >= 25     8.0小米华为都不行??
     */
    public static void installDynamicShortcut(Context context) {
        if (Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "id1")
                    .setShortLabel("Web site")
                    .setLongLabel("Open the web site")  // 弹出的标语title
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_launcher))  // 标语左侧icon
                    .setIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.mysite.example.com/")))
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        }

    }


    /**
     * 判断当前应用在桌面是否有桌面快捷方式。 nexus7.1实测无效
     */
    public static boolean hasShortcut(Context context) {
        boolean result = false;
        String title = null;
        try {
            final PackageManager pm = context.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)).toString();  // API-DEMO
        } catch (Exception e) {

        }
        title = "我的快捷方式";

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else if (android.os.Build.VERSION.SDK_INT < 19) {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher3.settings/favorites?notify=true";
        }
//        // 获取默认
//        String authority = getAuthorityFromPermissionDefault(context);    //xx
//        NLog.e("sjh7", " authority==" + authority);


        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = context.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;

    }


    public static String getAuthorityFromPermissionDefault(Context context) {
        return getThirdAuthorityFromPermission(context, "com.android.launcher3.permission.READ_SETTINGS");
    }

    public static String getThirdAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }
        try {
            List<PackageInfo> packs = context.getPackageManager()
                    .getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null) {
                return "";
            }
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission)
                                || permission.equals(provider.writePermission)) {

                            String authority = provider.authority;
                            if (!TextUtils.isEmpty(authority)
                                    && (authority
                                    .contains(".launcher.settings")
                                    || authority
                                    .contains(".twlauncher.settings") || authority
                                    .contains(".launcher2.settings")))
                                return authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}


