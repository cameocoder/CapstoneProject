package com.cameocoder.capstoneproject;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.capstoneproject.data.WasteContract.EventEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleItemHolder> {

    private static final int VIEW_TYPE_NEXT = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private Activity activity;
    private Cursor cursor;
    private View emptyView;


    public ScheduleAdapter(Activity activity, View emptyView) {
        this.activity = activity;
        this.emptyView = emptyView;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public ScheduleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_NEXT: {
                    layoutId = R.layout.list_item_schedule_next;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_schedule;
                    break;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ScheduleItemHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ScheduleItemHolder holder, int position) {
        cursor.moveToPosition(position);
        final String day = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_DAY));
        long currentDayMillis = Utility.datetoMillis(day);
        String friendlyDate;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_NEXT:
                friendlyDate = Utility.millisToNextDateString(currentDayMillis);
                break;
            default:
                friendlyDate = Utility.millisToLongDateString(currentDayMillis);
        }

        final boolean isBlackBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLACK_BIN)) > 0;
        final boolean isBlueBoxDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_BLUE_BIN)) > 0;
        final boolean isGarbageDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GARBAGE)) > 0;
        final boolean isGreenBinDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_GREEN_BIN)) > 0;
        final boolean isYardWasteDay = cursor.getInt(cursor.getColumnIndex(EventEntry.COLUMN_YARD_WASTE)) > 0;
        holder.date.setText(friendlyDate);
        holder.blackBin.setVisibility(isBlackBoxDay ? View.VISIBLE : View.GONE);
        holder.blueBin.setVisibility(isBlueBoxDay ? View.VISIBLE : View.GONE);
        holder.garbage.setVisibility(isGarbageDay ? View.VISIBLE : View.GONE);
        holder.greenBin.setVisibility(isGreenBinDay ? View.VISIBLE : View.GONE);
        holder.yardWaste.setVisibility(isYardWasteDay ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_NEXT : VIEW_TYPE_FUTURE_DAY;
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
