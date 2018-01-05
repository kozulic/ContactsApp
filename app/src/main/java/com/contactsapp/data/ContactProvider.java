package com.contactsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.contactsapp.data.ContactContract.ContactEntry;

public class ContactProvider extends ContentProvider {

    private static final int CONTACTS = 100;
    private static final int CONTACT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACT, CONTACTS);
        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACT + "/#", CONTACT_ID);
    }

    private ContactDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ContactDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch(match) {
            case CONTACTS:
                cursor = db.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return ContactEntry.CONTENT_LIST_TYPE;
            case CONTACT_ID:
                return ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, values);
            default:
                throw new IllegalArgumentException("Insert is not possible for uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                rowsDeleted = db.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not possible for uri: " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return updateContact(uri, values, selection, selectionArgs);
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateContact(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for uri: " + uri);
        }
    }

    private Uri insertContact(Uri uri, ContentValues values) {
        String lname = values.getAsString(ContactEntry.COLUMN_CONTACT_LAST_NAME);
        if(lname == null) {
            throw new IllegalArgumentException("Last name is required");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ContactEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(ContactEntry.COLUMN_CONTACT_LAST_NAME)) {
            String lname = values.getAsString(ContactEntry.COLUMN_CONTACT_LAST_NAME);
            if(lname == null) {
                throw new IllegalArgumentException("Last name is required");
            }
        }
        if(values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(ContactEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
