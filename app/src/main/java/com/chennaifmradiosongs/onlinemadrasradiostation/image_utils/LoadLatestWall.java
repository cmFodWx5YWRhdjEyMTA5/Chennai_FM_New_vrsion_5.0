package com.chennaifmradiosongs.onlinemadrasradiostation.image_utils;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoadLatestWall extends AsyncTask<String,String,Boolean> {


    private static final String TAG_WALL_ID = "id";
    private static final String TAG_WALL_IMAGE = "wallpaper_image";
    private static final String TAG_WALL_IMAGE_THUMB = "wallpaper_image_thumb";
    private static final String TAG_ROOT = "HD_WALLPAPER";

    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "category_name";
    private static final String TAG_CAT_IMAGE = "category_image";
    private static final String TAG_WALL_VIEWS = "total_views";
    private static final String TAG_WALL_AVG_RATE = "rate_avg";
    private static final String TAG_WALL_TOTAL_RATE = "total_rate";
    private static final String TAG_WALL_DOWNLOADS = "total_download";
    private static final String TAG_WALL_TAGS = "wall_tags";


    private LatestWallListener latestWallListener;
    private ArrayList<ItemWallpaper> arrayList;

    public LoadLatestWall(LatestWallListener latestWallListener) {
        this.latestWallListener = latestWallListener;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        latestWallListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String url = strings[0];
        String json = getJSONString(url);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                String id = objJson.getString(TAG_WALL_ID);
                String cid = objJson.getString(TAG_CAT_ID);
                String cat_name = objJson.getString(TAG_CAT_NAME);
                String img = objJson.getString(TAG_WALL_IMAGE).replace(" ","%20");
                String img_thumb = objJson.getString(TAG_WALL_IMAGE_THUMB).replace(" ","%20");
                String totalviews = objJson.getString(TAG_WALL_VIEWS);
                String totalrate = objJson.getString(TAG_WALL_TOTAL_RATE);
                String averagerate = objJson.getString(TAG_WALL_AVG_RATE);
                String tags = objJson.getString(TAG_WALL_TAGS);

                ItemWallpaper itemWallpaper = new ItemWallpaper(id, cid, cat_name, img, img_thumb, totalviews, totalrate, averagerate,"", tags);
                arrayList.add(itemWallpaper);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ee) {
            ee.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
        latestWallListener.onEnd(String.valueOf(s),arrayList);
        super.onPostExecute(s);
    }


    public static String getJSONString(String url) {
        String jsonString = null;
        HttpURLConnection linkConnection = null;
        try {
            URL linkurl = new URL(url);
            linkConnection = (HttpURLConnection) linkurl.openConnection();
            int responseCode = linkConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream linkinStream = linkConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j = 0;
                while ((j = linkinStream.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (linkConnection != null) {
                linkConnection.disconnect();
            }
        }
        return jsonString;
    }
}