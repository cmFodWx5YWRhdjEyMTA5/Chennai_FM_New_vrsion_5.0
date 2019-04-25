package com.chennaifmradiosongs.onlinemadrasradiostation.image_utils;


import java.util.ArrayList;

public interface LatestWallListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemWallpaper> arrayListCat);
}
