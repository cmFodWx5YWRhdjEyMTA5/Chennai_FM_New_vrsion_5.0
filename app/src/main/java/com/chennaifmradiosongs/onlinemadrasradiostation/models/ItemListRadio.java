package com.chennaifmradiosongs.onlinemadrasradiostation.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemListRadio implements Parcelable {

    public static final Parcelable.Creator<ItemListRadio> CREATOR
            = new Parcelable.Creator<ItemListRadio>() {
        public ItemListRadio createFromParcel(Parcel in) {
            return new ItemListRadio(in);
        }

        public ItemListRadio[] newArray(int size) {
            return new ItemListRadio[size];
        }
    };
    private String RadioId;
    private String RadioName;
    private String RadioUrl;
    private String RadioCateogory;
    private String RadioImageUrl;

    public ItemListRadio() {
    }

    public ItemListRadio(String Radioid) {
        this.RadioId = Radioid;
    }

    public ItemListRadio(String Radioid, String Radioname, String Radiourl, String RadioCat, String RadioImageUrl) {
        this.RadioId = Radioid;
        this.RadioName = Radioname;
        this.RadioUrl = Radiourl;
        this.RadioCateogory = RadioCat;
        this.RadioImageUrl = RadioImageUrl;
    }

    public ItemListRadio(Parcel source) {
        /*
         * Reconstruct from the Parcel. Keep same order as in writeToParcel()
         */
        RadioId = source.readString();
        RadioName = source.readString();
        RadioUrl = source.readString();
        RadioCateogory = source.readString();
        RadioImageUrl = source.readString();
    }

    public String getRadioUrl() {
        return RadioUrl;
    }

    public void setRadioUrl(String radioUrl) {
        RadioUrl = radioUrl;
    }

    public String getRadioImageUrl() {

        return RadioImageUrl;
    }

    public void setRadioImageUrl(String radioImageUrl) {
        RadioImageUrl = radioImageUrl;
    }

    public String getRadioCateogory() {
        return RadioCateogory;
    }

    public void setRadioCateogory(String radioCateogory) {
        RadioCateogory = radioCateogory;
    }

    public String getRadioId() {
        return RadioId;
    }

    public void setRadioId(String Radioid) {
        this.RadioId = Radioid;
    }

    public String getRadioName() {
        return RadioName;
    }

    public void setRadioName(String Radioname) {
        this.RadioName = Radioname;
    }

    public String getRadiourl() {
        return RadioUrl;
    }

    public void setRadiourl(String radiourl) {
        this.RadioUrl = radiourl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RadioId);
        dest.writeString(RadioName);
        dest.writeString(RadioUrl);
        dest.writeString(RadioCateogory);
        dest.writeString(RadioImageUrl);
    }
}