package com.contactsapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contactsapp.data.ContactContract.ContactEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_CONTACT_LOADER = 0;
    private Uri mCurrentContactUri;

    private TextView fullNameTextView;
    private TextView phoneNumberTextView;
    private TextView eMailTextView;
    private ImageView pictureImageView;

    private String num;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        final Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_CONTACT_LOADER, null, this);

        fullNameTextView = (TextView) findViewById(R.id.full_name_tv);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number_tv);
        eMailTextView = (TextView) findViewById(R.id.mail_tv);
        pictureImageView = (ImageView) findViewById(R.id.pic_iv);

        final Button callButton = (Button) findViewById(R.id.call_button);
        final Button smsButton = (Button) findViewById(R.id.sms_button);
        final Button mailButton = (Button) findViewById(R.id.mail_button);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + num));
                if(ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DetailsActivity.this, ModifyActivity.class);
                intent1.setData(mCurrentContactUri);
                startActivity(intent1);
            }
        });
    }

    protected void sendSMS() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", num);

        try {
            startActivity(smsIntent);
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DetailsActivity.this, "SMS failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendMail() {
        String[] TO = {mail};
        String[] CC = {""};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");

        try {
            startActivity(Intent.createChooser(emailIntent, "Sending E-Mail"));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DetailsActivity.this, "Install e-mail client on your phone", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_FIRST_NAME,
                ContactEntry.COLUMN_CONTACT_LAST_NAME,
                ContactEntry.COLUMN_CONTACT_PHONE_NUMBER,
                ContactEntry.COLUMN_CONTACT_MAIL,
                ContactEntry.COLUMN_CONTACT_PHOTO};
        return new CursorLoader(this, mCurrentContactUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1) {
            return;
        }
        if(cursor.moveToFirst()) {
            int fNameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_FIRST_NAME);
            int lNameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_LAST_NAME);
            int pNumberColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER);
            int eMailColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_MAIL);
            int pictureColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_PHOTO);

            String fName = cursor.getString(fNameColumnIndex);
            String lName = cursor.getString(lNameColumnIndex);
            int pNumber = cursor.getInt(pNumberColumnIndex);
            String eMail = cursor.getString(eMailColumnIndex);
            byte[] imgByte = cursor.getBlob(pictureColumnIndex);
            /*if(imgByte.length >= 0) {
                Bitmap imgShow = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            } */
            num = "0";
            num += Integer.toString(pNumber);
            mail = eMail;

            fullNameTextView.setText(fName + " " + lName);
            phoneNumberTextView.setText("0" + Integer.toString(pNumber));
            eMailTextView.setText(eMail);
            if(imgByte == null) {
                pictureImageView.setImageResource(R.drawable.contact_default_img);
            } else {
                Bitmap imgShow = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                pictureImageView.setImageBitmap(imgShow);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fullNameTextView.setText("");
        phoneNumberTextView.setText("");
        eMailTextView.setText("");
    }
}
