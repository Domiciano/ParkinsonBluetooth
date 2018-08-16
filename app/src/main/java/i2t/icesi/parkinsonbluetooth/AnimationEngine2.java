package i2t.icesi.parkinsonbluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 * Created by Domiciano on 19/12/2016.
 */

public class AnimationEngine2 extends SurfaceView implements Runnable, View.OnTouchListener {

    int WIDTH = 0;
    int HEIGHT = 0;
    int pixelCero = 0;
    int maxShowed = 0;
    int minShowed = 0;
    double max = 0;
    double min = 0;
    double amp1 = 0;
    double amp2 = 0;
    double amp3 = 0;
    double amp4 = 0;
    double amp5 = 0;
    double amp6 = 0;
    double amp7 = 0;
    double amp8 = 0;
    double ampmenos1 = 0;
    double ampmenos2 = 0;
    double ampmenos3 = 0;
    double ampmenos4 = 0;
    double ampmenos5 = 0;
    double ampmenos6 = 0;
    double ampmenos7 = 0;
    double ampmenos8 = 0;


    ModoLive main;

    Thread thread = null;
    boolean canDraw = false;
    Canvas canvas;

    SurfaceHolder surfaceHolder;
    Paint p;
    long frameCount = 0;

    boolean control = true;

    public static final int DERECHA = 0;
    public static final int IZQUIERDA = 1;
    public static final int DERECHA_HIGHER = 2;
    public static final int IZQUIERDA_HIGHER = 3;
    public int manilla = IZQUIERDA;

    int Ymax = 0, Ymin = 1023;

    public static int datosmostrados = 150;
    int ANCHO_LINEA = 3;

    CalculatorEngine calculator;

    public AnimationEngine2(Context context) {
        super(context);
        surfaceHolder = getHolder();
        setOnTouchListener(this);
        main = (ModoLive) context;
    }

    @Override
    public void run() {
        while (canDraw) {
            try {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }
                canvas = surfaceHolder.lockCanvas();
                WIDTH = canvas.getWidth();
                HEIGHT = canvas.getHeight();
                if (control) {
                    setup();
                    control = false;
                }
                draw();
                surfaceHolder.unlockCanvasAndPost(canvas);
                frameCount++;
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        canDraw = false;
        if (calculator != null) calculator.live = false;
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
    }

    public void resume() {
        canDraw = true;
        control = true;
        thread = new Thread(this);
        thread.start();
    }

    private void setup() {
        p = new Paint();
        calculator = new CalculatorEngine();
        calculator.start();
    }


    private void draw() {
        canvas.drawRGB(255, 255, 255);
        p.setStrokeWidth(1);
        p.setColor(Color.rgb(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0, i * canvas.getHeight() / 5, canvas.getWidth(), i * canvas.getHeight() / 5, p);
        }
        for (int i = 0; i < 10; i++) {
            canvas.drawLine(i * canvas.getWidth() / 10, 0, i * canvas.getWidth() / 10, canvas.getHeight(), p);
        }
        try {

            dibujarUnidades();
            dibujarEjeX();
            dibujarEjeY();
            dibujarEjeZ();
        } catch (IndexOutOfBoundsException ex) {
            Log.e("parkinson", "Error satisfactoriamente superado");
        }
    }

    private void dibujarUnidades() {
        p.setTextSize(35);
        p.setStrokeWidth(5);
        p.setColor(Color.BLACK);
        NumberFormat formatter = new DecimalFormat("#0.00");

        canvas.drawLine(0, pixelCero + 8 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 8 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos8) + "m/s", canvas.getWidth() / 100 + 10, pixelCero + 8 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 7 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 7 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos7), canvas.getWidth() / 100 + 10, pixelCero + 7 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 6 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 6 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos6), canvas.getWidth() / 100 + 10, pixelCero + 6 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 5 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 5 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos5), canvas.getWidth() / 100 + 10, pixelCero + 5 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 4 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 4 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos4), canvas.getWidth() / 100 + 10, pixelCero + 4 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 3 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 3 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos3), canvas.getWidth() / 100 + 10, pixelCero + 3 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + 2 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + 2 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos2), canvas.getWidth() / 100 + 10, pixelCero + 2 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero + canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero + canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(ampmenos1), canvas.getWidth() / 100 + 10, pixelCero + canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero, canvas.getWidth(), pixelCero, p);
        canvas.drawText("0", canvas.getWidth() / 100 + 10, pixelCero + 50, p);


        canvas.drawLine(0, pixelCero - canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp1), canvas.getWidth() / 100 + 10, pixelCero - canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 2 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 2 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp2), canvas.getWidth() / 100 + 10, pixelCero - 2 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 3 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 3 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp3), canvas.getWidth() / 100 + 10, pixelCero - 3 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 4 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 4 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp4), canvas.getWidth() / 100 + 10, pixelCero - 4 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 5 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 5 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp5), canvas.getWidth() / 100 + 10, pixelCero - 5 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 6 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 6 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp6), canvas.getWidth() / 100 + 10, pixelCero - 6 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 7 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 7 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp7), canvas.getWidth() / 100 + 10, pixelCero - 7 * canvas.getHeight() / 5 + 20, p);

        canvas.drawLine(0, pixelCero - 8 * canvas.getHeight() / 5, canvas.getWidth() / 100, pixelCero - 8 * canvas.getHeight() / 5, p);
        canvas.drawText("" + formatter.format(amp8), canvas.getWidth() / 100 + 10, pixelCero - 8 * canvas.getHeight() / 5 + 20, p);

        p.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
        p.setColor(Color.GRAY);
        canvas.drawLine(0, maxShowed, canvas.getWidth(), maxShowed, p);
        canvas.drawLine(0, minShowed, canvas.getWidth(), minShowed, p);
        p.setPathEffect(null);
        p.setColor(Color.BLACK);

        canvas.drawText("" + formatter.format(max), 0.8f * canvas.getWidth(), maxShowed - 10, p);
        canvas.drawText("" + formatter.format(min), 0.8f * canvas.getWidth(), minShowed + 35, p);
    }

    private void dibujarEjeX() {
        p.setColor(Color.rgb(200, 0, 0));
        p.setStrokeWidth(ANCHO_LINEA);
        if (manilla == IZQUIERDA) {
            if (main.izquierda.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeft().length; i++) {
                    int ti = calculator.getLeft()[i - 1].t;
                    int yi = calculator.getLeft()[i - 1].x;
                    int tf = calculator.getLeft()[i].t;
                    int yf = calculator.getLeft()[i].x;
                    canvas.drawLine(ti, yi, tf, yf, p);

                }
            }
        } else if (manilla == DERECHA) {
            if (main.derecha.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRight().length; i++) {
                    int ti = calculator.getRight()[i - 1].t;
                    int yi = calculator.getRight()[i - 1].x;
                    int tf = calculator.getRight()[i].t;
                    int yf = calculator.getRight()[i].x;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }else if (manilla == IZQUIERDA_HIGHER) {
            if (main.izquierda_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeftHigher().length; i++) {
                    int ti = calculator.getLeftHigher()[i - 1].t;
                    int yi = calculator.getLeftHigher()[i - 1].x;
                    int tf = calculator.getLeftHigher()[i].t;
                    int yf = calculator.getLeftHigher()[i].x;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }else if (manilla == DERECHA_HIGHER) {
            if (main.derecha_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRightHigher().length; i++) {
                    int ti = calculator.getRightHigher()[i - 1].t;
                    int yi = calculator.getRightHigher()[i - 1].x;
                    int tf = calculator.getRightHigher()[i].t;
                    int yf = calculator.getRightHigher()[i].x;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }
    }

    private void dibujarEjeY() {
        p.setColor(Color.rgb(0, 200, 0));
        p.setStrokeWidth(ANCHO_LINEA);
        if (manilla == IZQUIERDA) {
            if (main.izquierda.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeft().length; i++) {
                    int ti = calculator.getLeft()[i - 1].t;
                    int yi = calculator.getLeft()[i - 1].y;
                    int tf = calculator.getLeft()[i].t;
                    int yf = calculator.getLeft()[i].y;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }

        } else if (manilla == DERECHA) {
            if (main.derecha.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRight().length; i++) {
                    int ti = calculator.getRight()[i - 1].t;
                    int yi = calculator.getRight()[i - 1].y;
                    int tf = calculator.getRight()[i].t;
                    int yf = calculator.getRight()[i].y;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }else if(manilla == IZQUIERDA_HIGHER){
            if (main.izquierda_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeftHigher().length; i++) {
                    int ti = calculator.getLeftHigher()[i - 1].t;
                    int yi = calculator.getLeftHigher()[i - 1].y;
                    int tf = calculator.getLeftHigher()[i].t;
                    int yf = calculator.getLeftHigher()[i].y;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }else if(manilla == DERECHA_HIGHER){
            if (main.derecha_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRightHigher().length; i++) {
                    int ti = calculator.getRightHigher()[i - 1].t;
                    int yi = calculator.getRightHigher()[i - 1].y;
                    int tf = calculator.getRightHigher()[i].t;
                    int yf = calculator.getRightHigher()[i].y;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }
    }

    private void dibujarEjeZ() {
        p.setColor(Color.rgb(0, 0, 200));
        p.setStrokeWidth(ANCHO_LINEA);
        if (manilla == IZQUIERDA) {
            if (main.izquierda.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeft().length; i++) {
                    int ti = calculator.getLeft()[i - 1].t;
                    int yi = calculator.getLeft()[i - 1].z;
                    int tf = calculator.getLeft()[i].t;
                    int yf = calculator.getLeft()[i].z;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        } else if (manilla == DERECHA) {
            if (main.derecha.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRight().length; i++) {
                    int ti = calculator.getRight()[i - 1].t;
                    int yi = calculator.getRight()[i - 1].z;
                    int tf = calculator.getRight()[i].t;
                    int yf = calculator.getRight()[i].z;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }

        } else if(manilla == IZQUIERDA_HIGHER){
            if (main.izquierda_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getLeftHigher().length; i++) {
                    int ti = calculator.getLeftHigher()[i - 1].t;
                    int yi = calculator.getLeftHigher()[i - 1].z;
                    int tf = calculator.getLeftHigher()[i].t;
                    int yf = calculator.getLeftHigher()[i].z;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }else if(manilla == DERECHA_HIGHER){
            if (main.derecha_higher.size() >= datosmostrados) {
                for (int i = 1; i < calculator.getRightHigher().length; i++) {
                    int ti = calculator.getRightHigher()[i - 1].t;
                    int yi = calculator.getRightHigher()[i - 1].z;
                    int tf = calculator.getRightHigher()[i].t;
                    int yf = calculator.getRightHigher()[i].z;
                    canvas.drawLine(ti, yi, tf, yf, p);
                }
            }
        }
    }


    float XtouchIni;
    float multiplicador_escala = 0.4f;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                XtouchIni = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float delta = event.getX()-XtouchIni;
                int aumento = (int)(delta * multiplicador_escala);
                int dato = datosmostrados;
                dato += aumento;
                if(dato >= 10 && dato <= 500){
                    datosmostrados = dato;
                }
                XtouchIni = event.getX();
                break;
        }
        return true;
    }

    public class CalculatorEngine extends Thread {
        Vector[] right;
        Vector[] left;
        Vector[] right_higher;
        Vector[] left_higher;

        boolean live;

        public CalculatorEngine() {
            live = true;
            right = new Vector[datosmostrados];
            left = new Vector[datosmostrados];
            right_higher = new Vector[datosmostrados];
            left_higher = new Vector[datosmostrados];
            for (int i = 0; i < datosmostrados; i++) {
                left[i] = new Vector();
                left_higher[i] = new Vector();
                right[i] = new Vector();
                right_higher[i] = new Vector();
            }
        }

        @Override
        public void run() {
            while (live) {
                try {
                    Vector[] aux_right = new Vector[datosmostrados];
                    Vector[] aux_right_higher = new Vector[datosmostrados];
                    Vector[] aux_left = new Vector[datosmostrados];
                    Vector[] aux_left_higher = new Vector[datosmostrados];

                    //1. Extraer los últimos 50 datos de los dos dataset si hay más de 50 datos
                    if (main.derecha.size() >= datosmostrados) {
                        for (int i = 0; i < datosmostrados ; i++) {
                            int index = main.derecha.size()-datosmostrados+i;
                            if(index>=0 && index < main.derecha.size()) {
                                Vector v = main.derecha.get(index);
                                if(v != null) {
                                    Vector nuevo = new Vector(v.millis, v.x, v.y, v.z);
                                    nuevo.t = v.t;
                                    aux_right[i] = nuevo;
                                }
                            }
                        }
                    }
                    if (main.derecha_higher.size() >= datosmostrados) {
                        for (int i = 0; i < datosmostrados ; i++) {
                            int index = main.derecha_higher.size()-datosmostrados+i;
                            if(index>=0 && index < main.derecha_higher.size()) {
                                Vector v = main.derecha_higher.get(index);
                                if(v != null) {
                                    Vector nuevo = new Vector(v.millis, v.x, v.y, v.z);
                                    nuevo.t = v.t;
                                    aux_right_higher[i] = nuevo;
                                }
                            }
                        }
                    }
                    if (main.izquierda.size() >= datosmostrados) {
                        for (int i = 0; i < datosmostrados ; i++) {
                            int index = main.izquierda.size()-datosmostrados+i;
                            if(index>=0 && index < main.izquierda.size()) {
                                Vector v = main.izquierda.get(index);
                                if(v != null) {
                                    Vector nuevo = new Vector(v.millis, v.x, v.y, v.z);
                                    nuevo.t = v.t;
                                    aux_left[i] = nuevo;
                                }
                            }
                        }
                    }
                    if (main.izquierda_higher.size() >= datosmostrados) {
                        for (int i = 0; i < datosmostrados ; i++) {
                            int index = main.izquierda_higher.size()-datosmostrados+i;
                            if(index>=0 && index < main.izquierda_higher.size()) {
                                Vector v = main.izquierda_higher.get(index);
                                if(v != null) {
                                    Vector nuevo = new Vector(v.millis, v.x, v.y, v.z);
                                    nuevo.t = v.t;
                                    aux_left_higher[i] = nuevo;
                                }
                            }
                        }
                    }
                    //2. Calcular máximos y mínimos
                    determinarMaximosYMinimos(aux_left,aux_left_higher,aux_right, aux_right_higher);
                    //3. Atualizar los vectores
                    if (main.izquierda.size() >= datosmostrados) {
                        for (int i = 0; i < aux_left.length; i++) {
                            aux_left[i].t = (int) escalaX(main.izquierda, aux_left[i].t);
                            aux_left[i].x = (int) escalaY(aux_left[i].x);
                            aux_left[i].y = (int) escalaY(aux_left[i].y);
                            aux_left[i].z = (int) escalaY(aux_left[i].z);
                        }
                        left = aux_left;
                    }
                    if (main.izquierda_higher.size() >= datosmostrados) {
                        for (int i = 0; i < aux_left_higher.length; i++) {
                            aux_left_higher[i].t = (int) escalaX(main.izquierda_higher, aux_left_higher[i].t);
                            aux_left_higher[i].x = (int) escalaY(aux_left_higher[i].x);
                            aux_left_higher[i].y = (int) escalaY(aux_left_higher[i].y);
                            aux_left_higher[i].z = (int) escalaY(aux_left_higher[i].z);
                        }
                        left_higher = aux_left_higher;
                    }
                    if (main.derecha.size() >= datosmostrados) {
                        for (int i = 0; i < aux_right.length; i++) {
                            aux_right[i].t = (int) escalaX(main.derecha, aux_right[i].t);
                            aux_right[i].x = (int) escalaY(aux_right[i].x);
                            aux_right[i].y = (int) escalaY(aux_right[i].y);
                            aux_right[i].z = (int) escalaY(aux_right[i].z);
                        }
                        right = aux_right;
                    }
                    if (main.derecha_higher.size() >= datosmostrados) {
                        for (int i = 0; i < aux_right_higher.length; i++) {
                            aux_right_higher[i].t = (int) escalaX(main.derecha_higher, aux_right_higher[i].t);
                            aux_right_higher[i].x = (int) escalaY(aux_right_higher[i].x);
                            aux_right_higher[i].y = (int) escalaY(aux_right_higher[i].y);
                            aux_right_higher[i].z = (int) escalaY(aux_right_higher[i].z);
                        }
                        right_higher = aux_right_higher;
                    }
                    Thread.sleep(20);
                } catch (Exception e) {
                    Log.e("ERROR", "MESNAJE: "+e.getLocalizedMessage());
                    continue;
                }
            }
        }

        public void determinarMaximosYMinimos(Vector[] aux_left, Vector[] aux_left_higher, Vector[] aux_right, Vector[] aux_right_higher) {
            int MIN_RANGO = 360;
            int auxYmax = 0;
            int auxYmin = 1023;

            if (main.izquierda.size() >= datosmostrados) {
                for (int i = 0; i < aux_left.length; i++) {
                    auxYmax = Math.max(auxYmax, aux_left[i].x);
                    auxYmax = Math.max(auxYmax, aux_left[i].y);
                    auxYmax = Math.max(auxYmax, aux_left[i].z);
                    auxYmin = Math.min(auxYmin, aux_left[i].x);
                    auxYmin = Math.min(auxYmin, aux_left[i].y);
                    auxYmin = Math.min(auxYmin, aux_left[i].z);
                }
            }
            if (main.izquierda_higher.size() >= datosmostrados) {
                for (int i = 0; i < aux_left_higher.length; i++) {
                    auxYmax = Math.max(auxYmax, aux_left_higher[i].x);
                    auxYmax = Math.max(auxYmax, aux_left_higher[i].y);
                    auxYmax = Math.max(auxYmax, aux_left_higher[i].z);
                    auxYmin = Math.min(auxYmin, aux_left_higher[i].x);
                    auxYmin = Math.min(auxYmin, aux_left_higher[i].y);
                    auxYmin = Math.min(auxYmin, aux_left_higher[i].z);
                }
            }
            if (main.derecha.size() >= datosmostrados) {
                for (int i = 0; i < aux_right.length; i++) {
                    auxYmax = Math.max(auxYmax, aux_right[i].x);
                    auxYmax = Math.max(auxYmax, aux_right[i].y);
                    auxYmax = Math.max(auxYmax, aux_right[i].z);
                    auxYmin = Math.min(auxYmin, aux_right[i].x);
                    auxYmin = Math.min(auxYmin, aux_right[i].y);
                    auxYmin = Math.min(auxYmin, aux_right[i].z);
                }
            }
            if (main.derecha_higher.size() >= datosmostrados) {
                for (int i = 0; i < aux_right_higher.length; i++) {
                    auxYmax = Math.max(auxYmax, aux_right_higher[i].x);
                    auxYmax = Math.max(auxYmax, aux_right_higher[i].y);
                    auxYmax = Math.max(auxYmax, aux_right_higher[i].z);
                    auxYmin = Math.min(auxYmin, aux_right_higher[i].x);
                    auxYmin = Math.min(auxYmin, aux_right_higher[i].y);
                    auxYmin = Math.min(auxYmin, aux_right_higher[i].z);
                }
            }
            int delta = auxYmax - auxYmin;

            /*
            int auxMin = 1023;
            int auxMax = 0;
            if (main.izquierda.size() >= datosmostrados) {
                auxMin = Math.min(auxMin, aux_left[aux_left.length - 3].x);
                auxMin = Math.min(auxMin, aux_left[aux_left.length - 3].y);
                auxMin = Math.min(auxMin, aux_left[aux_left.length - 3].z);
                auxMax = Math.max(auxMax, aux_left[aux_left.length - 3].y);
                auxMax = Math.max(auxMax, aux_left[aux_left.length - 3].x);
                auxMax = Math.max(auxMax, aux_left[aux_left.length - 3].z);
            }
            if (main.derecha.size() >= datosmostrados) {
                auxMin = Math.min(auxMin, aux_right[aux_right.length - 3].x);
                auxMin = Math.min(auxMin, aux_right[aux_right.length - 3].y);
                auxMin = Math.min(auxMin, aux_right[aux_right.length - 3].z);
                auxMax = Math.max(auxMax, aux_right[aux_right.length - 3].x);
                auxMax = Math.max(auxMax, aux_right[aux_right.length - 3].y);
                auxMax = Math.max(auxMax, aux_right[aux_right.length - 3].z);
            }
            if (main.izquierda_higher.size() >= datosmostrados) {
                auxMin = Math.min(auxMin, aux_left_higher[aux_left_higher.length - 3].x);
                auxMin = Math.min(auxMin, aux_left_higher[aux_left_higher.length - 3].y);
                auxMin = Math.min(auxMin, aux_left_higher[aux_left_higher.length - 3].z);
                auxMax = Math.max(auxMax, aux_left_higher[aux_left_higher.length - 3].x);
                auxMax = Math.max(auxMax, aux_left_higher[aux_left_higher.length - 3].y);
                auxMax = Math.max(auxMax, aux_left_higher[aux_left_higher.length - 3].z);
            }
            if (main.derecha_higher.size() >= datosmostrados) {
                auxMin = Math.min(auxMin, aux_right_higher[aux_right_higher.length - 3].x);
                auxMin = Math.min(auxMin, aux_right_higher[aux_right_higher.length - 3].y);
                auxMin = Math.min(auxMin, aux_right_higher[aux_right_higher.length - 3].z);
                auxMax = Math.max(auxMax, aux_right_higher[aux_right_higher.length - 3].x);
                auxMax = Math.max(auxMax, aux_right_higher[aux_right_higher.length - 3].y);
                auxMax = Math.max(auxMax, aux_right_higher[aux_right_higher.length - 3].z);
            }
            */

            maxShowed = (int) escalaY(auxYmax);
            minShowed = (int) escalaY(auxYmin);
            max = (auxYmax - 511) * 0.0934285;
            min = (auxYmin - 511) * 0.0934285;



            if (delta > MIN_RANGO) {
                Ymax = auxYmax + (int) (delta * 0.05f);
                Ymin = auxYmin - (int) (delta * 0.05f);
            } else {
                float mult = (MIN_RANGO * 1.05f / delta) - 1;
                Ymax = auxYmax + (int) (delta * mult / 2);
                Ymin = auxYmin - (int) (delta * mult / 2);
            }
            float auxPixelCero = escalaY(511);
            pixelCero = (int) auxPixelCero;


            float deltaAmp = Ymax - Ymin;
            float unit = deltaAmp / HEIGHT; // -> amplitud / pixeles
            double auxamp1 = 511 + (int) (unit * HEIGHT / 5);
            double auxamp2 = 511 + (int) (2 * unit * HEIGHT / 5);
            double auxamp3 = 511 + (int) (3 * unit * HEIGHT / 5);
            double auxamp4 = 511 + (int) (4 * unit * HEIGHT / 5);
            double auxamp5 = 511 + (int) (5 * unit * HEIGHT / 5);
            double auxamp6 = 511 + (int) (6 * unit * HEIGHT / 5);
            double auxamp7 = 511 + (int) (7 * unit * HEIGHT / 5);
            double auxamp8 = 511 + (int) (8 * unit * HEIGHT / 5);
            double auxampmenos1 = 511 - (int) (unit * HEIGHT / 5);
            double auxampmenos2 = 511 - (int) (2 * unit * HEIGHT / 5);
            double auxampmenos3 = 511 - (int) (3 * unit * HEIGHT / 5);
            double auxampmenos4 = 511 - (int) (4 * unit * HEIGHT / 5);
            double auxampmenos5 = 511 - (int) (5 * unit * HEIGHT / 5);
            double auxampmenos6 = 511 - (int) (6 * unit * HEIGHT / 5);
            double auxampmenos7 = 511 - (int) (7 * unit * HEIGHT / 5);
            double auxampmenos8 = 511 - (int) (8 * unit * HEIGHT / 5);

            amp1 = (auxamp1 - 511) * 0.0934285;
            amp2 = (auxamp2 - 511) * 0.0934285;
            amp3 = (auxamp3 - 511) * 0.0934285;
            amp4 = (auxamp4 - 511) * 0.0934285;
            amp5 = (auxamp5 - 511) * 0.0934285;
            amp6 = (auxamp6 - 511) * 0.0934285;
            amp7 = (auxamp7 - 511) * 0.0934285;
            amp8 = (auxamp8 - 511) * 0.0934285;
            ampmenos1 = (auxampmenos1 - 518) * 0.0934285;
            ampmenos2 = (auxampmenos2 - 518) * 0.0934285;
            ampmenos3 = (auxampmenos3 - 518) * 0.0934285;
            ampmenos4 = (auxampmenos4 - 518) * 0.0934285;
            ampmenos5 = (auxampmenos5 - 518) * 0.0934285;
            ampmenos6 = (auxampmenos6 - 518) * 0.0934285;
            ampmenos7 = (auxampmenos7 - 518) * 0.0934285;
            ampmenos8 = (auxampmenos8 - 518) * 0.0934285;
        }

        private float escalaX(ArrayList<Vector> vector, int t) {
            float tfin = vector.get(vector.size() - 2).t;
            float tini = vector.get(vector.size() - datosmostrados).t;
            float deltat = tfin - tini;
            float unit = deltat / WIDTH;
            float pixeles = (t - vector.get(vector.size() - datosmostrados).t) / unit;
            return pixeles;
        }

        private float escalaY(int y) {
            float deltaAmp = Ymax - Ymin;
            float unit = deltaAmp / HEIGHT; // -> amplitud / pixeles
            float pixeles = (Ymax - y) / unit; // -> pixeles
            return pixeles;
        }

        public synchronized Vector[] getLeft() {
            return left;
        }

        public synchronized Vector[] getRight() {
            return right;
        }

        public synchronized Vector[] getLeftHigher() {
            return left_higher;
        }

        public synchronized Vector[] getRightHigher() {
            return right_higher;
        }

    }
}
