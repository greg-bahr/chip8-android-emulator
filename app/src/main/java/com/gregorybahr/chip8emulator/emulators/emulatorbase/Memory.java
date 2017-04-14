package com.gregorybahr.chip8emulator.emulators.emulatorbase;

/**
 * Created by bahrg on 4/7/17.
 */

public abstract class Memory {

    protected int[] memory;

    public int read(int index) { return memory[index]; }
    public void write(int num, int index) { memory[index] = num; }
    public int getSize() { return memory.length; }
}
