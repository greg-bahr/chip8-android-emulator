package com.gregorybahr.chip8emulator.emulator;

import java.io.File;

/**
 * Created by bahrg on 3/6/17.
 */

public class Rom {
    private File file;
    private String name;

    public Rom(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }


}
