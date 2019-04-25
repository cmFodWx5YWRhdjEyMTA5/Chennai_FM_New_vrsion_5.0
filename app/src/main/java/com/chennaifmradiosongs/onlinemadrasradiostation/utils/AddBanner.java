package com.chennaifmradiosongs.onlinemadrasradiostation.utils;

public class AddBanner {

    String sno;
    String Image_Banner;

    public AddBanner(String sno, String image, String Link) {
        this.Image_Banner = image;
        this.sno = sno;
        this.Link = Link;

    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getImage_Banner() {
        return Image_Banner;
    }

    public void setImage_Banner(String image_Banner) {
        Image_Banner = image_Banner;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    String Link;

}
