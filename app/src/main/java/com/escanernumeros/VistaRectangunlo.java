package com.escanernumeros;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * Created by Marines on 15/09/2016.
 */

public class VistaRectangunlo extends View {


    //Limiter de nuestro rectangulo al juntar
    private static final int LimiteAncho = 90;
    private static final int LimiteAlto = 50;

    private final Paint paint;
    private final int exRectangulo;
    private final int inRectangulo;
    private final int punRectangulo;

    public VistaRectangunlo(Context context, AttributeSet attrs) {
        super(context, attrs);


        //Agregamos anti_alias_flag para trabajar con los bordes de las forma en canvas para que no queden como en minecraft
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //Color en la parte exterior del rectangulo
        exRectangulo = Color.parseColor("#60000000");
        //Color dentro del rectangulo
        inRectangulo = Color.parseColor("#ffd6d6d6");
        //Color en los puntos del rectangulo
        punRectangulo =Color.parseColor("#09B649");
        this.setOnTouchListener(getTouchListener());
    }

    //Creamos una variable para el rectangulo
    private Rect Rectangulo;

    //Creamos una variable para los puntos de los esquineros
    private static Point Puntos;

    //Metodo para el rectangulo
    private  Rect getCanvasRectangulo() {

        //Verificamos si la variable es nula
        if (Rectangulo == null) {

            Puntos = Utilities.geTamañoPantalla(getContext());

            //El punto contiene dos cordenada X - Y

            //Obtenemos el tamaño de la pantalla X - Y
            //Y ocupara la 3 parte de la pantalla
            //X ocupara una 12 parte doceava parte de la pantalla
            //Para mostrar el los puntos del principio

            int alto = Puntos.x / 12;
            int largo = Puntos.y / 3;

            int X = (Puntos.x - largo) / 2;//X
            int Y = (Puntos.y - alto) / 2;//Y
            int anchoR=X+largo;//Ancho
            int altoR=Y+alto;//alto

            //Rectangulo con las cordenadas especificas
            //Rec(x, y,ancho, alto)
            /*
       (X,Y)+-----------|   A
            |           |   L
            |           |   T
            |-----------|   O
              Ancho               X  Y     Ancho   alto  */
            Rectangulo = new Rect(X, Y, anchoR, altoR);
                            }
                            return Rectangulo;
                    }

                public Rect getRectangulo() {
                    return Rectangulo;
                }





                private void actualizarRectangulo(int ancho, int alto) {

                    //Verificar que el nuevo largo respete el limite del alto definido anteriormente y
                    //Verificar que el nuevo alto respete el limite del alto definido anteriormente
                    int NuevoLargo = (Rectangulo.width() + ancho > Puntos.x - 4 || Rectangulo.width() + ancho < LimiteAncho)
                            ? 0: Rectangulo.width() + ancho;

                    int NuevoAlto = (Rectangulo.height() + alto > Puntos.y - 4 || Rectangulo.height() + alto < LimiteAlto)
                            ? 0
                            : Rectangulo.height() + alto;



                    int X = (Puntos.x - NuevoLargo) / 2;

                    int Y = (Puntos.y - NuevoAlto) / 2;

                    if (NuevoLargo < LimiteAncho || NuevoAlto < LimiteAlto)
                        return;

                    int AnchoR=X + NuevoLargo;
                    int altoR=Y + NuevoAlto;
                    //Rec(x, y,ancho, alto)
                    Rectangulo = new Rect(X, Y, AnchoR, altoR);
                }

                private OnTouchListener touchListener;

                private OnTouchListener getTouchListener() {

                    if (touchListener == null)
                        touchListener = new OnTouchListener() {

                            //cacha el ultimo movimiento
                            int ultimoTouchX = 0;
                            int ultimoTouchY = 0;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        ultimoTouchX =0;
                                        ultimoTouchY = 0;
                                        return true;
                                    case MotionEvent.ACTION_MOVE:
                                        //SE agregan la posicion de X y Y al tocar
                                        int touchX = (int) event.getX();
                                        int touchY = (int) event.getY();
                                        try {
                                            Rect rect = getCanvasRectangulo();
                                            final int BUFFER = 50;
                                            final int BIG_BUFFER = 60;
                                //Tiene que ser mayor a 0 eso significa que se movio el rectangulo
                                if (ultimoTouchX > 0) {
                         // Ajusta el tamaño del rectángulo del visor con los puntos encontrados en los esquineros
                                    if (((touchX >= rect.left - BIG_BUFFER
                                            && touchX <= rect.left + BIG_BUFFER)
                                            || (ultimoTouchX >= rect.left - BIG_BUFFER
                                            && ultimoTouchX <= rect.left + BIG_BUFFER))
                                            && ((touchY <= rect.top + BIG_BUFFER
                                            && touchY >= rect.top - BIG_BUFFER)
                                            || (ultimoTouchY <= rect.top + BIG_BUFFER
                                            && ultimoTouchY >= rect.top - BIG_BUFFER))) {
                                // Esquina superior izquierda: ajuste ambos lados superior e izquierdo

                                        /*
                                        X-----------|
                                        |           |
                                        |           |
                                        |-----------|

                                        */
                                        actualizarRectangulo(2 * (ultimoTouchX - touchX),
                                                2 * (ultimoTouchY - touchY));

                                    } else if (((touchX >= rect.right - BIG_BUFFER
                                            && touchX <= rect.right + BIG_BUFFER)
                                            || (ultimoTouchX >= rect.right - BIG_BUFFER
                                            && ultimoTouchX <= rect.right + BIG_BUFFER))
                                            && ((touchY <= rect.top + BIG_BUFFER
                                            && touchY >= rect.top - BIG_BUFFER)
                                            || (ultimoTouchY <= rect.top + BIG_BUFFER
                                            && ultimoTouchY >= rect.top - BIG_BUFFER))) {

                                        // Esquina superior derecha: ajuste ambos lados inferior e izquierdo

                                        /*
                                        | ----------X
                                        |           |
                                        |           |
                                        |-----------|
                                        */
                                        actualizarRectangulo(2 * (touchX - ultimoTouchX),
                                                2 * (ultimoTouchY - touchY));

                                    } else if (((touchX >= rect.left - BIG_BUFFER
                                            && touchX <= rect.left + BIG_BUFFER)
                                            || (ultimoTouchX >= rect.left - BIG_BUFFER
                                            && ultimoTouchX <= rect.left + BIG_BUFFER))
                                            && ((touchY <= rect.bottom + BIG_BUFFER
                                            && touchY >= rect.bottom - BIG_BUFFER)
                                            || (ultimoTouchY <= rect.bottom + BIG_BUFFER
                                            && ultimoTouchY >= rect.bottom - BIG_BUFFER))) {

                                        // Esquina inferior izquierda: ajuste ambos lados inferior e izquierdo

                                        /*
                                        | ----------|
                                        |           |
                                        |           |
                                        X-----------|
                                        */

                                        actualizarRectangulo(2 * (ultimoTouchX - touchX),
                                                2 * (touchY - ultimoTouchY));


                                    } else if (((touchX >= rect.right - BIG_BUFFER
                                            && touchX <= rect.right + BIG_BUFFER)
                                            || (ultimoTouchX >= rect.right - BIG_BUFFER
                                            && ultimoTouchX <= rect.right + BIG_BUFFER))
                                            && ((touchY <= rect.bottom + BIG_BUFFER
                                            && touchY >= rect.bottom - BIG_BUFFER)
                                            || (ultimoTouchY <= rect.bottom + BIG_BUFFER
                                            && ultimoTouchY >= rect.bottom - BIG_BUFFER))) {


                                        // Esquina inferior derecha: ajuste ambos lados inferior y derech
                                         /*
                                        |-----------|
                                        |           |
                                        |           |
                                        |-----------X
                                        */
                                        actualizarRectangulo(2 * (touchX - ultimoTouchX),
                                                2 * (touchY - ultimoTouchY));


                                    } else if (((touchX >= rect.left - BUFFER
                                            && touchX <= rect.left + BUFFER)
                                            || (ultimoTouchX >= rect.left - BUFFER
                                            && ultimoTouchX <= rect.left + BUFFER))
                                            && ((touchY <= rect.bottom
                                            && touchY >= rect.top)
                                            || (ultimoTouchY <= rect.bottom
                                            && ultimoTouchY >= rect.top))) {


                                        // Ajuste del lado izquierdo

                                        /*
                                        +-----------|
                                        +           |
                                        +           |
                                        +-----------|
                                        */
                                        actualizarRectangulo(2 * (ultimoTouchX - touchX), 0);

                                    } else if (((touchX >= rect.right - BUFFER
                                            && touchX <= rect.right + BUFFER)
                                            || (ultimoTouchX >= rect.right - BUFFER
                                            && ultimoTouchX <= rect.right + BUFFER))
                                            && ((touchY <= rect.bottom
                                            && touchY >= rect.top)
                                            || (ultimoTouchY <= rect.bottom
                                            && ultimoTouchY >= rect.top))) {

                                        // Ajuste del lado derecho

                                        /*
                                        |-----------+
                                        |           +
                                        |           +
                                        |-----------+
                                        */
                                        actualizarRectangulo(2 * (touchX - ultimoTouchX), 0);

                                    } else if (((touchY <= rect.top + BUFFER
                                            && touchY >= rect.top - BUFFER)
                                            || (ultimoTouchY <= rect.top + BUFFER
                                            && ultimoTouchY >= rect.top - BUFFER))
                                            && ((touchX <= rect.right
                                            && touchX >= rect.left)
                                            || (ultimoTouchX <= rect.right
                                            && ultimoTouchX >= rect.left))) {

                                        // Ajuste de la parte superior

                                         /*
                                        |+++++++++++|
                                        |           |
                                        |           |
                                        |-----------|
                                        */
                                        actualizarRectangulo(0, 2 * (ultimoTouchY - touchY));

                                    } else if (((touchY <= rect.bottom + BUFFER
                                            && touchY >= rect.bottom - BUFFER)
                                            || (ultimoTouchY <= rect.bottom + BUFFER
                                            && ultimoTouchY >= rect.bottom - BUFFER))
                                            && ((touchX <= rect.right
                                            && touchX >= rect.left)
                                            || (ultimoTouchX <= rect.right
                                            && ultimoTouchX >= rect.left))) {

                                        // Ajuste de la parte inferior

                                         /*
                                        |-----------|
                                        |           |
                                        |           |
                                        |+++++++++++|
                                        */
                                        actualizarRectangulo(0, 2 * (touchY - ultimoTouchY));
                                    }
                                }
                            } catch (NullPointerException e) {
                            }
                            v.invalidate();
                            ultimoTouchX = touchX;
                            ultimoTouchY = touchY;

                            return true;
                        case MotionEvent.ACTION_UP:
                            ultimoTouchX = 0;
                            ultimoTouchY = 0;
                            return true;
                    }
                    return false;
                }
            };

        return touchListener;
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = getCanvasRectangulo();

        int width = Puntos.x;
        int height = Puntos.y;


        //Pintar rectangulo exterior
        paint.setColor(exRectangulo);
        //Pintamos encima del rectangulo interior
        canvas.drawRect(0, 0, width, frame.top, paint);
        //Pintamos a la izquierda del rectangulo interior
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        //Pintamos a la derecha del rectangulo interior
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        //Pintamos debajo del rectangulo interior
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        //Pintar rectangulo interior
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(inRectangulo);
        //Pintar linea superior
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        //Pintar linea izquierda
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        //Pintar linea derecha
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        //Pintar linea inferior
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);


        //Pintar puntos
        paint.setColor(punRectangulo);
        //                     X             Y   Radio mitad del circulo  pintamos
        canvas.drawCircle(frame.left - 40, frame.top - 40, 40, paint);
        canvas.drawCircle(frame.right + 40, frame.top - 40, 40, paint);
        canvas.drawCircle(frame.left - 40, frame.bottom + 40, 40, paint);
        canvas.drawCircle(frame.right + 40, frame.bottom + 40, 40, paint);

    }
}
