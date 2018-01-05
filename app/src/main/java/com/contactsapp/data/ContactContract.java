package com.contactsapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ContactContract {

    public static final String CONTENT_AUTHORITY = "com.contactsapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CONTACT = "contact";

    public static final class ContactEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACT);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;

        public static final String TABLE_NAME = "contact";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CONTACT_FIRST_NAME = "fname";
        public static final String COLUMN_CONTACT_LAST_NAME = "lname";
        public static final String COLUMN_CONTACT_PHOTO = "photo";
        public static final String COLUMN_CONTACT_PHONE_NUMBER = "number";
        public static final String COLUMN_CONTACT_MAIL = "email";

    }
}
