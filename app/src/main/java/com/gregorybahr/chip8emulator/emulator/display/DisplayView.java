package com.gregorybahr.chip8emulator.emulator.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.gregorybahr.chip8emulator.emulator.Cpu;
import com.gregorybahr.chip8emulator.emulator.Memory;

/**
 * Created by bahrg on 2/28/17.
 */

public class DisplayView extends SurfaceView {

    private Paint paint;
    private Cpu emulator;
    private Memory memory;
    private int viewWidth, viewHeight;
    private Thread thread;

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        memory = new Memory();
        emulator = new Cpu(memory);
        paint = new Paint();

        setWillNotDraw(false);
    }

    public void emulate() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    emulator.cycle();
                    if (emulator.shouldDraw()) {
                        postInvalidate();
                    }
                }
            }
        });
        thread.start();
    }

    public void loadRomIntoMemory(byte[] array) {
        byte[] bytes = array;

        for (int i = 0; i < bytes.length; i++) {
            memory.write((bytes[i]&0xFF), 0x200+i);
        }
        emulator = new Cpu(memory);
        Log.i("DisplayView", Integer.toHexString(memory.read(0x200)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int widthScale = viewWidth / 64;
        int heightScale = viewHeight / 32;
        int[][] displayBuffer = emulator.getDisplayBuffer();

        for (int i = 0; i < displayBuffer.length; i++) {
            for (int j = 0; j < displayBuffer[i].length; j++) {
                if (displayBuffer[i][j] == 1) {
                    paint.setColor(Color.GREEN);
                } else {
                    paint.setColor(Color.BLACK);
                }

                float x1 = j * widthScale;
                float y1 = i * heightScale;
                float x2 = (j + 1) * widthScale;
                float y2 = (i + 1) * heightScale;

                canvas.drawRect(x1, y1, x2, y2, paint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.viewWidth = w;
        this.viewHeight = h;
    }

    public void setInputState(int index, boolean state) {
        emulator.setInputState(index, state);
    }
}
