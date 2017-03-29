package com.example.apidemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;

public class ShortCutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createShortCut();
	}
	
    public void createShortCut(){
        //判断是否需要添加快捷方式             
        if(getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT)){
        	Log.i("sjh2", "长按桌面添加快捷方式");
        	Intent addShortCut = new Intent();                 
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME , "长按得到的快捷方式");                 
            Parcelable icon = ShortcutIconResource.fromContext(this, R.drawable.earth);
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);     
            addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this, PowerManagerActivity.class));                 
            //OK，生成 
            setResult(RESULT_OK, addShortCut);     
        }else{                 
             //取消                 
            setResult(RESULT_CANCELED);     
        }     
    }     

}
