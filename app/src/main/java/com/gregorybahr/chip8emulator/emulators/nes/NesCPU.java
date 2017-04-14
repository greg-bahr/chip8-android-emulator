package com.gregorybahr.chip8emulator.emulators.nes;

/**
 * Created by bahrg on 4/12/17.
 */

public class NesCPU {

    private NesMMU memory;

    private int a, x, y, pc, sp, p;

    public NesCPU() {
        memory = new NesMMU();
    }


}
