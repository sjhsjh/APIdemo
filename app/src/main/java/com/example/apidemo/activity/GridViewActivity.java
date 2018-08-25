package com.example.apidemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import com.example.apidemo.R;
import com.example.apidemo.adapter.GridViewAdapter;

/**
 * Created by 47070 on 2018/5/17.
 */

public class GridViewActivity extends Activity {
    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_layout);
        init();
    }

    private void init(){
        // /storage/emulated/0/Android/data/com.example.apidemo/files/test
        String root = getExternalFilesDir(null).getPath() + "/test";
        String ImagePath[] = new String[]{root + "/1", root + "/2", root + "/3", root + "/4", root + "/5", root + "/6"};

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new GridViewAdapter(this, 0, ImagePath, mGridView);
        mGridView.setAdapter(mGridViewAdapter);
    }



}
