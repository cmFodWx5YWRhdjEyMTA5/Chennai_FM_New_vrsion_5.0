package com.chennaifmradiosongs.onlinemadrasradiostation.notification;

import android.content.Context;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.DatabaseHandler;
import com.chennaifmradiosongs.onlinemadrasradiostation.database.News;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    private DatabaseHandler db;
    private Context context2;

    public ExampleNotificationReceivedHandler(Context context) {
        context2 = context;
    }

    @Override
    public void notificationReceived(OSNotification notification) {
        String openURL = null;
        String title = null;

        JSONObject data = notification.payload.additionalData;
        String body = notification.payload.body;
        String bigPicture = notification.payload.bigPicture;
        title = notification.payload.title;

        if (data != null) {
            openURL = data.optString("openURL", null);
            if (openURL != null)
                Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
        } else {
            openURL = notification.payload.launchURL;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm");
        String currentDateandTime = sdf.format(new Date());

        db = new DatabaseHandler(context2);

        if (openURL != null) {
            db.addContact(new News(title, body, openURL, bigPicture, currentDateandTime),
                    DatabaseHandler.TABLE_NEWS);
        }
    }
}
