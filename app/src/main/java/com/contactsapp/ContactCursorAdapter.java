package com.contactsapp;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.VolumeProviderCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.contactsapp.data.ContactContract.ContactEntry;

public class ContactCursorAdapter extends CursorAdapter{

    public ContactCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView firstNameTextView = (TextView) view.findViewById(R.id.contactFirstName);
        TextView lastNameTextView = (TextView) view.findViewById(R.id.contactLastName);
        ImageView pictureImageView = (ImageView) view.findViewById(R.id.contactImage);

        int fnColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_FIRST_NAME);
        int lnColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_LAST_NAME);
        int picColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_PHOTO);

        String firstName = cursor.getString(fnColumnIndex);
        String lastName = cursor.getString(lnColumnIndex);
        byte[] imgByte = cursor.getBlob(picColumnIndex);

        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);
        if(imgByte == null) {
            pictureImageView.setImageResource(R.drawable.contact_default_img);
        } else {
            Bitmap imgShow = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            pictureImageView.setImageBitmap(imgShow);
        }
    }
}
