package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class TestActivity extends AppCompatActivity {

    ToggleButton toggle_a;
    ToggleButton toggle_b;
    ToggleButton toggle_c;
    ToggleButton toggle_d;
    ToggleButton toggle_e;
    ToggleButton toggle_f;
    MediaPlayer onSound;
    MediaPlayer offSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        onSound = MediaPlayer.create(this, R.raw.on_sound);
        offSound = MediaPlayer.create(this,R.raw.off_sound);

        toggle_a = findViewById(R.id.toggle_a);
        toggle_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });

        toggle_b = findViewById(R.id.toggle_b);
        toggle_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });

        toggle_c = findViewById(R.id.toggle_c);
        toggle_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });

        toggle_d = findViewById(R.id.toggle_d);
        toggle_d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });

        toggle_e = findViewById(R.id.toggle_e);
        toggle_e.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });

        toggle_f = findViewById(R.id.toggle_f);
        toggle_f.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCustomDrawable(buttonView,isChecked);
            }
        });
    }

    private void setCustomDrawable(CompoundButton buttonView, boolean isChecked){
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
