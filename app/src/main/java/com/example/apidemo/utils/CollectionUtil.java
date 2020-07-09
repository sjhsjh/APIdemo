package com.example.apidemo.utils;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @date 2020/6/28
 */
public class CollectionUtil {

    /**
     * Returns a copy of the given list that is safe to iterate over and perform actions that may
     * modify the original list.
     */
    @NonNull
    @SuppressWarnings("UseBulkOperation")
    public static <T> List<T> getSnapshot(@NonNull Collection<T> other) {
        // toArray creates a new ArrayList internally and does not guarantee that the values it contains
        // are non-null. Collections.addAll in ArrayList uses toArray internally and therefore also
        // doesn't guarantee that entries are non-null. WeakHashMap's iterator does avoid returning null
        // and is therefore safe to use. See #322, #2262.
        List<T> result = new ArrayList<>(other.size());
        for (T item : other) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

}
