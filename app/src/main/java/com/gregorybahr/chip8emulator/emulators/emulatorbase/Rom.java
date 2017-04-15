package com.gregorybahr.chip8emulator.emulators.emulatorbase;

import com.gregorybahr.chip8emulator.emulators.EmulatorType;

import java.io.Serializable;

/**
 * Created by bahrg on 3/6/17.
 */

public class Rom implements Serializable {
    private String file;
    private String name;
    private EmulatorType emulatorType;

    public Rom(String file, String name,EmulatorType emulatorType) {
        this.file = file;
        this.name = name;
        this.emulatorType = emulatorType;
    }

    public String getFile() {
        return file;
    }

    public EmulatorType getEmulatorType() {
        return emulatorType;
    }

    public String getName() {
        return name;
    }
}
