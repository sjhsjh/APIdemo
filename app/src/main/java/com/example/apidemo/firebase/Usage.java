package com.example.apidemo.firebase;

import android.os.Bundle;
import com.example.apidemo.APIDemoApplication;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Map;


public class Usage {

    public static void pv(String event) {
        FirebaseAnalytics.getInstance(APIDemoApplication.getContext()).logEvent(event, null);
    }

    public static void pv(String event, String value) {
        Bundle params = new Bundle();
        params.putString("key", value);
        FirebaseAnalytics.getInstance(APIDemoApplication.getContext()).logEvent(event, params);
    }

    public static void pv(String event, Map<String, Object> map) {
        Bundle params = new Bundle();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.putString(entry.getKey(), entry.getValue().toString());
        }

        FirebaseAnalytics.getInstance(APIDemoApplication.getContext()).logEvent(event, params);


//        Crashlytics.logException(new Exception("测试crash"));
//         // 示例
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "99");
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        FirebaseAnalytics.getInstance(MaisnActivity.this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

}
