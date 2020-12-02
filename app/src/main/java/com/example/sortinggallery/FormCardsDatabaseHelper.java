package com.example.sortinggallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class FormCardsDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="register.db2";
    public static final String TABLE_NAME="formdetails";
    public static final String COL_1="ID";
    public static final String COL_2="username";
    public static final String COL_3="form_title";
    public static final String COL_4="date_from";
    public static final String COL_5="date_to";
    public static final String COL_6="form_address";

    public FormCardsDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE formdetails");
        sqLiteDatabase.execSQL("CREATE TABLE formdetails (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, form_title TEXT, date_from TEXT, date_to TEXT, form_address TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addForm(String user, String from, String to, String title, String address){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("form_title", title);
        contentValues.put("date_from", from);
        contentValues.put("date_to", to);
        contentValues.put("form_address", address);
        long res = db.insert("formdetails", null, contentValues);

        // Check form_address' form SQLite database
        final Cursor cursor = db.rawQuery("SELECT * FROM formdetails;", null);
        if(cursor.moveToFirst()){
            do{
                Log.d("FormCardsDatabaseHelper", "Values : " + cursor.getString(5) + "  > Column Names : " + cursor.getColumnNames());
            } while(cursor.moveToNext());
        }

        db.close();
        return res;
    }

    public void deleteAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM formdetails";
        db.execSQL(sql);
        db.close();
    }

    public List<FormObject> getAllForms(Context context) throws ParseException {
        Log.d("FormCardsDatabaseHelper", "Inside getAllForms()");
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        List<FormObject> formList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query
        String select = "SELECT * FROM formdetails WHERE username != '" + sharedPreferences.getString("username", "default") + "'";
        Cursor cursor = db.rawQuery(select, null);

        // Loop through
        if(cursor.moveToFirst()){
            Log.d("FormCardsDatabaseHelper", "cursor got some values");
            do{
                Log.d("FormCardsDatabaseHelper", "Username gotten : " + cursor.getString(1));
                Log.d("FormCardsDatabaseHelper", "Form Title gotten : " + cursor.getString(2));
                Log.d("FormCardsDatabaseHelper", "From date gotten : " + cursor.getString(3));
                Log.d("FormCardsDatabaseHelper", "To date gotten : " + cursor.getString(4));
                Log.d("FormCardsDatabaseHelper", "Form Address gotten : " + cursor.getString(5));
                FormObject formObject = new FormObject();
                formObject.setIdentity( Integer.parseInt( cursor.getString(0) ) );
                formObject.setUsername(cursor.getString(1));
                formObject.setFormTitle(cursor.getString(2));
                formObject.setFromDate(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(3)));
                formObject.setToDate(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(4)));
                formObject.setFormAddress(cursor.getString(5));

                formList.add(formObject);
            } while(cursor.moveToNext());
        }
        else{
            Log.d("FormCardsDatabaseHelper", "cursor didn't get any values. Size of returned formList : " + formList.size());
        }

        db.close();
        return formList;
    }

    public List<FormObject> getAllFormsForProfile(Context context) throws ParseException {
        Log.d("FormCardsDatabaseHelper", "Inside getAllFormsForProfile()");
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        List<FormObject> formList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query
        String select = "SELECT * FROM formdetails WHERE username == '" + sharedPreferences.getString("username", "default") + "'";
        Cursor cursor = db.rawQuery(select, null);

        // Loop through
        if(cursor.moveToFirst()){
            Log.d("FormCardsDatabaseHelper", "cursor got some values");
            do{
                Log.d("FormCardsDatabaseHelper", "Username gotten : " + cursor.getString(1));
                Log.d("FormCardsDatabaseHelper", "Form Title gotten : " + cursor.getString(2));
                Log.d("FormCardsDatabaseHelper", "From date gotten : " + cursor.getString(3));
                Log.d("FormCardsDatabaseHelper", "To date gotten : " + cursor.getString(4));
                Log.d("FormCardsDatabaseHelper", "Form Address gotten : " + cursor.getString(5));
                FormObject formObject = new FormObject();
                formObject.setIdentity( Integer.parseInt( cursor.getString(0) ) );
                formObject.setUsername(cursor.getString(1));
                formObject.setFormTitle(cursor.getString(2));
                formObject.setFromDate(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(3)));
                formObject.setToDate(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(4)));
                formObject.setFormAddress(cursor.getString(5));

                formList.add(formObject);
            } while(cursor.moveToNext());
        }
        else{
            Log.d("FormCardsDatabaseHelper", "cursor didn't get any values. Size of returned formList : " + formList.size());
        }
        db.close();
        return formList;
    }

    public int deleteForm(String user, String title, String address, String path){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("form_title", title);
        contentValues.put("form_address", address);
        // long res = db.insert("formdetails", null, contentValues);

        String selection = COL_2 + "=?" + " and " + COL_3 + "=?" + " and " + COL_6 + "=?";
        String[] selectionArgs = {user, title, address};
        int res = db.delete(TABLE_NAME, selection, selectionArgs);

        db.close();

        File file = new File(path, address);
        Log.d("FormCardsDatabaseHelper", "Form Path : " + path + "/" + address);

        if(file.exists()){
            if(file.delete()){
                Log.d("FormCardsDatabaseHelper", "Form Deleted");
            }
            else {
                Log.d("FormCardsDatabaseHelper", "Form could not be Deleted");

            }
        }
        else{
            Log.d("FormCardsDatabaseHelper", "Form does not exist!");
        }

        return res;
    }



}
