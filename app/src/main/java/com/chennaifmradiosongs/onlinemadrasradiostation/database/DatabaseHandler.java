package com.chennaifmradiosongs.onlinemadrasradiostation.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 31;

    // Database Name
    private static final String DATABASE_NAME = "NewsManager";

    // Contacts table name
    public static final String TABLE_NEWS = "News";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "body";
    private static final String KEY_CODE = "image";
    private static final String KEY_LINK = "link";
    private static final String KEY_SEEN = "is_seen";
    private static final String KEY_DATEFORMAT = "dateformat";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_NAME + " TEXT," + KEY_DATE + " TEXT," + KEY_CODE
                + " TEXT," + KEY_LINK + " TEXT," + KEY_SEEN
                + " TEXT DEFAULT 'N'," + KEY_DATEFORMAT + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addContact(News contact, String Table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_DATE, contact.getDate()); // Contact Phone
        values.put(KEY_CODE, contact.getImage());
        values.put(KEY_LINK, contact.getLink());
        values.put(KEY_DATEFORMAT, contact.getDateformat());
        // Inserting Row
        db.insert(Table, null, values);
        db.close();
    }

    // Getting single contact
    News getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NEWS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DATE, KEY_LINK, KEY_CODE, KEY_SEEN}, KEY_ID
                        + "=?", new String[]{String.valueOf(id)}, null, null, null,
                null);
        if (cursor != null)
            cursor.moveToFirst();

        News contact = new News(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6));
        // return contact
        cursor.close();
        db.close();
        return contact;
    }

    public void setSeen(int c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SEEN, "Y");
        // updating row
        db.update(TABLE_NEWS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(c)});
        db.close();
    }

    public String getSeen(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String is_seen = null;
        Cursor cursor = db.query(TABLE_NEWS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DATE, KEY_LINK, KEY_CODE, KEY_SEEN}, KEY_ID
                        + "=?", new String[]{String.valueOf(id)}, null, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            is_seen = cursor.getString(5);
            cursor.close();
        }
        db.close();
        return is_seen;
    }

    // Getting All Contacts
    public List<News> getAllContacts(String Table) {
        List<News> contactList = new ArrayList<News>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table + " ORDER BY " + KEY_ID
                + " DESC";
        // ORDER BY "+ KEY_ID +" DESC
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Log.d("db", cursor.toString());
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                News contact = new News();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setDate(cursor.getString(2));
                contact.setImage(cursor.getString(3));
                contact.setLink(cursor.getString(4));
                contact.setSeen(cursor.getString(5));
                contact.setDateformat(cursor.getString(6));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    // Deleting single contact
    public void deleteContact(News contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r = db.delete(TABLE_NEWS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
        Log.d("Log", r + " DELETE RESUKT");
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_NEWS;
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery(countQuery, null);

            count = cursor.getCount();
            cursor.close();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        // return count
        db.close();
        return count;
    }

    public void deleteNews() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "delete from " + TABLE_NEWS + " ORDER BY " + KEY_ID
                    + " ASC" + " LIMIT 40";
            db.execSQL(selectQuery);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteAllNews() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "delete from " + TABLE_NEWS;
            db.execSQL(selectQuery);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
}
