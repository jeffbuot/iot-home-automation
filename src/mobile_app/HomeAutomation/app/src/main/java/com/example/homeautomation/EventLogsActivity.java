package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homeautomation.classes.ButtonEventLog;
import com.example.homeautomation.customadapters.EventLogListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class EventLogsActivity extends AppCompatActivity {

    private String TAG = "EVENTLOGS_ACTIVITY";
    final String PREFS_NAME = "smarthome_settings";
    String DEVICE_ID = "";
    FirebaseDatabase database;
    DatabaseReference logsdDbRef;
    ConstraintLayout layoutLoading;
    TextView txtLoadStatus;

    ListView listView;
    TextView txtHeader;
    Button btnFilter;

    Date filterStart;
    Date filterEnd;

    ArrayList<ButtonEventLog> logsList;
    final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_logs);

        listView = findViewById(R.id.listLogs);
        txtHeader = findViewById(R.id.txtDeviceId);
        txtLoadStatus = findViewById(R.id.txtLoadLogs);
        layoutLoading = findViewById(R.id.layoutLoadingLogs);
        btnFilter = findViewById(R.id.btnLogFilter);
        setDateFilterToToday();
        final SlyCalendarDialog.Callback callback = new SlyCalendarDialog.Callback() {
            @Override
            public void onCancelled() {
            }

            @Override
            public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
                filterStart = firstDate.getTime();
                filterEnd = secondDate.getTime();
                refreshList();
            }
        };
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setStartDate(filterStart)
                        .setEndDate(filterEnd)
                        .setFirstMonday(false)
                        .setHeaderColor(Color.parseColor("#00574B"))
                        .setSelectedColor(Color.parseColor("#00574B"))
                        .setCallback(callback)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        showStatus("Fetching cloud data...");
        checkDeviceId();
        logsList = new ArrayList<>();
    }

    public void setDateFilterToToday() {
        try {
            filterStart = dateFormatter.parse(dateFormatter.format(new Date()));
            filterEnd = addHoursToDate(filterStart, 23, 59, 59);
        } catch (ParseException e) {
            Calendar cal = Calendar.getInstance();
            cal.clear(Calendar.HOUR_OF_DAY);
            cal.clear(Calendar.AM_PM);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            filterStart = cal.getTime();
            filterEnd = addHoursToDate(filterStart, 23, 59, 59);
        }
    }

    public Date addHoursToDate(Date date, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    private void checkDeviceId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String d = settings.getString(getResources().getString(R.string.device_id_key), "");
        if (d.equals("")) {
            //Go to setup activity
            goToSwitchDevice();
        } else {
            DEVICE_ID = d;
            txtHeader.setText(d + " Event Logs");
            connectToCloud();
        }
    }

    private boolean isTheSameDay(Date s, Date e) {
        try {
            s = dateFormatter.parse(dateFormatter.format(s));
            e = dateFormatter.parse(dateFormatter.format(e));
            return s.compareTo(e) == 0;
        } catch (Exception x) {
            return false;
        }
    }

    private void refreshList() {
        ArrayList<ButtonEventLog> l = new ArrayList<>();
        for (ButtonEventLog e : logsList) {
            if (e.date.after(filterStart) && e.date.before(filterEnd)) {
                l.add(e);
            }
        }
        String filterText = isTheSameDay(filterStart, filterEnd) ? "Today" : dateFormatter.format(filterStart) + "-" +
                dateFormatter.format(filterEnd);
        btnFilter.setText(filterText);
        EventLogListAdapter adapter = new EventLogListAdapter(this, l.toArray(new ButtonEventLog[0]));

        listView.setAdapter(adapter);
        scrollListViewToBottom(adapter.getCount() - 1);
    }

    private void goToSwitchDevice() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void connectToCloud() {
        showStatus("Connecting to cloud..");
        database = FirebaseDatabase.getInstance();
        logsdDbRef = database.getReference("event_logs").child(DEVICE_ID).child("events");

        showStatus("Fetching data..");
        logsdDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                } else {

//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        // TODO: handle the post
//                        System.out.println(postSnapshot);
//                    }
                    hideStatus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                hideStatus();
                // A new comment has been added, add it to the displayed list
                ButtonEventLog eventLog = dataSnapshot.getValue(ButtonEventLog.class);
                logsList.add(eventLog);
                refreshList();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getCurrentContext(), "Failed to load device event logs.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

        };
        logsdDbRef.addChildEventListener(childEventListener);

    }

    private EventLogsActivity getCurrentContext() {
        return this;
    }

    private void scrollListViewToBottom(final int position) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(position);
            }
        });
    }

    private void showStatus(String loadingText) {
        layoutLoading.setVisibility(View.VISIBLE);
        txtLoadStatus.setText(loadingText);
    }

    private void hideStatus() {
        layoutLoading.setVisibility(View.GONE);
    }
}