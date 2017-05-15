package com.gregorybahr.chip8emulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gregorybahr.chip8emulator.emulators.chip8.display.DisplayViewChip8;
import com.gregorybahr.chip8emulator.emulators.emulatorbase.Rom;

import java.io.IOException;
import java.io.InputStream;

public class Chip8EmulatorActivity extends AppCompatActivity {

    private static final String TAG = "Chip8EmulatorActivity";

    private DisplayViewChip8 surfaceView;
    private Button pause, slowEmulationButton, fastEmulationButton, restartEmulationButton;
    private Rom rom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chip8_emulator);

        rom = (Rom) getIntent().getExtras().get("rom");

        surfaceView = (DisplayViewChip8) findViewById(R.id.surface_view);
        initButtons();

        pause = (Button) findViewById(R.id.pause_emulation_button);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.setEmulating(!surfaceView.isEmulating());
                surfaceView.emulate();
            }
        });

        slowEmulationButton = (Button) findViewById(R.id.slow_emulation_button);
        slowEmulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.decSpeed();
            }
        });

        fastEmulationButton = (Button) findViewById(R.id.speedup_emulation_button);
        fastEmulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.incSpeed();
            }
        });

        restartEmulationButton = (Button) findViewById(R.id.restart_emulation_button);
        restartEmulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.setEmulating(false);
                surfaceView.stop();
                surfaceView.loadRomIntoMemory(loadRomIntoByteArray());
                surfaceView.setEmulating(true);
                surfaceView.emulate();
            }
        });

        surfaceView.loadRomIntoMemory(loadRomIntoByteArray());
        surfaceView.emulate();

    }

    public void initButtons() {
        for(int i = 0; i < 16; i++) {
            final int j = i;
            String buttonName = "button_" + i;
            final int resourceID = getResources().getIdentifier(buttonName, "id", getPackageName());
            Button b = (Button) findViewById(resourceID);
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            surfaceView.setInputState(j, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            surfaceView.setInputState(j, false);
                            break;
                    }
                    return false;
                }
            });

        }
    }

    public byte[] loadRomIntoByteArray() {
        try {
            InputStream is = getAssets().open(rom.getFile());
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        surfaceView.setEmulating(false);
    }
}
