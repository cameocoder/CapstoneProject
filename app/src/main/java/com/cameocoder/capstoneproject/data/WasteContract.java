package com.cameocoder.capstoneproject.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WasteContract {
    public static final String CONTENT_AUTHORITY = "com.cameocoder.capstoneproject.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EVENT = "events";

    public static final class EventEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        // Table name
        public static final String TABLE_NAME = "schedule";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_ZONE_ID = "zone_id";
        public static final String COLUMN_BLACK_BIN = "black_bin";
        public static final String COLUMN_BLUE_BIN = "blue_bin";
        public static final String COLUMN_GARBAGE = "garbage";
        public static final String COLUMN_GREEN_BIN = "green_bin";
        public static final String COLUMN_YARD_WASTE = "yard_waste";

        public static Uri buildEventWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getEventIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
