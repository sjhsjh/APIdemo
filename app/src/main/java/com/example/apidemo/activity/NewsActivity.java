package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.adapter.RecycleViewAdapter;
import com.example.apidemo.view.CustomRecycleView;
import java.util.ArrayList;
import java.util.List;
import static com.example.apidemo.view.CustomRecycleView.READY_TO_RESET;

/**
 * <br> RecycleView的下拉刷新和加载更多
 * Created by jinhui.shao on 2017/4/11.
 */
public class NewsActivity extends BaseActivity {
    private CustomRecycleView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItem = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        mHandler = new Handler();
        mRecyclerView = (CustomRecycleView) findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Integer> itemDatas = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            itemDatas.add(Integer.valueOf(i));
        }
        mAdapter = new RecycleViewAdapter(itemDatas);

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                NLog.w("sjh0", "onScrolled dx = " + dx + " dy = " + dy);
//            }
//        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRecyclerViewListener(new CustomRecycleView.MyRecyclerViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.onStatusChange(READY_TO_RESET);
                    }
                }, 1500);
            }

            @Override
            public void onLoadMore() {

            }
        });

    }

}