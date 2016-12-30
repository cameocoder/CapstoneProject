package com.cameocoder.capstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static final String PREF_LATITUDE = "pref_latitude";
    public static final String PREF_LONGITUDE = "pref_longitude";
    public static final String PREF_PLACE_ID = "pref_place_id";

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    // http://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static long datetoMillis(String dateString) {
        DateFormat format = new SimpleDateFormat(DB_DATE_FORMAT, Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }

    @NonNull
    public static String getPlaceIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREF_PLACE_ID, "");
    }

    public static void savePlaceIdToPreferences(Context context, String id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREF_PLACE_ID, id).apply();
    }

    public static void saveLocationToPreferences(Context context, double latitude, double longitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        putDouble(editor, PREF_LATITUDE, Double.doubleToRawLongBits(latitude));
        putDouble(editor, PREF_LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.apply();
    }
}
