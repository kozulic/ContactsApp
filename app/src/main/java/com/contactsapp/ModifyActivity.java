package com.contactsapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.contactsapp.data.ContactContract.ContactEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.attr.data;


public class ModifyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SELECT_PHOTO = 100;

    private static final int EXISTING_CONTACT_LOADER = 0;
    private Uri mCurrentContactUri;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEMailEditText;

    private Bitmap newImg;

    private boolean mContactHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        if(mCurrentContactUri == null) {
            setTitle("Add a contact");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit contact");
            getLoaderManager().initLoader(EXISTING_CONTACT_LOADER, null, this);
        }

        mFirstNameEditText = (EditText) findViewById(R.id.first_name);
        mLastNameEditText = (EditText) findViewById(R.id.last_name);
        mPhoneNumberEditText = (EditText) findViewById(R.id.number);
        mEMailEditText = (EditText) findViewById(R.id.mail);

        mFirstNameEditText.setOnTouchListener(mTouchListener);
        mLastNameEditText.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mEMailEditText.setOnTouchListener(mTouchListener);

        final Button addPicButton = (Button) findViewById(R.id.add_picture_button);
        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap image = decodeUri(selectedImage);
                        newImg = image;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentContactUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveContactToDatabase();
                finish();
                return true;
            case R.id.action_delete:
                confirmDelete();
                return true;
            case android.R.id.home:
                if(!mContactHasChanged) {
                    //TODO: This has to be fixed
                    //NavUtils.navigateUpFromSameTask(ModifyActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: This has to be fixed
                                //NavUtils.navigateUpFromSameTask(ModifyActivity.this);
                            }
                        };
                unsavedChangesAlert(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ContactEntry._ID, ContactEntry.COLUMN_CONTACT_FIRST_NAME, ContactEntry.COLUMN_CONTACT_LAST_NAME, ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, ContactEntry.COLUMN_CONTACT_MAIL};
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

            String fName = cursor.getString(fNameColumnIndex);
            String lName = cursor.getString(lNameColumnIndex);
            int pNumber = cursor.getInt(pNumberColumnIndex);
            String eMail = cursor.getString(eMailColumnIndex);

            mFirstNameEditText.setText(fName);
            mLastNameEditText.setText(lName);
            mPhoneNumberEditText.setText("0" + Integer.toString(pNumber));
            mEMailEditText.setText(eMail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFirstNameEditText.setText("");
        mLastNameEditText.setText("");
        mPhoneNumberEditText.setText("");
        mEMailEditText.setText("");
    }

    private void saveContactToDatabase() {
        String fNameString = mFirstNameEditText.getText().toString().trim();
        String lNameString = mLastNameEditText.getText().toString().trim();
        String pNumberString = mPhoneNumberEditText.getText().toString().trim();
        String eMailString = mEMailEditText.getText().toString().trim();

        if(mCurrentContactUri == null && newImg == null && TextUtils.isEmpty(fNameString) && TextUtils.isEmpty(lNameString) && TextUtils.isEmpty(eMailString) && TextUtils.isEmpty(pNumberString)) {
            return;
        }

        ContentValues data = new ContentValues();
        data.put(ContactEntry.COLUMN_CONTACT_FIRST_NAME, fNameString);
        data.put(ContactEntry.COLUMN_CONTACT_LAST_NAME, lNameString);
        data.put(ContactEntry.COLUMN_CONTACT_MAIL, eMailString);
        if(newImg != null) {
            byte[] imgToSave = getBitmapAsByteArray(newImg);
            data.put(ContactEntry.COLUMN_CONTACT_PHOTO, imgToSave);
        }
        int phoneNumber = 0;
        if(!TextUtils.isEmpty(pNumberString)) {
            phoneNumber = Integer.parseInt(pNumberString);
        }
        if(phoneNumber != 0) {
            data.put(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phoneNumber);
        }
        if(mCurrentContactUri == null) {
            Uri newUri = getContentResolver().insert(ContactEntry.CONTENT_URI, data);
            if(newUri == null) {
                Toast.makeText(this, getString(R.string.fail),Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.sucess), Toast.LENGTH_SHORT).show();
            }
        } else {
            int changes = getContentResolver().update(mCurrentContactUri, data, null, null);
            if(changes == 0) {
                Toast.makeText(this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.sucess), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteContactFromDatabase() {
        if(mCurrentContactUri != null) {
            int contactsDeleted = getContentResolver().delete(mCurrentContactUri, null, null);
            if(contactsDeleted == 0) {
                Toast.makeText(this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.sucess), Toast.LENGTH_SHORT).show();
                //int add
                Intent intent = new Intent(ModifyActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        }
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_question);
        builder.setPositiveButton(R.string.delete_answer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContactFromDatabase();
            }
        });
        builder.setNegativeButton(R.string.cancel_answer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void unsavedChangesAlert(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.changes_question);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

     @Override
    public void onBackPressed() {
        if(!mContactHasChanged) {
            super.onBackPressed();

            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        unsavedChangesAlert(discardButtonClickListener);
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 140;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
