package com.gregorybahr.chip8emulator.emulator;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by bahrg on 2/28/17.
 */

public class Cpu {

    private int[] inputBuffer, registers;
    private int[][] displayBuffer;
    // timers decrease by 1 at a rate of 60hz
    private int index, delayTimer, soundTimer, pc, opcode;
    private Memory memory;
    private HashMap<Integer, Opcode> opcodeTable;
    private Stack<Integer> stack;
    private boolean shouldDraw;

    public Cpu() {
        registers = new int[16];
        inputBuffer = new int[16];
        displayBuffer = new int[64][32];
        pc = 0x200;
        memory = new Memory();
        opcodeTable = new HashMap<>();
        stack = new Stack<>();

        initOpcodes();
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
                    for(int j = 0; j < displayBuffer[i].length; j++) {
                        displayBuffer[i][j] = 0;
                    }
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

                if(value > 255) {
                    registers[0xf] = 1;
                    registers[x()] = value - 256;
                } else {
                    registers[0xf] = 0;
                    registers[x()] = value;
                }
                pc++;
            }
        });

        opcodeTable.put(0x8005, new Opcode() {
            String name  = "SUB Vx, Vy";
            @Override
            public void execute() {
                // Vf = NOT borrow
                if (registers[x()] > registers[y()]) {
                    registers[x()] -= registers[y()];
                    registers[0xf] = 1;
                } else {
                    registers[x()] = (registers[x()] - registers[y()]) + 256;
                    registers[0xf] = 0;
                }
                pc++;
            }
        });

        opcodeTable.put(0x8006, new Opcode() {
            String name = "SHR Vx {, Vy}";
            @Override
            public void execute() {
                registers[0xf] = registers[x()] & 0x1;
                registers[x()] >>= 1;
                pc++;
            }
        });

        opcodeTable.put(0x8007, new Opcode() {
            String name = "SUBN Vx, Vy";
            @Override
            public void execute() {
                if(registers[y()] > registers[x()]) {
                    registers[x()] = registers[y()] - registers[x()];
                    registers[0xf] = 1;
                } else {
                    registers[x()] = (registers[y()] - registers[x()]) + 256;
                    registers[0xf] = 0;
                }
                pc++;
            }
        });

        opcodeTable.put(0x800E, new Opcode() {
            String name = "SHL Vx {, Vy}";
            @Override
            public void execute() {
                registers[0xf] = ((registers[x()] & 0x00f0) >> 7);
                registers[x()] <<= 1;
                if(registers[x()] > 255) registers[x()] -= 256;
                pc++;
            }
        });

        opcodeTable.put(0x9000, new Opcode() {
            String name = "SNE Vx, Vy";
            @Override
            public void execute() {
                if(registers[x()] == registers[y()]) {
                    pc++;
                } else {
                    pc += 2;
                }
            }
        });

        opcodeTable.put(0xA000, new Opcode() {
            String name = "LD I, addr";
            @Override
            public void execute() {
                index = nnn();
                pc++;
            }
        });

        opcodeTable.put(0xB000, new Opcode() {
            String name = "JP V0, addr";
            @Override
            public void execute() {
                pc = nnn() + registers[0];
            }
        });

        opcodeTable.put(0xC000, new Opcode() {
            String name = "RND Vx, byte";
            @Override
            public void execute() {
                registers[x()] = (int)(Math.random()*256) & kk();
                pc++;
            }
        });

        opcodeTable.put(0xD000, new Opcode() {
            String name = "DRW Vx, Vy, nibble";
            @Override
            public void execute() {
                int coordx = registers[x()];
                int coordy = registers[y()];
                int height = n();
                int currentPixelRow;

                registers[0xf] = 0;
                for (int row = 0; row < height; row++) {
                    currentPixelRow = memory.read(index + row);
                    for (int column = 0; column < 8; column++) {
                        if((currentPixelRow & (0x80 >> column)) == 1) {
                            // if this specific bit in the sprite == 1, then draw it
                            if(displayBuffer[(coordy + row)%32][(coordx + column)%64] == 1) {
                                registers[0xf] = 1;
                            }
                            displayBuffer[(coordy + row)%32][(coordx + column)%64] ^= 1;
                        }
                    }
                }
                shouldDraw = true;
                pc++;
            }
        });


    }

    private byte x() { return (byte) (opcode & 0x0f00); }
    private byte y() { return (byte) (opcode & 0x00f0); }
    private byte kk() { return (byte) (opcode & 0x00ff); }
    private short nnn() { return (byte) (opcode & 0x0fff); }
    private byte n() { return (byte) (opcode & 0x000f); }

    public int[][] getDisplayBuffer() {
        return displayBuffer;
    }
}
