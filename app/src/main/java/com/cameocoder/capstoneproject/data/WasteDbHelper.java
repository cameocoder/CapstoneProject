package com.cameocoder.capstoneproject.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;

public class WasteDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    protected static final String DATABASE_NAME = "waste.db";

    public WasteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry._ID + " INTEGER PRIMARY KEY, " +
                EventEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_DAY + " TEXT, " +
                EventEntry.COLUMN_ZONE_ID + " TEXT, " +
                " UNIQUE (" + EventEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_EVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);

    }
}
