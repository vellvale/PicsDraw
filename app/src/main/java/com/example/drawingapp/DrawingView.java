package com.example.drawingapp;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;

import android.graphics.Color;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;


//пишем собственный класс, в котором будет происходить рисование
public class DrawingView extends View {
    private Path drawPath;                  //контур для отслеживания перемещения пальца
    private Paint drawPaint, canvasPaint;   //рисунок и холст (оболочки)
    private int paintColor = 0xFF660000;    //устанавливаем начальный первый цвет в палитре
    private Canvas drawCanvas;              //холст
    private Bitmap canvasBitmap;            //растровое изображение холста
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private int paintAlpha = 255;

// когда пользователь касается экрана и перемещает палец, мы будем использовать Path, чтобы отследить его рисование на холсте
// мы объявляем переменные для холста и растрового изображения - контуры,
// нарисованные пользователем с помощью drawPaint будут нарисованы на холсте-оболочке canvasPaint

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    //настройка холста для взаимодействия пользователя с ним
    private void setupDrawing() {
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPath = new Path();              //экземпляры объектов Path и Paint для рисования
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);     //начальный цвет

        //начальные свойства контура
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //экземпляр холста
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //метод вызывается, когда для исходного View присваивается размер
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //создаем экземпляр холста и растра, используя значения ширины и высоты
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //холст и контур рисования
        //каждый раз, когда пользователь рисует, мы аннулируем View, вызывая onDraw
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {    //обработчик касаний
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:               //перемещаемся в данные координаты, чтобы начать рисовать
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:               //в движении - рисуем контур вместе с касанием
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:                 //рисуем Path и сбрасываем его для следующей операции рисования
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();                                   //invalidate вызовет выполнение onDraw метода
        return true;
    }

    public void setColor(String newColor){
        invalidate();                                   //аннулирование View
        paintColor = Color.parseColor(newColor);        //далее устанавливаем цвет
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100*255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

}
