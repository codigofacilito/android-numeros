package com.escanernumeros;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by Marines on 15/09/2016.
 */
 public class VistaResultado extends AppCompatActivity {

    private Bitmap bmp;
    private String titulo;
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.image_dialog);
         //Obtenemos la imagen enviada
        bmp =getIntent().getParcelableExtra("imagen");
         //Obtenemos el titulo enviado
        titulo = getIntent().getStringExtra("titulo");
         System.out.println("titulo "+titulo);
         System.out.println("imagen "+bmp);
         //Llamar vistas
        ImageView imageView = (ImageView) findViewById(R.id.image_dialog_imageView);
        TextView textView = (TextView) findViewById(R.id.image_dialog_textView);

         //Verificar si la imagen y el titulo no estan nulos
        if (bmp != null)
            imageView.setImageBitmap(bmp);

        if(titulo!=null)
            textView.setText(this.titulo);


    }





 }