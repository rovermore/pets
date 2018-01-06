package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.pets.data.PetsContract.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetsContract.PATH_PETS;

/**
 * Created by robertomoreno on 26/12/17.
 */

public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mDbHelper;

    private final static int PETS = 101;
    private final static int PET_ID = 102;

    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS + "/#", PET_ID);

    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetsContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetsContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Set the notification to the content resolver to update the CursorLoader with new changes in the database
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        //Checks if name value is not null
        String name = values.getAsString(PetsContract.PetEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        //Checks breed value is not null
        String breed = values.getAsString(PetsContract.PetEntry.COLUMN_BREED);
        if (breed == null) {
            throw new IllegalArgumentException("Pet requires a breed");
        }
        //Checks if introduced gender is one of the three validated
        /*String gender = values.getAsString(PetsContract.PetEntry.COLUMN_GENDER);
        if (gender != "Female" || gender != "Male" || gender != "Unknown" || gender == null) {

            throw new IllegalArgumentException("Must enter a valid gender option");
        }*/
        //Checks if introduced weight is positive integer number
        Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_WEIGHT);
        if (weight < 0 && weight != null) {
            throw new IllegalArgumentException("Pet requires a positive weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long newRowId = database.insert(PetsContract.PetEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify the ContentResolver to update the the loader with the new info in the database
        getContext().getContentResolver().notifyChange(uri,null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(PetsContract.PetEntry.COLUMN_NAME)) {
            //Checks if name value is not null
            String name = values.getAsString(PetsContract.PetEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetsContract.PetEntry.COLUMN_BREED)) {
            //Checks breed value is not null
            String breed = values.getAsString(PetsContract.PetEntry.COLUMN_BREED);
            if (breed == null) {
                throw new IllegalArgumentException("Pet requires a breed");
            }
        }

        /*if (values.containsKey(PetsContract.PetEntry.COLUMN_GENDER)) {
            //Checks if introduced gender is one of the three validated
            String gender = values.getAsString(PetsContract.PetEntry.COLUMN_GENDER);
            if (gender != "Female" || gender != "Male" || gender != "Unknown" || gender == null) {

                throw new IllegalArgumentException("Must enter a valid gender option");
            }
        }*/

        if (values.containsKey(PetsContract.PetEntry.COLUMN_WEIGHT)) {
            //Checks if introduced weight is positive integer number
            Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_WEIGHT);
            if (weight < 0 && weight != null) {
                throw new IllegalArgumentException("Pet requires a positive weight");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int newUpdateId = database.update(PetsContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (newUpdateId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return 0;
        }


        //notify the ContentResolver to update the the loader with the new info in the database
        getContext().getContentResolver().notifyChange(uri,null);

        return newUpdateId;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                //notify the ContentResolver to update the the loader with the new info in the database
                getContext().getContentResolver().notifyChange(uri,null);
                // Delete all rows that match the selection and selection args
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:

                //notify the ContentResolver to update the the loader with the new info in the database
                getContext().getContentResolver().notifyChange(uri,null);
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}
