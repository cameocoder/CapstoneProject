package com.cameocoder.capstoneproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cameocoder.capstoneproject.R;
import com.cameocoder.capstoneproject.RetrofitRecollectInterface;
import com.cameocoder.capstoneproject.RetrofitRecollectService;
import com.cameocoder.capstoneproject.Utility;
import com.cameocoder.capstoneproject.data.WasteContract;
import com.cameocoder.capstoneproject.model.Event;
import com.cameocoder.capstoneproject.model.Place;
import com.cameocoder.capstoneproject.model.Places;
import com.cameocoder.capstoneproject.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WasteSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = WasteSyncAdapter.class.getSimpleName();

    private static final String ARG_SYNC_TYPE = "syncType";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_PLACE_ID = "placeId";

    private static final int PLACE = 111;
    private static final int SCHEDULE = 222;

    // Sync schedule every 6 hours (in seconds)
    private static final long SYNC_INTERVAL = TimeUnit.HOURS.toSeconds(6);
    private static final long SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public WasteSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final int syncType = extras.getInt(ARG_SYNC_TYPE, PLACE);
        if (syncType == PLACE) {
            final double latitude = extras.getDouble(ARG_LATITUDE, 0);
            final double longitude = extras.getDouble(ARG_LONGITUDE, 0);
            Log.d(TAG, "onPerformSync: PLACE " + latitude + "," + longitude);
            getPlace(latitude, longitude);
        } else if (syncType == SCHEDULE) {
            final String placeId = extras.getString(ARG_PLACE_ID, "");
            Log.d(TAG, "onPerformSync: SCHEDULE " + placeId);
            if (!TextUtils.isEmpty(placeId)) {
                getSchedule(placeId);
            }

        }
    }

    private void getPlace(double latitude, double longitude) {
        RetrofitRecollectInterface retrofitRecollectInterface = RetrofitRecollectService.createRecollectService();

        Call<Places> place = retrofitRecollectInterface.getPlace(latitude, longitude);
        place.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(Call<Places> call, Response<Places> response) {
                if (response != null && response.body() != null) {
                    final Place place = response.body().getPlace();
                    if (place != null) {
                        final String id = place.getId();
                        Log.d(TAG, "onResponse: id = " + id);
                        savePlaceIdToPreferences(getContext(), id);
                    }
                }
            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(TAG, "Unable to parse response: " + t.getMessage());
                }
            }
        });
    }

    private void getSchedule(String placeId) {
        RetrofitRecollectInterface retrofitRecollectInterface = RetrofitRecollectService.createRecollectService();

        Call<Schedule> schedule = retrofitRecollectInterface.getSchedule(placeId);
        schedule.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "onResponse: ");
                if (response != null && response.body() != null) {
                    addEvents(response.body().getEvents());
                }

            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(TAG, "Unable to parse response: " + t.getMessage());
                }
            }
        });

    }

    private void addEvents(List<Event> events) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(WasteContract.EventEntry.COLUMN_ID, event.getId());
            contentValue.put(WasteContract.EventEntry.COLUMN_DAY, event.getDay());
            contentValue.put(WasteContract.EventEntry.COLUMN_ZONE_ID, event.getZoneId());
            contentValues.add(contentValue);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        contentValues.toArray(contentValuesArray);

        int itemsAdded = getContext().getContentResolver().bulkInsert(WasteContract.EventEntry.CONTENT_URI, contentValuesArray);
        Log.d(TAG, itemsAdded + "/" + contentValuesArray.length + " events added to database");

    }

    private static void savePlaceIdToPreferences(Context context, String id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Utility.PREF_PLACE_ID, id).apply();
    }

    private static String getPlaceIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Utility.PREF_PLACE_ID, "");
    }


    /**
     * Helper method to have the sync adapter sync schedule immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncPlace(Context context, double latitude, double longitude) {
        Bundle bundle = new Bundle(3);
        bundle.putInt(ARG_SYNC_TYPE, PLACE);
        bundle.putDouble(ARG_LATITUDE, latitude);
        bundle.putDouble(ARG_LONGITUDE, longitude);
        Log.d(TAG, "syncPlace: " + latitude + "," + longitude);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter sync schedule immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncSchedule(Context context, String placeId) {
        Bundle bundle = new Bundle(3);
        bundle.putInt(ARG_SYNC_TYPE, SCHEDULE);
        bundle.putString(ARG_PLACE_ID, placeId);
        Log.d(TAG, "syncSchedule: " + placeId);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle bundle = new Bundle(2);
            bundle.putInt(ARG_SYNC_TYPE, SCHEDULE);
            bundle.putString(ARG_PLACE_ID, getPlaceIdFromPreferences(context));

            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(bundle).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        WasteSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncSchedule(context, getPlaceIdFromPreferences(context));
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
