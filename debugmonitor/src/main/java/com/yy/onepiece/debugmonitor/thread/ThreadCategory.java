/*
 * Copyright (C) 2015-2016 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yy.onepiece.debugmonitor.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 相同类型的thread集合
 * The thread category.
 */
public class ThreadCategory   {
    private String mStartWidthKey;
    private String mAlias;
    private List<ThreadInfo> mInfoList;
    private final static Map<String, String> THREAD_NAME_LOW_CACHE = new HashMap<>();

    boolean is(String threadName) {
        if (threadName == null) {
            return false;
        }

        final int diff = threadName.length() - mStartWidthKey.length();
        if (diff < 0) {
            return false;
        }

        String lowCaseThreadName = THREAD_NAME_LOW_CACHE.get(threadName);
        if (lowCaseThreadName == null) {
            lowCaseThreadName = threadName.toLowerCase();
            THREAD_NAME_LOW_CACHE.put(threadName, lowCaseThreadName);
        }

        return lowCaseThreadName.startsWith(mStartWidthKey);
    }

    int size() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mAlias);
        if (mInfoList == null || mInfoList.isEmpty()) {
            builder.append("[]");
        } else {
            builder.append('[');
            Iterator<ThreadInfo> it = mInfoList.iterator();
            while (it.hasNext()) {
                ThreadInfo next = it.next();
                builder.append(next.threadName);
                if (it.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append(']');
        }
        return builder.toString();
    }

    public StringBuilder appendAlias(StringBuilder builder) {
        return builder.append(mAlias).append(": ");
    }


    public static class Builder {
        private final ThreadCategory mThreadCategory = new ThreadCategory();

        public Builder(String key) {
            this(key, key);
        }

        public Builder(String startWithKey, String alias) {
            reset();
            mThreadCategory.mAlias = alias;
            mThreadCategory.mStartWidthKey = startWithKey.toLowerCase();
        }

        /**
         * @return Whether digested.
         */
        public boolean addIfSameCategory(final int hashCode, final String threadName) {
            if (mThreadCategory.is(threadName)) {
                add(hashCode, threadName);
                return true;
            }

            return false;
        }

        public void add(int hashCode, String threadName) {
            if (mThreadCategory.mInfoList == null) {
                mThreadCategory.mInfoList = new ArrayList<>();
            }

            mThreadCategory.mInfoList.add(new ThreadInfo(hashCode, threadName));
        }

        public void reset() {
            if (mThreadCategory.mInfoList != null) {
                mThreadCategory.mInfoList.clear();
            }
        }

        public ThreadCategory build() {
            return mThreadCategory;
        }
    }


    private static class ThreadInfo {
        private final int hashCode;
        private final String threadName;

        ThreadInfo(int hashCode, String threadName) {
            this.hashCode = hashCode;
            this.threadName = threadName;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ThreadInfo)) {
                return false;
            }

            final ThreadInfo another = (ThreadInfo) o;
            return another.hashCode == hashCode && threadName.equals(another.threadName);
        }
    }
}
