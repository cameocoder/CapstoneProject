package com.cameocoder.capstoneproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestDb {

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(WasteDbHelper.DATABASE_NAME);
    }

    @Test
    public void testCreateDb() throws Throwable {
        Context context = InstrumentationRegistry.getTargetContext();

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WasteContract.EventEntry.TABLE_NAME);

        SQLiteDatabase db = new WasteDbHelper(context).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: database was created without all expected tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + WasteContract.EventEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> eventColumnHashSet = new HashSet<String>();
        eventColumnHashSet.add(WasteContract.EventEntry._ID);
        eventColumnHashSet.add(WasteContract.EventEntry.COLUMN_ID);
        eventColumnHashSet.add(WasteContract.EventEntry.COLUMN_DAY);
        eventColumnHashSet.add(WasteContract.EventEntry.COLUMN_ZONE_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            eventColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: database doesn't contain all of the required event columns",
                eventColumnHashSet.isEmpty());
        c.close();
        db.close();
    }

    @Test
    public void testInsert() {
        Context context = InstrumentationRegistry.getTargetContext();
        WasteDbHelper dbHelper = new WasteDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(WasteContract.EventEntry.COLUMN_ID, "487718");
        testValues.put(WasteContract.EventEntry.COLUMN_DAY, "2016-12-23");
        testValues.put(WasteContract.EventEntry.COLUMN_ZONE_ID, "1177");

        long locationRowId;
        locationRowId = db.insert(WasteContract.EventEntry.TABLE_NAME, null, testValues);

        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(
                WasteContract.EventEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue( "Error: No Records returned from event query", cursor.moveToFirst() );


        validateCurrentRecord("Error: Event Query Validation Failed", cursor, testValues);

        assertFalse( "Error: More than one record returned from location query", cursor.moveToNext() );

        cursor.close();
        db.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
