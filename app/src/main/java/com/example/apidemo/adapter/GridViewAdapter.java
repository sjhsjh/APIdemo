package com.example.apidemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;
import com.example.apidemo.R;

/**
 * LruCache的流程分析:
 * 从第一次进入应用的情况下开始
 * 1 依据图片的Path从LruCache缓存中取图片. 若图片存在缓存中,则显示该图片;否则显示默认图片
 * 2 因为是第一次进入该界面所以会执行: loadBitmaps(firstVisibleItem, visibleItemCount);
 *   从loadBitmaps()方法作为切入点,继续往下梳理
 * 3 尝试从LruCache缓存中取图片.如果在显示即可,否则进入4
 * 4 从SDCrad读取图片,并且将读取后的图片保存到LruCache缓存中
 * 5 在停止滑动时,会调用loadBitmaps(firstVisibleItem, visibleItemCount) 显示目前GridView可见Item的图片
 *
 * Created by 47070 on 2018/5/17.
 */
public class GridViewAdapter extends ArrayAdapter<String> {
    private GridView mGridView;
    //图片缓存类
    private LruCache<String, Bitmap> mLruCache;
    //GridView中可见的第一张图片的下标
    private int mFirstVisibleItem;
    //GridView中可见的图片的数量
    private int mVisibleItemCount;
    //记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private String mImagePath[];
    private Bitmap mDefaultBitmap;
    /**
     * @param context
     * @param textViewResourceId
     * @param objects adapter的数据 List<T> mObjects！！！
     * @param gridView
     */
    public GridViewAdapter(Context context, int textViewResourceId, String[] objects, GridView gridView) {
        super(context, textViewResourceId, objects);
        mGridView = gridView;
        mGridView.setOnScrollListener(new ScrollListenerImpl());
        //应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        System.out.println("maxMemory=" + maxMemory);   // 134 217 728 bytes
        //设置图片缓存大小为maxMemory的1/3
        int cacheSize = maxMemory / 3;
        mImagePath = objects;
        mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String path = getItem(position);    // 从mObjects取出数据！！
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        //为该ImageView设置一个Tag,防止图片错位
        imageView.setTag(path);
        // 设置了OnScrollListener，即使用了“停下加载”而放弃了“滑动时加载”，因此滑动过程中item需要设置正确的图片。
        // 因为ListView会复用itemView，因此滑到尚未加载内容的view时必须设置图片，否则就显示复用的图片了。
        setCacheBitmap(imageView, path);

        return view;
    }

    /**
     * 为ImageView设置图片
     * 1 从缓存中获取图片
     * 2 若图片不在缓存中则为其设置默认图片
     */
    private void setCacheBitmap(ImageView imageView, String imagePath) {
        Bitmap bitmap = getBitmapFromLruCache(imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher);
        }
    }

    /**
     * 将图片存储到LruCache
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache缓存获取图片
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 从本地or缓存中读取图片，并加载到GridView.
     *
     * @param firstVisibleItem GridView中可见的第一张图片的下标
     * @param visibleItemCount GridView中可见的图片的数量
     */
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                String imagePath = mImagePath[i];
                Bitmap bitmap = getBitmapFromLruCache(imagePath);
                if (bitmap == null) {
                    System.out.println("---> 图片" + imagePath + "不在缓存中,  所以从SDCard读取");
                    bitmap = BitmapFactory.decodeFile(imagePath);
                    if(bitmap == null){
                        return;
                    }
                    //将从SDCard读取的图片添加到LruCache中
                    addBitmapToLruCache(imagePath, bitmap);
                } else {
                    System.out.println("---> 图片在缓存中=" + imagePath + ",从缓存中取出即可");
                }
                //依据Tag找到对应的ImageView显示图片
                ImageView imageView = (ImageView) mGridView.findViewWithTag(imagePath);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过onScrollStateChanged获知:每次GridView停止滑动时加载图片
     * 但是存在一个特殊情况:
     * 当第一次入应用的时候,此时并没有滑动屏幕的操作即不会调用onScrollStateChanged,但应该加载图片，
     * 所以在onScroll进行初始化.
     * 其余的都是正常情况.
     * 所以我们需要不断保存:firstVisibleItem和visibleItemCount
     * 从而便于中在onScrollStateChanged()判断当停止滑动时加载图片
     */
    private class ScrollListenerImpl implements OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            if (isFirstEnterThisActivity && visibleItemCount > 0) {
                System.out.println("---> onScroll 初始化加载图片. ");
                loadBitmaps(firstVisibleItem, visibleItemCount);
                isFirstEnterThisActivity = false;
            }
        }

        /**
         * GridView停止滑动时下载图片
         * 其余情况下取消所有正在下载或者等待下载的任务
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                System.out.println("---> GridView停止滑动  mFirstVisibleItem=" + mFirstVisibleItem + ", mVisibleItemCount=" + mVisibleItemCount);
                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
            }
        }
    }


}

