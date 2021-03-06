package com.tuprojects.hd.callalarm;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
MODEL:

    This class will serve as an abstraction of the contact list data that is pulled from the phone.
    Ultimately the data will be formatted as such:

    [
        ("Call ID", "FirstName", "LastName", "Length of call", "Date of call", "Time of call", "Incoming vs. Outgoing"),
        ("Call ID", "FirstName", "LastName", "Length of call", "Date of call", "Time of call", "Incoming vs. Outgoing"),
        ...,
    ]

    [
        (Consider only looking at the last x number of calls in the users call log)
        - Look into the new Call Log Permission policy implemented by Android
    ]
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "call_list.db"; //get the proper nomenclature if there is an sql naming convention
    private static final String TABLE_NAME = "call_list_table";

    private static final String COL0 = "id"; //integer, primary key
    private static final String COL1 = "stripped_phone_number"; //string
    private static final String COL2 = "unstripped_phone_number"; //string
    private static final String COL3 = "last_call_datetime"; //date
    private static final String COL4 = "last_call_duration"; //integer, seconds
    private static final String COL5 = "calls_per_period"; //integer
    private static final String COL6 = "frequency"; //integer
    private static final String COL7 = "period"; //integer [1=day, 7=week, 30=month, 90=quarter]
    private static final String COL8 = "name"; //string
    private static final String COL9 = "intervalHr"; //integer
    private static final String COL10 = "intervalMin"; //integer
    private static final String COL11 = "next_call_datetime"; //date
    private static final String COL12 = "frequency_display"; //text

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    SQLiteDatabase readableDatabase;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL1 + " TEXT, "
                + COL2 + " TEXT, "
                + COL3 + " DATE, "
                + COL4 + " INTEGER, "
                + COL5 + " INTEGER, "
                + COL6 + " INTEGER, "
                + COL7 + " INTEGER, "
                + COL8 + " TEXT, "
                + COL9 + " INTEGER, "
                + COL10 + " INTEGER, "
                + COL11 + " DATE, "
                + COL12 + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openReadableDatabase() {
        readableDatabase = this.getReadableDatabase();
    }

    //Necessary to prevent memory leaks
    public void closeReadableDatabase() {
        readableDatabase.close();
    }

    public boolean addData(String strippedNumber, String number, String date, int duration, int cpp, int freq, int per, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, strippedNumber);
        contentValues.put(COL2, number);
        contentValues.put(COL3, date);
        contentValues.put(COL4, duration);
        contentValues.put(COL5, cpp);
        contentValues.put(COL6, freq);
        contentValues.put(COL7, per);
        contentValues.put(COL8, name);

        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        if (result == -1) { //negatively inserted data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean updateCallsPerPeriod(int newCPP, String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL5, newCPP);

        long result = db.update(TABLE_NAME, contentValues, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively updated data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean updateFrequency(int newFreq, String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL6, newFreq);

        long result = db.update(TABLE_NAME, contentValues, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively updated data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean updatePeriod(int newPeriod, String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL7, newPeriod);

        long result = db.update(TABLE_NAME, contentValues, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively updated data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean updateInterval(int intervalHr, int intervalMin, String nextCallDate, String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL9, intervalHr);
        contentValues.put(COL10, intervalMin);
        contentValues.put(COL11, nextCallDate);

        long result = db.update(TABLE_NAME, contentValues, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively updated data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean updateFrequencyDisplay(String freqDisp, String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL12, freqDisp);

        long result = db.update(TABLE_NAME, contentValues, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively updated data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean removeData(String strippedNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, COL1 + " = ?", new String[]{strippedNumber});

        db.close();

        if (result==-1) { //negatively deleted data will be result in -1
            return false;
        } else {
            return true;
        }
    }

    public boolean hasContact(String strippedNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor contactsDBcursor = db.query(TABLE_NAME, null, COL1 + " = ?", new String[]{strippedNumber}, null, null, null);

        if ((contactsDBcursor != null) && (contactsDBcursor.getCount() > 0)) { //check if Cursor (database) is empty after querying it
            contactsDBcursor.close();
            db.close();
            return true;
        } else  {
            contactsDBcursor.close();
            db.close();
            return false;
        }
    }

    public Cursor getCallListCursor() {
        openReadableDatabase();

        try {
            return readableDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact ", ex.getMessage());
        }

        return null;

    }

    public String getContactNumber(String strippedNumber) {
        openReadableDatabase();

        try {
            Cursor cursor = readableDatabase.query(TABLE_NAME, new String[]{COL2}, COL1 + " = ?", new String[]{strippedNumber}, null, null, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COL2));
        } catch (Exception ex) {
            Log.e("Error on contact ", ex.getMessage());
        }

        return null;

    }

    public String getContactName(String strippedNumber) {
        openReadableDatabase();

        try {
            Cursor cursor = readableDatabase.query(TABLE_NAME, new String[]{COL8}, COL1 + " = ?", new String[]{strippedNumber}, null, null, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COL8));
        } catch (Exception ex) {
            Log.e("Error on contact ", ex.getMessage());
        }

        return null;

    }

    public String getFrequencyDisplay(String strippedNumber) {
        openReadableDatabase();

        try {
            Cursor cursor = readableDatabase.query(TABLE_NAME, new String[]{COL12}, COL1 + " = ?", new String[]{strippedNumber}, null, null, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COL12));
        } catch (Exception ex) {
            Log.e("Error on contact ", ex.getMessage());
        }

        return null;

    }

}
