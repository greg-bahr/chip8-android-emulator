package com.gregorybahr.chip8emulator.emulators.nes;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Memory;

/**
 * Created by bahrg on 4/12/17.
 */

public class NesMMU extends Memory {

    // 0 - 0xFF : Zero page memory
    // 0x100 - 0x1FF : Stack - sp wraps
    // 0x200 - 0x7FF : Gen Purpose RAM
    // 0x800 - 0x2000 : Mirrors 0x0 - 0x7FF
    // 0x2000 - 0x2007 : PPU Registers
    // 0x2008 - 0x4000 : Mirror of the PPU Registers
    // 0x4000 - 0x4020 : APU and I/O registers
    // 0x4020 - 0x6000 : Expansion ROM
    // 0x6000 - 0x8000 : SRAM
    // 0x8000 - ... : Game pak data, if only one bank it is mirrored at 0xC000

    public NesMMU() {
        memory = new int[0x10000];

    }

    @Override
    public int read(int index) {
        if(index < 0x2000) {
            return memory[index%0x800];
        } else if(index < 0x4000) {
            return memory[index%8];
        }
        return memory[index];
    }

    @Override
    public void write(int num, int index) {
        if(index < 0x2000) {
            memory[index%0x800] = num;
            return;
        } else if(index < 0x4000) {
            memory[(index%8)+0x2000] = num;
            return;
        }
        memory[index] = num;
    }
}
