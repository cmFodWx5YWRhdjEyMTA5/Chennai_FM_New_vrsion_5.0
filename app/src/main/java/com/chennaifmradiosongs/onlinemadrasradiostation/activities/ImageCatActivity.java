package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.AdapterWallpaper;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.EndlessRecyclerViewScrollListener;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.ItemWallpaper;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.LatestWallListener;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.LoadLatestWall;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.RecyclerViewClickListener;
import com.chennaifmradiosongs.onlinemadrasradiostation.image_utils.WallPaperDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class ImageCatActivity extends AppCompatActivity {

    public static final String URL_WALLPAPER_BY_CAT = "http://www.sportspider.in/gallery_gifs/web/api.php?cat_id=";
    public static String[] ALL_REQUIRED_PERMISSION = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static Boolean isLoadMore = false;
    Toolbar toolbar;
    RecyclerView recyclerView;
    AdapterWallpaper adapter;
    ArrayList<ItemWallpaper> arrayList;
    ProgressBar progressBar;
    Boolean isOver = false, isScroll = false;
    TextView textView_empty;
    LoadLatestWall loadWallpaper;
    int page = 1;
    GridLayoutManager grid;
    String cid;
    InterstitialAd interstitial;
    private List<Object> dataCombined;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;

    public static boolean isAllPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permission = ALL_REQUIRED_PERMISSION;
            if (permission.length == 0) return false;
            for (String s : permission) {
                if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showvideo() {
        // Show the ad if it's ready
        if (Config.ENABLE_ADS && interstitial != null && interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showvideo();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void showDialogPermission() {
        if (!isAllPermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(ALL_REQUIRED_PERMISSION, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkConnected()) {
            if (!isAllPermissionGranted(ImageCatActivity.this)) {
                showDialogPermission();
            }
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitial.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall_activity_wall_by_cat);

        coordinatorLayout = findViewById(R.id
                .main_content);

        if (getIntent() != null) {
            cid = getIntent().getStringExtra("image_cat");
        } else {
            cid = "1";
        }

        toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        if (dataCombined == null || !isLoadMore) {
            dataCombined = new ArrayList<>();
        }

        progressBar = findViewById(R.id.pb_wallcat);
        textView_empty = findViewById(R.id.tv_empty_wallcat);

        recyclerView = findViewById(R.id.rv_wall_by_cat);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(ImageCatActivity.this, 1);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getWallpaperData();
                        }
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });
        loadNativeAds();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;


    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getWallpaperData() {
        if (isNetworkAvailable()) {
            loadWallpaper = new LoadLatestWall(new LatestWallListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, ArrayList<ItemWallpaper> arrayListWall) {
                    if (arrayListWall.size() == 0) {
                        isOver = true;
                        try {
                            adapter.hideHeader();
                        } catch (Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            setEmptTextView();
                            e.printStackTrace();
                        }
                    } else {
                        for (int i = 0; i < arrayListWall.size(); i++) {
                        }
                        page = page + 1;
                        arrayList.addAll(arrayListWall);
                        progressBar.setVisibility(View.INVISIBLE);
                        setAdapter();
                    }
                }
            });
            loadWallpaper.execute(URL_WALLPAPER_BY_CAT + cid + "&page=" + page);
        }
    }

    private void loadNativeAds() {
        if (Config.ENABLE_ADS) {
            AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_ads));
            adLoader = builder.forUnifiedNativeAd(
                    new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            // A native ad loaded successfully, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            mNativeAds.add(unifiedNativeAd);
                            getWallpaperData();
                        }
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // A native ad failed to load, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another.");
                            getWallpaperData();
                        }
                    }).withNativeAdOptions(new NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()).build();

            // Load the Native ads.
            adLoader.loadAds(new AdRequest.Builder().build(), 1);
        } else {
            getWallpaperData();
        }
    }

    public void setAdapter() {
        dataCombined.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if ((i % 10 == 0 && i != 0)) {
                for (UnifiedNativeAd ad : mNativeAds) {
                    dataCombined.add(i, ad);
                }
            } else if (i == 2) {
                for (UnifiedNativeAd ad : mNativeAds) {
                    dataCombined.add(i, ad);
                }
            }
            dataCombined.add(arrayList.get(i));

        }

        if (!isScroll && !isLoadMore) {
            adapter = new AdapterWallpaper(ImageCatActivity.this, dataCombined, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(ImageCatActivity.this, WallPaperDetailsActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("Arraylist", arrayList);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setEmptTextView();
        } else {
            adapter.notifyDataSetChanged();
            isLoadMore = false;
        }
    }

    private void setEmptTextView() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }
}
