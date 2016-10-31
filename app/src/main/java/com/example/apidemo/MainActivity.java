package com.example.apidemo;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.example.apidemo.activity.PowerManagerActivity;
import com.example.apidemo.activity.ResolveInfoActivity;
import com.example.apidemo.adapter.DemoAdapter;
import com.example.apidemo.utils.HardWareUtils;


public class MainActivity extends Activity {
    private static final boolean DEBUG = true;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView =(ListView) findViewById(R.id.listview);

        Intent shortCutIntent = new Intent(MainActivity.this, PowerManagerActivity.class);
        // shortCutIntent.setComponent(new ComponentName("com.example.some3", "com.example.some3.MainActivity"));
//		shortCutIntent.setAction(Intent.ACTION_MAIN);
//		shortCutIntent.addCategory(Intent.CATEGORY_LAUNCHER);

//		Intent createShortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//		createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
//		Parcelable parcelableIcon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
//		createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, parcelableIcon);
//		createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "快捷方式");
//		sendBroadcast(createShortCutIntent);


        HardWareUtils.registerBluetoothListener(this);

        List<Object> list = new ArrayList<Object>();
        // TUDO
        list.add(ResolveInfoActivity.class);
        list.add(PowerManagerActivity.class);

        DemoAdapter adapter = new DemoAdapter(MainActivity.this, list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(DEBUG) Log.i("sjh0", "onItemClick position　test11111　" + position);

                Object activityClass = parent.getItemAtPosition(position); //!!!
                if(DEBUG) Log.i("sjh0", " activityClass = " + activityClass.toString());

                Intent intent = new Intent(MainActivity.this, (Class<?>) activityClass);
                startActivity(intent);
                // overridePendingTransition(R.animator.slide_right_in, R.animator.slide_left_out);
            }

        });






    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardWareUtils.unRegisterBluetoothListener(this);
    }



}
