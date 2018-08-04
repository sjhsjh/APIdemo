package com.example.apidemo;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.example.apidemo.activity.DIYViewActivity;
import com.example.apidemo.activity.EventDispatchActivity;
import com.example.apidemo.activity.GestureDectorActivity;
import com.example.apidemo.activity.GsonActivity;
import com.example.apidemo.activity.HardWareActivity;
import com.example.apidemo.activity.MessengerActivity;
import com.example.apidemo.activity.NewsActivity;
import com.example.apidemo.activity.NotificationActivity;
import com.example.apidemo.activity.PowerManagerActivity;
import com.example.apidemo.activity.ReferenceActivity;
import com.example.apidemo.activity.ResolveInfoActivity;
import com.example.apidemo.activity.RxJavaActivity;
import com.example.apidemo.activity.SDActivity;
import com.example.apidemo.activity.SQLActivity;
import com.example.apidemo.activity.ScrollActivity;
import com.example.apidemo.activity.SlideActivity;
import com.example.apidemo.activity.TestServiceActivity;
import com.example.apidemo.activity.WindowActivity;
import com.example.apidemo.adapter.DemoAdapter;
import com.example.apidemo.utils.HardWareUtils;
import com.example.apidemo.utils.NLog;

public class MainActivity extends BaseActivity {
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);

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
        list.add(EventDispatchActivity.class);
        list.add(GestureDectorActivity.class);
        list.add(SlideActivity.class);
        list.add(ScrollActivity.class);
        list.add(NewsActivity.class);
        list.add(DIYViewActivity.class);
        list.add(TestServiceActivity.class);
        list.add(NotificationActivity.class);
        list.add(SQLActivity.class);
        list.add(HardWareActivity.class);
        list.add(GsonActivity.class);
        list.add(WindowActivity.class);
        list.add(RxJavaActivity.class);
        list.add(ReferenceActivity.class);
        list.add(SDActivity.class);
        list.add(MessengerActivity.class);

        DemoAdapter adapter = new DemoAdapter(MainActivity.this, list);
        mListView.setAdapter(adapter);
        // 若itemView实现了Checkable接口，设置该句会改变item的chosen状态，即单击item会回调所有可见itemView的setChecked()。注意CHOICE_MODE_SINGLE无法取消单选。
        // mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object activityClass = parent.getItemAtPosition(position); // 根据位置判断跳转哪个activity!!!会调用adapter的getItem().
                NLog.i("sjh0", "onItemClick position is " + position + "  activity Class = " + activityClass.toString());

                Intent intent = new Intent(MainActivity.this, (Class<?>) activityClass);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_right_in, R.animator.slide_left_out);
            }

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardWareUtils.unRegisterBluetoothListener(this);
    }



}
