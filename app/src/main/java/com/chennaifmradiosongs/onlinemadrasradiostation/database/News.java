package com.chennaifmradiosongs.onlinemadrasradiostation.database;

/**
 * Created by Priyesh on 23-Apr-14.
 */


public class News {

    //private variables
    int _id;
    String _name;
    String date;
    String link;
    String image;
    String seen;

    public String getDateformat() {
        return dateformat;
    }

    public void setDateformat(String dateformat) {
        this.dateformat = dateformat;
    }

    String dateformat;

    // Empty constructor
    public News(){

    }
    // constructor
    public News(int id, String name, String date, String link, String image, String seen,String dateformat){
        this._id = id;
        this._name = name;
        this.date = date;
        this.image = image;
        this.link=link;
        this.seen=seen;
        this.dateformat=dateformat;
    }

    // constructor
    public News(String name, String date, String link, String image,String dateformat){
        this._name = name;
        this.date = date;
        this.image = image;
        this.link=link;
        this.dateformat=dateformat;
    }
   
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getDate(){
        return this.date;
    }

    // setting phone number
    public void setDate(String date){
        this.date = date;
    }

    // getting phone number
    public String getLink(){
        return this.link;
    }

    // setting phone number
    public void setLink(String link){
        this.link = link;
    }


    // getting phone number
    public String getImage(){
        return this.image;
    }

    // setting phone number
    public void setImage(String image){
        this.image = image;
    }
    public String getSeen() {
        // TODO Auto-generated method stub
        return this.seen;
    }
    public void setSeen(String seen){
        this.seen=seen;
    }

}
