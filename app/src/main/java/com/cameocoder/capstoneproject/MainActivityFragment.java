package com.cameocoder.capstoneproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    protected static final String TAG = MainActivityFragment.class.getSimpleName();

    public static final String[] MOVIE_COLUMNS = {
            EventEntry._ID,
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

    ScheduleAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ScheduleAdapter(getActivity());
        scheduleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        scheduleList.setAdapter(adapter);
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
        String[] columns = MOVIE_COLUMNS;

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
        adapter.swapCursor(data);
        if (data != null && data.moveToFirst()) {
            scheduleEmpty.setVisibility(View.GONE);
            scheduleList.setVisibility(View.VISIBLE);
        } else {
            scheduleEmpty.setVisibility(View.VISIBLE);
            scheduleList.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        scheduleEmpty.setVisibility(View.VISIBLE);
        scheduleList.setVisibility(View.GONE);
    }
}
