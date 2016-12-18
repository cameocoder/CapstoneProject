package com.cameocoder.capstoneproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.cameocoder.capstoneproject.R;
import com.cameocoder.capstoneproject.RetrofitRecollectInterface;
import com.cameocoder.capstoneproject.RetrofitRecollectService;
import com.cameocoder.capstoneproject.model.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WasteSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = WasteSyncAdapter.class.getSimpleName();

    private static final String ARG_SYNC_TYPE = "syncType";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private static final int PLACE = 111;
    private static final int SCHEDULE = 222;

    // Interval at which to sync movies, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public WasteSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final int syncType = extras.getInt(ARG_SYNC_TYPE, PLACE);
        Log.d(LOG_TAG, "onPerformSync: ");
        if (syncType == PLACE) {
            final double latitude = extras.getDouble(ARG_LATITUDE, 0);
            final double longitude = extras.getDouble(ARG_LONGITUDE, 0);
            Log.d(LOG_TAG, "onPerformSync: PLACE " + latitude + "," + longitude);

            getPlace(latitude, longitude);
        } else if (syncType == SCHEDULE) {
//            getSchedule();

        }
    }

    private void getPlace(double latitude, double longitude) {
        RetrofitRecollectInterface retrofitRecollectInterface = RetrofitRecollectService.createRecollectService();

        Call<Place> place = retrofitRecollectInterface.getPlace(latitude, longitude);
        place.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                if (response != null && response.body() != null) {
                    Log.d(LOG_TAG, "onResponse: " + response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(LOG_TAG, "Unable to parse response: " + t.getMessage());
                }
            }
        });
    }


    /**
     * Helper method to have the sync adapter sync movies immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncPlace(Context context, double latitude, double longitude) {
        Bundle bundle = new Bundle(3);
        bundle.putDouble(ARG_LATITUDE, latitude);
        bundle.putDouble(ARG_LONGITUDE, longitude);
        bundle.putInt(ARG_SYNC_TYPE, PLACE);
        Log.d(LOG_TAG, "syncPlace: " + latitude + "," + longitude);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
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
//        getSchedule();
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
