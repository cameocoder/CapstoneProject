package com.cameocoder.capstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    public static final String PREF_ZONE_ID = "pref_zone_id";
    public static final String PREF_ZONE_NAME = "pref_zone_name";
    public static final String PREF_ENABLE_NOTIFICATIONS = "pref_enable_notifications";
    public static final String PREF_NOTIFICATION_DAY = "notification_day";

    public static final String DB_DATE_FORMAT = "yyyyMMdd";
    public static final String NEXT_DATE_FORMAT = "EEEE, MMMM dd";
    public static final String LONG_DATE_FORMAT = "EEEE, MMMM dd, yyyy";
    public static final String SHORT_DATE_FORMAT = "EEEE";

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

    public static String millisToDateString(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT, Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String millisToNextDateString(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(NEXT_DATE_FORMAT, Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String millisToLongDateString(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(LONG_DATE_FORMAT, Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String millisToShortDateString(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(SHORT_DATE_FORMAT, Locale.ENGLISH);
        return formatter.format(date);
    }

    public static boolean showNotifications(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_show_notifications_key), true);
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

    public static int getZoneIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(PREF_ZONE_ID, 0);
    }

    public static void saveZoneIdToPreferences(Context context, int zoneId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(PREF_ZONE_ID, zoneId).apply();
    }

    @NonNull
    public static String getZoneNameFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREF_ZONE_NAME, "");
    }
    public static void saveZoneNameToPreferences(Context context, String zoneName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREF_ZONE_NAME, zoneName).apply();
    }

    public static void saveLocationToPreferences(Context context, double latitude, double longitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        putDouble(editor, PREF_LATITUDE, Double.doubleToRawLongBits(latitude));
        putDouble(editor, PREF_LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.apply();
    }

    @NonNull
    public static String getNotificationDateFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREF_NOTIFICATION_DAY, "");
    }

    public static void saveNotificationDateToPreferences(Context context, String day) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREF_NOTIFICATION_DAY, day).apply();
    }

    @NonNull
    public static String getNotificationBody(Context context, boolean isBlackBoxDay,
                                             boolean isBlueBoxDay, boolean isGarbageDay,
                                             boolean isGreenBinDay, boolean isYardWasteDay) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isBlackBoxDay) {
            stringBuilder.append(context.getString(R.string.black_box));
            stringBuilder.append(", ");
        }
        if (isBlueBoxDay) {
            stringBuilder.append(context.getString(R.string.blue_box));
            stringBuilder.append(", ");
        }
        if (isGarbageDay) {
            stringBuilder.append(context.getString(R.string.garbage));
            stringBuilder.append(", ");
        }
        if (isGreenBinDay) {
            stringBuilder.append(context.getString(R.string.green_bin));
        }
        if (isYardWasteDay) {
            stringBuilder.append(" ").append(context.getString(R.string.and)).append(" ");
            stringBuilder.append(context.getString(R.string.yard_waste));
        }

        return stringBuilder.toString();
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
