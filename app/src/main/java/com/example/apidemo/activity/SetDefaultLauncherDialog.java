package com.example.apidemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;


/**
 * Created by jinhui.shao on 2016/8/26.
 * 首次启动Launcher，引导页结束之后弹框提示设置默认桌面
 */
public class SetDefaultLauncherDialog extends Dialog {
	
	public SetDefaultLauncherDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		initView();
	}

	private Context mContext;
	private ImageView mCloseView;
	private TextView mTipsView;
	private Button mOKBtn;
	
	public SetDefaultLauncherDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
		initView();
		//cancelListener.
	}

	
	private void initView(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View glView = inflater.inflate(R.layout.default_launcher_tips, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // Android 自定义Dialog去除title导航栏. requestFeature() must be called before adding content 
		setContentView(glView);
		
		setCancelable(true);
		mCloseView = (ImageView) glView.findViewById(R.id.closeView);
		mTipsView = (TextView) glView.findViewById(R.id.tipsView);
		mOKBtn = (Button) glView.findViewById(R.id.OKBtn);
		
		mOKBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NLog.e("sjh2", "onClick");
				SetDefaultLauncherDialog.this.dismiss();
			}
		});

		mCloseView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SetDefaultLauncherDialog.this.dismiss();
			}
		});
		
		Window dialogWindow = this.getWindow();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);	
		 View decore = dialogWindow.getDecorView();
				 decore.setPadding(  16, 50,0, 0);
				 
		// NLog.e("sjh2", "asd" + decore.getPaddingTop());
		// mTipsView.setText("");

//		Window window = getWindow();
//		window.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);	// dialod默认在应用程序中央，设置x、y偏离是相对于应用程序中央的偏离，因此要设置顶部对齐。
//		// 去除dialog自带padding
//		View decore = window.getDecorView();
//		decore.setPadding(0, 0, 0, 0);
//		// dialog位置移动
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.width = 100;
//		lp.height = 100;
//		lp.y = 0;
//		window.setAttributes(lp);

	}



}
