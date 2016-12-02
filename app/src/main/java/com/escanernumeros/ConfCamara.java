package com.escanernumeros;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Marines on 15/09/2016.
 */

    public class ConfCamara {

        boolean Encendido;
        //Camara librería de Hardware
        Camera camara;
        //Es el modulo que comunica la camara con el layout
        SurfaceHolder surfaceHolder;

    //Metodo para verificar si el dispositivo es compatible con el enfoque automatico
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    //Metodo para verificar si la camara esta encendida
    public boolean estaEncendido() {

        return Encendido;
    }

    //Constructor que recive una variable tipo SurfaceHolder esta
    //permitira controlar el tamaño y formato de superficie
    private ConfCamara(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public ConfCamara Nuevo(SurfaceHolder surfaceHolder){
        return  new ConfCamara(surfaceHolder);
    }

    public void enfoque() {
        if (camara == null)
            return;

        if (estaEncendido()) {

            //Si la camara esta encendida entonces agregamos el enfoque
            //Se coloca en un try catch por si la camara no cuenta con enfoque
            try {
                camara.autoFocus(autoFocusCallback);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void start() {
        //Iniciamos la camara
        this.camara = getCamara();
        //verificamos si la camara se encendio correctamente si esta vacia que no regrese nada
        if (this.camara == null) {
            return;
        }

        try {

            //Para vizualizar en tiempo real
            this.camara.setPreviewDisplay(this.surfaceHolder);
            //Para asegurar la correcta orientación de la vista previa.
            this.camara.setDisplayOrientation(90);
            //iniciar la vista
            this.camara.startPreview();

            Encendido = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if(camara != null){
            camara.release();
            camara = null;

        //apagamos la camara;
        }

        Encendido = false;

    }
//ShutterCallback señalar el momento de la captura de la imagen en tiempo real.

//PictureCallbackla devolución de llamada para datos de imagen sin comprimir

//Devolucion de datos de imagen
                         public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){
        if(estaEncendido()){
            camara.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }

    //habilitar la camara
    public static Camera getCamara() {
        try {
            return Camera.open();

        } catch (Exception e){
        return null;
        }
    }
}
