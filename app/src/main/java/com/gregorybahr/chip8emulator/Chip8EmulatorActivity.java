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
    private Rom rom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chip8_emulator);

        rom = (Rom) getIntent().getExtras().get("rom");

        surfaceView = (DisplayViewChip8) findViewById(R.id.surface_view);
        initButtons();
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
        surfaceView.stopEmulation();
    }
}
