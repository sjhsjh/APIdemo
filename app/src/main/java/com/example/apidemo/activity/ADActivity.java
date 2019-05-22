package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.manager.ADManager;
import com.google.android.gms.ads.AdView;


public class ADActivity extends BaseActivity {
    private static final String TAG = "ADActivity";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);

        mAdView = findViewById(R.id.adView);

        ((Button) findViewById(R.id.btn1)).setText("loadADToADView");
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ADManager.getInstance().loadADToADView(mAdView, TAG);

                View view = ADManager.getInstance().getBannerADViewAndLoad(ADActivity.this, R.string.banner_ad_unit_id, TAG);
                ((FrameLayout) ADActivity.this.findViewById(R.id.root)).addView(view);
            }
        });

        ((Button) findViewById(R.id.btn2)).setText("loadFullScreenAD");
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ADManager.getInstance().loadFullScreenAD(ADActivity.this, R.string.interstitial_unit_id, TAG);
            }
        });

        ((Button) findViewById(R.id.btn3)).setText("showFullScreenAD");
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ADManager.getInstance().showFullScreenAD(R.string.interstitial_unit_id);
            }
        });

    }


}
