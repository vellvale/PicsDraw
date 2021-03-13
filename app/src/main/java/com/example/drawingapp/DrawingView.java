package com.example.drawingapp;
//определим собственный класс View, в котором будет происходить рисование
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;  //суперкласс
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class DrawingView extends View {
    //путь (контур) рисования
    private Path drawPath;
    //рисование и краска для холста
    private Paint drawPaint, canvasPaint;
    //устанавливаем начальный цвет
    private int paintColor = 0xFF660000;
    //холст
    private Canvas drawCanvas;
    //растровое изображение холста
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;

    private boolean erase=false;

// Когда пользователь касается экрана и перемещает палец для рисования, мы будем использовать Path, чтобы отследить его рисование на холсте.
// И холст, и рисунок на нем представлены объектами Paint. Первоначальный цвет краски соответствует первому цвету в палитре, которую мы создали в прошлый раз,
// которая будет изначально выбрана при запуске приложения. Наконец, мы объявляем переменные для холста и растрового изображения - пользовательские пути,
// нарисованные с помощью, drawPaint будут нарисованы на холсте, который нарисован с помощью canvasPaint.

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        //создадим экземпляры некоторых из этих переменных, чтобы настроить класс для рисования. Сначала создайте экземпляр объектов Path и Paint для рисования:
        drawPath = new Path();
        drawPaint = new Paint();

        //Затем установите начальный цвет
        drawPaint.setColor(paintColor);

        //Теперь установите начальные свойства пути
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //экземпляр объекта Paint холста
        //This time we set dithering by passing a parameter to the constructor
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

// Нам нужно переопределить несколько методов, чтобы пользовательский вид работал как вид чертежа.
// Во-первых, переопределите onSizeChanged метод, который будет вызываться, когда пользовательскому View будет назначен размер
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //вызовите метод суперкласса
        super.onSizeChanged(w, h, oldw, oldh);
        //создайте экземпляр холста и растрового изображения, используя значения ширины и высоты
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//нарисуйте холст и путь рисования
        //Каждый раз, когда пользователь рисует с помощью сенсорного взаимодействия, мы аннулируем View, вызывая выполнение onDraw метода.
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    //detect user touch
        //Когда вид чертежа находится на экране приложения, мы хотим, чтобы пользователь касался его для регистрации как операций рисования.
        // Для этого нам нужно прослушивать события касания
        //позиции X и Y касания пользователя
        float touchX = event.getX();
        float touchY = event.getY();

        // MotionEvent параметр в onTouchEvent метод позволит нам реагировать на определенные события прикосновения.
        // Действия , которые мы заинтересованы в том, чтобы реализовать рисованию down, move и up.
        // Добавьте в метод оператор switch для ответа на каждый из них
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //завершите метод, сделав представление недействительным и вернув истинное значение:
        invalidate();
        return true;
        //Вызов invalidate вызовет выполнение onDraw метода
    }

    public void setColor(String newColor){
//set color
        //начните с аннулирования View
        invalidate();

        //Далее парсим и устанавливаем цвет для рисования
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);


    }

    public void setBrushSize(float newSize){
//update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }
    public void setErase(boolean isErase){
//set erase true or false
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
