package com.cameocoder.capstoneproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cameocoder.capstoneproject.data.WasteContract;

import java.util.concurrent.TimeUnit;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * This class will determine if a notification can be raised and raise the notification if needed.
 */
public class NotificationService extends IntentService {

    public static final String NOTIFICATION_CHANNEL_ID = "com.cameocoder.capstoneproject.PickupReminder";

    private static final String TAG = NotificationService.class.getSimpleName();

    private static final String RAISE_NOTIFICATION = "com.cameocoder.capstoneproject.action.RAISE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 613;

    public static final String[] SCHEDULE_COLUMNS = {
            WasteContract.EventEntry._ID,
            WasteContract.EventEntry.COLUMN_ZONE_ID,
            WasteContract.EventEntry.COLUMN_DAY,
            WasteContract.EventEntry.COLUMN_BLACK_BIN,
            WasteContract.EventEntry.COLUMN_BLUE_BIN,
            WasteContract.EventEntry.COLUMN_GARBAGE,
            WasteContract.EventEntry.COLUMN_GREEN_BIN,
            WasteContract.EventEntry.COLUMN_YARD_WASTE
    };
    public static final int NOTIFICATION_PERIOD_HOURS = 12;

    public NotificationService() {
        super("NotificationService");
    }

    /**
     * Starts this service to perform action RaiseNotification with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRaiseNotification(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(RAISE_NOTIFICATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (RAISE_NOTIFICATION.equals(action)) {
                handleActionRaiseNotification();
            }
        }
    }

    /**
     * Handle action RaiseNotification in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRaiseNotification() {
        notifyNextPickup();
    }

    private void notifyNextPickup() {
        Context context = getApplicationContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = Utility.PREF_ENABLE_NOTIFICATIONS;
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey, true);

        if (displayNotifications) {

            String currentDay = Utility.millisToDateString(System.currentTimeMillis());
            int zoneId = Utility.getZoneIdFromPreferences(context);
            if (zoneId == 0) {
                return;
            }
            String select = "((" + WasteContract.EventEntry.COLUMN_ZONE_ID + " = " + zoneId + ") AND (" + WasteContract.EventEntry.COLUMN_DAY + " > " + currentDay + "))";
            String cursorSortOrder = WasteContract.EventEntry.COLUMN_DAY + " ASC";

            Cursor cursor = context.getContentResolver().query(WasteContract.EventEntry.CONTENT_URI, SCHEDULE_COLUMNS, select,
                    null, cursorSortOrder);

            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return;
            }


            final String day = cursor.getString(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_DAY));
            long nextPickupDayMillis = Utility.datetoMillis(day);

            final boolean isBlackBoxDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_BLACK_BIN)) > 0;
            final boolean isBlueBoxDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_BLUE_BIN)) > 0;
            final boolean isGarbageDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_GARBAGE)) > 0;
            final boolean isGreenBinDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_GREEN_BIN)) > 0;
            final boolean isYardWasteDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_YARD_WASTE)) > 0;

            cursor.close();

            Notification notification = getNotification(context, isBlackBoxDay, isBlueBoxDay, isGarbageDay, isGreenBinDay, isYardWasteDay);

            scheduleNotification(notification, currentDay, nextPickupDayMillis);
        }
    }

    private void scheduleNotification(Notification notification, String day, long nextPickupTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        Context context = getApplicationContext();

        // Don't rise notification if setting turned off
        if (!Utility.showNotifications(context)) {
            return;
        }

        Log.d(TAG, "scheduleNotification: next " + Utility.millisToDateString(nextPickupTimeMillis) + " current " + Utility.millisToDateString(currentTimeMillis));
        final long notificationPeriodMillis = TimeUnit.HOURS.toMillis(NOTIFICATION_PERIOD_HOURS);
        Log.d(TAG, "scheduleNotification: (nextPickupTimeMillis - currentTimeMillis)" + (nextPickupTimeMillis - currentTimeMillis) + " HOURS " + notificationPeriodMillis);
        // Raise notification if we are within 12 hours of the next pickup time
        if ((nextPickupTimeMillis - currentTimeMillis) < notificationPeriodMillis) {
            String lastNotificationDate = Utility.getNotificationDateFromPreferences(context);
            Log.d(TAG, "scheduleNotification: lastNotificationDate " + lastNotificationDate);
            Log.d(TAG, "scheduleNotification: day " + day);
            // Don't raise the notification if we have already raised it
            if (!lastNotificationDate.equals(day)) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    Utility.saveNotificationDateToPreferences(context, day);
                }
            }
        }
    }

    @NonNull
    private Notification getNotification(Context context, boolean isBlackBoxDay, boolean isBlueBoxDay,
                                         boolean isGarbageDay, boolean isGreenBinDay, boolean isYardWasteDay) {
        String title = context.getString(R.string.next_pickup_tomorrow);
        String contentText = Utility.getNotificationBody(context, isBlackBoxDay, isBlueBoxDay, isGarbageDay, isGreenBinDay, isYardWasteDay);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification_trash)
                        .setColor(ContextCompat.getColor(context, R.color.notification_bg))
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setPriority(PRIORITY_HIGH)
                        .setCategory(Notification.CATEGORY_REMINDER)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }

}
