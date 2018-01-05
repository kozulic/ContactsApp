package com.contactsapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.contactsapp.data.ContactContract.ContactEntry;

public class ContactDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "contacts.db";
    public static final int DATABASE_VERSION = 1;

    public ContactDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + " ("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContactEntry.COLUMN_CONTACT_FIRST_NAME + " TEXT, "
                + ContactEntry.COLUMN_CONTACT_LAST_NAME + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_CONTACT_PHOTO + " BLOB, "
                + ContactEntry.COLUMN_CONTACT_PHONE_NUMBER + " INTEGER, "
                + ContactEntry.COLUMN_CONTACT_MAIL + " TEXT);";
        db.execSQL(SQL_CREATE_CONTACT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
