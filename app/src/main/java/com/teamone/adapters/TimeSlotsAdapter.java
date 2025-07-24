package com.teamone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.teamone.auroraspa.R;

import java.util.List;

public class TimeSlotsAdapter extends BaseAdapter {
    private final List<String> timeslots;
    private final Context context;
    private final int layout;
    private int selectedPosition = -1;

    public TimeSlotsAdapter(Context context, int layout, List<String> timeslots) {
        this.context = context;
        this.layout = layout;
        this.timeslots = timeslots;
    }

    @Override
    public int getCount() {
        return timeslots.size();
    }

    @Override
    public Object getItem(int i) {
        return timeslots.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layout, parent, false);
            holder = new ViewHolder();
            holder.txtTimeSlot = view.findViewById(R.id.txtTimeSlot);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Binding text
        holder.txtTimeSlot.setText(timeslots.get(i));

        // Set background khi time được chọn
        if (i == selectedPosition) {
            holder.txtTimeSlot.setBackgroundResource(R.drawable.bg_orange_nostroke);
            holder.txtTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.txtTimeSlot.setBackgroundResource(R.drawable.bg_time_slot);
            holder.txtTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }

        return view;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class ViewHolder {
        TextView txtTimeSlot;
    }
}
