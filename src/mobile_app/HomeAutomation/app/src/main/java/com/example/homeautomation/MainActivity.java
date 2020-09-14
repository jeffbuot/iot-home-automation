package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.homeautomation.classes.ApiWrapper;
import com.example.homeautomation.classes.ChannelData;
import com.example.homeautomation.classes.Switch;
import com.example.homeautomation.customadapters.SwitchListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // GestureDetector gestureDetector;
    //ListView listView;]
    private String TAG = "MAIN ACTIVITY";
    final String PREFS_NAME = "smarthome_settings";
    String DEVICE_ID = "";
    SwitchListAdapter switchListAdapter;
    // final String[] channelstring = new String[]{"Channel A", "Channel B", "Channel C", "Channel D", "Channel E", "Channel F"};
    FirebaseDatabase database;
    DatabaseReference myRef;
    ToggleButton toggle_a;
    ToggleButton toggle_b;
    ToggleButton toggle_c;
    ToggleButton toggle_d;
    ToggleButton toggle_e;
    ToggleButton toggle_f;
    MediaPlayer onSound;
    MediaPlayer offSound;
    Button btnSwitchDevice;
    TextView txtDeviceId;
    TextView txtA;
    TextView txtB;
    TextView txtC;
    TextView txtD;
    TextView txtE;
    TextView txtF;
    Switch switches;
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
        txtDeviceId = findViewById(R.id.txtDeviceId);

        scrollView = findViewById(R.id.scrollLayoutButtons);
//        layoutSwitchA = findViewById(R.id.layoutSwitchA);
//        layoutSwitchB = findViewById(R.id.layoutSwitchB);
//        layoutSwitchC = findViewById(R.id.layoutSwitchC);
//        layoutLabel1 = findViewById(R.id.layoutLabel1);
//        layoutLabel2 = findViewById(R.id.layoutLabel2);
        layoutLoading = findViewById(R.id.layoutLoading);
        txtLoading = findViewById(R.id.txtLoad);
        progressBar = findViewById(R.id.progressBar);
        hideSwitches();
        showStatus("Connecting to cloud..", true);
        checkDeviceId();

        btnSwitchDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSwitchDevice();
            }
        });

        onSound = MediaPlayer.create(this, R.raw.on_sound);
        offSound = MediaPlayer.create(this, R.raw.off_sound);

        toggle_a = findViewById(R.id.toggle_a);
        toggle_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setA(isChecked);
                //Make sure that the rules are set into read: true, and write: true
                //Catch error using Firebase OnSuccessListener and OnFailureListener
                myRef.setValue(switches);
                //Log the transaction if success..
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "Successful Transaction");
//                    }
//                })
//                        //Log the transaction if its failed.
//                        .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, e.getMessage());
//                    }
//                });
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_b = findViewById(R.id.toggle_b);
        toggle_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setB(isChecked);
                myRef.setValue(switches);
                setCustomDrawable(buttonView, isChecked);
//                if (!isLoading) {
//                    switches.setB(isChecked);
//                    updateFirebaseSwitches(switches);
//                }
//                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_c = findViewById(R.id.toggle_c);
        toggle_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setC(isChecked);
                myRef.setValue(switches);
                setCustomDrawable(buttonView, isChecked);
//                if (!isLoading) {
//                    switches.setC(isChecked);
//                    updateFirebaseSwitches(switches);
//                }
//                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_d = findViewById(R.id.toggle_d);
        toggle_d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setD(isChecked);
                myRef.setValue(switches);
                setCustomDrawable(buttonView, isChecked);
//                if (!isLoading) {
//                    switches.setD(isChecked);
//                    updateFirebaseSwitches(switches);
//                }
//                setCustomDrawable(buttonView, isChecked);
            }
        });
        toggle_e = findViewById(R.id.toggle_e);
        toggle_e.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setE(isChecked);
                myRef.setValue(switches);
                setCustomDrawable(buttonView, isChecked);
//                if (!isLoading) {
//                    switches.setD(isChecked);
//                    updateFirebaseSwitches(switches);
//                }
//                setCustomDrawable(buttonView, isChecked);
            }
        });
        toggle_f = findViewById(R.id.toggle_f);
        toggle_f.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switches.setF(isChecked);
                myRef.setValue(switches);
                setCustomDrawable(buttonView, isChecked);
//                if (!isLoading) {
//                    switches.setD(isChecked);
//                    updateFirebaseSwitches(switches);
//                }
//                setCustomDrawable(buttonView, isChecked);
            }
        });
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

    private void showStatus(String loadingText, boolean showProgress) {
        layoutLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        txtLoading.setText(loadingText);
    }

    private void hideStatus() {
        layoutLoading.setVisibility(View.GONE);
    }

    private void goToSwitchDevice() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void connectToCloud() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(DEVICE_ID);
//        final AlertDialog al = createWaitDialog();
//        al.show();
        switches = new Switch(false, false, false, false, false, false);

        DatabaseReference rootRef = database.getReference();
        rootRef.child(DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    showStatus("Device not found.", false);
                } else {
                    hideStatus();
                    showSwitches();
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
//        myRef.addValueEventListener(new ValueEventListener() {
//            //            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                isLoading = true;
////                // This method is called once with the initial value and again
////                // whenever data at this location is updated.
//                switches.setA(dataSnapshot.child("a").getValue(boolean.class));
//                switches.setB(dataSnapshot.child("b").getValue(boolean.class));
//                switches.setC(dataSnapshot.child("c").getValue(boolean.class));
//                switches.setD(dataSnapshot.child("d").getValue(boolean.class));
//                switches.setE(dataSnapshot.child("e").getValue(boolean.class));
//                switches.setF(dataSnapshot.child("f").getValue(boolean.class));
//                Log.d("MainActivity", "Switches updated: " + switches);
////                updateButtons(switches);
////                isLoading = false;
//            }
//
//            //
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("", "Failed to read value.", error.toException());
//            }
//        });
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
            onSound.start();
            Drawable d = getResources().getDrawable(R.drawable.sw_on);
            buttonView.setBackground(d);
        } else {
            offSound.start();
            Drawable d = getResources().getDrawable(R.drawable.sw_off);
            buttonView.setBackground(d);
        }

    }

}
