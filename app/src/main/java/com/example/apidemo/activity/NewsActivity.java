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

/**
 * <br> RecycleView的下拉刷新和加载更多
 * Created by jinhui.shao on 2017/4/11.
 */
public class NewsActivity extends BaseActivity {
    private CustomRecycleView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
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

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRecyclerViewListener(new CustomRecycleView.MyRecyclerViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.animateBackWhenUpdateFinish();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int lastNumber = mAdapter.mItemDatas.get(mAdapter.mItemDatas.size() - 1).intValue();
                        for(int i = 0; i < 5; i++){
                            mAdapter.mItemDatas.add(Integer.valueOf(i + lastNumber + 1));
                        }
                        // mAdapter.notifyItemInserted(4);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setLoadMoreComplete();
                    }
                }, 2000);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

//        View container = findViewById(R.id.container);
//        ViewGroup.LayoutParams lp = container.getLayoutParams();
//        lp.height = container.getMeasuredHeight() + getResources().getDimensionPixelSize(R.dimen.header_height);
//        container.setLayoutParams(lp);
    }

}