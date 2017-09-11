package com.example.apidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventDispatchActivity extends BaseActivity {
    private MyHorizontalScrollView mScrollView;
    private ChildView mChildView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dispatch);
        mScrollView = (MyHorizontalScrollView) findViewById(R.id.scrollview);
        mChildView = (ChildView) findViewById(R.id.childview);
        mListView = (ListView)findViewById(R.id.listview1);

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
//        NLog.d("sjh1", "first child text : " + ((TextView)((RelativeLayout)(mListView).getChildAt(0)).getChildAt(0)).getText()
//         + "  ===  " + ((TextView)((RelativeLayout)(mListView.getAdapter().getView(10, null, null))).getChildAt(0)).getText()); // getScrollY 0
        return super.dispatchTouchEvent(event);
    }

    /**
     * ListView通过position指定view(使用getView!)
     */
    public View getViewByPosition(int pos, ListView listView) {
        final int firstPosition = listView.getFirstVisiblePosition();
        // getChildCount（）  返回的是显示在屏幕上可见的item的数量(包含显示不玩全的)
        final int lastPosition = firstPosition + listView.getChildCount() -1;
        if(pos < firstPosition || pos > lastPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        }else{
            final int childIndex = pos - firstPosition;
            return listView.getChildAt(childIndex);
        }

    }

    /**
     * ListView没有提供得到滚动高度的任何方法，必须自己根据getChildAt(0).top和getFirstVisiblePosition()来综合计算获得。(前提是每个item的高度相同)
     */
    public int getListViewScrollYDistance() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight() ;
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
