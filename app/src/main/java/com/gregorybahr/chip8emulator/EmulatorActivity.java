package com.gregorybahr.chip8emulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gregorybahr.chip8emulator.emulator.display.DisplayView;

public class EmulatorActivity extends AppCompatActivity {

    private DisplayView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);

        surfaceView = (DisplayView) findViewById(R.id.surface_view);
    }
}
