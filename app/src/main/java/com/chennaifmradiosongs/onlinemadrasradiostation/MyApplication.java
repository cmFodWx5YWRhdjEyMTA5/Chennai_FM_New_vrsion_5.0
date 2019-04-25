package com.chennaifmradiosongs.onlinemadrasradiostation;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chennaifmradiosongs.onlinemadrasradiostation.notification.ExampleNotificationOpenedHandler;
import com.chennaifmradiosongs.onlinemadrasradiostation.notification.ExampleNotificationReceivedHandler;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.onesignal.OneSignal;


public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;
    Context context;
    private RequestQueue mRequestQueue;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.context = base;
        MultiDex.install(context);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Typefile.overrideFont(getContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FacebookSdk.sdkInitialize(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        FacebookSdk.setLimitEventAndDataUsage(this, true);
        AppEventsLogger.activateApp(this);

        OneSignal.startInit(this)
                .autoPromptLocation(false) // default call promptLocation later
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler(this))
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(this))
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}