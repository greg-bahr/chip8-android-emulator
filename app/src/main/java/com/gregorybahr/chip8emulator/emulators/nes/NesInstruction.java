package com.gregorybahr.chip8emulator.emulators.nes;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Opcode;

/**
 * Created by bahrg on 4/12/17.
 */

public abstract class NesInstruction implements Opcode {

    private AddressMode addressMode;

    @Override
    public abstract void execute();

    private enum AddressMode {
        ACCUMULATOR,
        IMMEDIATE,
        IMPLIED,
        RELATIVE,
        ABSOLUTE,
        ZEROPAGE,
        INDIRECT,
        ABSINDEXED,
        ZPINDEXED,
        INDEXEDINDIRECT,
        INDIRECTINDEXED
    }
}
