package com.example.homeautomation.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.homeautomation.R;
import com.example.homeautomation.classes.ButtonEventLog;

public class EventLogListAdapter extends ArrayAdapter<ButtonEventLog> {
    private final Context context;
    private final ButtonEventLog[] values;

    public EventLogListAdapter(Context context, ButtonEventLog[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_event_log_item, parent, false);
        TextView txtDate = rowView.findViewById(R.id.txtLogDate);
        TextView txtLog = rowView.findViewById(R.id.txtLogInfo);
        ImageView imageView = rowView.findViewById(R.id.imgLogStatus);
        txtDate.setText(values[position].getDateString());
        txtLog.setText(values[position].getLogInfo());

        imageView.setImageResource(values[position].status?R.drawable.ic_log_on:R.drawable.ic_log_off);
        return rowView;
    }
}
