package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.fragments.FragmentListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.services.RadioStreamingService;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;


public class RadioPlayActivity extends AppCompatActivity{

    public static InterstitialAd interstitial;
    public static String[] ALL_REQUIRED_PERMISSION = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    boolean isSleepTimerEnabled = false;
    boolean isSleepTimerTimeout = false;
    long timerSetTime = 0;
    int timerTimeOutDuration = 0;
    List<String> minuteList;
    Handler sleepHandler;
    ImageView Menu;
    boolean mSlideState = false;
    Dialog dialog1;
    SharedPreferences settings;
    RadioDatabase databaseHandler;
    RelativeLayout notification;
    TextView notification_count;
    com.chennaifmradiosongs.onlinemadrasradiostation.database.DatabaseHandler handler;
    IntentFilter filter;

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showvideo() {
        // Show the ad if it's ready
        if (interstitial != null && interstitial.isLoaded()) {
            interstitial.show();
        }
    }

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

    int OVERLAY_PERMISSION_REQ_CODE = 1001;

    public void ShowOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
    }

    private void showDialogPermission() {
        if (!isAllPermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    requestPermissions(ALL_REQUIRED_PERMISSION, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (databaseHandler != null)
                if (databaseHandler.RadioisExist() != 0) {
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    Fragment fragment = new FragmentListRadio();
                    if (getIntent() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Radio_id", getIntent().getStringExtra("Radio_id"));
                        bundle.putInt("number", getIntent().getIntExtra("Radio_position", 0));
                        bundle.putInt("favorite", getIntent().getIntExtra("favorite_activity", 0));
                        fragment.setArguments(bundle);
                    }
                    mFragmentTransaction.replace(R.id.frame_container, fragment).commit();

                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_play_main);
        databaseHandler = new RadioDatabase(RadioPlayActivity.this);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setCurrentScreen(RadioPlayActivity.this, "Radio Playing Screen", null /* class override */);


        interstitial = new InterstitialAd(this);
        if (Config.ENABLE_ADS) {
            interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
        }
        String PREFS_NAME = "MyPrefsFile";
        settings = getSharedPreferences(PREFS_NAME, 0);
        dialog1 = new Dialog(this);

        sleepHandler = new Handler();

        minuteList = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            minuteList.add(String.valueOf(i * 5));
        }

        if (isNetworkAvailable(RadioPlayActivity.this)) {
            if (!isAllPermissionGranted(RadioPlayActivity.this)) {
                showDialogPermission();
            } else {
                if (databaseHandler != null)
                    if (databaseHandler.RadioisExist() != 0) {
                        mFragmentManager = getSupportFragmentManager();
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        Fragment fragment = new FragmentListRadio();
                        if (getIntent() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("Radio_id", getIntent().getStringExtra("Radio_id"));
                            bundle.putInt("number", getIntent().getIntExtra("Radio_position", 0));
                            bundle.putInt("favorite", getIntent().getIntExtra("favorite_activity", 0));
                            fragment.setArguments(bundle);
                        }
                        mFragmentTransaction.replace(R.id.frame_container, fragment).commit();

                    }
            }

        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Your Mobile has no internet Connection,Please Check with Setting Page")
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            finish();
                            Toast.makeText(getApplicationContext(), "After Enable Network,please open the app again", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ActivityMain.class));
    }

    public void showSleepDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sleep_timer_dialog);

        final WheelView wheelPicker = dialog.findViewById(R.id.wheelPicker);
        wheelPicker.setItems(minuteList);

        TextView title = dialog.findViewById(R.id.sleep_dialog_title_text);

        Button setBtn = dialog.findViewById(R.id.set_button);
        Button cancelBtn = dialog.findViewById(R.id.cancel_button);
        Button removerBtn = dialog.findViewById(R.id.remove_timer_button);

        LinearLayout buttonWrapper = dialog.findViewById(R.id.button_wrapper);

        TextView timerSetText = dialog.findViewById(R.id.timer_set_text);

        if (isSleepTimerEnabled) {
            wheelPicker.setVisibility(View.GONE);
            buttonWrapper.setVisibility(View.GONE);
            removerBtn.setVisibility(View.VISIBLE);
            timerSetText.setVisibility(View.VISIBLE);

            long currentTime = System.currentTimeMillis();
            long difference = currentTime - timerSetTime;

            int minutesLeft = (int) (timerTimeOutDuration - ((difference / 1000) / 60));
            if (minutesLeft > 1) {
                timerSetText.setText("ஸ்லீப் டைமர் இப்போது " + minutesLeft + " நிமிடங்களுக்கு உள்ளது");
            } else if (minutesLeft == 1) {
                timerSetText.setText("ஸ்லீப் டைமர் இப்போது " + 1 + "  நிமிடம் மட்டுமே உள்ளது.");
            }

        } else {
            wheelPicker.setVisibility(View.VISIBLE);
            buttonWrapper.setVisibility(View.VISIBLE);
            removerBtn.setVisibility(View.GONE);
            timerSetText.setVisibility(View.GONE);
        }

        removerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = false;
                isSleepTimerTimeout = false;
                timerTimeOutDuration = 0;
                timerSetTime = 0;
                sleepHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getApplicationContext(), "டைமர் அகற்றப்பட்டது", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = true;
                int minutes = Integer.parseInt(wheelPicker.getItems().get(wheelPicker.getSelectedPosition()));
                timerTimeOutDuration = minutes;
                timerSetTime = System.currentTimeMillis();
                sleepHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSleepTimerTimeout = true;
                        if (RadioStreamingService.getInstance().isPlaying()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "நேரம் முடிந்தது", Toast.LENGTH_SHORT).show();
                                    stopService(new Intent(RadioPlayActivity.this, RadioStreamingService.class));
                                    RadioStreamingService.getInstance().notificationCancel();
                                    finish();
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }, minutes * 60 * 1000);
                Toast.makeText(getApplicationContext(), "டைமர் இப்போது " + minutes + " நிமிடங்கள் அமைக்கப்பட்டது.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = false;
                isSleepTimerTimeout = false;
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void onFinish() {
        RadioPlayActivity.this.finish();
    }

}