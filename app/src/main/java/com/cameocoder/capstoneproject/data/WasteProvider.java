package com.cameocoder.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public class WasteProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private WasteDbHelper mOpenHelper;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WasteContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
//        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
//        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
//        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITES);
//        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WasteDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
