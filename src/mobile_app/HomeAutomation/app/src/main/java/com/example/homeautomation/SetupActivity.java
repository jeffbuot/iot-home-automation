package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    final String PREFS_NAME = "smarthome_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        editText = findViewById(R.id.editDeviceId);
        button = findViewById(R.id.btnNext);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String d = settings.getString(getResources().getString(R.string.device_id_key), "");

        editText.setText(d);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        if (editText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter a device id.", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            String v = editText.getText().toString().trim();
            String k = getResources().getString(R.string.device_id_key);
            editor.putString(k, v);
            editor.commit();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
