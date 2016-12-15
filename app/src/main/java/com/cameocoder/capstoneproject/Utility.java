package com.cameocoder.capstoneproject;

import android.content.SharedPreferences;

public class Utility {

    public static final String PREF_LATITUDE = "latitude";
    public static final String PREF_LONGITUDE = "latitude";

    // http://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
