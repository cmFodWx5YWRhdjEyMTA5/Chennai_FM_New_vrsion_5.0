package com.chennaifmradiosongs.onlinemadrasradiostation.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.chennaifmradiosongs.onlinemadrasradiostation.activities.SplashActivity;
import com.chennaifmradiosongs.onlinemadrasradiostation.ui.CustomWebView;

import org.json.JSONObject;

public class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    private Context context2;

    public ExampleNotificationOpenedHandler(Context context) {
        context2 = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;

        String title = null;
        String openURL = null;

        String body = result.notification.payload.body;
        if (data != null) {
            title = data.optString("title", null);
            openURL = data.optString("openURL", null);
            if (openURL != null)
                Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
        } else {
            title = result.notification.payload.title;
            openURL = result.notification.payload.launchURL;
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
        }
        if (context2 != null) {
            if (openURL != null) {
                Intent intent = new Intent(context2, CustomWebView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title", title);
                intent.putExtra("openURL", openURL);
                intent.putExtra("FromActivity", 0);
                context2.startActivity(intent);
            } else {
                Intent intent = new Intent(context2, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context2.startActivity(intent);
            }
        }
    }
}
