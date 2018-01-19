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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract;

import static android.widget.Toast.makeText;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    private Uri uri;
    //this boolean detects shows if a change was made in the activity by the user
    private boolean mPetHasChanged = false;

    static final String[] PROJECTION = new String[] {
            PetsContract.PetEntry._ID,
            PetsContract.PetEntry.COLUMN_NAME,
            PetsContract.PetEntry.COLUMN_BREED,
            PetsContract.PetEntry.COLUMN_GENDER,
            PetsContract.PetEntry.COLUMN_WEIGHT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // OnTouchListener that listens for any user touches on a View, implying that they are modifying
        // the view, and we change the mPetHasChanged boolean to true.
        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPetHasChanged = true;
                return false;
            }
        };

        setViews();
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);


        //Saves the intent that started the activity
        Intent intent = getIntent();
        //Gets the Uri of the intent
        uri = intent.getData();
        //Evaluates the uri and sets the activity title
        if(uri!=null){
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            //setViews();
            setupSpinner();
            getSupportLoaderManager().initLoader(0, null, this);
        }else{
            setTitle(getString(R.string.editor_activity_title_new_pet));
            invalidateOptionsMenu();
            setupSpinner();
        }


    }

    private void setViews(){
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private boolean insertPet(){

        boolean isPetInserted = true;

        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String weightText = mWeightEditText.getText().toString().trim();
        int weight=0;
        if (!TextUtils.isEmpty(weightText)) {
            weight = Integer.parseInt(weightText);
        }
        String genderText = mGenderSpinner.getSelectedItem().toString().trim();
        int gender;
        switch (genderText){

            case "Unknown":
                gender = 0;
                break;
            case "Male":
                gender = 1;
                break;
            case "Female":
                gender = 2;
                break;
            default:
                gender = 0;
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetEntry.COLUMN_NAME, name);
        values.put(PetsContract.PetEntry.COLUMN_BREED, breed);
        values.put(PetsContract.PetEntry.COLUMN_GENDER, gender);
        values.put(PetsContract.PetEntry.COLUMN_WEIGHT, weight);

        if(uri==null) {
            //checks if fields contains values or are in blank for the new insert to be complete
            boolean isNameInserted = TextUtils.isEmpty(name);
            boolean isBreedInserted = TextUtils.isEmpty(breed);

            if(isNameInserted && isBreedInserted){

                return isPetInserted=false;
            }else {

                // Insert the new row, returning the primary key value of the new row
                Uri newUri = getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, values);

                if (newUri == null) {

                    Toast toast = makeText(this, R.string.editor_error_insert_pet, Toast.LENGTH_LONG);
                    toast.show();

                } else {

                    Toast toast2 = makeText(this, R.string.editor_insert_pet_successful + String.valueOf(ContentUris.parseId(newUri)), Toast.LENGTH_LONG);
                    toast2.show();

                }
            }
        }else{
            int petsUpdated = getContentResolver().update(uri,values,null,null);
        }

        return isPetInserted;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (uri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Saves pet in database
                boolean inserted=insertPet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if(uri!=null){
                    DeleteConfirmationDialog();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = new CursorLoader(getBaseContext(),uri,PROJECTION,null,null,null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        data.moveToFirst();
        //set the data from the cursor to the views
        String name = data.getString(data.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_NAME));

        String breed = data.getString(data.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_BREED));

        int gender = data.getInt(data.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_GENDER));

        int weight = data.getInt(data.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_WEIGHT));

        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mGenderSpinner.setSelection(gender);
        mWeightEditText.setText(Integer.toString(weight));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //set the views in blank
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText(null);

    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void DeleteConfirmationDialog(){

        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(this);
        deleteDialogBuilder.setMessage(R.string.delete_dialog_msg);
        deleteDialogBuilder.setPositiveButton(R.string.delete,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //calls a method that deletes the entry of the table
                        deletePet(uri);
                    }
                });
        deleteDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog=deleteDialogBuilder.create();
        alertDialog.show();
    }

    //deletes the row in the table with the id in the uri
    private void deletePet(Uri uri){
        if(uri!=null) {
            int deletedLines = getContentResolver().delete(uri, null, null);

            //toast a different message depending on the number of rows deleted
            if (deletedLines != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Pet deleted", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No pet was deleted", Toast.LENGTH_LONG);
                toast.show();
            }
            finish();
        }
    }


}