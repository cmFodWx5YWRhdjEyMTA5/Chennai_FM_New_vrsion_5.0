package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.CommonNewsAdapter;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.CommonRSSItem;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.CommonRSSparser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;


public class CommonCategoryNewsActivity extends AppCompatActivity {

    List<CommonRSSItem> rssItems = new ArrayList<>();
    ProgressDialog progressBar;
    ViewPager viewPager;
    InterstitialAd interstitialAd;
    CommonRSSparser rssParser = new CommonRSSparser();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news_fragment);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progressBar = new ProgressDialog(CommonCategoryNewsActivity.this);
        progressBar.setMessage("Loading & Please Wait....");
        progressBar.show();

        viewPager = findViewById(R.id.viewpager);
        new MyAsyncTask(getIntent().getStringExtra("Category URL")).execute();
        ((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra("Category Title"));
    }


    private class MyAsyncTask extends AsyncTask<Object, Void, ArrayAdapter> {
        String newsurl;

        public MyAsyncTask(String url) {
            this.newsurl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        protected ArrayAdapter doInBackground(Object[] params) {
            String rss_url = newsurl;
            rssItems = rssParser.getRSSFeedItems(rss_url);
            return null;
        }

        protected void onPostExecute(ArrayAdapter adapter) {
            progressBar.dismiss();
            CommonNewsAdapter newsAdapter = new CommonNewsAdapter(CommonCategoryNewsActivity.this, rssItems, getIntent().getIntExtra("news_category", 1));
            viewPager.setAdapter(newsAdapter);
        }
    }
}
