package com.example.apidemo.activity;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

public class ResolveInfoActivity extends BaseActivity {
	private static final String TAG = "ResolveInfoActivity";
	private Handler mHandler = new Handler();
	private boolean cycleLog = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_layout);

		
		final OnDismissListener listener = new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				NLog.e("sjh2", "onDismiss");
				
			}
		};
		
		final DialogInterface.OnCancelListener cancel = new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				NLog.e("sjh2", "onCancel");
				
			}

		};
		
		((Button)findViewById(R.id.button1)).setText("openApp : some3");
		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openApp("com.example.some3");

				PackageManager packageManager = getPackageManager();
				final Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);	//过滤出只含有该action和category的intent
				resolveIntent.addCategory("android.intent.category.sjh");
				//resolveIntent.setPackage("com.example.apidemo");
				List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
				for(ResolveInfo ri : apps){
					NLog.i("sjh2", " label = " + ri.loadLabel(packageManager) + " packageName = " + ri.activityInfo.packageName + " classname = " + ri.activityInfo.name);
				}
				NLog.i("sjh2", " apps.size() = " + apps.size());

//				try {
//					startActivity(resolveIntent);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		});
		
		((Button)findViewById(R.id.button2)).setText("openApp : dialer");
		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openApp("com.android.dialer");
			}
		});
		
		((Button)findViewById(R.id.button3)).setText("openApp : calendar");
		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openApp("com.android.calendar");
			}
		});
		((Button)findViewById(R.id.button4)).setText("readInstalledApps");
		findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				readInstalledApps();
			}
		});
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(cycleLog) {
					NLog.i("sjh2", "getMonitorAppPkg = " + getMonitorAppPkg());
					mHandler.postDelayed(this, 1000);
				}
			}
		};

		((Button)findViewById(R.id.button5)).setText("Log MonitorAppPkg");
		findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mHandler.postDelayed(runnable, 1000);
			}
		});
	}
	

	/**
	 * 先检查包是否存在，再检查指定的Intent是否存在，再启动符合条件的第一个resolveInfo.
	 * @param packageName
	 */
	private void openApp(String packageName) {
		PackageManager packageManager = getPackageManager();
		//该段代码块仅是用于检查packageName是否存在
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(packageName, 0);////!!!!!!
		} catch (NameNotFoundException e) {
			NLog.e("sjh3", "openApp() : " + e.getMessage());
			Toast.makeText(ResolveInfoActivity.this, packageName + " not found", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		if (packageInfo != null) {
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);	//过滤出只含有该action和category的intent
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			
//			Intent resolveIntent = new Intent();
//			resolveIntent.setAction("android.intent.action.qwe");
//			resolveIntent.addCategory("android.intent.category.cate"); 
//			resolveIntent.setData(Uri.parse("itcast://www.itcast.com/kdksl"));

			resolveIntent.setPackage(packageInfo.packageName);	//or packageName

			List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0); //询问给定intent是指向哪个包的哪个activity的。////!!!!!!
			for(ResolveInfo ri : apps){
				NLog.i("sjh3", " " + ri.activityInfo.name + " -- " );
				//NLog.i("sjh2", " label = " + ri.loadLabel(packageManager) + " -- " );
			}
			NLog.i("sjh2", " apps.size() = " + apps.size() + " -- " );

			if(!apps.isEmpty()){
				ResolveInfo resolveInfo = apps.iterator().next(); //取第一个ResolveInfo
				String packageName2 = resolveInfo.activityInfo.packageName;
				String className = resolveInfo.activityInfo.name;  //入口activity的name

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName2, className);
				NLog.i("sjh3", " className = " + className + " ; " + resolveInfo.loadLabel(packageManager));
				
				//setIcon(resolveInfo.loadIcon(packageManager));

				intent.setComponent(cn);
				startActivity(intent);  //注意这里启动的activity和 该activity所在应用启动自身的activity可能在两个进程中，两者可同时独立运行！！
			}
			else{
				Toast.makeText(ResolveInfoActivity.this, " Activity not found", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	private void setIcon(Drawable drawable){
		if(drawable != null){
			ImageView image = new ImageView(ResolveInfoActivity.this);
			image.setImageDrawable(drawable);
			((RelativeLayout)findViewById(R.id.relative)).addView(image);
		}
	}
	
	private void readInstalledApps(){
		PackageManager pm = getPackageManager();
		
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		//intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
 		//intent.setPackage("com.google.android.calculator");
		// 这样的app,所以得到的要比getInstalledPackages少(因为这会少了那种service、provider、receiver之类的app).
		// 例如Bluetooth.apk内的activity就没有action.MAIN！而有些应用却有多个activity都有action.MAIN。
		List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
		for(ResolveInfo ri : list){
			if (ri != null && ri.activityInfo != null) {
				NLog.i("sjh5", "label = " + ri.loadLabel(pm) +  " packageName = " + ri.activityInfo.packageName + " classname = " + ri.activityInfo.name);
			}

		}
		NLog.e("sjh5", "size" + list.size());//24
		
		
		List<PackageInfo> packageList = pm.getInstalledPackages(0);
		for(PackageInfo pi : packageList){
			if((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
				NLog.i("sjh2", "系统程序 label = " + pi.applicationInfo.loadLabel(pm) + " ==" + pi.packageName + " ==" + pi.applicationInfo.className); //若应用程序没有设置label则会读出包名！
				// 这里的pi.applicationInfo.className  经常读出null或者错误的classname，如：com.tct.calculator/com.tct.calculator.Calculator变成com.tct.calculator/com.tct.calculator.ClientApplication
				// 下面的ai.className同理不准确. 用queryIntentActivities即可得到classname.
				//label = Google packageName = com.google.android.googlequicksearchbox classname = com.google.android.googlequicksearchbox.SearchActivity
				// I/sjh2: 系统程序 label = Google 应用  ：用pi.applicationInfo.loadLabel(pm)也将label“Google”错读取成“Google 应用”了。
			}
			else{
				NLog.w("sjh2", "用户程序 label = " + pi.applicationInfo.loadLabel(pm) + " ==" + pi.packageName + " ==" + pi.applicationInfo.className);
			}
		}
		NLog.e("sjh2", "size" + packageList.size());//81
	
	
		List<ApplicationInfo> applicationList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo ai : applicationList){
			if((ai.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
				NLog.i("sjh3", "系统程序 label = " + ai.loadLabel(pm) + " ==" + ai.packageName + " ==" + ai.className);
			}
			else{
				NLog.w("sjh3", "用户程序 label = " + ai.loadLabel(pm)+ " ==" + ai.packageName + " ==" + ai.className);
			}
		}
		NLog.e("sjh3", "size" + applicationList.size());//81
	}

	/**
	 * @return 返回当前最前端应用的包名，5.0以上需要“查看其他应用使用情况”的权限
     */
	private String getMonitorAppPkg(){
		String currentApp = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
			long time = System.currentTimeMillis();
			List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
			if (appList != null && appList.size() > 0) {
				SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
				for (UsageStats usageStats : appList) {
					mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
				}
				if (mySortedMap != null && !mySortedMap.isEmpty()) {
					currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
				}
			}
		} else {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
			currentApp = tasks.get(0).processName;
		}
		return currentApp;
	}

	@Override
	protected void onDestroy() {
		// NLog.i("sjh1", "onDestroy");
		cycleLog = false;
		super.onDestroy();
	}


	@Override
	public void finish() {
		// NLog.i("sjh1", TAG + "finish");
		super.finish();
		// 通过调用overridePendingTransition() 可以实时修改Activity的切换动画。但需注意的是:该函数必须在调用startActivity()或者finish()后立即调用，且只有效一次。
		 overridePendingTransition(R.animator.slide_left_in, R.animator.slide_right_out);
	}
}
