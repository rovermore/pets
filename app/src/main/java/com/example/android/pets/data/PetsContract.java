package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by robertomoreno on 20/12/17.
 */

public final class PetsContract {

    private PetsContract(){};

    //Parses the new content URI into constants
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";


        public static class PetEntry implements BaseColumns{
            //Each of the name of the columns of the pets table
            public static final String TABLE_NAME = "pets";
            public static final String _ID = BaseColumns._ID;
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_BREED = "breed";
            public static final String COLUMN_GENDER = "gender";
            public static final String COLUMN_WEIGHT = "weight";

            //Constant to access the content URI for the table pets
            public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

            //The MIME type of the {@link #CONTENT_URI} for a list of pets.
            public static final String CONTENT_LIST_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

            //The MIME type of the {@link #CONTENT_URI} for a single pet.
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

            //Possible genders of the pets
            public static final int GENDER_UNKNOWN = 0;
            public static final int GENDER_MALE = 1;
            public static final int GENDER_FEMALE = 2;


        }

}
