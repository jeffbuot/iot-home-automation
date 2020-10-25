package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.homeautomation.classes.ButtonEventLog;
import com.example.homeautomation.classes.Switch;
import com.example.homeautomation.customadapters.SwitchListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // GestureDetector gestureDetector;
    //ListView listView;]
    private String TAG = "MAIN_ACTIVITY";
    final String PREFS_NAME = "smarthome_settings";
    String DEVICE_ID = "";
    String USERNAME = "";
    SwitchListAdapter switchListAdapter;
    // final String[] channelstring = new String[]{"Channel A", "Channel B", "Channel C", "Channel D", "Channel E", "Channel F"};
    FirebaseDatabase database;
    DatabaseReference switchesDbRef;
    DatabaseReference logsdDbRef;
    ToggleButton toggle_a;
    ToggleButton toggle_b;
    ToggleButton toggle_c;
    ToggleButton toggle_d;
    ToggleButton toggle_e;
    ToggleButton toggle_f;
    MediaPlayer onSound;
    MediaPlayer offSound;
    Button btnSwitchDevice;
    Button btnEventLogs;
    TextView txtDeviceId;
    TextView txtA;
    TextView txtB;
    TextView txtC;
    TextView txtD;
    TextView txtE;
    TextView txtF;
    Switch switches;
    CompoundButton.OnCheckedChangeListener buttonEvent;
    //boolean isLoading = true;
    ConstraintLayout layoutLoading;
    //    ConstraintLayout layoutSwitchA;
//    ConstraintLayout layoutSwitchB;
//    ConstraintLayout layoutSwitchC;
//    ConstraintLayout layoutLabel1;
//    ConstraintLayout layoutLabel2;
    ScrollView scrollView;
    TextView txtLoading;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home Automation Switches App");
        setContentView(R.layout.activity_main);
        //checks if there are saved devicdId
        // initialize the Gesture Detector
        // initDatabase();

        txtA = findViewById(R.id.textViewa);
        txtB = findViewById(R.id.textViewb);
        txtC = findViewById(R.id.textViewc);
        txtD = findViewById(R.id.textViewd);
        txtE = findViewById(R.id.textViewe);
        txtF = findViewById(R.id.textViewf);
        btnSwitchDevice = findViewById(R.id.btnSwitchDevice);
        btnEventLogs = findViewById(R.id.btnLogs);

        txtDeviceId = findViewById(R.id.txtDeviceId);

        scrollView = findViewById(R.id.scrollLayoutButtons);
        layoutLoading = findViewById(R.id.layoutLoading);
        txtLoading = findViewById(R.id.txtLoad);
        progressBar = findViewById(R.id.progressBar);
        USERNAME = (Build.BRAND + "_" + Build.DEVICE + "_" + android.os.Build.MODEL).toUpperCase();
        hideSwitches();
        showStatus("Connecting to cloud..", true, false);
        checkDeviceId();

        btnSwitchDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSwitchDevice();
            }
        });

        btnEventLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSEventLogsActivity();
            }
        });
        onSound = MediaPlayer.create(this, R.raw.on_sound);
        offSound = MediaPlayer.create(this, R.raw.off_sound);

        buttonEvent = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String buttonName = "";
                if (((CompoundButton) toggle_a).equals(buttonView)) {
                    switches.setA(isChecked);
                    buttonName = "switch_a";
                } else if (((CompoundButton) toggle_b).equals(buttonView)) {
                    switches.setB(isChecked);
                    buttonName = "switch_b";
                } else if (((CompoundButton) toggle_c).equals(buttonView)) {
                    switches.setC(isChecked);
                    buttonName = "switch_c";
                } else if (((CompoundButton) toggle_d).equals(buttonView)) {
                    switches.setD(isChecked);
                    buttonName = "switch_d";
                } else if (((CompoundButton) toggle_e).equals(buttonView)) {
                    switches.setE(isChecked);
                    buttonName = "switch_e";
                } else if (((CompoundButton) toggle_f).equals(buttonView)) {
                    switches.setF(isChecked);
                    buttonName = "switch_f";
                }
                if (isChecked) {
                    onSound.start();
                } else {
                    offSound.start();
                }
                try {
                    switchesDbRef.setValue(switches);

                    String key = logsdDbRef.push().getKey();
                    ButtonEventLog log = new ButtonEventLog(key, USERNAME, new Date(), isChecked, buttonName);
                    Map<String, Object> logValues = log.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/events/" + key, logValues);
                    logsdDbRef.updateChildren(childUpdates);
                } catch (Exception e) {
                    Toast.makeText(getCurrentContext(), "A problem occured from cloud connection", Toast.LENGTH_SHORT).show();
                }
                setCustomDrawable(buttonView, isChecked);
            }
        };
        toggle_a = findViewById(R.id.toggle_a);
        toggle_a.setOnCheckedChangeListener(buttonEvent);
        toggle_b = findViewById(R.id.toggle_b);
        toggle_b.setOnCheckedChangeListener(buttonEvent);
        toggle_c = findViewById(R.id.toggle_c);
        toggle_c.setOnCheckedChangeListener(buttonEvent);
        toggle_d = findViewById(R.id.toggle_d);
        toggle_d.setOnCheckedChangeListener(buttonEvent);
        toggle_e = findViewById(R.id.toggle_e);
        toggle_e.setOnCheckedChangeListener(buttonEvent);
        toggle_f = findViewById(R.id.toggle_f);
        toggle_f.setOnCheckedChangeListener(buttonEvent);
    }

    private void goToSEventLogsActivity() {
        Intent intent = new Intent(this, EventLogsActivity.class);
        startActivity(intent);
    }

    private void checkDeviceId() {
        //  SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String d = settings.getString(getResources().getString(R.string.device_id_key), "");
        if (d.equals("")) {
            //Go to setup activity
            goToSwitchDevice();
        } else {
            DEVICE_ID = d;
            txtDeviceId.setText(d);
            connectToCloud();
        }
    }

    private void showSwitches() {
        scrollView.setVisibility(View.VISIBLE);
//        layoutSwitchA.setVisibility(View.VISIBLE);
//        layoutSwitchB.setVisibility(View.VISIBLE);
//        layoutSwitchC.setVisibility(View.VISIBLE);
//        layoutLabel1.setVisibility(View.VISIBLE);
//        layoutLabel2.setVisibility(View.VISIBLE);
    }

    private void hideSwitches() {
        scrollView.setVisibility(View.GONE);
//        layoutSwitchA.setVisibility(View.GONE);
//        layoutSwitchB.setVisibility(View.GONE);
//        layoutSwitchC.setVisibility(View.GONE);
//        layoutLabel1.setVisibility(View.GONE);
//        layoutLabel2.setVisibility(View.GONE);
    }

    private void showStatus(String loadingText, boolean showProgress, boolean isDeviceNotFound) {
        layoutLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        txtLoading.setText(loadingText);
        if (isDeviceNotFound) {
            txtLoading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_android, 0, 0);
        }
    }

    private void hideStatus() {
        layoutLoading.setVisibility(View.GONE);
    }

    private void goToSwitchDevice() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    //Sync buttons without affecting the on change event
    private void syncButtons() {
        toggle_a.setOnCheckedChangeListener(null);
        toggle_a.setChecked(switches.isA());
        setCustomDrawable(toggle_a, switches.isA());
        toggle_a.setOnCheckedChangeListener(buttonEvent);

        toggle_b.setOnCheckedChangeListener(null);
        toggle_b.setChecked(switches.isB());
        setCustomDrawable(toggle_b, switches.isB());
        toggle_b.setOnCheckedChangeListener(buttonEvent);

        toggle_c.setOnCheckedChangeListener(null);
        toggle_c.setChecked(switches.isC());
        setCustomDrawable(toggle_c, switches.isC());
        toggle_c.setOnCheckedChangeListener(buttonEvent);

        toggle_d.setOnCheckedChangeListener(null);
        toggle_d.setChecked(switches.isD());
        setCustomDrawable(toggle_d, switches.isD());
        toggle_d.setOnCheckedChangeListener(buttonEvent);

        toggle_e.setOnCheckedChangeListener(null);
        toggle_e.setChecked(switches.isE());
        setCustomDrawable(toggle_e, switches.isE());
        toggle_e.setOnCheckedChangeListener(buttonEvent);

        toggle_f.setOnCheckedChangeListener(null);
        toggle_f.setChecked(switches.isF());
        setCustomDrawable(toggle_f, switches.isF());
        toggle_f.setOnCheckedChangeListener(buttonEvent);
    }

    private void connectToCloud() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        switchesDbRef = database.getReference(DEVICE_ID);
        logsdDbRef = database.getReference("event_logs").child(DEVICE_ID);
//        final AlertDialog al = createWaitDialog();
//        al.show();
        switches = new Switch(false, false, false, false, false, false);

        DatabaseReference rootRef = database.getReference();
        rootRef.child(DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    showStatus("Device [" + DEVICE_ID + "] is not found.", false, true);
                } else {
                    switches.setA(dataSnapshot.child("a").getValue(boolean.class));
                    switches.setB(dataSnapshot.child("b").getValue(boolean.class));
                    switches.setC(dataSnapshot.child("c").getValue(boolean.class));
                    switches.setD(dataSnapshot.child("d").getValue(boolean.class));
                    switches.setE(dataSnapshot.child("e").getValue(boolean.class));
                    switches.setF(dataSnapshot.child("f").getValue(boolean.class));
                    hideStatus();
                    showSwitches();
                    syncButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //myRef.setValue(switches);
//                (new ValueEventListener() {
//            @Override
//            void onDataChange(DataSnapshot snapshot) {
//                if (snapshot.getValue() == null) {
//                    // The child doesn't exist
//                }
//            }
//        });
        // Read from the database
        rootRef.child(DEVICE_ID).addValueEventListener(new ValueEventListener() {
            //            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                isLoading = true;
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
                try {
                    switches.setA(dataSnapshot.child("a").getValue(boolean.class));
                    switches.setB(dataSnapshot.child("b").getValue(boolean.class));
                    switches.setC(dataSnapshot.child("c").getValue(boolean.class));
                    switches.setD(dataSnapshot.child("d").getValue(boolean.class));
                    switches.setE(dataSnapshot.child("e").getValue(boolean.class));
                    switches.setF(dataSnapshot.child("f").getValue(boolean.class));
                    syncButtons();
                } catch (Exception e) {
                    Toast.makeText(getCurrentContext(), "A problem occured from cloud connection", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    private MainActivity getCurrentContext() {
        return this;
    }
//    private void updateButtons(Switch s) {
//        toggle_a.setChecked(s.isA());
//        toggle_b.setChecked(s.isB());
//        toggle_c.setChecked(s.isC());
//        toggle_d.setChecked(s.isD());
//    }

    //    private void updateFirebaseSwitches(Switch s) {
//        myRef.setValue(s);
//    }
    private void setCustomDrawable(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Drawable d = getResources().getDrawable(R.drawable.sw_on);
            buttonView.setBackground(d);
        } else {
            Drawable d = getResources().getDrawable(R.drawable.sw_off);
            buttonView.setBackground(d);
        }

    }

}
