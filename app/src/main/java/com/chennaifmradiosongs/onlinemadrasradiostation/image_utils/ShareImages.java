package com.chennaifmradiosongs.onlinemadrasradiostation.image_utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShareImages extends AsyncTask<String, String, String> {
    URL myFileUrl;
    String option;
    Bitmap bmImg = null;
    File file;
    Context context;
    Uri uri;
    private ProgressDialog pDialog;

    public ShareImages(Context context, String option) {
        this.option = option;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
        if (option.equals("save")) {
            pDialog.setMessage(context.getResources().getString(R.string.downloading_wallpaper));
        } else {
            pDialog.setMessage(context.getResources().getString(R.string.please_wait));
        }
        pDialog.setIndeterminate(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... args) {
        try {
            myFileUrl = new URL(args[0]);
            String path = myFileUrl.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/" + context.getResources().getString(R.string.app_name) + "/Wallpapers");
            dir.mkdirs();
            file = new File(dir, fileName);

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

                if (option.equals("save")) {
                    MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                }
                return "1";
            } else {
                return "2";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    String path ;
    @Override
    protected void onPostExecute(String args) {

        if (args.equals("1") || args.equals("2")) {
            switch (option) {
                case "share":
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            (getBitmap()), "Design", null);
                    uri = Uri.parse(path);

                 //   Uri imageUri0 = Uri.parse("file://" + file.getAbsolutePath());
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.get_more_wall) + "\n" + context.getResources().getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_wallpaper)));
                    pDialog.dismiss();
                    break;
                case "whatsapp":
                  //  Uri imageUri = Uri.parse("file://" + file.getAbsolutePath());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.whatsapp");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.get_more_wall) + "\n" + context.getResources().getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            (getBitmap()), "Design", null);
                    uri = Uri.parse(path);

                    //   Uri imageUri0 = Uri.parse("file://" + file.getAbsolutePath());
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(shareIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        Toast.makeText(context, "Whatsapp Not Install in your Mobile", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "facebook":
                  //  Uri imageUri1 = Uri.parse("file://" + file.getAbsolutePath());
                    Intent shareIntent1 = new Intent();
                    shareIntent1.setAction(Intent.ACTION_SEND);
                    shareIntent1.setPackage("com.facebook.katana");
                    shareIntent1.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.get_more_wall) + "\n" + context.getResources().getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            (getBitmap()), "Design", null);
                    uri = Uri.parse(path);

                    //   Uri imageUri0 = Uri.parse("file://" + file.getAbsolutePath());
                    shareIntent1.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent1.setType("image/*");
                    shareIntent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(shareIntent1);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        Toast.makeText(context, "Facebook App Not Install in your Mobile", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "instagram":
                  //  Uri imageUri2 = Uri.parse("file://" + file.getAbsolutePath());
                    Intent shareIntent2 = new Intent();
                    shareIntent2.setAction(Intent.ACTION_SEND);
                    shareIntent2.setPackage("com.instagram.android");
                    shareIntent2.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.get_more_wall) + "\n" + context.getResources().getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            (getBitmap()), "Design", null);
                    uri = Uri.parse(path);

                    //   Uri imageUri0 = Uri.parse("file://" + file.getAbsolutePath());
                    shareIntent2.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent2.setType("image/*");
                    shareIntent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(shareIntent2);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        Toast.makeText(context, "Instagram App Not Install in your Mobile", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "twitter":
               //     Uri imageUri3 = Uri.parse("file://" + file.getAbsolutePath());
                    Intent shareIntent3 = new Intent();
                    shareIntent3.setAction(Intent.ACTION_SEND);
                    shareIntent3.setPackage("com.twitter.android");
                    shareIntent3.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.get_more_wall) + "\n" + context.getResources().getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            (getBitmap()), "Design", null);
                    uri = Uri.parse(path);

                    //   Uri imageUri0 = Uri.parse("file://" + file.getAbsolutePath());
                    shareIntent3.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent3.setType("image/*");
                    shareIntent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(shareIntent3);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        Toast.makeText(context, "Twitter App Not Install in your Mobile", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        pDialog.dismiss();
    }

    public Bitmap getBitmap() {
        try {
            Bitmap bitmap = null;
            String path = myFileUrl.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/" + context.getResources().getString(R.string.app_name) + "/Wallpapers");
            dir.mkdirs();
            file = new File(dir, fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}