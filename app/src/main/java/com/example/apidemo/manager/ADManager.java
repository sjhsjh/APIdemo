package com.example.apidemo.manager;


import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * 注意外部需要执行adView.destroy();
 * Log tag是“Ads”
 */
public class ADManager {
    private static final String TAG = "ADManager";
    private static ADManager sInstance;
    private SparseArray<InterstitialAd> sparseArray = new SparseArray<>();

    public static ADManager getInstance() {
        if (null == sInstance) {
            synchronized (ADManager.class) {
                if (null == sInstance) {
                    sInstance = new ADManager();
                }
            }
        }
        return sInstance;
    }

    public void initAD(Context context){
        MobileAds.initialize(context.getApplicationContext(), context.getResources().getString(R.string.admod_appid));
    }

    /**
     * @param adView 设置好尺寸和位置的adView
     * @param source 用于统计记录广告位
     */
    public void loadADToADView(final AdView adView, String source){
        if(TextUtils.isEmpty(source)){
            source = "empty";
        }
        // 含hashed device ID。 e.g:"Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("F2070FCC5025203FC17BE15E9546F427")
                .build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // todo 加统计
                // Code to be executed when an ad finishes loading.
                Toast.makeText(adView.getContext(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
                NLog.w(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                NLog.w(TAG, "onAdFailedToLoad errorCode = " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                NLog.w(TAG, "onAdOpened");
            }

            @Override
            public void onAdClicked() {     // 并无触发？？
                // Code to be executed when the user clicks on an ad.
                NLog.w(TAG, "onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                NLog.w(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                NLog.w(TAG, "onAdClosed");
            }
        });
        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }


    /**
     * @param adStringID ad ID 的string id
     * @return Add adView to your view hierarchy.
     */
    public AdView getBannerADViewAndLoad(Context context, int adStringID, String source) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(context.getResources().getString(adStringID));

        loadADToADView(adView, source);
        return adView;
    }


    public void loadFullScreenAD(final Context context, int adStringID, String source){
        InterstitialAd interstitialAd = sparseArray.get(adStringID);
        if(interstitialAd == null){
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getResources().getString(adStringID));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // todo 加统计
                    // Code to be executed when an ad finishes loading.
                    Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                    NLog.w(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    NLog.w(TAG, "onAdFailedToLoad errorCode = " + errorCode);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                    NLog.w(TAG, "onAdOpened");
                }

                @Override
                public void onAdClicked() {     // 并无触发？？
                    // Code to be executed when the user clicks on an ad.
                    NLog.w(TAG, "onAdClicked");
                }

                @Override
                public void onAdLeftApplication() {     // 并无触发？？
                    // Code to be executed when the user has left the app.
                    NLog.w(TAG, "onAdLeftApplication");
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                    NLog.w(TAG, "onAdClosed");
                }
            });

            sparseArray.put(adStringID, interstitialAd);
        }
        NLog.w(TAG, "isLoading = " + interstitialAd.isLoading() + " isLoaded = " + interstitialAd.isLoaded());
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

    }

    public void showFullScreenAD(int adStringID) {
        InterstitialAd interstitialAd = sparseArray.get(adStringID);
        if (interstitialAd == null || interstitialAd.isLoading() || !interstitialAd.isLoaded()) {   // 已经展示过的嵌入式广告，interstitialAd.isLoaded()为false
            return;
        }
        interstitialAd.show();
    }


}
