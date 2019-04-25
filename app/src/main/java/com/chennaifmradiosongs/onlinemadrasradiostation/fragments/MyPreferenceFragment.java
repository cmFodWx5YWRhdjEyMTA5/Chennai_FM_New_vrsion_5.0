package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.CustomWebView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

/**
 * Created by melvin on 04/10/2016.
 * <p>
 * Preferences fragment. This is shown inside SettingsActivity.
 * It is not added in the main fragment because the PreferenceFragment doesnt extend the support Fragment :/
 */

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.setCurrentScreen(getActivity(), "Setting Page", null /* class override */);


        Preference preference = findPreference("privacy_policy");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = getResources().getString(R.string.Privacy_Policy);
                Intent intent1 = new Intent(getActivity(), CustomWebView.class);
                intent1.putExtra("openURL", url);
                intent1.putExtra("FromActivity", 1);
                intent1.putExtra("title", getResources().getString(R.string.privacy));
                startActivity(intent1);
                return true;
            }
        });

        Preference preference1 = findPreference("mail_feedback");
        preference1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback of our apps " + getActivity().getPackageName());
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });
        //get push notification preference and detect Change
        Preference myPref = findPreference("pref_enable_push_notifications");
        myPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    OneSignal.setSubscription(true);
                    Toast.makeText(getActivity(), "User CAN receive notifications ", Toast.LENGTH_SHORT).show();
                } else {
                    OneSignal.setSubscription(false);
                    Toast.makeText(getActivity(), "User CANNOT receive notifications", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }
}
