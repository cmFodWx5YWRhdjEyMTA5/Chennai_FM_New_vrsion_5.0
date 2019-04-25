package com.chennaifmradiosongs.onlinemadrasradiostation.utils;

public class RSSItem {

    public String title;
    public String link;
    public String description;
    public String pubdate;
    public String images;
    public String guid;

    public RSSItem(String title, String link,String images, String description, String pubdate, String guid) {
        this.title = title;
        this.link = link;
        this.images=images;
        this.description = description;
        this.pubdate = pubdate;
        this.guid = guid;
    }
}