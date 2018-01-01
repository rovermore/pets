package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by robertomoreno on 24/12/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetsContract.PetEntry.TABLE_NAME + " (" +
                    PetsContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PetsContract.PetEntry.COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    PetsContract.PetEntry.COLUMN_BREED + TEXT_TYPE + COMMA_SEP +
                    PetsContract.PetEntry.COLUMN_GENDER + " INTEGER" + COMMA_SEP +
                    PetsContract.PetEntry.COLUMN_WEIGHT + " INTEGER" + " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +
            PetsContract.PetEntry.TABLE_NAME;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME ="selther.db";

    public PetDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
