package com.example.apidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.view.ChildView;
import com.example.apidemo.view.MyHorizontalScrollView;
import java.util.ArrayList;
import android.app.Activity;

public class EventDispatchActivity extends BaseActivity {
    private MyHorizontalScrollView mScrollView;
    private ChildView mChildView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dispatch);
        mScrollView = (MyHorizontalScrollView) findViewById(R.id.scrollview);
        mChildView = (ChildView) findViewById(R.id.childview);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm2 = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm2);
        NLog.i("sjh1", "dm.widthPixels " + dm2.widthPixels);    // 1440

        findViewById(R.id.scroll_btn).getLayoutParams().width = dm2.widthPixels;
        createList(R.id.listview1, dm2.widthPixels);
        createList(R.id.listview2, dm2.widthPixels);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
     //   NLog.i("sjh1", "activity dispatchTouchEvent. " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        new Exception("sjh2").printStackTrace();
//        NLog.i("sjh1", "activity onTouchEvent. " + event.getAction());
        return super.onTouchEvent(event);
    }

    private void createList(int resourceID, int width) {
        ListView listView = (ListView) findViewById(resourceID);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add(resourceID + " name " + i);
        }
        listView.getLayoutParams().width = width;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.itemview, list);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//                Toast.makeText(DemoActivity_1.this, "click item " + position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }



}
