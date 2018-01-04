/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetsContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static PetDbHelper mDbHelper;

    static final String[] PROJECTION = new String[] {
            PetsContract.PetEntry._ID,
            PetsContract.PetEntry.COLUMN_NAME,
            PetsContract.PetEntry.COLUMN_BREED};

    PetCursorAdapter petCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);

        //displayDatabaseInfo();

        petCursorAdapter = new PetCursorAdapter(this,null);

        ListView petsListView = (ListView)findViewById(R.id.list_view);

        petsListView.setAdapter(petCursorAdapter);

        petsListView.setEmptyView(findViewById(R.id.empty_view));

        getLoaderManager().initLoader(0,null,CatalogActivity.this).forceLoad();

    }

    /*public void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        //Cursor cursor = db.rawQuery("SELECT * FROM " + PetsContract.PetEntry.TABLE_NAME, null);

        String[] projection = {
                PetsContract.PetEntry._ID,
                PetsContract.PetEntry.COLUMN_NAME,
                PetsContract.PetEntry.COLUMN_BREED,
                PetsContract.PetEntry.COLUMN_GENDER,
                PetsContract.PetEntry.COLUMN_WEIGHT
        };
        Cursor cursor = db.query(
                PetsContract.PetEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }*/

    public void insertPet(){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetEntry.COLUMN_NAME, "Toto");
        values.put(PetsContract.PetEntry.COLUMN_BREED, "Terrier");
        values.put(PetsContract.PetEntry.COLUMN_GENDER, 1);
        values.put(PetsContract.PetEntry.COLUMN_WEIGHT, 7);

        // Insert the new row, returning the primary Uri and the id of the new row
        Uri newUri = getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, values);

        Log.v("CatalogActivity", R.string.editor_insert_pet_successful + String.valueOf(ContentUris.parseId(newUri)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,PetsContract.PetEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        petCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        petCursorAdapter.swapCursor(null);
    }
}
