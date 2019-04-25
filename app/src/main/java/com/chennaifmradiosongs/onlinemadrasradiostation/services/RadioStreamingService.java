package com.chennaifmradiosongs.onlinemadrasradiostation.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.Picasso;
import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ActivityMain;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.RadioDatabase;
import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class RadioStreamingService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "radio_channel";
    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_STOP = "com.chennaifmradiosongs.onlinemadrasradiostation.ACTION_STOP";
    public static final String ACTION_NEXT = "com.chennaifmradiosongs.onlinemadrasradiostation.ACTION_NEXT";
    public static final String ACTION_PREV = "com.chennaifmradiosongs.onlinemadrasradiostation.ACTION_PREV";
    public static final String ACTION_CLOSE = "com.chennaifmradiosongs.onlinemadrasradiostation.ACTION_CLOSE";
    private static final int RENDERER_COUNT = 1; //since we want to render simple audio
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    public static int Audio_RadioStreamingService;
    public static ProgressTask task;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static NotificationCompat.Builder notification;
    public static RadioStreamingService service;
    public static ArrayList<ItemListRadio> itemListRadios;
    public static ExoPlayer exoPlayer;
    public static int Radio_id;
    public static ItemListRadio station;
    public static boolean mAudioIsPlaying = false;
    // Jellybean
    public static NotificationManager nManager;
    public static Context context;
    // for http mp3 audio stream, use these values
    RadioDatabase radioDatabase;
    int audioSessionId;
    PowerManager pm;
    PowerManager.WakeLock wakelock;
    Bitmap imageBitmap = null;
    Intent intent;
    boolean currentVersionSupportBigNotification = currentVersionSupportBigNotification();
    private TelephonyManager telephonyManager;
    private boolean onGoingCall = false;
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (!isPlaying()) return;
                onGoingCall = true;
                StopExoPlayer();

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return;
                onGoingCall = false;
                PlayExoPlayer();
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (!isPlaying()) return;
                onGoingCall = true;
                StopExoPlayer();
            }
        }

    };
    private WifiManager.WifiLock wifiLock;

    public static ItemListRadio getStation() {
        return station;
    }

    public static RadioStreamingService getInstance() {
        if (service == null) {
            service = new RadioStreamingService();
        }
        return service;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public int station_position() {
        Radio_id = preferences.getInt("radio_id_value", 0);
        return Radio_id;
    }

    public void initialize(Context context1, ItemListRadio station, ArrayList<ItemListRadio> arrayItemListRadio, int inwhich) {
        RadioStreamingService.context = context1;
        RadioStreamingService.station = station;
        RadioStreamingService.itemListRadios = arrayItemListRadio;
        Log.e("inwhich", "" + inwhich);
    }

    public void initialize(Context context1, ItemListRadio station, int inwhich) {
        RadioStreamingService.context = context1;
        RadioStreamingService.station = station;
        Log.e("inwhich", "" + inwhich);
    }

    public void NextPlay() {
        if (itemListRadios != null) {
            if (Radio_id < (itemListRadios.size() - 1)) {
                // PlayRadio(itemListRadios.get(Radio_id + 1));
                initialize(context, itemListRadios.get(Radio_id + 1), 1);
                context.startService(new Intent(context, RadioStreamingService.class));
                Radio_id = Radio_id + 1;
                editor.putInt("radio_id_value", Radio_id);
                editor.commit();
            } else {
                //  PlayRadio(itemListRadios.get(0));
                initialize(context, itemListRadios.get(0), 1);
                context.startService(new Intent(context, RadioStreamingService.class));
                Radio_id = 0;
                editor.putInt("radio_id_value", Radio_id);
                editor.commit();
            }
        }
    }

    public void PrevPlay() {
        if (itemListRadios != null) {
            if (Radio_id > 0) {
                initialize(context, itemListRadios.get(Radio_id - 1), 1);
                context.startService(new Intent(context, RadioStreamingService.class));
                Radio_id = Radio_id - 1;

                editor.putInt("radio_id_value", Radio_id);
                editor.commit();
            } else {

                initialize(context, itemListRadios.get(itemListRadios.size() - 1), 1);
                context.startService(new Intent(context, RadioStreamingService.class));
                Radio_id = itemListRadios.size() - 1;

                editor.putInt("radio_id_value", Radio_id);
                editor.commit();
            }
        }
    }

    public void StopExoPlayer() {
        if (exoPlayer != null && mAudioIsPlaying) {
            exoPlayer.setPlayWhenReady(false);
            mAudioIsPlaying = false;
        }
    }

    public void PlayExoPlayer() {
        if (exoPlayer != null && !mAudioIsPlaying) {
            exoPlayer.setPlayWhenReady(true);
            mAudioIsPlaying = true;
        }
    }

    public void createNotification() {
        if (context != null) {
            nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /* Create or update. */
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.enableVibration(false);
                nManager.createNotificationChannel(channel);
            }

            PendingIntent pi = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                    new Intent(context, ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);

            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.notificationview);
            RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.notificationview);

            setListeners(simpleContentView);
            setListeners(expandedView);

            notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            notification.setSmallIcon(R.drawable.music)
                    .setLargeIcon(imageBitmap)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false)
                    .setContentTitle(station.getRadioName())
                    .setContentText(station.getRadioCateogory())
                    .setContentIntent(pi).build();

            notification.setContent(simpleContentView);
            if (currentVersionSupportBigNotification) {
                notification.setContent(expandedView);
            }
            try {
                simpleContentView.setImageViewBitmap(R.id.notifimage, imageBitmap);
                if (currentVersionSupportBigNotification) {
                    expandedView.setImageViewBitmap(R.id.notifimage, imageBitmap);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exoPlayer != null && !mAudioIsPlaying) {
                simpleContentView.setImageViewResource(R.id.btn2, R.drawable.notifi_play);

                if (currentVersionSupportBigNotification) {
                    expandedView.setImageViewResource(R.id.btn2, R.drawable.notifi_play);
                }
            } else {
                simpleContentView.setImageViewResource(R.id.btn2, R.drawable.notifi_stop);
                if (currentVersionSupportBigNotification) {
                    expandedView.setImageViewResource(R.id.btn2, R.drawable.notifi_stop);
                }
            }

            simpleContentView.setTextViewText(R.id.notiftitle, station.getRadioName());
            simpleContentView.setTextViewText(R.id.notiftitle1, station.getRadioCateogory());
            simpleContentView.setTextViewText(R.id.time, getCurrentTime());

            if (currentVersionSupportBigNotification) {
                expandedView.setTextViewText(R.id.notiftitle, station.getRadioName());
                expandedView.setTextViewText(R.id.notiftitle1, station.getRadioCateogory());
                expandedView.setTextViewText(R.id.time, getCurrentTime());
            }

            if (nManager != null)
                startForeground(NOTIFICATION_ID, notification.build());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public void setListeners(RemoteViews view) {
        //listener 2
        intent = new Intent(ACTION_STOP);
        intent.setClass(context, NotificationReturnSlot.class);
        PendingIntent btn2 = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn2, btn2);

        intent = new Intent(ACTION_PREV);
        intent.setClass(context, NotificationReturnSlot.class);
        PendingIntent btn1 = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.prev, btn1);

        intent = new Intent(ACTION_NEXT);
        intent.setClass(context, NotificationReturnSlot.class);
        PendingIntent btn = PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.next, btn);

        intent = new Intent(ACTION_CLOSE);
        intent.setClass(context, NotificationReturnSlot.class);
        PendingIntent btn3 = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.close_app, btn3);
    }

    public void notificationCancel() {
        if (nManager != null) {
            nManager.cancel(NOTIFICATION_ID);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("onTaskRemoved called");
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"CommitPrefEdits", "WakelockTimeout"})
    @Override
    public void onCreate() {
        exoPlayer = ExoPlayer.Factory.newInstance(RENDERER_COUNT);

        radioDatabase = new RadioDatabase(context);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Radio_id = preferences.getInt("radio_id_value", 0);
        editor = preferences.edit();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        wakelock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = new ProgressTask(context);
        task.execute();
        if (radioDatabase.RecentRadioisExist(station)) {
            radioDatabase.deleteRecentRadiodb(station);
            radioDatabase.AddRecentRadio(station);
        } else {
            radioDatabase.AddRecentRadio(station);
        }
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        wakelock.release();
        if (telephonyManager != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        if ((mAudioIsPlaying) || (exoPlayer != null && exoPlayer.getPlayWhenReady())) {
            onStop();
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop();
                exoPlayer.release();
                if (wifiLock != null)
                    wifiLock.release();
                exoPlayer = null;
                stopForeground(true);
            }
            mAudioIsPlaying = false;
        }
    }

    public void onStop() {
        exoPlayer.stop();
        exoPlayer.seekTo(0);
        if (task != null) {
            task.cancel(true);
        }
    }


    public boolean isPlaying() {
        return (exoPlayer != null && exoPlayer.getPlayWhenReady() && mAudioIsPlaying);
    }

    public ItemListRadio getPlayingRadioStation() {
        return station;
    }

    public Bitmap getImageBitmapFromURL(final String imageUrl) {
        Bitmap imageBitmap1 = null;
        try {
            imageBitmap1 = Picasso.with(context).load(String.valueOf(imageUrl))
                    .placeholder(R.drawable.placeholder)
                    .error(R.mipmap.ic_launcher)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return imageBitmap1;
    }

    public int getAudioSessionID() {
        return audioSessionId;
    }

    @SuppressLint("StaticFieldLeak")
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        Context mContext;

        public ProgressTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                Uri uri = null;
                try {
                    if (station.getRadioUrl() != null) {
                        if (station.getRadioUrl().endsWith(".m3u8")) {
                            uri = Uri.parse(Parser.parse(station.getRadioUrl()));
                        } else {
                            uri = Uri.parse(station.getRadioUrl());
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageBitmap = getImageBitmapFromURL(station.getRadioImageUrl());

                if (mContext != null) {
                    try {
                        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
                        OkHttpClient okHttpClient = new OkHttpClient();

                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

                        DataSource dataSource = new DefaultUriDataSource(mContext, bandwidthMeter,
                                new OkHttpDataSource(okHttpClient, userAgent, null, null,
                                        CacheControl.FORCE_NETWORK));

                        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

                        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                                MediaCodecSelector.DEFAULT, null, true,
                                null, null,
                                AudioCapabilities.getCapabilities(mContext), AudioManager.STREAM_MUSIC) {
                            @Override
                            protected void onAudioSessionId(int audioId) {
                                audioSessionId = audioId;
                            }
                        };
                        exoPlayer.prepare(audioRenderer);
                        exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, 1f);
                    }  catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return true;
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (SecurityException e1) {
                e1.printStackTrace();
            }  catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (exoPlayer != null) {
                if (success) {
                    wifiLock = ((WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                            .createWifiLock(WifiManager.WIFI_MODE_FULL, "RadiophonyLock");
                    wifiLock.acquire();

                    exoPlayer.addListener(new ExoPlayer.Listener() {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                            switch (playbackState) {
                                case ExoPlayer.STATE_BUFFERING:
                                    break;
                                case ExoPlayer.STATE_ENDED:
                                    //do what you want
                                    break;
                                case ExoPlayer.STATE_IDLE:
                                    break;
                                case ExoPlayer.STATE_PREPARING:
                                    break;
                                case ExoPlayer.STATE_READY:
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onPlayWhenReadyCommitted() {

                        }

                        @Override
                        public void onPlayerError(ExoPlaybackException error) {
                            Log.d("Error_listerner", error.getLocalizedMessage());
                            if (error.getLocalizedMessage().contains("Unable to connect to")) {
                                Toast.makeText(context, "Unable to Connect stream " +
                                        station.getRadioName(), Toast.LENGTH_SHORT).show();
                                NextPlay();
                            }
                        }
                    });
                    exoPlayer.seekTo(0);
                    exoPlayer.setPlayWhenReady(true);
                    mAudioIsPlaying = true;
                    createNotification();
                }

            }
        }
    }

}
