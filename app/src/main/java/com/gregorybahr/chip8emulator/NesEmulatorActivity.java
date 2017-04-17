package com.gregorybahr.chip8emulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Rom;

public class NesEmulatorActivity extends AppCompatActivity {

    private Rom rom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nes_emulator);

        rom = (Rom) getIntent().getExtras().get("rom");


    }
}
