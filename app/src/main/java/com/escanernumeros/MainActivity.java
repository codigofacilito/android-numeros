package com.escanernumeros;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        Camera.PictureCallback, Camera.ShutterCallback {



    FocusBoxView focusBox;
    //Layout donde se vizualizara la imagen de la camara
    SurfaceView surfaceView;
    //Obtener la confCamara de la camara
    ConfCamara confCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //Verificamos que la camara este encendida e iniciamos
        if (confCamara != null && !confCamara.EstaEncendido()) {
            confCamara.start();
        }

        if (confCamara != null && confCamara.EstaEncendido()) {
            return;
        }

        confCamara = ConfCamara.Nuevo(holder);
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

        surfaceView = (SurfaceView) findViewById(R.id.camera_frame);
        focusBox = (FocusBoxView) findViewById(R.id.focus_box);


        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (confCamara != null && confCamara.EstaEncendido()) {
            confCamara.stop();
        }

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data == null) {
            return;
        }

        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getRectangulo());


        new TessAsyncEngine().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

    }

    @Override
    public void onShutter() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camara:
                    if(confCamara != null && confCamara.EstaEncendido()){
                        confCamara.takeShot(this, this, this);
                    }

                    if(confCamara!=null && confCamara.EstaEncendido()){
                        confCamara.requestFocus();
                }
                return true;
            case R.id.enfoque:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public String detectText(Bitmap bitmap) {

        TessDataManager.initTessTrainedData(getApplicationContext());
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path =TessDataManager.getTesseractFolder();

        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng"); //Init the Tess with the trained data file, with english language

        //Separar palabras
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT);

        //Agregar la imagen a la libreria
        tessBaseAPI.setImage(bitmap);

        //Lista blanco donde vamos a permitir solo los caracteres mencionados
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");

        //Lista negrado donde se restringen los caracteres mencionados
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");

        //Aplicamos formato y nos regresa el texto de la imagen
        String text = tessBaseAPI.getUTF8Text();
        System.out.println("text1 "+text);
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

                if(!(params[0] instanceof Activity) || !(params[1] instanceof Bitmap)) {
                    return null;
                }

                context = (Activity)params[0];

                bmp = (Bitmap)params[1];

                if(context == null || bmp == null) {
                    return null;
                }

                int rotate = 0;

                if(params.length == 3 && params[2]!= null && params[2] instanceof Integer){
                    rotate = (Integer) params[2];
                }

                if(rotate >= -180 && rotate <= 180 && rotate != 0)
                {
                    bmp = Tools.preRotateBitmap(bmp, rotate);
                }
                String result = detectText(bmp);
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