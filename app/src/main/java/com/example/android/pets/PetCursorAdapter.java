package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

/**
 * Created by robertomoreno on 3/1/18.
 */

public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //inflates the item_view
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //saves the text views from the list_item into TextView variables
        TextView nameView = (TextView) view.findViewById(R.id.pet_name);
        TextView breedView = (TextView) view.findViewById(R.id.pet_breed);

        //gets the values of a certain column of the cursor and saves it into a variable
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetsContract.PetEntry.COLUMN_BREED));

        //Sets the text in the TextValues from de values from the variables
        nameView.setText(name);
        if(TextUtils.isEmpty(breed)){
            breedView.setText("Breed Unknown");
        }else{
            breedView.setText(breed);
        }

    }


}
