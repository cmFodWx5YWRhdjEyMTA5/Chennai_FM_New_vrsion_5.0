package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.Config;
import com.chennaifmradiosongs.onlinemadrasradiostation.ImageLoaderDefintion;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ChannelCategoryActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ImageCatActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.International_NewsActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.NewsActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.RadioPlayActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.RecentPlayRadioActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.AdapterHomeListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.adapters.RecyclerViewClickListener;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;
import com.chennaifmradiosongs.onlinemadrasradiostation.services.RadioStreamingService;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.CustomWebView;
import com.chennaifmradiosongs.onlinemadrasradiostation.utilities.MyViewPager;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.AddBanner;
import com.chennaifmradiosongs.onlinemadrasradiostation.utils.HttpServicesClass;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    public ArrayList<ItemListRadio> titles;
    RadioDatabase databaseHandler;
    CategoryAdapter adapterCategories;
    RecyclerView recyclerview1, recyclerview2, recyclerview3, recent_recyclerview;
    LinearLayoutManager grid1, grid2, grid3, recent_grid;

    ArrayList<ItemListRadio> list_radio, list_radio1, list_radio2;
    AdapterHomeListRadio adapterListRadio, adapterListRadio1, adapterListRadio2, recentadapter;

    TextView tv_cat_title;
    int OVERLAY_PERMISSION_REQ_CODE = 1001;
    int position = 0, radio_position = 0;
    RelativeLayout linear_container;
    ImageView radio_image;
    TextView radio_name;
    ImageView prev, play, next;
    String Banner_url = "http://www.appsarasan.com/fm_banners/get_banner.php";
    MyViewPager viewPager;
    int currentPage = 0;
    Intent intent;
    int Random_number = 0;
    LinearLayout recentlinearLayout;
    ArrayList<ItemListRadio> recentRadiolist;
    private RecyclerView list_category;
    private ImageLoader imgloader = ImageLoader.getInstance();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RadioStreamingService.getInstance() != null) {
            RadioStreamingService.getInstance().PlayExoPlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
       /* MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
*/
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
    private UnifiedNativeAd nativeAd;
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
                FrameLayout frameLayout1 = rootView.findViewById(R.id.fl_adplaceholder1);
                if (isAdded()) {
                    if (Config.ENABLE_ADS) {
                        frameLayout.setVisibility(View.VISIBLE);
                        frameLayout1.setVisibility(View.VISIBLE);
                        @SuppressLint("InflateParams")
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_home_native, null, false);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        UnifiedNativeAdView adView1 = (UnifiedNativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_home_native1, null, false);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView1);
                        UnifiedNativeAdView adView2 = (UnifiedNativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_home_native2, null, false);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView2);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                        frameLayout1.removeAllViews();
                        frameLayout1.addView(adView1);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                        frameLayout1.setVisibility(View.GONE);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fm_home_screen
                , container, false);

        recentlinearLayout = rootView.findViewById(R.id.recent_layout);
        MobileAds.initialize(getActivity(), getString(R.string.admob_app_id));

        titles = new ArrayList<ItemListRadio>();
        list_radio = new ArrayList<ItemListRadio>();
        list_radio1 = new ArrayList<ItemListRadio>();
        list_radio2 = new ArrayList<ItemListRadio>();

        if (Config.ENABLE_ADS) {
            refreshAd(rootView);
        }

        list_category = rootView.findViewById(R.id.list_category);
        databaseHandler = new RadioDatabase(getActivity());

        viewPager = rootView.findViewById(R.id.pager);

        recentRadiolist = new ArrayList<>();
        recentRadiolist = databaseHandler.getAllRecentRadio();
        if (recentRadiolist.size() == 0) {
            recentlinearLayout.setVisibility(View.GONE);
        } else {
            recentlinearLayout.setVisibility(View.VISIBLE);
        }

        ImageLoaderDefintion.initImageLoader(getActivity());
        new GetHttpResponse(getActivity()).execute();


        linear_container = rootView.findViewById(R.id.container);

        radio_image = rootView.findViewById(R.id.radio_image);
        radio_name = rootView.findViewById(R.id.radio_name);
        prev = rootView.findViewById(R.id.previous);
        play = rootView.findViewById(R.id.play);
        next = rootView.findViewById(R.id.next);
        ImageLoaderDefintion.initImageLoader(getActivity());

        titles = databaseHandler.CategorytypeList();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        list_category.setLayoutManager(horizontalLayoutManager);
        setAdapter();

        recent_recyclerview = rootView.findViewById(R.id.list_recent_fm_list);
        recent_recyclerview.setHasFixedSize(true);
        recent_grid = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recent_recyclerview.setLayoutManager(recent_grid);


        recyclerview1 = rootView.findViewById(R.id.list_entertainment);
        recyclerview1.setHasFixedSize(true);
        grid1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview1.setLayoutManager(grid1);


        recyclerview2 = rootView.findViewById(R.id.list_musicdirector);
        recyclerview2.setHasFixedSize(true);
        grid2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview2.setLayoutManager(grid2);

        recyclerview3 = rootView.findViewById(R.id.list_fm_list);
        recyclerview3.setHasFixedSize(true);
        grid3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview3.setLayoutManager(grid3);


        list_radio = databaseHandler.CategorytypeListRadio(titles.get(0).getRadioCateogory());
        list_radio1 = databaseHandler.CategorytypeListRadio(titles.get(1).getRadioCateogory());
        list_radio2 = databaseHandler.CategorytypeListRadio(titles.get(2).getRadioCateogory());


        rootView.findViewById(R.id.click1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                        putExtra("viewpager_position", 0));
            }
        });
        rootView.findViewById(R.id.click2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                        putExtra("viewpager_position", 1));
            }
        });
        rootView.findViewById(R.id.click3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                        putExtra("viewpager_position", 2));
            }
        });

        rootView.findViewById(R.id.recent_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RecentPlayRadioActivity.class));
            }
        });
        if (list_radio != null) {
            ArrayList<ItemListRadio> sublist1 = new ArrayList<ItemListRadio>(list_radio.subList(0,
                    Math.min(list_radio.size(), 20)));
            adapterListRadio = new AdapterHomeListRadio(getActivity(), sublist1, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        getActivity().stopService(new Intent(getActivity(),
                                RadioStreamingService.class));
                    }
                    startActivity(new Intent(getActivity(), RadioPlayActivity.class)
                            .putExtra("Radio_id", list_radio.get(position).getRadioId())
                            .putExtra("Radio_position", position)
                            .putExtra("favorite_activity", 0));
                }
            });
            recyclerview1.setAdapter(adapterListRadio);
        }


        if (list_radio1 != null) {
            ArrayList<ItemListRadio> sublist1 = new ArrayList<ItemListRadio>(list_radio1.subList(0,
                    Math.min(list_radio1.size(), 20)));
            adapterListRadio1 = new AdapterHomeListRadio(getActivity(), sublist1, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        getActivity().stopService(new Intent(getActivity(),
                                RadioStreamingService.class));
                    }
                    startActivity(new Intent(getActivity(), RadioPlayActivity.class)
                            .putExtra("Radio_id", list_radio1.get(position).getRadioId())
                            .putExtra("Radio_position", position)
                            .putExtra("favorite_activity", 0));
                }
            });
            recyclerview2.setAdapter(adapterListRadio1);
        }


        if (list_radio2 != null) {
            ArrayList<ItemListRadio> sublist1 = new ArrayList<ItemListRadio>(list_radio2.subList(0,
                    Math.min(list_radio2.size(), 20)));
            adapterListRadio2 = new AdapterHomeListRadio(getActivity(), sublist1, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        getActivity().stopService(new Intent(getActivity(),
                                RadioStreamingService.class));
                    }
                    startActivity(new Intent(getActivity(), RadioPlayActivity.class)
                            .putExtra("Radio_id", list_radio2.get(position).getRadioId())
                            .putExtra("Radio_position", position)
                            .putExtra("favorite_activity", 0));
                }
            });
            recyclerview3.setAdapter(adapterListRadio2);
        }

        if (recentRadiolist != null) {
            ArrayList<ItemListRadio> sublist1 = new ArrayList<ItemListRadio>(recentRadiolist.subList(0,
                    Math.min(recentRadiolist.size(), 20)));
            recentadapter = new AdapterHomeListRadio(getActivity(), sublist1, new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    if (RadioStreamingService.getInstance().isPlaying()) {
                        getActivity().stopService(new Intent(getActivity(),
                                RadioStreamingService.class));
                    }
                    startActivity(new Intent(getActivity(), RadioPlayActivity.class)
                            .putExtra("Radio_id", recentRadiolist.get(position).getRadioId())
                            .putExtra("Radio_position", position)
                            .putExtra("favorite_activity", 0));
                }
            });
            recent_recyclerview.setAdapter(recentadapter);
        }

        if (RadioStreamingService.getInstance().isPlaying()) {
            play.setImageResource(R.drawable.ic_pause);
            linear_container.setVisibility(View.VISIBLE);
            linear_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), RadioPlayActivity.class)
                            .putExtra("Radio_id", RadioStreamingService.getStation().getRadioId())
                            .putExtra("Radio_position", RadioStreamingService.getInstance().station_position())
                            .putExtra("favorite_activity", 0));
                }
            });
            radio_position = RadioStreamingService.getInstance().station_position();
            radio_name.setText(RadioStreamingService.getStation().getRadioName());
            try {
                imgloader.displayImage(RadioStreamingService.getStation().getRadioImageUrl(), radio_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioStreamingService.getInstance().PrevPlay();
                    radio_name.setText(RadioStreamingService.getStation().getRadioName());
                    try {
                        imgloader.displayImage(RadioStreamingService.getStation().getRadioImageUrl(), radio_image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    radio_position = RadioStreamingService.getInstance().station_position();
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioStreamingService.getInstance().NextPlay();
                    radio_name.setText(RadioStreamingService.getStation().getRadioName());
                    try {
                        imgloader.displayImage(RadioStreamingService.getStation().getRadioImageUrl(), radio_image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    radio_position = RadioStreamingService.getInstance().station_position();
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!RadioStreamingService.getInstance().isPlaying()) {
                        RadioStreamingService.getInstance().PlayExoPlayer();
                        play.setImageResource(R.drawable.ic_pause);
                    } else {
                        play.setImageResource(R.drawable.ic_play);
                        RadioStreamingService.getInstance().StopExoPlayer();
                    }
                }
            });
        } else {
            linear_container.setVisibility(View.GONE);
        }


      /*  if (appInstalledOrNot("com.tamilfmradiostations.onlinetamilsongs")) {
            ((TextView) rootView.findViewById(R.id.install_1)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_1)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilfmradiostations.onlinetamilsongs")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilfmradiostations.onlinetamilsongs");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilfmradiostations.onlinetamilsongs"));
                    startActivity(intent);
                }
            }
        });

        if (appInstalledOrNot("com.tamilthirukkural.thirukkuralbook")) {
            ((TextView) rootView.findViewById(R.id.install_2)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_2)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilthirukkural.thirukkuralbook")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilthirukkural.thirukkuralbook");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilthirukkural.thirukkuralbook"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.riddlegames.riddlequiztamil")) {
            ((TextView) rootView.findViewById(R.id.install_3)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_3)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.riddlegames.riddlequiztamil")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.riddlegames.riddlequiztamil");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.riddlegames.riddlequiztamil"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.tamilnurserysongs.kidsrhymestamil")) {
            ((TextView) rootView.findViewById(R.id.install_4)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_4)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilnurserysongs.kidsrhymestamil")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilnurserysongs.kidsrhymestamil");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilnurserysongs.kidsrhymestamil"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.tamilgk3000.quizexams")) {
            ((TextView) rootView.findViewById(R.id.install_5)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_5)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilgk3000.quizexams")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilgk3000.quizexams");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilgk3000.quizexams"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.nalamnamkaiyil.healthnewstamil")) {
            ((TextView) rootView.findViewById(R.id.install_6)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_6)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.nalamnamkaiyil.healthnewstamil")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.nalamnamkaiyil.healthnewstamil");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.nalamnamkaiyil.healthnewstamil"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.tamilulaa")) {
            ((TextView) rootView.findViewById(R.id.install_7)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_7)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilulaa")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilulaa");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilulaa"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.tamilarichuvadi.arasanalphabets")) {
            ((TextView) rootView.findViewById(R.id.install_8)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_8)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.tamilarichuvadi.arasanalphabets")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.tamilarichuvadi.arasanalphabets");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.tamilarichuvadi.arasanalphabets"));
                    startActivity(intent);
                }
            }
        });
        if (appInstalledOrNot("com.memorygame.brainpicturematch")) {
            ((TextView) rootView.findViewById(R.id.install_9)).setText("App Installed");
        } else {
            ((TextView) rootView.findViewById(R.id.install_9)).setText("Install Now");
        }
        rootView.findViewById(R.id.app_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInstalledOrNot("com.memorygame.brainpicturematch")) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage("com.memorygame.brainpicturematch");
                    startActivity(LaunchIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.memorygame.brainpicturematch"));
                    startActivity(intent);
                }
            }
        });*/


        rootView.findViewById(R.id.tamil_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewsActivity.class));
            }
        });

        rootView.findViewById(R.id.world_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), International_NewsActivity.class));
            }
        });

        rootView.findViewById(R.id.tamil_love_quotes_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "7"));
            }
        });
        rootView.findViewById(R.id.thirukkural_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "1"));

            }
        });

        rootView.findViewById(R.id.motivational).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "6"));

            }
        });

        rootView.findViewById(R.id.devotional).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "8"));

            }
        });

        rootView.findViewById(R.id.image_layout_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "10"));
            }
        });
        rootView.findViewById(R.id.image_layout_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "19"));
            }
        });
        rootView.findViewById(R.id.image_layout_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "2"));
            }
        });
        rootView.findViewById(R.id.image_layout_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "18"));
            }
        });
        rootView.findViewById(R.id.image_layout_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "17"));
            }
        });
        rootView.findViewById(R.id.image_layout_10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImageCatActivity.class)
                        .putExtra("image_cat", "9"));
            }
        });


        return rootView;

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setAdapter() {
        adapterCategories = new CategoryAdapter(getActivity(), titles, new RecyclerViewClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                        putExtra("viewpager_position", position));
            }
        });
        list_category.setAdapter(adapterCategories);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public Context context;

        String JSonResult;
        ArrayList<AddBanner> studentList;

        public GetHttpResponse(Context context) {
            this.context = context;
            studentList = new ArrayList<AddBanner>();
            studentList.add(0, new AddBanner("0",
                    "http://www.theindianapps.com/FM_Apps_with_Image/AppsArasan/Image_Banners/ch.png",
                    "0"));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewPager.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Passing HTTP URL to HttpServicesClass Class.
            HttpServicesClass httpServicesClass = new HttpServicesClass(Banner_url);
            try {
                httpServicesClass.ExecutePostRequest();

                if (httpServicesClass.getResponseCode() == 200) {
                    JSonResult = httpServicesClass.getResponse();

                    if (JSonResult != null) {
                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray(JSonResult);
                            JSONObject jsonObject;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                studentList.add(i + 1, new AddBanner(jsonObject.getString("sno"),
                                        jsonObject.getString("Image_Banner"),
                                        jsonObject.getString("Link")));
                            }
                        } catch (JSONException e) {

                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            viewPager.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new CustomPagerAdapter(getActivity(), studentList));
            viewPager.setOffscreenPageLimit(studentList.size());
            viewPager.setAnimationEnabled(true);
            viewPager.setPageMargin(20);
            viewPager.setClipToPadding(false);

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    currentPage = i;

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage == studentList.size()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(update);
                }
            }, 5000, 5000);
        }
    }


    public class CustomPagerAdapter extends PagerAdapter {


        ArrayList<AddBanner> addBanners;
        private Context mContext;

        public CustomPagerAdapter(Context context, ArrayList<AddBanner> studentList) {
            this.mContext = context;
            this.addBanners = studentList;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.banner_slidingimages_layout, collection, false);
            collection.addView(layout);
            ImageView touchImageView = layout.findViewById(R.id.image);
            ((TextView) layout.findViewById(R.id.total_size)).setText("" + (position + 1) + "/" + addBanners.size());
            try {
                ImageLoader imgloader = ImageLoader.getInstance();
                imgloader.displayImage(addBanners.get(position).getImage_Banner(), touchImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addBanners.get(position).getLink() != null &&
                            (addBanners.get(position).getLink().startsWith("http") ||
                                    addBanners.get(position).getLink().startsWith("https"))) {
                        Intent gov_intent = new Intent(getActivity(), CustomWebView.class);
                        gov_intent.putExtra("title", getResources().getString(R.string.app_name));
                        gov_intent.putExtra("openURL", addBanners.get(position).getLink());
                        gov_intent.putExtra("FromActivity", 1);
                        startActivity(gov_intent);
                    } else {
                        startActivity(new Intent(getActivity(), ChannelCategoryActivity.class).
                                putExtra("viewpager_position", position));
                    }
                }
            });

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return addBanners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    @SuppressLint("RecyclerView")
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
        private final Context currentContext;
        private final ArrayList<ItemListRadio> data;
        private final int[] user_images = new int[]{R.drawable.border};
        ArrayList<Integer> bg_array1;
        private RecyclerViewClickListener recyclerViewClickListener;

        public CategoryAdapter(Context context, ArrayList<ItemListRadio> info, RecyclerViewClickListener recyclerViewClickListener) {
            this.currentContext = context;
            this.data = info;
            this.recyclerViewClickListener = recyclerViewClickListener;
        }

        @NonNull
        @Override
        public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fm_category, parent, false);
            CategoryAdapter.MyViewHolder myViewHolder = new CategoryAdapter.MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, int position) {
            bg_array1 = new ArrayList<Integer>();

            for (int i = 0; i < data.size(); i++) {
                for (int user_image : user_images) {
                    bg_array1.add(user_image);
                }
            }
            bg_array1.subList(data.size(), bg_array1.size()).clear();

            holder.txt_name.setText(data.get(position).getRadioCateogory());
            holder.txt_name.setBackgroundResource(bg_array1.get(position));
            holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
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