package com.gregorybahr.chip8emulator.emulator;

import android.util.Log;

import java.util.Arrays;
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

    public Cpu(Memory memory) {
        registers = new int[16];
        inputBuffer = new int[16];
        displayBuffer = new int[32][64];
        pc = 0x200;
        this.memory = memory;
        opcodeTable = new HashMap<>();
        stack = new Stack<>();

        initOpcodes();
    }

    public void setInputState(int index, boolean state) {
        inputBuffer[index] = state ? 1 : 0;
    }

    public void cycle() {

        long waitTime, timeDiff, tpi, startTime, currentTime;

        startTime = System.nanoTime();
        shouldDraw = false;
        opcode = (memory.read(pc)<<8) | memory.read(++pc);
        opcodeTable.get(opcode&0xF000).execute();
        if(delayTimer > 0) delayTimer--;
        if(soundTimer > 0) soundTimer--;

        currentTime = System.nanoTime();
        timeDiff = currentTime-startTime;
        tpi = 1000000/20;
        waitTime = tpi-timeDiff;

        if(waitTime > 0) {
            try {
                Thread.sleep(waitTime/1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initOpcodes() {
        // implement opcodes here
        opcodeTable.put(0, new Opcode() {
            String name = "";
            @Override
            public void execute() {
                if (opcode == 0) {
                    pc++;
                    return;
                }
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
                shouldDraw = true;
                pc++;
            }
        });

        opcodeTable.put(0x00EE, new Opcode() {
            String name = "RET";
            @Override
            public void execute() {
                pc = stack.pop();
            }
        });

        opcodeTable.put(0x1000, new Opcode() {
            String name = "JMP nnn";
            @Override
            public void execute() {
                pc = nnn();
            }
        });

        opcodeTable.put(0x2000, new Opcode() {
            String name = "CALL nnn";
            @Override
            public void execute() {
                stack.push(pc);
                pc = nnn();
            }
        });

        opcodeTable.put(0x3000, new Opcode() {
            String name = "SE Vx, kk";
            @Override
            public void execute() {
                if (registers[x()] == kk()) {
                    pc += 3;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x4000, new Opcode() {
            String name = "SNE Vx, kk";
            @Override
            public void execute() {
                if (registers[x()] != kk()) {
                    pc += 3;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x5000, new Opcode() {
            String name = "SE Vx, Vy";
            @Override
            public void execute() {
                if (registers[x()] == registers[y()]) {
                    pc += 3;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0x6000, new Opcode() {
            String name = "LD Vx, byte";
            @Override
            public void execute() {
                registers[x()] = kk();
                pc++;
            }
        });

        opcodeTable.put(0x7000, new Opcode() {
            String name = "ADD Vx, byte";
            @Override
            public void execute() {
                registers[x()] += kk();
                pc++;
            }
        });

        opcodeTable.put(0x8000, new Opcode() {
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
                    pc += 3;
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
                        if((currentPixelRow & (0x80 >> column)) != 0) {
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

        opcodeTable.put(0xE000, new Opcode() {
            String name = "";
            @Override
            public void execute() {
                opcodeTable.get(opcode & 0xF0FF).execute();
            }
        });

        opcodeTable.put(0xE09E, new Opcode() {
            String name = "SKP Vx";
            @Override
            public void execute() {
                if(inputBuffer[x()] == 1) {
                    pc += 3;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0xE0A1, new Opcode() {
            String name = "SKNP Vx";
            @Override
            public void execute() {
                if(inputBuffer[x()] == 0) {
                    pc += 3;
                } else {
                    pc++;
                }
            }
        });

        opcodeTable.put(0xF000, new Opcode() {
            String name = "";
            @Override
            public void execute() {
                opcodeTable.get(opcode & 0xF0FF).execute();
            }
        });

        opcodeTable.put(0xF007, new Opcode() {
            String name = "LD Vx, DT";
            @Override
            public void execute() {
                registers[x()] = delayTimer;
                pc++;
            }
        });

        opcodeTable.put(0xF00A, new Opcode() {
            String name = "LD Vx, K";
            @Override
            public void execute() {
                int keyPressed = -1;
                for (int i = 0; i < inputBuffer.length; i++) {
                    if(inputBuffer[i] == 1) keyPressed = i;
                }
                if(keyPressed >= 0) {
                    pc++;
                } else {
                    pc--;
                }
            }
        });

        opcodeTable.put(0xF015, new Opcode() {
            String name = "LD DT, Vx";
            @Override
            public void execute() {
                delayTimer = registers[x()];
                pc++;
            }
        });

        opcodeTable.put(0xF018, new Opcode() {
            String name = "LD ST, Vx";
            @Override
            public void execute() {
                soundTimer = registers[x()];
                pc++;
            }
        });

        opcodeTable.put(0xF01E, new Opcode() {
            String name = "ADD I, Vx";
            @Override
            public void execute() {
                int value = index + registers[x()];

                if(value > 255) {
                    registers[0xf] = 1;
                    index = value - 256;
                } else {
                    registers[0xf] = 0;
                    index = value;
                }
                pc++;
            }
        });

        opcodeTable.put(0xF029, new Opcode() {
            String name = "LD F, Vx";
            @Override
            public void execute() {
                index = registers[x()] * 5;
                pc++;
            }
        });

        opcodeTable.put(0xF033, new Opcode() {
            String name = "LD B, Vx";
            @Override
            public void execute() {
                int num = registers[x()];

                for(int i = 2; i >= 0; i--) {
                    memory.write(num%10, index+i);
                    num /= 10;
                }
                pc++;
            }
        });

        opcodeTable.put(0xF055, new Opcode() {
            String name = "LD [I], Vx";
            @Override
            public void execute() {
                for(int i = 0; i <= x(); i++) {
                    memory.write(registers[i], i+index);
                }
                pc++;
            }
        });

        opcodeTable.put(0xF065, new Opcode() {
            String name = "LD Vx, [I]";
            @Override
            public void execute() {
                for(int i = 0; i <= x(); i++) {
                    registers[i] = memory.read(index+i);
                }
                pc++;
            }
        });
    }

    private int x() { return (opcode & 0x0f00)>>8; }
    private int y() { return (opcode & 0x00f0)>>4; }
    private int kk() { return (opcode & 0x00ff); }
    private int nnn() { return (opcode & 0x0fff); }
    private int n() { return (opcode & 0x000f); }

    public int[][] getDisplayBuffer() {
        return displayBuffer;
    }

    public boolean shouldDraw() {
        return shouldDraw;
    }
}
