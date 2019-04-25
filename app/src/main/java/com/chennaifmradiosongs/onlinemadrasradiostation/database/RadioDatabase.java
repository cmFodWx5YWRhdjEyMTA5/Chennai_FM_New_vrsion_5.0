package com.chennaifmradiosongs.onlinemadrasradiostation.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chennaifmradiosongs.onlinemadrasradiostation.models.ItemListRadio;

import java.util.ArrayList;
import java.util.List;

public class RadioDatabase extends SQLiteOpenHelper {

    public static final String TABLE_FAV = "favorite";
    public static final String TABLE_RADIO = "radio";
    public static final String TABLE_RECENT_RADIO = "recent_radio";
    private static final int DATABASE_VERSION = 31;
    private static final String DATABASE_NAME = "TamilRadioFM";
    private static final String F_ID = "radio_id";
    private static final String F_RADIO_URL = "radio_url";
    private static final String F_RADIO_NAME = "radio_name";
    private static final String F_RADIO_CAT = "radio_category";
    private static final String F_RADIO_IMAGE_URL = "radio_img_url";

    private static final String R_ID = "fm_id";
    private static final String R_URL = "fm_url";
    private static final String R_NAME = "fm_name";
    private static final String R_CAT = "fm_category";
    private static final String R_IMAGE_URL = "fm_img_url";


    private static final String ID = "recent_sno";
    private static final String RECENT_ID = "recent_fm_id";
    private static final String RECENT_URL = "recent_fm_url";
    private static final String RECENT_NAME = "recent_fm_name";
    private static final String RECENT_CAT = "recent_fm_category";
    private static final String RECENT_IMAGE_URL = "recent_fm_img_url";

    public RadioDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FAV + "("
                + F_ID + " TEXT,"
                + F_RADIO_NAME + " TEXT,"
                + F_RADIO_IMAGE_URL + " TEXT,"
                + F_RADIO_CAT + " TEXT,"
                + F_RADIO_URL + " TEXT "
                + ")";
        String CREATE_RADIO_TABLE = "CREATE TABLE " + TABLE_RADIO + "("
                + R_ID + " TEXT,"
                + R_NAME + " TEXT,"
                + R_IMAGE_URL + " TEXT,"
                + R_CAT + " TEXT,"
                + R_URL + " TEXT "
                + ")";

        String CREATE_RECENT_RADIO_TABLE = "CREATE TABLE " + TABLE_RECENT_RADIO + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RECENT_ID + " TEXT,"
                + RECENT_NAME + " TEXT,"
                + RECENT_IMAGE_URL + " TEXT,"
                + RECENT_CAT + " TEXT,"
                + RECENT_URL + " TEXT "
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_RADIO_TABLE);
        db.execSQL(CREATE_RECENT_RADIO_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RADIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_RADIO);
        // Create tables again
        onCreate(db);
    }

    //Adding Record in Database

    public void AddOneFavorite(ItemListRadio pj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(F_ID, pj.getRadioId());
        values.put(F_RADIO_NAME, pj.getRadioName());
        values.put(F_RADIO_IMAGE_URL, pj.getRadioImageUrl());
        values.put(F_RADIO_CAT, pj.getRadioCateogory());
        values.put(F_RADIO_URL, pj.getRadiourl());
        db.insert(TABLE_FAV, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public boolean isExist(String table, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + F_ID + " = ?", new String[]{id});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Getting All Data
    public List<ItemListRadio> getAllFavData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ItemListRadio> list = new ArrayList<ItemListRadio>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAV, null);

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                ItemListRadio contact = new ItemListRadio();
                contact.setRadioId(cursor.getString(0));
                contact.setRadioName(cursor.getString(1));
                contact.setRadioImageUrl(cursor.getString(2));
                contact.setRadioCateogory(cursor.getString(3));
                contact.setRadiourl(cursor.getString(4));
                // Adding contact to list
                list.add(contact);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }

    public void deleteFavorites(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAV, F_ID + " = ?", new String[]{String.valueOf(id + "")});
    }


    //Added the FM in SQLITE
    public void deleteRadiodb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RADIO, null, null);
    }

    public int RadioisExist() {
        String countQuery = "SELECT * FROM " + TABLE_RADIO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void AddRadio(ItemListRadio listRadio) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values_radio = new ContentValues();
        values_radio.put(R_ID, listRadio.getRadioId());
        values_radio.put(R_NAME, listRadio.getRadioName());
        values_radio.put(R_IMAGE_URL, listRadio.getRadioImageUrl());
        values_radio.put(R_CAT, listRadio.getRadioCateogory());
        values_radio.put(R_URL, listRadio.getRadiourl());
        db.insert(TABLE_RADIO, null, values_radio);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    // Getting All Data
    public ArrayList<ItemListRadio> getAllRadio() {
        ArrayList<ItemListRadio> dataList = new ArrayList<ItemListRadio>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RADIO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();
                contact.setRadioId((cursor.getString(0)));
                contact.setRadioName(cursor.getString(1));
                contact.setRadioImageUrl(cursor.getString(2));
                contact.setRadioCateogory(cursor.getString(3));
                contact.setRadiourl(cursor.getString(4));
                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return dataList;
    }


    public ArrayList<ItemListRadio> CategorytypeList() {
        ArrayList<ItemListRadio> datacategorytype = new ArrayList<ItemListRadio>();

        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RADIO + " Group By " + R_CAT + " ORDER BY CAST(" + R_CAT + " As TEXT) ASC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();
                contact.setRadioCateogory(cursor.getString(3));
                // Adding contact to list
                datacategorytype.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return datacategorytype;

    }

    public ArrayList<ItemListRadio> CategorytypeListRadio(String status2) {
        // TODO Auto-generated method stub
        ArrayList<ItemListRadio> data = new ArrayList<ItemListRadio>();
        String[] arg = {status2};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RADIO + " WHERE " + R_CAT + "= ?", arg);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();
                contact.setRadioId((cursor.getString(0)));
                contact.setRadioName(cursor.getString(1));
                contact.setRadioImageUrl(cursor.getString(2));
                contact.setRadioCateogory(cursor.getString(3));
                contact.setRadiourl(cursor.getString(4));
                data.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }


    public void deleteRecentRadiodb(ItemListRadio contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECENT_RADIO, RECENT_ID + " = ?",
                new String[]{String.valueOf(contact.getRadioId())});
        db.close();
    }

    public boolean RecentRadioisExist(ItemListRadio contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_RECENT_RADIO + " WHERE " + RECENT_ID + " = ?";
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(contact.getRadioId())});
        return cursor.getCount() > 0;
    }

    public void AddRecentRadio(ItemListRadio listRadio) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values_radio = new ContentValues();
        values_radio.put(RECENT_ID, listRadio.getRadioId());
        values_radio.put(RECENT_NAME, listRadio.getRadioName());
        values_radio.put(RECENT_IMAGE_URL, listRadio.getRadioImageUrl());
        values_radio.put(RECENT_CAT, listRadio.getRadioCateogory());
        values_radio.put(RECENT_URL, listRadio.getRadiourl());
        db.insert(TABLE_RECENT_RADIO, null, values_radio);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    // Getting All Data
    public ArrayList<ItemListRadio> getAllRecentRadio() {
        ArrayList<ItemListRadio> dataList = new ArrayList<ItemListRadio>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RECENT_RADIO + " ORDER BY " + ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();
                contact.setRadioId((cursor.getString(1)));
                contact.setRadioName(cursor.getString(2));
                contact.setRadioImageUrl(cursor.getString(3));
                contact.setRadioCateogory(cursor.getString(4));
                contact.setRadiourl(cursor.getString(5));
                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return dataList;
    }
}
