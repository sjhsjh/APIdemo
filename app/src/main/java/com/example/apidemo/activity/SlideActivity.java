package com.example.apidemo.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import java.util.ArrayList;

/**
 * <br> 悬浮header的ListView，根布局是StickyLayout
 * Created by jinhui.shao on 2017/8/28.
 */
public class SlideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_layout);
        createList(R.id.listview_slide);
    }

    private void createList(int resourceID) {
        ListView listView = (ListView) findViewById(resourceID);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add(resourceID + " item " + i);
        }

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