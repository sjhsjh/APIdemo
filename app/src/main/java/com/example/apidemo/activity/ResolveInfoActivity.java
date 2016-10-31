package com.example.apidemo.activity;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.apidemo.R;

public class ResolveInfoActivity extends Activity {
	private static final boolean DEBUG = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_layout);

		
		
		final OnDismissListener listener = new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				Log.e("sjh2", "onDismiss");
				
			}
		};
		
		final DialogInterface.OnCancelListener cancel = new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.e("sjh2", "onCancel");
				
			}

		};
		
		((Button)findViewById(R.id.button1)).setText("openApp : some3");
		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// openApp("com.example.some3");
				PackageManager packageManager = getPackageManager();
				final Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);	//过滤出只含有该action和category的intent
				resolveIntent.addCategory("android.intent.category.sjh");
				//resolveIntent.setPackage("com.example.apidemo");
				List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
				for(ResolveInfo ri : apps){
					if (DEBUG) Log.i("sjh2", " " + ri.activityInfo.name + " -- " );
					//if (DEBUG) Log.i("sjh2", " label = " + ri.loadLabel(packageManager) + " -- " );
				}
				if (DEBUG) Log.i("sjh2", " apps.size() = " + apps.size() + " -- " );
				Intent intent = new Intent(ResolveInfoActivity.this, PowerManagerActivity.class);
				try {
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
			Log.e("sjh3", "openApp() : " + e.getMessage());
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
				if (DEBUG) Log.i("sjh3", " " + ri.activityInfo.name + " -- " );
				//if (DEBUG) Log.i("sjh2", " label = " + ri.loadLabel(packageManager) + " -- " );
			}
			if (DEBUG) Log.i("sjh2", " apps.size() = " + apps.size() + " -- " );

			if(!apps.isEmpty()){
				ResolveInfo resolveInfo = apps.iterator().next(); //取第一个ResolveInfo
				String packageName2 = resolveInfo.activityInfo.packageName;
				String className = resolveInfo.activityInfo.name;  //入口activity的name

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName2, className);
				if (DEBUG) Log.i("sjh3", " className = " + className + " ; " + resolveInfo.loadLabel(packageManager));
				
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
		// 这样的app,所以得到的要比getInstalledPackages少(因为这会少了那种service、provider、receiver之类的app).
		// 例如Bluetooth.apk内的activity就没有action.MAIN！而有些应用却有多个activity都有action.MAIN。
		List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
		for(ResolveInfo pi : list){
			if (DEBUG) Log.i("sjh5", "label = " + pi.loadLabel(pm)  );
		}
		if (DEBUG) Log.e("sjh5", "size" + list.size());//24
		
		
		List<PackageInfo> packageList = pm.getInstalledPackages(0);
		for(PackageInfo pi : packageList){
			if((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
				if (DEBUG) Log.i("sjh2", "系统程序 label = " + pi.applicationInfo.loadLabel(pm) + " ==" + pi.packageName); //若应用程序没有设置label则会读出包名！
			}
			else{
				if (DEBUG) Log.w("sjh2", "用户程序 label = " + pi.applicationInfo.loadLabel(pm) + " ==" + pi.packageName + " ==" + pi.applicationInfo.className); 
			}
		}
		if (DEBUG) Log.e("sjh2", "size" + packageList.size());//81
	
	
		List<ApplicationInfo> applicationList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo ai : applicationList){
			if((ai.flags & ApplicationInfo.FLAG_SYSTEM) > 0){//81
				if (DEBUG) Log.i("sjh3", "系统程序 label = " + ai.loadLabel(pm) + " ==" + ai.packageName); 
			}
			else{
				if (DEBUG) Log.w("sjh3", "用户程序 label = " + ai.loadLabel(pm)+ " ==" + ai.packageName + " ==" + ai.className); 
			}
		}
		if (DEBUG) Log.e("sjh3", "size" + applicationList.size());
	}

	@Override
	protected void onDestroy() {
		// if (DEBUG) Log.i("sjh1", "onDestroy");
		super.onDestroy();
	}


	@Override
	public void finish() {
		// if (DEBUG) Log.i("sjh1", "finish");
		super.finish();
		// 通过调用overridePendingTransition() 可以实时修改Activity的切换动画。但需注意的是:该函数必须在调用startActivity()或者finish()后立即调用，且只有效一次。
		// overridePendingTransition(R.animator.slide_left_in, R.animator.slide_right_out);
	}
}
