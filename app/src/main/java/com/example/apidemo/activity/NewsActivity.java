package com.example.apidemo.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.adapter.RecycleViewAdapter;
import com.example.apidemo.view.CustomRecycleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/11.
 */
public class NewsActivity extends BaseActivity {
    private CustomRecycleView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItem = 0;
    // private List<SourceInfo> allSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        mRecyclerView = (CustomRecycleView) findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Object> itemDatas = new ArrayList<Object>();
        for(int i = 0; i < 10; i++){
            Object object = new Object();
            itemDatas.add(object);
        }
        mAdapter = new RecycleViewAdapter(itemDatas);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.w("sjh0", "onScrolled dx = " + dx + " dy = " + dy);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

}