package com.escanernumeros;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class FocusBoxView extends View {


    //Limiter de nuestro rectangulo al juntar
    private static final int LimiteLargo = 90;
    private static final int LimiteAlto = 50;

    private final Paint paint;
    private final int ExRectangulo;
    private final int InRectangulo;
    private final int PunRectangulo;

    public FocusBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //Color en la parte exterior del rectangulo
        ExRectangulo = Color.parseColor("#60000000");
        //Color dentro del rectangulo
        InRectangulo = Color.parseColor("#ffd6d6d6");
        //Color en los puntos del rectangulo
        PunRectangulo =Color.parseColor("#09B649");
        this.setOnTouchListener(getTouchListener());
    }

    //Creamos una variable para el rectangulo
    private Rect Rectangulo;

    //Creamos una variable para los puntos de los esquineros
    private static Point Puntos;

    //Metodo para el rectangulo
    private  Rect getBoxRect() {

        //Verificamos si la variable es nula
        if (Rectangulo == null) {
            
            Puntos = FocusBoxUtils.geTamañoPantalla(getContext());

            //El punto contiene dos cordenada X - Y

            //Obtenemos el tamaño de la pantalla X - Y
            //X ocupara la 3 parte de la pantalla
            //Y ocupara una 12 parte doceava parte de la pantalla
            //Para mostrar el los puntos del principio

            int alto = Puntos.x / 3;
            int largo = Puntos.y / 12;

            largo = largo == 0 ? LimiteLargo : largo < LimiteLargo ? LimiteLargo : largo;

            alto = alto == 0 ? LimiteAlto : alto < LimiteAlto ? LimiteAlto : alto;

            int izquierdo = (Puntos.x - largo) / 2;//X
            int arriba = (Puntos.y - alto) / 2;//Y
            int derecha=izquierdo+largo;//X
            int abajo=arriba+alto;//Y

            //Rectangulo con las cordenadas especificas
            //Rec(izquierda, arriba,derecha, abajo)
            Rectangulo = new Rect(izquierdo, arriba, derecha, abajo);
        }
        return Rectangulo;
    }

    public Rect getRectangulo() {
        return Rectangulo;
    }

    private void ActualizarRectangulo(int largo, int alto) {

        
        //Verificar que el nuevo largo no sobrepase el largo definido anteriormente si lo sobre pasa el nuevo sera 0
        //pero si no lo el tamaño anterior se le agregara el nuevo
        int NuevoLargo = (Rectangulo.width() + largo > Puntos.x - 4 || Rectangulo.width() + largo < LimiteLargo)
                ? 0: Rectangulo.width() + largo;

        //Verificar que el nuevo alto  no sobrepase el alto definido anteriormente si lo sobre pasa el nuevo sera 0
        //pero si no lo el tamaño anterior se le agregara el nuevo
        int NuevoAlto = (Rectangulo.height() + alto > Puntos.y - 4 || Rectangulo.height() + alto < LimiteAlto)
                ? 0
                : Rectangulo.height() + alto;

        int izquierda = (Puntos.x - NuevoLargo) / 2;

        int arriba = (Puntos.y - NuevoAlto) / 2;

        if (NuevoLargo < LimiteLargo || NuevoAlto < LimiteAlto)
            return;

        int derecha=izquierda + NuevoLargo;
        int abajo=arriba + NuevoAlto;
        //Rec(izquierda, arriba,derecha, abajo)
        Rectangulo = new Rect(izquierda, arriba, derecha, abajo);
    }

    private OnTouchListener touchListener;

    private OnTouchListener getTouchListener() {

        if (touchListener == null)
            touchListener = new OnTouchListener() {

                int lastX = 0;
                int lastY = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = 0;
                            lastY = 0;

                            System.out.println("touchR abajo x "+lastX);
                            System.out.println("touchR abajo y "+lastY);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int currentX = (int) event.getX();
                            int currentY = (int) event.getY();
                            try {
                                Rect rect = getBoxRect();
                                final int BUFFER = 50;
                                final int BIG_BUFFER = 60;
                                //Tiene que ser mayor a 0 eso significa que se movio el rectangulo
                                if (lastX > 0) {
                         // Ajusta el tamaño del rectángulo del visor con los puntos encontrados en los esquineros
                                    if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {
                                // Esquina superior izquierda: ajuste ambos lados superior e izquierdo

                                        /*
                                        X-----------|
                                        |           |
                                        |           |
                                        |-----------|

                                        */

                                        ActualizarRectangulo(2 * (lastX - currentX),
                                                2 * (lastY - currentY));

                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {

                                        // Esquina superior derecha: ajuste ambos lados inferior e izquierdo

                                        /*
                                        | ----------X
                                        |           |
                                        |           |
                                        |-----------|
                                        */
                                        ActualizarRectangulo(2 * (currentX - lastX),
                                                2 * (lastY - currentY));

                                    } else if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {

                                        // Esquina inferior izquierda: ajuste ambos lados inferior e izquierdo

                                        /*
                                        | ----------|
                                        |           |
                                        |           |
                                        X-----------|
                                        */
                                        ActualizarRectangulo(2 * (lastX - currentX),
                                                2 * (currentY - lastY));


                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {


                                        // Esquina inferior derecha: ajuste ambos lados inferior y derech
                                         /*
                                        |-----------|
                                        |           |
                                        |           |
                                        |-----------X
                                        */
                                        ActualizarRectangulo(2 * (currentX - lastX),
                                                2 * (currentY - lastY));


                                    } else if (((currentX >= rect.left - BUFFER
                                            && currentX <= rect.left + BUFFER)
                                            || (lastX >= rect.left - BUFFER
                                            && lastX <= rect.left + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {


                                        // Ajuste del lado izquierdo

                                        /*
                                        +-----------|
                                        +           |
                                        +           |
                                        +-----------|
                                        */
                                        ActualizarRectangulo(2 * (lastX - currentX), 0);

                                    } else if (((currentX >= rect.right - BUFFER
                                            && currentX <= rect.right + BUFFER)
                                            || (lastX >= rect.right - BUFFER
                                            && lastX <= rect.right + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {

                                        // Ajuste del lado derecho

                                        /*
                                        |-----------+
                                        |           +
                                        |           +
                                        |-----------+
                                        */
                                        ActualizarRectangulo(2 * (currentX - lastX), 0);

                                    } else if (((currentY <= rect.top + BUFFER
                                            && currentY >= rect.top - BUFFER)
                                            || (lastY <= rect.top + BUFFER
                                            && lastY >= rect.top - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {

                                        // Ajuste de la parte superior

                                         /*
                                        |+++++++++++|
                                        |           |
                                        |           |
                                        |-----------|
                                        */
                                        ActualizarRectangulo(0, 2 * (lastY - currentY));

                                    } else if (((currentY <= rect.bottom + BUFFER
                                            && currentY >= rect.bottom - BUFFER)
                                            || (lastY <= rect.bottom + BUFFER
                                            && lastY >= rect.bottom - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {

                                        // Ajuste de la parte inferior

                                         /*
                                        |-----------|
                                        |           |
                                        |           |
                                        |+++++++++++|
                                        */
                                        ActualizarRectangulo(0, 2 * (currentY - lastY));
                                    }
                                }
                            } catch (NullPointerException e) {
                            }
                            v.invalidate();
                            lastX = currentX;
                            lastY = currentY;

                            return true;
                        case MotionEvent.ACTION_UP:
                            lastX = 0;
                            lastY = 0;
                            return true;
                    }
                    return false;
                }
            };

        return touchListener;
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = getBoxRect();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(ExRectangulo);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        paint.setAlpha(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(InRectangulo);
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

        paint.setColor(PunRectangulo);
        canvas.drawCircle(frame.left - 32, frame.top - 32, 32, paint);
        canvas.drawCircle(frame.right + 32, frame.top - 32, 32, paint);
        canvas.drawCircle(frame.left - 32, frame.bottom + 32, 32, paint);
        canvas.drawCircle(frame.right + 32, frame.bottom + 32, 32, paint);

    }
}
