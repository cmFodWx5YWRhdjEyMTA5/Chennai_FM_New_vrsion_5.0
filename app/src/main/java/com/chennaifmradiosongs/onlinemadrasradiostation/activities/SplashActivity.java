package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.DatabaseHandler;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.News;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.CustomWebView;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by AswinBalaji on 2018-04-20.
 */

public class SplashActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextViews() {
        Config.ENABLE_ADS = !bp.isPurchased(Config.PRODUCT_ID);
        if (getIntent() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String linkUrl = getIntent().getStringExtra("linkUrl");
                    String title = getIntent().getStringExtra("title");
                    String message = getIntent().getStringExtra("message");
                    String imageUrl = getIntent().getStringExtra("imageUrl");
                    String currentDateandTime = getIntent().getStringExtra("currentDateandTime");

                    if (linkUrl != null) {

                        databaseHandler.addContact(new News(title, message, linkUrl, imageUrl, currentDateandTime),
                                DatabaseHandler.TABLE_NEWS);
                        Intent intent1 = new Intent(getApplicationContext(), CustomWebView.class);
                        intent1.putExtra("title", title);
                        intent1.putExtra("openURL", linkUrl);
                        intent1.putExtra("FromActivity", 0);
                        startActivity(intent1);
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                        finish();
                    }
                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        databaseHandler = new DatabaseHandler(getApplicationContext());
        FacebookSdk.sdkInitialize(this);

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }
        bp = new BillingProcessor(SplashActivity.this,
                Config.LICENSE_KEY, Config.MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                updateTextViews();
                Log.d(LOG_TAG, "onProductPurchased: " + productId);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Log.d(LOG_TAG, "onBillingError: " + errorCode);
            }

            @Override
            public void onBillingInitialized() {
                Log.d(LOG_TAG, "onBillingInitialized: ");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }
}
