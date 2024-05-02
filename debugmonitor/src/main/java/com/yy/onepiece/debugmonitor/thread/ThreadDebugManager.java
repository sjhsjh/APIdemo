package com.yy.onepiece.debugmonitor.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author shaojinhui@yy.com
 * @date 2020/7/24
 */
public class ThreadDebugManager {
    private static final String TAG = "ThreadDebugManager";

    public ThreadDebugManager() {
        initCategoryBuilderList();
    }

    private final List<ThreadCategory.Builder> mCategoryBuilderList = new ArrayList<>();

    public ThreadDebugManager add(String key) {
        return add(key, key);
    }

    public ThreadDebugManager add(String startWithKey, String alias) {
        mCategoryBuilderList.add(new ThreadCategory.Builder(startWithKey, alias));
        return this;
    }

    public void initCategoryBuilderList() {
        add(CommonThreadKey.OpenSource.OKHTTP)
                .add(CommonThreadKey.OpenSource.OKIO)
                .add(CommonThreadKey.OpenSource.RETROFIT)
                .add(CommonThreadKey.OpenSource.CRASHLYTICS)
                .add(CommonThreadKey.OpenSource.LEAKCANARY)
                .add(CommonThreadKey.OpenSource.RX_JAVA, "RxJava")
                .add(CommonThreadKey.OpenSource.PICASSO)
                .add(CommonThreadKey.OpenSource.FILEDOWNLOADER)
                .add(CommonThreadKey.OpenSource.GLIDE)

                .add(CommonThreadKey.System.MAIN)
                .add(CommonThreadKey.System.CHROME)
                .add(CommonThreadKey.System.ASYNC_TASK)
                .add(CommonThreadKey.System.BINDER)
                .add(CommonThreadKey.System.FINALIZER)
                .add(CommonThreadKey.System.WIFI)
                .add(CommonThreadKey.System.RENDER_THREAD)
                .add(CommonThreadKey.System.HEAP_TASK_DAEMON)
                .add(CommonThreadKey.System.REFERENCE_QUEUE_DAEMON)
                .add(CommonThreadKey.System.FINALIZER_DAEMON)
                .add(CommonThreadKey.System.FINALIZER_WATCHDOG_DAEMON)
                .add(CommonThreadKey.System.JDWP)
                .add(CommonThreadKey.Media.AUDIO)
                .add(CommonThreadKey.Media.MEDIA)
                .add(CommonThreadKey.Media.EXO_PLAYER)
                .add(CommonThreadKey.Others.THREAD_DEBUGGER)
                .add(CommonThreadKey.Others.QUEUE)
                .add(CommonThreadKey.Others.THREAD_DEFAULT_PREFIX, "default(Thread-xxx)");
    }

    private int mSize = 0;
    private List<ThreadCategory> mCategoryList = new ArrayList<ThreadCategory>();           // 已知类型的
    private List<ThreadCategory> mOtherCategoryList = new ArrayList<ThreadCategory>();      // 未知类型的

    private void resetData() {
        for (ThreadCategory.Builder builder : mCategoryBuilderList) {
            builder.reset();
        }
        mCategoryList.clear();
        mOtherCategoryList.clear();
    }

    public int dumpThreadData() {
        final Thread[] threads = ThreadUtils.getAllThreads();
        resetData();

        boolean hasAdd = false;
        int size = 0;
        for (Thread thread : threads) {
            if (thread != null) {
                size++;

                hasAdd = false;
                for (ThreadCategory.Builder builder : mCategoryBuilderList) {
                    if (builder.addIfSameCategory(thread.hashCode(), thread.getName())) {
                        hasAdd = true;
                        break;
                    }
                }
                if (!hasAdd) {
                    ThreadCategory.Builder builder = new ThreadCategory.Builder(thread.getName());
                    builder.add(thread.hashCode(), thread.getName());
                    mOtherCategoryList.add(builder.build());
                }
            }
        }

        for (ThreadCategory.Builder builder : mCategoryBuilderList) {
            mCategoryList.add(builder.build());
        }

        mSize = size;
        return size;
    }

    private final static String NO_DATA = "NO data";
    private final static String CATEGORY_SPLIT = " | ";

    private final static Comparator<ThreadCategory> THREAD_CATEGORY_COMPARATOR = new Comparator<ThreadCategory>() {
        @Override
        public int compare(ThreadCategory lhs, ThreadCategory rhs) {
            return rhs.size() - lhs.size();
        }
    };

    private static void deleteLastSplit(StringBuilder builder) {
        builder.delete(builder.length() - CATEGORY_SPLIT.length(), builder.length());
    }

    private static void appendSplit(StringBuilder builder) {
        builder.append(CATEGORY_SPLIT);
    }

    private void sort(List<ThreadCategory> categoryList) {
        Collections.sort(categoryList, THREAD_CATEGORY_COMPARATOR);
    }

    /**
     * 返回线程分类信息，按数量大小排序
     */
    public String drawUpEachThreadSize() {
        if (mCategoryList.isEmpty()) {
            return NO_DATA;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Thread total count = ")
                .append(mSize)
                .append(". ");

        // 从多到少排序
        sort(mCategoryList);
        boolean hasSplit = false;
        for (ThreadCategory threadCategory : mCategoryList) {
            if (threadCategory.size() > 0) {
                threadCategory.appendAlias(builder).append(threadCategory.size());
                hasSplit = true;
                appendSplit(builder);
            }
        }

        if (hasSplit) {
            deleteLastSplit(builder);
        }

        builder.append("\n");
        for (ThreadCategory threadCategory : mOtherCategoryList) {
            if (threadCategory.size() > 0) {
                threadCategory.appendAlias(builder).append(threadCategory.size());
                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
