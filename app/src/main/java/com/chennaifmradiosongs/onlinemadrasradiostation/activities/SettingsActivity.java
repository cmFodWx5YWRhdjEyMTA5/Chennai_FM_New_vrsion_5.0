package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.fragments.MyPreferenceFragment;

/**
 * Settings activity which basically shows MyPreferenceFragment.
 * Done like this due to a limitation of PreferenceFragment
 */
public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.context = this;

        //set toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close on back button pressed
                finish();
            }
        });


        //set fragment to MyPreferenceFragment
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();

    }


}
