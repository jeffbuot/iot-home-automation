package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homeautomation.classes.Switch;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class LoadingActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView textView;
    ImageView imageView;
    Switch switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        textView = findViewById(R.id.txtLoading);
        imageView = findViewById(R.id.imgConnection);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //String deviceId = editor.


        //textView.setText("Connecting to cloud...");
        //imageView.setImageResource(R.drawable.connecting);
        try {
            connectToDb();
        } catch (Exception e) {
            textView.setText("Unable to connect.");
            imageView.setImageResource(R.drawable.disconnected);
        }
    }

    private void connectToDb() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("s");

        switches = new Switch(false, false, false, false, false, false);
        // Read from the database)
        myRef.onDisconnect().cancel(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d("MainActivity", "Switches updated: " + switches);
            }
        });
        myRef.child("s").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                textView.setText("Connected successfully.");
//                imageView.setImageResource(R.drawable.connected);
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                switches.setA(dataSnapshot.child("a").getValue(boolean.class));
//                switches.setB(dataSnapshot.child("b").getValue(boolean.class));
//                switches.setC(dataSnapshot.child("c").getValue(boolean.class));
//                switches.setD(dataSnapshot.child("d").getValue(boolean.class));
//                switches.setE(dataSnapshot.child("e").getValue(boolean.class));
//                switches.setF(dataSnapshot.child("f").getValue(boolean.class));
//                Log.d("MainActivity", "Switches updated: " + switches);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                textView.setText("Unable to connect.");
//                imageView.setImageResource(R.drawable.disconnected);
//                // Failed to read value
//                Log.w("", "Failed to read value.", error.toException());
//            }
//        });
    }
}
