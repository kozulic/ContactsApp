package com.contactsapp;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.contactsapp.data.ContactContract.ContactEntry;

public class ContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CONTACT_LOADER = 0;
    ContactCursorAdapter mCursorAdapter;

    private String selection;
    static private String[] selectionArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        selection = "";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, ModifyActivity.class);
                startActivity(intent);
            }
        });

        ListView contactsListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        contactsListView.setEmptyView(emptyView);

        mCursorAdapter = new ContactCursorAdapter(this, null);
        contactsListView.setAdapter(mCursorAdapter);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactsActivity.this, DetailsActivity.class);
                Uri currentContactUri = ContentUris.withAppendedId(ContactEntry.CONTENT_URI,id);
                intent.setData(currentContactUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(CONTACT_LOADER ,null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: Implement Search
                start();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ////TODO: Implement Search
                return false;
            }
        });
        return true;
    }

    public void start() {
        getLoaderManager().initLoader(CONTACT_LOADER ,null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_FIRST_NAME,
                ContactEntry.COLUMN_CONTACT_LAST_NAME,
                ContactEntry.COLUMN_CONTACT_PHOTO};
        return new CursorLoader(this, ContactEntry.CONTENT_URI, projection, null, null, ContactEntry.COLUMN_CONTACT_FIRST_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
