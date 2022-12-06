package com.example.photobook;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingPaper extends View {

    private ArrayList<Path> pathList = new ArrayList<Path>();
    private ArrayList<Paint> paintList = new ArrayList<Paint>();
    private int currentPath = 0;
    private int currentPaint = 0;
    private int currentColor = Color.BLACK;
    private float currentStrokeWidth = 10f;

    public DrawingPaper(Context context) {
        super(context);
        initPaper();
    }

    public void initPaper() {
        pathList.add(new Path());
        paintList.add(new Paint());
        paintList.get(currentPaint).setAntiAlias(true);
        paintList.get(currentPaint).setColor(currentColor);
        paintList.get(currentPaint).setStrokeWidth(currentStrokeWidth);
        paintList.get(currentPaint).setStyle(Paint.Style.STROKE);
        paintList.get(currentPaint).setStrokeJoin(Paint.Join.ROUND);
    }


    public void setPaintColor(int color) {
        paintList.add(new Paint());
        pathList.add(new Path());
        currentPath++;
        currentPaint++;
        currentColor = color;
        paintList.get(currentPaint).setColor(color);
        paintList.get(currentPaint).setAntiAlias(true);
        paintList.get(currentPaint).setStyle(Paint.Style.STROKE);
        paintList.get(currentPaint).setStrokeJoin(Paint.Join.ROUND);
        paintList.get(currentPaint).setStrokeWidth(currentStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        for (i=0; i<pathList.size(); i++){
            canvas.drawPath(pathList.get(i), paintList.get(i));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pathList.get(currentPath).moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                pathList.get(currentPath).lineTo(x, y);
                invalidate();
            case MotionEvent.ACTION_UP:
                break;
            default:
                return true;
        }

        invalidate();
        return true;
    }
}