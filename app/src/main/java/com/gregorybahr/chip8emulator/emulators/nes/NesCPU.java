package com.gregorybahr.chip8emulator.emulators.nes;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Opcode;

import java.util.HashMap;

/**
 * Created by bahrg on 4/12/17.
 */

public class NesCPU {

    private NesMMU memory;
    private HashMap<Integer, Opcode> opcodes;

    private int a, x, y, pc, sp, p;
    private int cycles;

    public NesCPU() {
        memory = new NesMMU();
        for (int i = 0; i < 0x800; i++) {
            memory.write(i, 0xFF);
        }
        pc = memory.read(0xFFFD) * 256 + memory.read(0xFFFC);
        sp = 0xFD;
        p = 0x34;

        initOpcodes();
    }

    private void initOpcodes() {
        // write opcodes here
    }
}
