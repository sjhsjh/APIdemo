package com.example.apidemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.adapter.RecycleViewAdapter;
import com.example.apidemo.utils.NLog;
import com.example.apidemo.view.CustomRecycleView;
import java.util.ArrayList;
import java.util.List;

/**
 * <br> RecycleView的下拉刷新和加载更多
 * Created by jinhui.shao on 2017/4/11.
 */
public class NewsActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        mHandler = new Handler();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Integer> itemDatas = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            itemDatas.add(Integer.valueOf(i));
        }

        mAdapter = new RecycleViewAdapter(itemDatas);
        mRecyclerView.setAdapter(mAdapter);

        // mRecyclerView.setRecyclerViewListener(new CustomRecycleView.MyRecyclerViewListener() {
        //     @Override
        //     public void onRefresh() {
        //         mHandler.postDelayed(new Runnable() {
        //             @Override
        //             public void run() {
        //                 mRecyclerView.animateBackWhenUpdateFinish();
        //             }
        //         }, 2000);
        //     }
        //
        //     @Override
        //     public void onLoadMore() {
        //         // mHandler.postDelayed(new Runnable() {
        //         //     @Override
        //         //     public void run() {
        //         //         int lastNumber = mAdapter.mItemDatas.get(mAdapter.mItemDatas.size() - 1).intValue();
        //         //         for(int i = 0; i < 5; i++){
        //         //             mAdapter.mItemDatas.add(Integer.valueOf(i + lastNumber + 1));
        //         //             // mAdapter.notifyItemInserted(i + lastNumber + 2);    // 移除item的时候会先闪烁一下recycleView的背景
        //         //         }
        //         //         mAdapter.notifyDataSetChanged();
        //         //         mRecyclerView.setLoadMoreComplete();
        //         //     }
        //         // }, 2000);
        //     }
        // });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                NLog.w("sjh7", "range = " + mRecyclerView.computeVerticalScrollRange()
                        + "  offset = " + mRecyclerView.computeVerticalScrollOffset()
                        + "  extent = " + mRecyclerView.computeVerticalScrollExtent()
                        + "  getHeight = " + mRecyclerView.getHeight()
                        + "  container getHeight = " + findViewById(R.id.container).getHeight()
                        + "  last = " + mLayoutManager.findLastVisibleItemPosition());
            }
        });

        // mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
        //     @Override
        //     public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //
        //         NLog.d("sjh7", "range = " + mRecyclerView.computeVerticalScrollRange()
        //                 + "  offset = " + mRecyclerView.computeVerticalScrollOffset()
        //                 + "  extent = " + mRecyclerView.computeVerticalScrollExtent());
        //     }
        // });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // View container = findViewById(R.id.container);
        // ViewGroup.LayoutParams lp = container.getLayoutParams();
        // lp.height = container.getMeasuredHeight() + getResources().getDimensionPixelSize(R.dimen.header_height);
        // container.setLayoutParams(lp);
    }

}