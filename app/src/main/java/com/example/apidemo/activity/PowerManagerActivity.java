package com.example.apidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;

/**
 * 这三个Level在API17被弃用。PowerManager.FULL_WAKE_LOCK 等3个类型的作用无非就是保持屏幕长亮or唤醒屏幕。
 * 所以推荐是用WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON。
 * 使用方法是： 在Activity中： getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 * 甚至可以在布局中添加这个属性：android:keepScreenOn=”true”
 * @author shao
 *
 */
public class PowerManagerActivity extends BaseActivity {
	private static Context context;

	// @SuppressLint("WrongViewCast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_layout);

		context = this;


		ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub);		// 让ViewStub的内容显示
		viewStub.inflate();	// or viewStub.setVisibility(View.VISIBLE);
		// viewStub.setVisibility(View.GONE);	// 竟然还生效
		TextView inflateViewStub = (TextView) findViewById(R.id.inflate_viewstub);
		inflateViewStub.setText("I'm viewStub. ");
		Log.i("sjh2", "viewStub " + viewStub + findViewById(R.id.textview));		// viewstub内的R.id.textview被 android:inflatedId覆盖了 应该
		////////////////////////////////////////////////////////////////////////////////////////////

		final PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "apidemo:PowerManagerActivity");
		//设置是否判断wakelock的acquire和release次数是否相等，若为false，则一个release就取消所有acquire。若为true，没有wakelock的时候release则报错。
		wakeLock.setReferenceCounted(false);

		Log.i("sjh2", "not interactive");

		((Button) findViewById(R.id.button1)).setText("acquire wakelock");
		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				wakeLock.acquire(); //可以申请多个wakeLock
			}
		});

		((Button) findViewById(R.id.button2)).setText("release wakelock");
		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				wakeLock.release();
			}
		});

		((Button) findViewById(R.id.button3)).setText("tudo ");
		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
}
