package com.gregorybahr.chip8emulator.emulator.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.gregorybahr.chip8emulator.emulator.Cpu;

/**
 * Created by bahrg on 2/28/17.
 */

public class DisplayView extends SurfaceView {

    private Paint paint;
    private Cpu emulator;
    private int viewWidth, viewHeight;
    private Thread thread;
    private boolean emulating;

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        emulator = new Cpu();
        paint = new Paint();
        paint.setColor(Color.WHITE);

        setWillNotDraw(false);
    }

    public void emulate() {
        emulating = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (emulating) {
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
        emulator.getMemory().reset();
        for (int i = 0; i < bytes.length; i++) {
            emulator.getMemory().write((bytes[i]&0xFF), 0x200+i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int widthScale = viewWidth / 64;
        int heightScale = viewHeight / 32;
        int[][] displayBuffer = emulator.getDisplayBuffer();

        canvas.drawColor(Color.BLACK);

        for (int i = 0; i < displayBuffer.length; i++) {
            for (int j = 0; j < displayBuffer[i].length; j++) {
                if (displayBuffer[i][j] == 1) {

                    float x1 = j * widthScale;
                    float y1 = i * heightScale;
                    float x2 = (j + 1) * widthScale;
                    float y2 = (i + 1) * heightScale;

                    canvas.drawRect(x1, y1, x2, y2, paint);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.viewWidth = w;
        this.viewHeight = h;
    }

    public void stopEmulation() { emulating = false; }

    public void setInputState(int index, boolean state) {
        emulator.setInputState(index, state);
    }
}
