package com.cameocoder.capstoneproject;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    protected static final String TAG = MainActivityFragment.class.getSimpleName();

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

    public static final int SCHEDULE_LOADER = 0;

    @BindView(R.id.schedule_label)
    TextView scheduleLabel;

    @BindView(R.id.schedule_list)
    RecyclerView scheduleList;

    @BindView(R.id.schedule_empty)
    TextView scheduleEmpty;

    private String zoneName;
    private int zoneId;
    private long currentTimeMillis;

    ScheduleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTimeMillis = System.currentTimeMillis();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ScheduleAdapter(getActivity(), scheduleEmpty);
        scheduleLabel.setVisibility(BuildConfig.DEBUG ? VISIBLE : GONE);
        scheduleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        scheduleList.setAdapter(adapter);
        updateZone();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = EventEntry.CONTENT_URI;
        String[] columns = SCHEDULE_COLUMNS;

        String currentDay = Utility.millisToDateString(currentTimeMillis);
        int zoneId = Utility.getZoneIdFromPreferences(getContext());
        Log.d(TAG, "onCreateLoader: zoneId " + zoneId);
        String select = "((" + EventEntry.COLUMN_ZONE_ID + " = " + zoneId + ") AND (" + EventEntry.COLUMN_DAY + " >= " + currentDay + "))";
        Log.d(TAG, "onCreateLoader: select " + select);
        String cursorSortOrder = EventEntry.COLUMN_DAY + " ASC";
        return new CursorLoader(getActivity(),
                uri,
                columns,
                null,
                null,
                cursorSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            Log.d(TAG, "onLoadFinished: " + data.getCount());
        }
        adapter.swapCursor(data);
        updateZone();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void updateZone() {
        zoneName = Utility.getZoneNameFromPreferences(getContext());
        zoneId = Utility.getZoneIdFromPreferences(getContext());
        scheduleLabel.setText(zoneName);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Utility.PREF_ZONE_NAME)) {
            updateZone();
            Log.d(TAG, "onSharedPreferenceChanged: ");
            // When the location changes we need to restart the loader with updated information
            getLoaderManager().restartLoader(SCHEDULE_LOADER, null, this);
        }
    }
}
