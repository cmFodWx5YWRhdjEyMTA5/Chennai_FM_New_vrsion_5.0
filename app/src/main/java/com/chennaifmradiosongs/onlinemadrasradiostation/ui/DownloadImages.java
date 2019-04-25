package com.chennaifmradiosongs.onlinemadrasradiostation.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImages extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private String Url;
    @SuppressLint("StaticFieldLeak")
    private CustomWebView customWebView;

    public DownloadImages(CustomWebView custom, String replace) {

        this.customWebView = custom;
        this.Url = replace;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            progressDialog = new ProgressDialog(customWebView);
            progressDialog.setMessage("Downloading Images....");
            progressDialog.show();
        }  catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String doInBackground(String... urls) {
        try {
            URL myFileUrl = new URL(Url);
            String path = myFileUrl.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/" + customWebView.getResources().getString(R.string.app_name) + "/Notification");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            File file = new File(dir, fileName);

            if (!file.exists()) {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                FileOutputStream fos = new FileOutputStream(file);
                byte data[] = new byte[4096];
                int count;
                while ((count = is.read(data)) != -1) {
                    if (isCancelled()) {
                        is.close();
                        return null;
                    }
                    fos.write(data, 0, count);
                }
                fos.flush();
                fos.close();
                return "image";
            } else {
                return "already";
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String bitmap) {
        // TODO: check this.exception
        // TODO: do something with the feed
        progressDialog.dismiss();
        switch (bitmap) {
            case "image":
                Toast.makeText(customWebView, "Download Image Completed", Toast.LENGTH_SHORT).show();
                break;
            case "already":
                Toast.makeText(customWebView, "Already Image Downloaded", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(customWebView, "Download Image failed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

