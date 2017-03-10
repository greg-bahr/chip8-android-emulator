package com.gregorybahr.chip8emulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gregorybahr.chip8emulator.emulator.Cpu;
import com.gregorybahr.chip8emulator.emulator.Memory;
import com.gregorybahr.chip8emulator.emulator.Rom;
import com.gregorybahr.chip8emulator.emulator.display.DisplayView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EmulatorActivity extends AppCompatActivity {

    private static final String TAG = "EmulatorActivity";

    private DisplayView surfaceView;
    private Rom rom;
    private Memory memory;
    private Cpu emulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);

        rom = (Rom) getIntent().getExtras().get("rom");
        memory = new Memory();
        loadRomIntoMemory();
        emulator = new Cpu(memory);

        surfaceView = (DisplayView) findViewById(R.id.surface_view);
        initButtons();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                emulator.cycle();
            }
        }, 0, 16, TimeUnit.MILLISECONDS);
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
                            emulator.setInputState(j, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            emulator.setInputState(j, false);
                            break;
                    }
                    return false;
                }
            });

        }
    }

    public void loadRomIntoMemory() {
        try {
            InputStream is = getAssets().open(rom.getFile());
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            for (int i = 0; i < bytes.length; i++) {
                memory.write((bytes[i]&0xFF), 0x200+i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
