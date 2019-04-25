package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.fragments.ChannelFragment;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

/**
 * Created by AswinBalaji on 08-Feb-18.
 */

public class ChannelCategoryActivity extends AppCompatActivity {

    public ArrayList<ItemListRadio> titles;
    ViewPager vpPager;
    TabLayout tabLayout;
    RadioDatabase databaseHandler;
    ArrayList<ItemListRadio> content;
    FragmentPagerAdapter adapterViewPager;

    AdView mAdView;
    SharedPreferences preferences;
    int viewpager_position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tablayout);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent() != null) {
            viewpager_position = getIntent().getIntExtra("viewpager_position", 0);
        }
        mAdView = findViewById(R.id.adView);
        if (Config.ENABLE_ADS) {
            AdRequest adRequest1 = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest1);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView.loadAd(new AdRequest.Builder().build());
                }
            });
        } else {
            mAdView.setVisibility(View.GONE);
        }

        databaseHandler = new RadioDatabase(ChannelCategoryActivity.this);
        tabLayout = findViewById(R.id.sliding_tabs);
        titles = new ArrayList<ItemListRadio>();
        content = new ArrayList<ItemListRadio>();

        preferences = PreferenceManager.getDefaultSharedPreferences(ChannelCategoryActivity.this);


        titles = databaseHandler.CategorytypeList();
        if (titles != null)
            for (int i = 0; i < titles.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(titles.get(i).getRadioCateogory()));
            }

        vpPager = findViewById(R.id.viewpager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        vpPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(vpPager);

        vpPager.setCurrentItem(viewpager_position);
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ChannelCategoryActivity.this);
                if (titles != null)
                    firebaseAnalytics.setCurrentScreen(ChannelCategoryActivity.this, titles.get(position).getRadioCateogory(), null);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        int tabcount;

        MyPagerAdapter(FragmentManager fragmentManager, int tabCount) {
            super(fragmentManager);
            this.tabcount = tabCount;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return tabcount;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            if (databaseHandler != null)
                content = databaseHandler.CategorytypeListRadio(titles.get(position).getRadioCateogory());
            return ChannelFragment.newInstance(content);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String Page_title;
            Page_title = titles.get(position).getRadioCateogory();
            return Page_title;
        }

    }
}
