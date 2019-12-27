package com.example.homeautomation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    GestureDetector gestureDetector;
    ListView listView;
    Button button;
    SwitchListAdapter switchListAdapter;
    final String[] channelstring = new String[]{"Channel A", "Channel B", "Channel C", "Channel D", "Channel E", "Channel F"};
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
    TextView txtA;
    TextView txtB;
    TextView txtC;
    TextView txtD;
    TextView txtE;
    TextView txtF;
    Switch switches;
    boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home Automation Switches App");
        setContentView(R.layout.activity_main);
        // initialize the Gesture Detector
        initDatabase();
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);


        txtA = findViewById(R.id.textViewa);
        txtB = findViewById(R.id.textViewb);
        txtC = findViewById(R.id.textViewc);
        txtD = findViewById(R.id.textViewd);
        txtE = findViewById(R.id.textViewe);
        txtF = findViewById(R.id.textViewf);

        txtA.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        onSound = MediaPlayer.create(this, R.raw.on_sound);
        offSound = MediaPlayer.create(this, R.raw.off_sound);

        toggle_a = findViewById(R.id.toggle_a);
        toggle_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setA(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_b = findViewById(R.id.toggle_b);
        toggle_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setB(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_c = findViewById(R.id.toggle_c);
        toggle_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setC(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_d = findViewById(R.id.toggle_d);
        toggle_d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setD(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_e = findViewById(R.id.toggle_e);
        toggle_e.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setE(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });

        toggle_f = findViewById(R.id.toggle_f);
        toggle_f.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isLoading) {
                    switches.setF(isChecked);
                    updateFirebaseSwitches(switches);
                }
                setCustomDrawable(buttonView, isChecked);
            }
        });
    }

    private void initDatabase() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("s");

//        final AlertDialog al = createWaitDialog();
//        al.show();
        switches = new Switch(false, false, false, false, false, false);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isLoading = true;
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                switches.setA(dataSnapshot.child("a").getValue(boolean.class));
                switches.setB(dataSnapshot.child("b").getValue(boolean.class));
                switches.setC(dataSnapshot.child("c").getValue(boolean.class));
                switches.setD(dataSnapshot.child("d").getValue(boolean.class));
                switches.setE(dataSnapshot.child("e").getValue(boolean.class));
                switches.setF(dataSnapshot.child("f").getValue(boolean.class));
                Log.d("MainActivity", "Switches updated: " + switches);
                updateButtons(switches);
                isLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    private void updateButtons(Switch s) {
        toggle_a.setChecked(s.isA());
        toggle_b.setChecked(s.isB());
        toggle_c.setChecked(s.isC());
        toggle_d.setChecked(s.isD());
        toggle_e.setChecked(s.isE());
        toggle_f.setChecked(s.isF());
    }

    private void updateFirebaseSwitches(Switch s) {
        myRef.setValue(s);
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {

        Toast.makeText(MainActivity.this, "Double Tap on Screen is Working.", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {

        return false;
    }
}
