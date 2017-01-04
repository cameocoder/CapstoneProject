package com.cameocoder.capstoneproject.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.cameocoder.capstoneproject.MainActivity;
import com.cameocoder.capstoneproject.R;
import com.cameocoder.capstoneproject.Utility;
import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WasteWidgetIntentService extends IntentService {

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

    public WasteWidgetIntentService() {
        super("WasteWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WasteWidgetProvider.class));

        String currentDay = Utility.millisToDateString(System.currentTimeMillis());
        int zoneId = Utility.getZoneIdFromPreferences(this);
        if (zoneId == 0) {
            return;
        }
        String select = "((" + EventEntry.COLUMN_ZONE_ID + " = " + zoneId + ") AND (" + EventEntry.COLUMN_DAY + " > " + currentDay + "))";
        String cursorSortOrder = EventEntry.COLUMN_DAY + " ASC";

        Cursor cursor = getContentResolver().query(EventEntry.CONTENT_URI, SCHEDULE_COLUMNS, select,
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
        String friendlyDate = Utility.millisToShortDateString(nextPickupDayMillis);

        final boolean isBlackBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLACK_BIN)) > 0;
        final boolean isBlueBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLUE_BIN)) > 0;
        final boolean isGarbageDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GARBAGE)) > 0;
        final boolean isGreenBinDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GREEN_BIN)) > 0;
        final boolean isYardWasteDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_YARD_WASTE)) > 0;

        // Create an Intent to launch MainActivity
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);

            int layoutId = R.layout.waste_widget;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setTextViewText(R.id.widget_date, friendlyDate);

            views.setViewVisibility(R.id.widget_black_bin, isBlackBoxDay ? VISIBLE : GONE);
            views.setViewVisibility(R.id.widget_blue_bin, isBlueBoxDay ? VISIBLE : GONE);
            views.setViewVisibility(R.id.widget_garbage, isGarbageDay ? VISIBLE : GONE);
            views.setViewVisibility(R.id.widget_green_bin, isGreenBinDay ? VISIBLE : GONE);
            views.setViewVisibility(R.id.widget_yard_waste, isYardWasteDay ? VISIBLE : GONE);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
    }

}
