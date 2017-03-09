package com.gregorybahr.chip8emulator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gregorybahr.chip8emulator.emulator.Cpu;
import com.gregorybahr.chip8emulator.emulator.Memory;
import com.gregorybahr.chip8emulator.emulator.Rom;
import com.gregorybahr.chip8emulator.emulator.display.DisplayView;

import java.io.IOException;
import java.io.InputStream;

public class EmulatorActivity extends AppCompatActivity {

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
