package com.chennaifmradiosongs.onlinemadrasradiostation.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.ImageLoaderDefintion;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.AdapterGridRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.services.RadioStreamingService;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class RecentPlayRadioActivity extends AppCompatActivity {

    GridView listView;
    AdapterGridRadio adapterRadio;
    ItemListRadio itemListRadio;
    RadioDatabase databaseHandler;
    TextView textView1;
    String StrName, StrUrl;
    String StrId;
    List<ItemListRadio> arrayItemListRadio;
    ArrayList<ItemListRadio> arrayListItemListRadio;

    Intent intent;


    SharedPreferences preferences;
    AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.intent = new Intent(RecentPlayRadioActivity.this, RadioStreamingService.class);
        ImageLoaderDefintion.initImageLoader(RecentPlayRadioActivity.this);

        ((TextView) findViewById(R.id.dateformat)).setText("Recent Radio Playlist");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listView = findViewById(R.id.list_radio);
        textView1 = findViewById(R.id.textView1);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(RecentPlayRadioActivity.this);
        firebaseAnalytics.setCurrentScreen(RecentPlayRadioActivity.this, "Favorites List", null /* class override */);

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
        preferences = PreferenceManager.getDefaultSharedPreferences(RecentPlayRadioActivity.this);


        databaseHandler = new RadioDatabase(RecentPlayRadioActivity.this);
        this.arrayListItemListRadio = new ArrayList<ItemListRadio>();

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemListRadio = arrayItemListRadio.get(position);
                String radioId = itemListRadio.getRadioId();

                startActivity(new Intent(RecentPlayRadioActivity.this, RadioPlayActivity.class)
                        .putExtra("Radio_id", radioId)
                        .putExtra("Radio_position", position)
                        .putExtra("favorite_activity", 1));


            }
        });
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int index = info.position;

        itemListRadio = arrayItemListRadio.get(index);
        StrName = itemListRadio.getRadioName();

        menu.setHeaderTitle(String.valueOf(StrName));
        getMenuInflater().inflate(R.menu.recent_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        itemListRadio = arrayItemListRadio.get(index);

        StrName = itemListRadio.getRadioName();
        StrId = itemListRadio.getRadioId();
        StrUrl = itemListRadio.getRadiourl();

        switch (item.getItemId()) {
            case R.id.menu_context_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_title)
                        + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayItemListRadio = databaseHandler.getAllRecentRadio();
        if (arrayItemListRadio != null)
            adapterRadio = new AdapterGridRadio(RecentPlayRadioActivity.this, R.layout.fav_item_list_radio, arrayItemListRadio);
        listView.setAdapter(adapterRadio);

        if (arrayItemListRadio.size() == 0) {
            textView1.setVisibility(View.VISIBLE);
        } else {
            textView1.setVisibility(View.INVISIBLE);
        }
    }


}