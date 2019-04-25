package com.chennaifmradiosongs.onlinemadrasradiostation.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.DatabaseHandler;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.chennaifmradiosongs.onlinemadrasradiostation.fragments.HomeFragment;
import com.chennaifmradiosongs.onlinemadrasradiostation.fragments.InQueryFragment;
import com.chennaifmradiosongs.onlinemadrasradiostation.json.JsonConstant;
import com.chennaifmradiosongs.onlinemadrasradiostation.json.JsonUtils;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.services.RadioStreamingService;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.CustomWebView;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.NotificationActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.utilities.PermissionUtil;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import hotchemi.android.rate.StoreType;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;

import static android.content.ContentValues.TAG;
import static com.chennaifmradiosongs.onlinemadrasradiostation.utilities.PermissionUtil.ALL_REQUIRED_PERMISSION;

@SuppressLint("StaticFieldLeak")
public class ActivityMain extends AppCompatActivity {

    private static final String KEY_POSITION_X = "position_x";
    private static final String KEY_POSITION_Y = "position_y";
    public static boolean isActive;
    public static InterstitialAd interstitial;
    public static Activity fa;
    public static SharedPreferences app_preferences;
    public ImageView Refresh;

    DrawerLayout mDrawerLayout;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    NavigationView navigationView;
    List<String> minuteList;
    Handler sleepHandler;
    ImageView Menu;
    boolean mSlideState = false;
    Dialog dialog1;
    SharedPreferences settings;
    RadioDatabase databaseHandler;
    RelativeLayout notification;
    TextView notification_count;
    DatabaseHandler handler;
    Boolean isFirstTime;
    SharedPreferences.Editor editor;
    boolean doubleBackToExitPressedOnce = false;

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

    private void showDialogPermission() {
        if (!PermissionUtil.isAllPermissionGranted(this)) {
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
            new GetTask(Config.SERVER_URL).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHandler = new RadioDatabase(ActivityMain.this);
        fa = ActivityMain.this;

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setCurrentScreen(ActivityMain.this, "Radio Home Page", null /* class override */);


        app_preferences = getSharedPreferences("isFirstTime", Context.MODE_PRIVATE);
        editor = app_preferences.edit();
        isFirstTime = app_preferences.getBoolean("isFirstTime", true);

        isActive = true;
        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY)
                .setInstallDays(5) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10 times.
                .setRemindInterval(1)
                .setShowLaterButton(true) // default true.
                .setDebug(false) // default false.
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(ActivityMain.class.getName(), Integer.toString(which));
                    }
                })
                .setMessage(R.string.rate_dialog_message)
                .setTitle(R.string.rate_dialog_title)
                .setTextLater(R.string.rate_dialog_cancel)
                .setTextNever(R.string.rate_dialog_no)
                .setTextRateNow(R.string.rate_dialog_ok)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(ActivityMain.this);
        // Show a dialog if meets conditions

        interstitial = new InterstitialAd(this);
        if (Config.ENABLE_ADS) {
            interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ActivityMain.this, EndSplashActivity.class));
                    finish();
                    super.onAdClosed();
                }

                @Override
                public void onAdLeftApplication() {
                    startActivity(new Intent(ActivityMain.this, EndSplashActivity.class));
                    finish();
                    super.onAdLeftApplication();
                }
            });
        }
        String PREFS_NAME = "MyPrefsFile";
        settings = getSharedPreferences(PREFS_NAME, 0);
        dialog1 = new Dialog(this);

        sleepHandler = new Handler();

        minuteList = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            minuteList.add(String.valueOf(i * 5));
        }

        Menu = findViewById(R.id.menu);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSlideState) {
                    mDrawerLayout.closeDrawer(Gravity.END);
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        Refresh = findViewById(R.id.refresh);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetTaskonRefresh(Config.SERVER_URL).execute();
            }
        });

        notification_count = findViewById(R.id.notification_count);
        handler = new com.chennaifmradiosongs.onlinemadrasradiostation.database.DatabaseHandler(ActivityMain.this);
        if (handler.getContactsCount() != 0 && handler.getContactsCount() > 10) {
            notification_count.setVisibility(View.VISIBLE);
            notification_count.setText("10+");
        } else if (handler.getContactsCount() != 0 && handler.getContactsCount() < 10) {
            notification_count.setVisibility(View.VISIBLE);
            notification_count.setText("" + handler.getContactsCount());
        } else if (handler.getContactsCount() == 0) {
            notification_count.setVisibility(View.GONE);
        }

        notification = findViewById(R.id.notification);
        notification.setAnimation(AnimationUtils.loadAnimation(ActivityMain.this, R.anim.vibrate));
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, NotificationActivity.class));
            }
        });


        mDrawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.toolbar).bringToFront();

        initDrawerMenu();

        Boolean isFirstTime;
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(ActivityMain.this);
        SharedPreferences.Editor editor = app_preferences.edit();
        isFirstTime = app_preferences.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            new MaterialTapTargetSequence()
                    .addPrompt(new MaterialTapTargetPrompt.Builder(ActivityMain.this)
                            .setTarget(findViewById(R.id.menu))
                            .setPrimaryText("Menu Option")
                            .setBackgroundColour(getResources().getColor(R.color.top_color))
                            .setSecondaryText("To View Multiple Option Avaliable on Menu")
                            .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                            .setFocalPadding(R.dimen.dp40)
                            .setIcon(R.drawable.ic_menu).create(), 4000)
                    .addPrompt(new MaterialTapTargetPrompt.Builder(ActivityMain.this)
                            .setTarget(findViewById(R.id.notification))
                            .setBackgroundColour(getResources().getColor(R.color.top_color))
                            .setPrimaryText("Notificaton")
                            .setSecondaryText("To View News/Notification Avaliable From Our Developer")
                            .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                            .setFocalPadding(R.dimen.dp40)
                            .setIcon(R.drawable.ic_notify).create(), 4000)
                    .addPrompt(new MaterialTapTargetPrompt.Builder(ActivityMain.this)
                            .setTarget(findViewById(R.id.refresh))
                            .setBackgroundColour(getResources().getColor(R.color.top_color))
                            .setPrimaryText("Update the Radio From Server")
                            .setSecondaryText("Refresh Button help to update the Radios Avaliable in local")
                            .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                            .setFocalPadding(R.dimen.dp40)
                            .setIcon(R.drawable.ic_refresh))
                    .show();

            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }

        if (isNetworkAvailable(ActivityMain.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showDialogPermission();
            }
            if (databaseHandler != null)
                if (databaseHandler.RadioisExist() != 0) {
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    Fragment fragment = new HomeFragment();
                    mFragmentTransaction.replace(R.id.frame_container, fragment);
                    mFragmentTransaction.commit();
                } else {
                    new GetTask(Config.SERVER_URL).execute();
                }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Your Mobile has no internet Connection,Please Check with App Setting")
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
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
                        }
                    })
                    .show();
        }
    }

    private void initDrawerMenu() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mSlideState = false;//is Closed
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
                mSlideState = true;//is Opened
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.main_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                displayActivity(menuItem.getItemId(), menuItem.getTitle().toString());
                drawer.closeDrawers();
                return true;
            }
        });
    }

    private void displayActivity(int itemId, String title) {

        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (itemId) {
            case R.id.equalizer:
                if (RadioStreamingService.getInstance().isPlaying()) {
                    startActivity(new Intent(ActivityMain.this, EqualizerActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "No Radio Streaming Playing", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.drawer_fav:
                startActivity(new Intent(ActivityMain.this, FavoriteActivity.class));
                break;
            case R.id.ads_free:
                startActivity(new Intent(ActivityMain.this, InAppPurchaseActivity.class));
                break;
            case R.id.add:
                mFragmentManager = getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                Fragment inQueryFragment = new InQueryFragment();
                mFragmentTransaction.replace(R.id.frame_container, inQueryFragment).commit();
                break;
            case R.id.drawer_privacy:
                String url1 = getResources().getString(R.string.Privacy_Policy);
                Intent intent1 = new Intent(ActivityMain.this, CustomWebView.class);
                intent1.putExtra("openURL", url1);
                intent1.putExtra("FromActivity", 1);
                intent1.putExtra("title", getResources().getString(R.string.privacy));
                startActivity(intent1);
                break;
            case R.id.drawer_radio:
                startActivity(new Intent(getApplicationContext(), ChannelCategoryActivity.class)
                        .putExtra("viewpager_position", 0));
                break;
            case R.id.notification:
                Intent i1 = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(i1);
                break;
            case R.id.drawer_terms:
                String url5 = getResources().getString(R.string.terms_conditions);
                Intent intent5 = new Intent(ActivityMain.this, CustomWebView.class);
                intent5.putExtra("openURL", url5);
                intent5.putExtra("title", getResources().getString(R.string.tems_conditions));
                intent5.putExtra("FromActivity", 1);
                startActivity(intent5);
                break;
            case R.id.nav_facebook:
                String url2 = getResources().getString(R.string.whatsapp_link);
                Intent intent2 = new Intent(ActivityMain.this, CustomWebView.class);
                intent2.putExtra("openURL", url2);
                intent2.putExtra("FromActivity", 1);
                intent2.putExtra("title", getResources().getString(R.string.title_nav_social));
                startActivity(intent2);
                break;
            case R.id.drawer_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.drawer_rate:
                String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
                break;
            case R.id.drawer_about:
                String url4 = getResources().getString(R.string.website);
                Intent intent4 = new Intent(ActivityMain.this, CustomWebView.class);
                intent4.putExtra("openURL", url4);
                intent4.putExtra("FromActivity", 1);
                intent4.putExtra("title", getResources().getString(R.string.app_name));
                startActivity(intent4);
                break;
            case R.id.setting:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {

        //if stack has items left
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            //get current fragment
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            //only close if in HomeFragment else go to HomeFragment
            if (fragment instanceof HomeFragment) {
                DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    //drawer is open
                    mDrawerLayout.closeDrawers();
                } else {
                    if (doubleBackToExitPressedOnce) {
                        if (RadioStreamingService.getInstance().isPlaying()) {
                            Toast.makeText(ActivityMain.this, "Songs Play in Background", Toast.LENGTH_SHORT).show();
                            isActive = true;
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);
                        } else {
                            MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(this)
                                    .setTitle(R.string.app_name)
                                    .setDescription("ஆப்ஸை விட்டு வெளியேற ?")
                                    .withDialogAnimation(false)
                                    .setIcon(R.drawable.exit)
                                    .withIconAnimation(false)
                                    .setHeaderColor(R.color.top_color)
                                    .setStyle(Style.HEADER_WITH_ICON)
                                    .setNegativeText("REVIEW NOW")
                                    .setPositiveText("EXIT")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            isActive = false;
                                            stopService(new Intent(ActivityMain.this, RadioStreamingService.class));
                                            RadioStreamingService.getInstance().notificationCancel();
                                            if (Config.ENABLE_ADS &&
                                                    isNetworkAvailable(ActivityMain.this) && interstitial.isLoaded()) {
                                                showvideo();
                                            } else {
                                                startActivity(new Intent(ActivityMain.this, EndSplashActivity.class));
                                                finish();
                                            }
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            String appName = getPackageName();
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                                            }
                                        }
                                    }).build();
                            dialog.show();

                        }
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }else if(fragment instanceof InQueryFragment){
                mFragmentManager = getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.frame_container, new HomeFragment()).commit();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class GetTask extends AsyncTask<String, Void, Void> {

        String url;
        ProgressDialog progressDialog;

        public GetTask(String serverUrl) {
            this.url = serverUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityMain.this);
            progressDialog.setMessage("Downloading Radio Channels from Database...");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            JsonUtils sh = new JsonUtils();

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                if (databaseHandler.RadioisExist() >= 0) {
                    databaseHandler.deleteRadiodb();
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Getting JSON Array node
                        JSONArray contacts = jsonObj.getJSONArray(JsonConstant.RADIO_ARRAY);
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            databaseHandler.AddRadio(new ItemListRadio((c.getString(JsonConstant.RADIO_ID)),
                                    c.getString(JsonConstant.RADIO_NAME), c.getString(JsonConstant.RADIO_URL),
                                    c.getString(JsonConstant.RADIO_CATEGORY),
                                    c.getString(JsonConstant.RADIO_IMAGE_URL)));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (isFirstTime) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityMain.this);
                builder1.setMessage("Thank you for installing our app " + getString(R.string.app_name)
                        + "\n\nBy Clicking privacy tab,you can read our privacy policy and agree to the " +
                        "terms of privacy policy to continue using " + getString(R.string.app_name));
                builder1.setIcon(getResources().getDrawable(R.mipmap.ic_launcher));
                builder1.setTitle("Privacy & Terms");
                builder1.setCancelable(false);

                builder1.setNegativeButton("PRIVACY POLICY", null);

                builder1.setPositiveButton(
                        "AGREE & CONTINUE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                editor.putBoolean("isFirstTime", false);
                                editor.commit();
                                dialog.dismiss();
                            }
                        });

                final AlertDialog alert11 = builder1.create();
                alert11.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alert11.getButton(AlertDialog.BUTTON_NEGATIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                String url1 = getResources().getString(R.string.Privacy_Policy);
                                Intent intent1 = new Intent(ActivityMain.this, CustomWebView.class);
                                intent1.putExtra("openURL", url1);
                                intent1.putExtra("FromActivity", 1);
                                intent1.putExtra("title", getResources().getString(R.string.privacy));
                                startActivity(intent1);
                            }
                        });
                    }
                });
                alert11.show();


            }
            if (databaseHandler.RadioisExist() > 0) {
                mFragmentManager = getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                Fragment fragment = new HomeFragment();
                mFragmentTransaction.replace(R.id.frame_container, fragment).commit();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry!! ,No data obtained from server", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class GetTaskonRefresh extends AsyncTask<String, Void, Void> {

        String url;
        ProgressDialog progressDialog;

        public GetTaskonRefresh(String serverUrl) {
            this.url = serverUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityMain.this);
            progressDialog.setMessage("Updating Radio Channels from Database...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            JsonUtils sh = new JsonUtils();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                if (databaseHandler.RadioisExist() >= 0) {
                    databaseHandler.deleteRadiodb();
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Getting JSON Array node
                        JSONArray contacts = jsonObj.getJSONArray(JsonConstant.RADIO_ARRAY);
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);

                            databaseHandler.AddRadio(new ItemListRadio((c.getString(JsonConstant.RADIO_ID)),
                                    c.getString(JsonConstant.RADIO_NAME), c.getString(JsonConstant.RADIO_URL),
                                    c.getString(JsonConstant.RADIO_CATEGORY),
                                    c.getString(JsonConstant.RADIO_IMAGE_URL)));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (databaseHandler.RadioisExist() > 0) {
                Toast.makeText(ActivityMain.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry!!,No data obtained from server", Toast.LENGTH_SHORT).show();
            }

        }
    }
}