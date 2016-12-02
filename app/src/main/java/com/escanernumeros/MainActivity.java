package com.escanernumeros;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Marines on 15/09/2016.
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        Camera.PictureCallback, Camera.ShutterCallback {

        Toolbar toolbar;
        VistaRectangunlo rectangulo;
        //Layout donde se vizualizara la imagen de la camara
        SurfaceView surfaceView;
        //Obtener la confCamara de la camara
        ConfCamara confCamara;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        }



        @Override
        public void surfaceCreated(SurfaceHolder holder) {



            //si la camara esta apagada pero hay una configuracion iniciamos la camara
            if (confCamara != null && !confCamara.estaEncendido()) { //Solo es necesario iniciar la camara
                confCamara.start();
            }

            //si la camara esta encendida y hay una configuracion solo retornamos
            if (confCamara != null && confCamara.estaEncendido()) {// Todo bien
                return;
            }

            //Si no cumple ninguna condicion entonces creamos una configuracion e iniciamos
            confCamara = ConfCamara.Nuevo(holder);//Tenemos que crear una nueva vista e inicar
            confCamara.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        @Override
        protected void onResume() {
            super.onResume();

            //llamamos la vista de la camara
            surfaceView = (SurfaceView) findViewById(R.id.camera_frame);

            //llamamos el rectangulo
            rectangulo = (VistaRectangunlo) findViewById(R.id.focus_box);


            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        protected void onPause() {
            super.onPause();

            //Verificamos que exista una configuracion y que este encendido entonces detenemos
            if (confCamara != null && confCamara.estaEncendido()) {
                confCamara.stop();
            }

            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        //Si no tomo nada entonces no se retorna nada
        if (data == null) {
            return;
        }

        //Si se tomo la foto data contiene algo entonces obtenemos la imagen del rectangulo
        Bitmap bmp = Tools.getImagenEnfocada(this, camera, data, rectangulo.getRectangulo());


        //Iniciamos el Asyntask serial_executor ejecutar una tarea a la vez
        new TessAsyncEngine().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

    }

    @Override
    public void onShutter() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camara:
                    if(confCamara != null && confCamara.estaEncendido()){
                        confCamara.takeShot(this, this, this);
                    }

                return true;
            case R.id.enfoque:
                if(confCamara!=null && confCamara.estaEncendido()){
                    confCamara.enfoque();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public String detectarTexto(Bitmap bitmap) {
        //
        String listaNegra="!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?";


        String listaBlanca="1234567890";
        TessDataManager.initTessTrainedData(getApplicationContext());
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path =TessDataManager.getTesseractFolder();
        //Modo depuracion para tener un control al reconocer los caracteres
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng"); //Iniciara en el idioma ingles

        //Separar palabras
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT);

        //Agregar la imagen a la libreria
        tessBaseAPI.setImage(bitmap);

        //Lista blanco donde vamos a permitir solo los caracteres mencionados
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,listaBlanca );

        //Lista negrado donde se restringen los caracteres mencionados
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, listaNegra);

        //Aplicamos formato y nos regresa el texto de la imagen
        String text = tessBaseAPI.getUTF8Text();
        //Fianlizamos elTessBase
        tessBaseAPI.end();

        //Retornamos el texto detectado
        return text;
    }

    public class TessAsyncEngine extends AsyncTask<Object, Void, String> {
        private Bitmap bmp;
        private Activity context;


        @Override
        protected String doInBackground(Object... params) {

            try {

                if(params.length < 2) {
                    return null;
                }

                //utilizar instanceof para verificar si parametro 0 es tipo activity y parametro 1 es imagen sino  retornar null
                if(!(params[0] instanceof Activity) || !(params[1] instanceof Bitmap)) {
                    return null;
                }

                context = (Activity)params[0];

                bmp = (Bitmap)params[1];

                if(context == null || bmp == null) {
                    return null;
                }
                String result = detectarTexto(bmp);
                System.out.println("Haciendo "+result);
                return result;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }


            @Override
        protected void onPostExecute(String s) {

            if(s == null || bmp == null || context == null)
                return;

            Intent vista = new Intent(getApplicationContext(), VistaResultado.class);
            vista.putExtra("imagen",bmp);
            vista.putExtra("titulo",s);
            startActivity(vista);
            super.onPostExecute(s);
        }
    }
}