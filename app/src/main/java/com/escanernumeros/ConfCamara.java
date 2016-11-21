package com.escanernumeros;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

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

    public boolean EstaEncendido() {

        return Encendido;
    }

    //Constructor que recive una variable tipo SurfaceHolder esta
    //permitira controlar el tamaño y formato de superficie, editar los píxeles en la superficie, y monitorizar
    // los cambios en la superficie.
    private ConfCamara(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public ConfCamara Nuevo(SurfaceHolder surfaceHolder){
        return  new ConfCamara(surfaceHolder);
    }

    public void requestFocus() {
        if (camara == null)
            return;

        if (EstaEncendido()) {
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

            this.camara.setPreviewDisplay(this.surfaceHolder);
            this.camara.setDisplayOrientation(90);//Portrait Camera
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
        }

        Encendido = false;

    }
//ShutterCallback señalar el momento de la captura de la imagen en tiempo real.
    // devolución de llamada utilizado para suministrar datos de imagen a partir de una captura de fotos.

    public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){
        if(EstaEncendido()){
            camara.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }

    //habilitar la camara
    public static Camera getCamara() {
        try {
            return Camera.open();

        } catch (Exception e) {
            return null;
        }
    }
}
