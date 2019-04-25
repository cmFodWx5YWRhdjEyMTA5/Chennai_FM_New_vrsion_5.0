package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.CommonNewsAdapter;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.CommonRSSItem;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.CommonRSSparser;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.RecyclerItemClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;


public class International_NewsActivity extends AppCompatActivity {

    List<CommonRSSItem> rssItems = new ArrayList<>();
    ProgressDialog progressBar;
    ViewPager viewPager;
    InterstitialAd interstitialAd;
    String[] category_name = new String[]{"Top Stories", "India", "World", "Business",
            "Cricket", "Sports", "Life & Style"
            , "Science"};

    String[] category_url = new String[]{"https://www.thehindu.com/news/national/feeder/default.rss",
            "https://www.thehindu.com/news/national/feeder/default.rss",
            "https://www.thehindu.com/news/international/feeder/default.rss",
            "https://www.thehindu.com/business/feeder/default.rss",
            "https://www.thehindu.com/sport/cricket/feeder/default.rss",
            "https://www.thehindu.com/sport/feeder/default.rss",
            "https://www.thehindu.com/life-and-style/feeder/default.rss",
            "https://www.thehindu.com/sci-tech/science/feeder/default.rss"};

    CategoryAdapter adapterCategories;
    CommonRSSparser rssParser = new CommonRSSparser();
    private RecyclerView list_category;

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
        list_category = findViewById(R.id.list_category);

        ((TextView) findViewById(R.id.title)).setText("World News");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(International_NewsActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        list_category.setLayoutManager(horizontalLayoutManager);

        progressBar = new ProgressDialog(International_NewsActivity.this);
        progressBar.setMessage("Loading & Please Wait....");
        progressBar.show();

        viewPager = findViewById(R.id.viewpager);
        new MyAsyncTask("https://www.thehindu.com/news/national/feeder/default.rss").execute();

        setAdapter();
        list_category.addOnItemTouchListener(new RecyclerItemClickListener(International_NewsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(International_NewsActivity.this, CommonCategoryNewsActivity.class)
                        .putExtra("Category URL", category_url[position])
                        .putExtra("Category Title", category_name[position])
                        .putExtra("news_category", 1));
            }
        }));

    }

    public void setAdapter() {
        adapterCategories = new CategoryAdapter(International_NewsActivity.this, category_name);
        list_category.setAdapter(adapterCategories);
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
            CommonNewsAdapter newsAdapter = new CommonNewsAdapter(International_NewsActivity.this, rssItems, 2);
            viewPager.setAdapter(newsAdapter);
        }
    }


    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
        private final Context currentContext;
        private final String[] data;

        private final int[] user_images = new int[]{R.drawable.border_1, R.drawable.border_2, R.drawable.border_3
                , R.drawable.border_4, R.drawable.border_5, R.drawable.border_6
                , R.drawable.border_7, R.drawable.border_8, R.drawable.border_9
                , R.drawable.border_10, R.drawable.border_11, R.drawable.border_12
                , R.drawable.border_13};
        ArrayList<Integer> bg_array1;

        public CategoryAdapter(Context context, String[] info) {
            currentContext = context;
            this.data = info;
        }

        @NonNull
        @Override
        public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_news_home_category, parent, false);
            CategoryAdapter.MyViewHolder myViewHolder = new CategoryAdapter.MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
            bg_array1 = new ArrayList<Integer>();

            for (int i = 0; i < data.length; i++) {
                for (int user_image : user_images) {
                    bg_array1.add(user_image);
                }
            }
            bg_array1.subList(data.length, bg_array1.size()).clear();
            holder.txt_name.setText(data[position]);
            holder.txt_name.setBackgroundResource(bg_array1.get(position));
        }

        @Override
        public int getItemCount() {
            return data.length;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView txt_name;

            MyViewHolder(View view) {
                super(view);
                txt_name = view.findViewById(R.id.tv_cat_title);
            }
        }
    }
}
