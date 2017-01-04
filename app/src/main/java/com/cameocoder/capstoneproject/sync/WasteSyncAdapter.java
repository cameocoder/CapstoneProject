package com.cameocoder.capstoneproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.cameocoder.capstoneproject.MainActivity;
import com.cameocoder.capstoneproject.R;
import com.cameocoder.capstoneproject.RetrofitRecollectInterface;
import com.cameocoder.capstoneproject.RetrofitRecollectService;
import com.cameocoder.capstoneproject.Utility;
import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;
import com.cameocoder.capstoneproject.model.Event;
import com.cameocoder.capstoneproject.model.Flag;
import com.cameocoder.capstoneproject.model.Place;
import com.cameocoder.capstoneproject.model.Places;
import com.cameocoder.capstoneproject.model.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WasteSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = WasteSyncAdapter.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED =
            "com.cameocoder.capstoneproject.app.ACTION_DATA_UPDATED";

    private static final String ARG_SYNC_TYPE = "syncType";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_PLACE_ID = "placeId";
    private static final String ARG_ZONE_NAME = "zoneName";

    private static final int PLACE = 111;
    private static final int SCHEDULE = 222;
    private static final int PICKUPDAYS = 333;

    private static final int NOTIFICATION_ID = 3004;

    // Sync schedule every 6 hours (in seconds)
    private static final long SYNC_INTERVAL = TimeUnit.HOURS.toSeconds(6);
    private static final long SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public static final String[] SCHEDULE_COLUMNS = {
            EventEntry._ID,
            EventEntry.COLUMN_ZONE_ID,
            EventEntry.COLUMN_DAY,
            EventEntry.COLUMN_BLACK_BIN,
            EventEntry.COLUMN_BLUE_BIN,
            EventEntry.COLUMN_GARBAGE,
            EventEntry.COLUMN_GREEN_BIN,
            EventEntry.COLUMN_YARD_WASTE
    };

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
        } else if (syncType == PICKUPDAYS) {
            final String zoneName = extras.getString(ARG_ZONE_NAME, "");
            Log.d(TAG, "onPerformSync: PICKUPDAYS " + zoneName);
            if (!TextUtils.isEmpty(zoneName)) {
                getPickUpDays(zoneName);
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
                        final String placeId = place.getId();
                        Log.d(TAG, "onResponse: placeId = " + placeId);
                        if (!TextUtils.isEmpty(placeId)) {
                            Utility.savePlaceIdToPreferences(getContext(), placeId);
                            // Now that we have the placeId we can get the schedule
                            getSchedule(placeId);
                        }
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

        Call<Schedule> schedule = retrofitRecollectInterface.getScheduleFromPlace(placeId);
        schedule.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "onResponse: ");
                if (response != null && response.body() != null) {
                    addEvents(response.body().getEvents());
                    if (!response.body().getZones().isEmpty()) {
                        // Get the zoneId from the first entry
                        final int zoneId = response.body().getZones().entrySet().iterator().next().getValue().getId();
                        final String zoneName = response.body().getZones().entrySet().iterator().next().getValue().getName();
                        Log.d(TAG, "onResponse: zoneId = " + zoneId + " zoneName = " + zoneName);
                        if (!TextUtils.isEmpty(zoneName)) {
                            Utility.saveZoneNameToPreferences(getContext(), zoneName);
                            Utility.saveZoneIdToPreferences(getContext(), zoneId);
                            // now that we have a zoneName we can ge the extended schedule
                            getPickUpDays(zoneName);
                        }
                    }
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

    private void getPickUpDays(String zoneName) {
        RetrofitRecollectInterface retrofitRecollectInterface = RetrofitRecollectService.createRecollectService();

        Call<Schedule> schedule = retrofitRecollectInterface.getScheduleFromZone(zoneName);
        schedule.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "onResponse: ");
                if (response != null && response.body() != null) {
                    addEvents(mergeEvents(response.body().getEvents()));
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

    private List<Event> mergeEvents(List<Event> events) {
        Map<String, Event> mergedEvents = new HashMap<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (mergedEvents.containsKey(event.getDay())) {
                for (int j = 0; j < event.getFlags().size(); j++) {
                    Flag flag = event.getFlags().get(j);
                    mergedEvents.get(event.getDay()).getFlags().add(flag);
                }
            } else {
                mergedEvents.put(event.getDay(), event);
            }
        }

        return new ArrayList<>(mergedEvents.values());
    }

    private void addEvents(List<Event> events) {
        final long currentTimeMillis = System.currentTimeMillis();
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            final long eventDateMillis = Utility.datetoMillis(event.getDay());
            if (eventDateMillis < currentTimeMillis) {
                // Don't add schedule items in the past
                continue;
            }
            ContentValues contentValue = new ContentValues();
            contentValue.put(EventEntry.COLUMN_ID, event.getId());
            contentValue.put(EventEntry.COLUMN_DAY, event.getDay());
            contentValue.put(EventEntry.COLUMN_ZONE_ID, event.getZoneId());
            contentValue.put(EventEntry.COLUMN_BLACK_BIN, event.isBlackBoxDay());
            contentValue.put(EventEntry.COLUMN_BLUE_BIN, event.isBlueBoxDay());
            contentValue.put(EventEntry.COLUMN_GARBAGE, event.isGarbageDay());
            contentValue.put(EventEntry.COLUMN_GREEN_BIN, event.isGreenBinDay());
            contentValue.put(EventEntry.COLUMN_YARD_WASTE, event.isYardWasteDay());
            contentValues.add(contentValue);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        contentValues.toArray(contentValuesArray);

        int itemsAdded = getContext().getContentResolver().bulkInsert(EventEntry.CONTENT_URI, contentValuesArray);
        Log.d(TAG, itemsAdded + "/" + contentValuesArray.length + " events added to database");

        updateWidgets();
        notifyNextPickup();
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    private void notifyNextPickup() {
        Context context = getContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = Utility.PREF_ENABLE_NOTIFICATIONS;
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey, true);

        if (displayNotifications) {

            String currentDay = Utility.millisToDateString(System.currentTimeMillis());
            int zoneId = Utility.getZoneIdFromPreferences(context);
            if (zoneId == 0) {
                return;
            }
            String select = "((" + EventEntry.COLUMN_ZONE_ID + " = " + zoneId + ") AND (" + EventEntry.COLUMN_DAY + " > " + currentDay + "))";
            String cursorSortOrder = EventEntry.COLUMN_DAY + " ASC";

            Cursor cursor = context.getContentResolver().query(EventEntry.CONTENT_URI, SCHEDULE_COLUMNS, select,
                    null, cursorSortOrder);

            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return;
            }


            final String day = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_DAY));
            long nextPickupDayMillis = Utility.datetoMillis(day);

            final boolean isBlackBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLACK_BIN)) > 0;
            final boolean isBlueBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLUE_BIN)) > 0;
            final boolean isGarbageDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GARBAGE)) > 0;
            final boolean isGreenBinDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GREEN_BIN)) > 0;
            final boolean isYardWasteDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_YARD_WASTE)) > 0;

            cursor.close();

            Resources resources = context.getResources();
            String title = context.getString(R.string.app_name);
            String contentText = Utility.getNotificationBody(context, isBlackBoxDay, isBlueBoxDay, isGarbageDay, isGreenBinDay, isYardWasteDay);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
//                                .setColor(resources.getColor(R.color.primary_light))
                            .setSmallIcon(R.drawable.ic_trash_black_24dp)
                            .setContentTitle(title)
                            .setContentText(contentText);

            Intent resultIntent = new Intent(context, MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
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
        Bundle bundle = new Bundle(2);
        bundle.putInt(ARG_SYNC_TYPE, SCHEDULE);
        bundle.putString(ARG_PLACE_ID, placeId);
        Log.d(TAG, "syncSchedule: " + placeId);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter sync extended schedule immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncPickUpDays(Context context, String zoneName) {
        Bundle bundle = new Bundle(2);
        bundle.putInt(ARG_SYNC_TYPE, PICKUPDAYS);
        bundle.putString(ARG_ZONE_NAME, zoneName);
        Log.d(TAG, "syncPickUpDays: " + zoneName);
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
            bundle.putString(ARG_PLACE_ID, Utility.getPlaceIdFromPreferences(context));

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
        syncSchedule(context, Utility.getPlaceIdFromPreferences(context));
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
