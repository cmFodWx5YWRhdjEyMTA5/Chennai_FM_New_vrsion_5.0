package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.ImageLoaderDefintion;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ActivityMain;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ChannelCategoryActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.EndSplashActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.EqualizerActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.FavoriteActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.InAppPurchaseActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.RadioPlayActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.services.RadioStreamingService;
import com.chennaifmradiosongs.onlinemadrasradiostation.utilities.OnSwipeTouchListerner;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.chennaifmradiosongs.onlinemadrasradiostation.activities.ActivityMain.isNetworkAvailable;

public class FragmentListRadio extends Fragment {

    private static final String SHOWCASE_ID = "sequence example";
    public final int CATEGORY_ID = 0;
    public ImageView play_img, favorite_img, previous_img, next_img, volume_control, Record_control;
    public java.util.Timer _timer;
    LinearLayout share_layout;
    LinearLayout shutdown;
    Intent intent;
    ItemListRadio itemListRadio;
    RadioDatabase databaseHandler;
    TextView StrName, StrCat;
    TextView StrId;
    int Radio_id;
    int[] day_radio_img; //items in the alertdialog that displays radiobuttons
    ArrayList<ItemListRadio> arrayItemListRadio;
    Chronometer recording_text;
    boolean isRecording = false;
    ImageView themes;
    ImageView bg_images;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ImageView Timer;
    int counter = 1;
    RelativeLayout channel_list;
    /* VisualizerView mVisualizerView;
     Visualizer mVisualizer;*/
    Bundle bundle;
    AudioManager audioManager;
    InterstitialAd interstitial;
    Dialog dialog;
    int RECORD_AUDIO_REQUEST_CODE = 1;
    AnimationDrawable animationDrawable, animationDrawable1;
    AlertDialog.Builder builder;
    private InterstitialAd mInterstitialAd;
    private MediaRecorder mRecorder;
    private String fileName = null;
    private ImageView _imagView;
    private ImageLoader imgloader = ImageLoader.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.intent = new Intent(getActivity(), RadioStreamingService.class);
        day_radio_img = new int[]{R.drawable.theme_1, R.drawable.theme_2, R.drawable.theme_3,
                R.drawable.theme_4, R.drawable.theme_5, R.drawable.theme_6, R.drawable.theme_7,
                R.drawable.theme_8, R.drawable.theme_9, R.drawable.theme_6
        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void showvideo() {
        // Show the ad if it's ready
        if (interstitial != null && interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    public void doWork(final View view) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    TextView txtCurrentTime = view.findViewById(R.id.dateformat);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yy  hh:mm:ss a");
                    String formattedDate = df.format(c.getTime());
                    if (txtCurrentTime != null && formattedDate != null)
                        txtCurrentTime.setText(formattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private UnifiedNativeAd nativeAd;

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.mediaview);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param rootView
     */
    private void refreshAd(final View rootView) {
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getResources().getString(R.string.native_ads));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = rootView.findViewById(R.id.fl_adplaceholder);
                if (isAdded()) {
                    if (Config.ENABLE_ADS) {
                        frameLayout.setVisibility(View.VISIBLE);
                        @SuppressLint("InflateParams")
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_home_native_media, null, false);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                }
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .setClickToExpandRequested(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_radio_play_screen, container, false);
        //    mVisualizerView = (VisualizerView) rootView.findViewById(R.id.myvisualizerview);
        databaseHandler = new RadioDatabase(getActivity());
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.setCurrentScreen(getActivity(), "Radio Streaming Fragment", null /* class override */);

        ImageLoaderDefintion.initImageLoader(getActivity());
        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ActivityMain.class));
            }
        });


        Thread myThread = null;
        Runnable runnable = new CountDownRunner(rootView);
        myThread = new Thread(runnable);
        myThread.start();

        _imagView = rootView.findViewById(R.id.imageView1);

        interstitial = new InterstitialAd(getActivity());
        if (Config.ENABLE_ADS) {
            refreshAd(rootView);

            interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(getActivity(), EndSplashActivity.class));
                    getActivity().finish();
                    super.onAdClosed();
                }

                @Override
                public void onAdClicked() {
                    startActivity(new Intent(getActivity(), EndSplashActivity.class));
                    getActivity().finish();
                    super.onAdClicked();
                }
            });
        }


        /*rootView.findViewById(R.id.advertise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.gcm_dialog2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                TextView date = dialog.findViewById(R.id.date);
                AppCompatButton btn_0 = dialog.findViewById(R.id.btn);
                AppCompatButton btn_1 = dialog.findViewById(R.id.btn1);

                btn_0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String phone = "+918012627000";
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });
                btn_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Advertisements Request");
                        i.putExtra(Intent.EXTRA_TEXT, "body of email");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                String formattedDate = df.format(c);
                date.setText(formattedDate);
                dialog.show();
            }
        });*/
       /* rootView.findViewById(R.id.help_us).setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.gcm_dialog1);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                TextView date = dialog.findViewById(R.id.date);
                RatingBar btn0 = dialog.findViewById(R.id.ratingbar);
                AppCompatButton btn_0 = dialog.findViewById(R.id.btn0);
                AppCompatButton btn1 = dialog.findViewById(R.id.btn1);
                AppCompatButton btn2 = dialog.findViewById(R.id.btn2);


                TextView msg = dialog.findViewById(R.id.msg_3);
                msg.setText(getResources().getString(R.string.content2) + getEmojiByUnicode(0x1F64F));
                btn_0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getResources().getString(R.string.share_content) +
                                "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                        try {
                            getActivity().startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                        }
                    }
                });
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                String formattedDate = df.format(c);
                date.setText(formattedDate);
                btn0.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            // TODO perform your action here
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                            }
                            dialog.dismiss();
                        }
                        return true;
                    }
                });
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
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
                    }
                });

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });*/

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Radio_id = preferences.getInt("radio_id_value", 0);
        editor = preferences.edit();


        mInterstitialAd = new InterstitialAd(getActivity());
        if (Config.ENABLE_ADS) {
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        }

        /*if (Config.ENABLE_ADS) {
            rootView.findViewById(R.id.ad_click).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.ad_click).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), InAppPurchaseActivity.class));
                }
            });
        } else {
            rootView.findViewById(R.id.ad_click).setVisibility(View.GONE);
        }*/

        StrId = rootView.findViewById(R.id.radio_category);
        StrName = rootView.findViewById(R.id.radio_name);
        favorite_img = rootView.findViewById(R.id.favorite);
        share_layout = rootView.findViewById(R.id.share_layout);
        previous_img = rootView.findViewById(R.id.previous);
        next_img = rootView.findViewById(R.id.next);
        play_img = rootView.findViewById(R.id.play);
        bg_images = rootView.findViewById(R.id.bgimages);

        for (int i = 0; i < day_radio_img.length; i++) {
            if (preferences.getInt("radio_bg_images", 0) == i) {
                bg_images.setImageResource(day_radio_img[i]);
            }
        }

      /*  themes = rootView.findViewById(R.id.themes);
        themes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(CATEGORY_ID);
            }
        });*/
        Timer = rootView.findViewById(R.id.timer);
        Timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RadioPlayActivity) getActivity()).showSleepDialog();
            }
        });


        channel_list = rootView.findViewById(R.id.channel_list);
        channel_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                        putExtra("viewpager_position", 0));
            }
        });

        rootView.findViewById(R.id.favorites_radio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FavoriteActivity.class));
            }
        });

        itemListRadio = new ItemListRadio();

        arrayItemListRadio = new ArrayList<ItemListRadio>();

        arrayItemListRadio = databaseHandler.getAllRadio();

        bundle = this.getArguments();
        if (bundle != null) {
            if (databaseHandler.RadioisExist() > 0) {

                String _id = bundle.getString("Radio_id");
                if (bundle.getInt("favorite", 0) == 1) {
                    Toast.makeText(getActivity(), "playing from favorite list", Toast.LENGTH_SHORT).show();
                } else {
                    if (bundle.getInt("favorite", 0) == 0) {
                        Radio_id = bundle.getInt("number", 0);
                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    }
                }
                if (arrayItemListRadio != null)
                    for (int i = 0; i < arrayItemListRadio.size(); i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Objects.equals(arrayItemListRadio.get(i).getRadioId(), _id)) {

                                PlayRadio(arrayItemListRadio.get(i));
                                Radio_id = i;
                                editor.putInt("radio_id_value", Radio_id);
                                editor.commit();
                            }
                        }
                    }
            }
        } else {
            Toast.makeText(getActivity(), "Please Select the Radio From Channel list", Toast.LENGTH_SHORT).show();
        }

        if (RadioStreamingService.getInstance() != null)
            if (RadioStreamingService.getInstance().isPlaying()) {
                StrId.setText(RadioStreamingService.getInstance().getPlayingRadioStation().getRadioCateogory());
                StrName.setText(RadioStreamingService.getInstance().getPlayingRadioStation().getRadioName()
                        + " " + RadioStreamingService.getInstance().getPlayingRadioStation().getRadioId());
                if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, RadioStreamingService.getInstance().getPlayingRadioStation().getRadioId() + "")) {
                    favorite_img.setImageResource(R.drawable.ic_like_fill);
                } else {
                    favorite_img.setImageResource(R.drawable.ic_like_outline);
                }
            }


        play_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayItemListRadio != null) {
                    Radio_id = preferences.getInt("radio_id_value", 0);
                    if (RadioStreamingService.getInstance() != null) {
                        if (RadioStreamingService.getInstance().isPlaying()) {
                            ActivityMain.isActive = false;
                            RadioStreamingService.getInstance().StopExoPlayer();
                            play_img.setImageResource(R.drawable.ic_play);
                        } else {
                            ActivityMain.isActive = true;
                            PlayRadio(arrayItemListRadio.get(Radio_id));
                            play_img.setImageResource(R.drawable.ic_pause);
                        }
                    }
                }
            }
        });


        favorite_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RadioStreamingService.getInstance() != null)
                    if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, RadioStreamingService.getInstance().getPlayingRadioStation().getRadioId() + "")) {
                        databaseHandler.deleteFavorites(RadioStreamingService.getInstance().getPlayingRadioStation().getRadioId());
                        favorite_img.setImageResource(R.drawable.ic_like_outline);
                        Toast.makeText(getActivity(), RadioStreamingService.getInstance().getPlayingRadioStation().getRadioName() + " " + getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                    } else {
                        databaseHandler.AddOneFavorite(RadioStreamingService.getInstance().getPlayingRadioStation());
                        favorite_img.setImageResource(R.drawable.ic_like_fill);
                        Toast.makeText(getActivity(), RadioStreamingService.getInstance().getPlayingRadioStation().getRadioName() + " " + getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();

                    }
            }
        });
        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_title)
                        + "\n" + getResources().getString(R.string.share_content)
                        + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });
        previous_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter == 6) {
                    if (Config.ENABLE_ADS && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    counter = 1;
                } else {
                    if (Config.ENABLE_ADS && !mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mInterstitialAd.loadAd(adRequest);
                    }
                    counter++;
                }

                if (arrayItemListRadio != null) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        RadioStreamingService.getInstance().StopExoPlayer();
                        RadioStreamingService.getInstance().PrevPlay();
                        if (databaseHandler.isExist(RadioDatabase.TABLE_FAV,
                                RadioStreamingService.getInstance().getStation().getRadioId() + "")) {
                            favorite_img.setImageResource(R.drawable.ic_like_fill);
                        } else {
                            favorite_img.setImageResource(R.drawable.ic_like_outline);
                        }
                        itemListRadio = RadioStreamingService.getInstance().getStation();

                        StrId.setText(itemListRadio.getRadioCateogory());
                        StrName.setText(itemListRadio.getRadioName() + "  " + itemListRadio.getRadioId());
                        try {
                            imgloader.displayImage(itemListRadio.getRadioImageUrl(), _imagView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    /* if (Radio_id > 0) {
                     *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(Radio_id - 1));
                        Radio_id = Radio_id - 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    } else {
                       *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(arrayItemListRadio.size() - 1));
                        Radio_id = arrayItemListRadio.size() - 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    }*/
                }

            }
        });
        next_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counter == 6) {
                    if (Config.ENABLE_ADS && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    counter = 1;
                } else {
                    if (Config.ENABLE_ADS && !mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mInterstitialAd.loadAd(adRequest);
                    }
                    counter++;
                }

                if (arrayItemListRadio != null) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        RadioStreamingService.getInstance().StopExoPlayer();
                        RadioStreamingService.getInstance().NextPlay();
                        if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, RadioStreamingService.getInstance().getStation().getRadioId() + "")) {
                            favorite_img.setImageResource(R.drawable.ic_like_fill);
                        } else {
                            favorite_img.setImageResource(R.drawable.ic_like_outline);
                        }
                        itemListRadio = RadioStreamingService.getInstance().getStation();

                        StrId.setText(itemListRadio.getRadioCateogory());
                        StrName.setText(itemListRadio.getRadioName() + "  " + itemListRadio.getRadioId());
                        try {
                            imgloader.displayImage(itemListRadio.getRadioImageUrl(), _imagView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    /* if (Radio_id < (arrayItemListRadio.size() - 1)) {
                     *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(Radio_id + 1));
                        Radio_id = Radio_id + 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    } else {
                        *//*if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(0));
                        Radio_id = 0;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    }*/
                }
            }
        });

        rootView.findViewById(R.id.background).setOnTouchListener(new OnSwipeTouchListerner(getActivity()) {
            public void onSwipeRight() {
                if (counter == 6) {
                    if (Config.ENABLE_ADS && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    counter = 1;
                } else {
                    if (Config.ENABLE_ADS && !mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mInterstitialAd.loadAd(adRequest);
                    }
                    counter++;
                }

                if (arrayItemListRadio != null) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        RadioStreamingService.getInstance().StopExoPlayer();
                        RadioStreamingService.getInstance().PrevPlay();
                        if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, RadioStreamingService.getInstance().getStation().getRadioId() + "")) {
                            favorite_img.setImageResource(R.drawable.ic_like_fill);
                        } else {
                            favorite_img.setImageResource(R.drawable.ic_like_outline);
                        }
                        itemListRadio = RadioStreamingService.getInstance().getStation();

                        StrId.setText(itemListRadio.getRadioCateogory());
                        StrName.setText(itemListRadio.getRadioName() + "  " + itemListRadio.getRadioId());
                        try {
                            imgloader.displayImage(itemListRadio.getRadioImageUrl(), _imagView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    /*  if (Radio_id > 0) {
                     *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(Radio_id - 1));
                        Radio_id = Radio_id - 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    } else {
                       *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(arrayItemListRadio.size() - 1));
                        Radio_id = arrayItemListRadio.size() - 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    }*/
                }
            }

            public void onSwipeLeft() {
                if (counter == 6) {
                    if (Config.ENABLE_ADS && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    counter = 1;
                } else {
                    if (Config.ENABLE_ADS && !mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mInterstitialAd.loadAd(adRequest);
                    }
                    counter++;
                }

                if (arrayItemListRadio != null) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        RadioStreamingService.getInstance().StopExoPlayer();
                        RadioStreamingService.getInstance().NextPlay();
                        if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, RadioStreamingService.getInstance().getStation().getRadioId() + "")) {
                            favorite_img.setImageResource(R.drawable.ic_like_fill);
                        } else {
                            favorite_img.setImageResource(R.drawable.ic_like_outline);
                        }
                        itemListRadio = RadioStreamingService.getInstance().getStation();

                        StrId.setText(itemListRadio.getRadioCateogory());
                        StrName.setText(itemListRadio.getRadioName() + "  " + itemListRadio.getRadioId());
                        try {
                            imgloader.displayImage(itemListRadio.getRadioImageUrl(), _imagView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    /* if (Radio_id < (arrayItemListRadio.size() - 1)) {
                     *//* if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(Radio_id + 1));
                        Radio_id = Radio_id + 1;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    } else {
                        *//*if (mVisualizer != null) {
                            mVisualizer.setEnabled(false);
                        }*//*
                        PlayRadio(arrayItemListRadio.get(0));
                        Radio_id = 0;

                        editor.putInt("radio_id_value", Radio_id);
                        editor.commit();
                    }*/
                }
            }
        });

        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);

        rootView.findViewById(R.id.music_equalizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), EqualizerActivity.class));
            }
        });
        Record_control = rootView.findViewById(R.id.recording);
        recording_text = rootView.findViewById(R.id.recording_text);

        Record_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getPermissionToRecordAudio();
                    }
                } else {
                    if (!isRecording) {
                        recording_text.setFormat("Recording... - %s");
                        recording_text.setVisibility(View.VISIBLE);
                        startRecording();
                        isRecording = true;
                    } else {
                        stopRecording();
                        isRecording = false;
                        recording_text.setVisibility(View.GONE);
                    }
                }
            }
        });

        volume_control = rootView.findViewById(R.id.volume);
        volume_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();

            }
        });


        shutdown = rootView.findViewById(R.id.shutdown);
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain.isActive = false;
                if (isRecording) {
                    stopRecording();
                    isRecording = false;
                }
                getActivity().stopService(new Intent(getActivity(), RadioStreamingService.class));
                RadioStreamingService.getInstance().notificationCancel();
                if (Config.ENABLE_ADS && isNetworkAvailable(getActivity()) && interstitial.isLoaded()) {
                    showvideo();
                } else {
                    startActivity(new Intent(getActivity(), EndSplashActivity.class));
                    getActivity().finish();
                }
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        if (preferences.getBoolean("my_first_time", true)) {
            Toast.makeText(getActivity(), "If Radio Streaming is not Working means, Please Click the Refresh Button and New URL load to FM Streaming ", Toast.LENGTH_SHORT).show();
            preferences.edit().putBoolean("my_first_time", false).commit();
        }

        return rootView;
    }

    /*public void showDialog(int id) {
        switch (id) {
            case CATEGORY_ID:
                AlertDialog.Builder builder;
                Context mContext = getActivity();
                LayoutInflater inflater = null;
                if (mContext != null) {
                    inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                }
                View layout = null;
                if (inflater != null) {
                    layout = inflater.inflate(R.layout.choice_theme, null);
                }
                GridView gridview = null;
                if (layout != null) {
                    gridview = layout.findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(getActivity()));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int which, long arg3) {
                            // TODO Auto-generated method stub
                            bg_images.setImageResource(day_radio_img[which]);
                            editor.putInt("radio_bg_images", which);
                            editor.commit();
                            dialog.dismiss();
                        }
                    });
                }
                ImageView close = null;
                if (layout != null) {
                    close = layout.findViewById(R.id.close);
                }
                close.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
                dialog = builder.create();
                dialog.show();
                break;
            default:
                dialog = null;
        }
    }*/

    private void startRecording() {
        //we use the MediaRecorder class to record
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/MyRecording" + "/" +
                getActivity().getResources().getString(R.string.app_name) + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = root.getAbsolutePath() + "/MyRecording" + "/" +
                getActivity().getResources().getString(R.string.app_name) + "/" +
                String.valueOf(System.currentTimeMillis() + ".mp3");
        Log.d("filename", fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            if (mRecorder != null) {
                mRecorder.prepare();
                mRecorder.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //starting the chronometer
        Record_control.setImageResource(R.drawable.ic_stop);
        recording_text.setBase(SystemClock.elapsedRealtime());
        recording_text.start();
    }

    private void stopRecording() {

        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        Record_control.setImageResource(R.drawable.ic_record);
        recording_text.stop();
        recording_text.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        Toast.makeText(getActivity(), "Recording saved successfully and Saved in \n\n" +
                android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRecording" + "/" +
                getActivity().getResources().getString(R.string.app_name) + "/", Toast.LENGTH_SHORT).show();
    }

    public void ShowDialog() {
        AlertDialog.Builder popDialog = new AlertDialog.Builder(getActivity());
        SeekBar seek = new SeekBar(getActivity());
        seek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        popDialog.setMessage("Volume Level 1-100 \n\n");
        popDialog.setView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });

        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        popDialog.create();
        popDialog.show();

    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    private void PlayRadio(ItemListRadio listRadio) {
        if (databaseHandler.isExist(RadioDatabase.TABLE_FAV, listRadio.getRadioId() + "")) {
            favorite_img.setImageResource(R.drawable.ic_like_fill);
        } else {
            favorite_img.setImageResource(R.drawable.ic_like_outline);
        }
        itemListRadio = listRadio;

        StrId.setText(itemListRadio.getRadioCateogory());
        StrName.setText(itemListRadio.getRadioName() + "  " + itemListRadio.getRadioId());
        try {
            imgloader.displayImage(itemListRadio.getRadioImageUrl(), _imagView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayItemListRadio != null)
            if (RadioStreamingService.getInstance().isPlaying()) {
                getActivity().stopService(new Intent(getActivity(), RadioStreamingService.class));
                play_img.setImageResource(R.drawable.ic_play);
                ItemListRadio radio = itemListRadio;
                RadioStreamingService.getInstance().initialize(getActivity(), radio, arrayItemListRadio, 1);
                getActivity().startService(new Intent(getActivity(), RadioStreamingService.class));
                play_img.setImageResource(R.drawable.ic_pause);
            } else {
                ItemListRadio radio = itemListRadio;
                RadioStreamingService.getInstance().initialize(getActivity(), radio, arrayItemListRadio, 1);
                getActivity().startService(new Intent(getActivity(), RadioStreamingService.class));
                play_img.setImageResource(R.drawable.ic_pause);
            }
        int audioSessionId = RadioStreamingService.getInstance().getAudioSessionID();

    }

    public void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
            isRecording = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                if (!isRecording) {
                    recording_text.setFormat("Recording... - %s");
                    recording_text.setVisibility(View.VISIBLE);
                    startRecording();
                    isRecording = true;
                } else {
                    stopRecording();
                    isRecording = false;
                    recording_text.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), "You must give permissions to use this app", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "You must give permissions to use this app.", Toast.LENGTH_SHORT).show();
        }
    }

    class CountDownRunner implements Runnable {
        View view;

        public CountDownRunner(View rootView) {
            this.view = rootView;
        }

        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork(view);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ImageAdapter extends BaseAdapter {
        int[] day_radio_img1 = new int[]{R.drawable.th_1, R.drawable.th_2, R.drawable.th_3,
                R.drawable.th_4, R.drawable.th_5, R.drawable.th_6, R.drawable.th_7,
                R.drawable.th_8, R.drawable.th_9, R.drawable.th_10};
        String[] day_radio_text = new String[]{"Theme 1", "Theme 2", "Theme 3", "Theme 4", "Theme 5",
                "Theme 6", "Theme 7", "Theme 8", "Theme 9", "Theme 10"};
        private Context mContext;
        private LayoutInflater mInflater;

        public ImageAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
            mContext = c;
        }

        public int getCount() {
            return day_radio_img1.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {  // if it's not recycled,
                convertView = mInflater.inflate(R.layout.choice_theme_item, null);
                convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.image);
                holder.text = convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (day_radio_text != null && day_radio_img1 != null) {
                holder.icon.setBackgroundResource(day_radio_img1[position]);
                holder.icon.setAdjustViewBounds(true);
                holder.text.setText(day_radio_text[position]);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView text;
        }
    }
}

