package com.gregorybahr.chip8emulator.emulators.nes;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Memory;

/**
 * Created by bahrg on 4/12/17.
 */

public class NesMMU extends Memory {


    // 0 - 0xFF : Zero page memory
    // 0x100 - 0x1FF : Stack - sp wraps
    // 0x200 - 0x800 : Gen Purpose RAM
    // 0x801 - 0x2000 : Mirrors 0x0 - 0x7FF
    // 0x2000 - 0x2007 : PPU Registers
    // 0x2008 - 0x4000 : Mirror of the PPU Registers
    // 0x4000 - 0x4020 : APU and I/O registers
    // 0x4020 - 0x6000 : Expansion ROM
    // 0x6000 - 0x8000 : SRAM
    // 0x8000 - ... : Game pak data, if only one bank it is mirrored at 0xC000

    public NesMMU() {
        memory = new int[0xFFFF+1];

    }
}
