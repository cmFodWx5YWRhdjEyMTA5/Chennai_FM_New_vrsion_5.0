package com.chennaifmradiosongs.onlinemadrasradiostation;

import java.io.Serializable;

public class Config implements Serializable {


    public static final String EMAIL ="democlientm@gmail.com"; //your-gmail-username
    public static final String PASSWORD ="DemoClientGtechAndroid123!@"; //your-gmail-password


    public static final String SERVER_URL = "http://www.theindianapps.com/FM_Apps_with_Image/AppsArasan/ChenniaFM.php";


    public final static String PRODUCT_ID = "com.chennaifmradiosongs.adfree.purchased";
    public final static String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArMZntKu0AFkhVIVlHZBNkkmAG6GrWbZ/hujXd+nEOLzpY5BT/OvBZULp7kl+pEc97VTLCh57e/zameynkABWiadRNi3ZG0JsGvuSwNG1cJQSBzGWercZ3Xlrc8/Z0DIyNZxsfVzKEmFxLiQrYSq33WAurIegrq+4WBA9b10kYLv6fX9lpy6dQMRtr7OZX8jnXjoXp3zVV923NP7qflQFYmEqAisTMUNaFjV6Gizoz9Kuec5FuQUqrTpv21fmjF8o0xlXUL1BhCZuYYT4uUXZHJVqAbGHXjSXFf+uTQj7qYxbVKU9/ebOsA6dL0GGFHZAYct2fNhexUyyaVyMBVt4tQIDAQAB";
    public static final String MERCHANT_ID = "13347991332124313071";


    public static Boolean ENABLE_ADS = true;




    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String SHARED_PREF = "ah_firebase";

}