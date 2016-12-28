package com.cameocoder.capstoneproject;

import android.content.SharedPreferences;

public class Utility {

    public static final String PREF_LATITUDE = "pref_latitude";
    public static final String PREF_LONGITUDE = "pref_longitude";
    public static final String PREF_PLACE_ID = "pref_place_id";

    // http://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
