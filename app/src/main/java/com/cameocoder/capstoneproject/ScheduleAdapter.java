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
    Cursor cursor;

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
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }    }

    public class ScheduleItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date)
        TextView date;

        public ScheduleItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
