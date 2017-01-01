package com.cameocoder.capstoneproject;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.capstoneproject.data.WasteContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleItemHolder> {

    private Activity activity;
    private Cursor cursor;


    public ScheduleAdapter(Activity activity) {
        this.activity = activity;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();

    }

    @Override
    public ScheduleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.schedule_item, parent, false);
        return new ScheduleItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ScheduleItemHolder holder, int position) {
        cursor.moveToPosition(position);
        final String date = cursor.getString(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_DAY));
        final boolean isBlackBoxDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_BLACK_BIN)) > 0;
        final boolean isBlueBoxDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_BLUE_BIN)) > 0;
        final boolean isGarbageDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_GARBAGE)) > 0;
        final boolean isGreenBinDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_GREEN_BIN)) > 0;
        final boolean isYardWasteDay = cursor.getInt(cursor.getColumnIndex(WasteContract.EventEntry.COLUMN_YARD_WASTE)) > 0;
        holder.date.setText(date);
        holder.blackBin.setVisibility(isBlackBoxDay ? View.VISIBLE : View.GONE);
        holder.blueBin.setVisibility(isBlueBoxDay ? View.VISIBLE : View.GONE);
        holder.garbage.setVisibility(isGarbageDay ? View.VISIBLE : View.GONE);
        holder.greenBin.setVisibility(isGreenBinDay ? View.VISIBLE : View.GONE);
        holder.yardWaste.setVisibility(isYardWasteDay ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        if ( null == cursor ) {
            return 0;
        }
        return cursor.getCount();
    }

    public class ScheduleItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.black_bin)
        TextView blackBin;
        @BindView(R.id.blue_bin)
        TextView blueBin;
        @BindView(R.id.garbage)
        TextView garbage;
        @BindView(R.id.green_bin)
        TextView greenBin;
        @BindView(R.id.yard_waste)
        TextView yardWaste;

        public ScheduleItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
