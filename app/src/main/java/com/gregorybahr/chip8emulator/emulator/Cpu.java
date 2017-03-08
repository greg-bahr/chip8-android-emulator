package com.gregorybahr.chip8emulator.emulator;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by bahrg on 2/28/17.
 */

public class Cpu {

    private int[] inputBuffer, displayBuffer, registers;
    // timers decrease by 1 at a rate of 60hz
    private int index, delayTimer, soundTimer, pc, opcode;
    private Memory memory;
    private HashMap<Integer, Opcode> opcodeTable;
    private Stack<Integer> stack;

    public Cpu() {
        registers = new int[16];
        inputBuffer = new int[16];
        displayBuffer = new int[64*32];
        pc = 0x200;
        memory = new Memory();
        opcodeTable = new HashMap<>();
        stack = new Stack<>();
    }

    private void initOpcodes() {
        // implement opcodes here
        opcodeTable.put(0, new Opcode() {
            String name = "";
            @Override
            public void execute() {
                opcodeTable.get((opcode & 0x00ff)).execute();
            }
        });

        opcodeTable.put(0x00E0, new Opcode() {
            String name = "CLS";
            @Override
            public void execute() {
                for (int i = 0; i < displayBuffer.length; i++) {
                    displayBuffer[i] = 0;
                }
                pc++;
            }
        });

        opcodeTable.put(0x00EE, new Opcode() {
            String name = "RET";
            @Override
            public void execute() {
                stack.pop();
                pc++;
            }
        });

        opcodeTable.put(0x1, new Opcode() {
            String name = "JMP nnn";
            @Override
            public void execute() {
                pc = nnn();
            }
        });

        opcodeTable.put(0x2, new Opcode() {
            String name = "CALL nnn";
            @Override
            public void execute() {
                stack.push(pc);
                pc = nnn();
            }
        });

        opcodeTable.put(0x3, new Opcode() {
            String name = "SE Vx, kk";
            @Override
            public void execute() {
                if (registers[x()] == kk()) {
                    pc += 2;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x4, new Opcode() {
            String name = "SNE Vx, kk";
            @Override
            public void execute() {
                if (registers[x()] != kk()) {
                    pc += 2;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x5, new Opcode() {
            String name = "SE Vx, Vy";
            @Override
            public void execute() {
                if (registers[x()] == registers[y()]) {
                    pc += 2;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x6, new Opcode() {
            String name = "LD Vx, byte";
            @Override
            public void execute() {
                registers[x()] = kk();
                pc++;
            }
        });

        opcodeTable.put(0x7, new Opcode() {
            String name = "ADD Vx, byte";
            @Override
            public void execute() {
                registers[x()] += kk();
                pc++;
            }
        });

        opcodeTable.put(0x8, new Opcode() {
            String name = "LD Vx, Vy";
            @Override
            public void execute() {
                if (n() > 0) {
                    opcodeTable.get(opcode & 0xf00f).execute();
                    return;
                }

                registers[x()] = registers[y()];
                pc++;
            }
        });

        opcodeTable.put(0x8001, new Opcode() {
            String name = "OR Vx, Vy";
            @Override
            public void execute() {
                registers[x()] |= registers[y()];
                pc++;
            }
        });

        opcodeTable.put(0x8002, new Opcode() {
            String name = "AND Vx, Vy";
            @Override
            public void execute() {
                registers[x()] &= registers[y()];
                pc++;
            }
        });

        opcodeTable.put(0x8003, new Opcode() {
            String name = "XOR Vx, Vy";
            @Override
            public void execute() {
                registers[x()] ^= registers[y()];
                pc++;
            }
        });

        opcodeTable.put(0x8004, new Opcode() {
            String name = "ADD Vx, Vy";
            @Override
            public void execute() {
                int value = (registers[x()] + registers[y()]);

            }
        });
    }

    private byte x() { return (byte) (opcode & 0x0f00); }
    private byte y() { return (byte) (opcode & 0x00f0); }
    private byte kk() { return (byte) (opcode & 0x00ff); }
    private short nnn() { return (byte) (opcode & 0x0fff); }
    private byte n() { return (byte) (opcode & 0x000f); }
}
