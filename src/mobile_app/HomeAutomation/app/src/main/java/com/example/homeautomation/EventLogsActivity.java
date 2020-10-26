package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Date;

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

    ArrayList<ButtonEventLog> logsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_logs);

        listView = findViewById(R.id.listLogs);
        txtHeader = findViewById(R.id.txtDeviceId);
        txtLoadStatus = findViewById(R.id.txtLoadLogs);
        layoutLoading = findViewById(R.id.layoutLoadingLogs);
        showStatus("Fetching cloud data...");
        checkDeviceId();
        logsList =  new ArrayList<>();
    }

    private void checkDeviceId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String d = settings.getString(getResources().getString(R.string.device_id_key), "");
        if (d.equals("")) {
            //Go to setup activity
            goToSwitchDevice();
        } else {
            DEVICE_ID = d;
            txtHeader.setText(d+" Event Logs");
            connectToCloud();
        }
    }

    private void refreshList(){
        EventLogListAdapter adapter = new EventLogListAdapter(this, logsList.toArray(new ButtonEventLog[0]));
        listView.setAdapter(adapter);
    }

    private void goToSwitchDevice() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void connectToCloud() {
        database = FirebaseDatabase.getInstance();
        logsdDbRef = database.getReference("event_logs").child(DEVICE_ID).child("events");
        logsdDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                } else {

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        System.out.println(postSnapshot);
                    }
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

    private void showStatus(String loadingText ){
        layoutLoading.setVisibility(View.VISIBLE);
        txtLoadStatus.setText(loadingText);
    }

    private void hideStatus() {
        layoutLoading.setVisibility(View.GONE);
    }
}