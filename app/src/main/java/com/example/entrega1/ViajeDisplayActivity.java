package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entrega1.entity.Util;
import com.example.entrega1.entity.Viaje;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class ViajeDisplayActivity extends AppCompatActivity {

    private TextView textViewTitulo, textViewFechaLlegada, textViewFechaSalida, textViewLugarSalida,
    textViewPrecio, textViewDescription, textViewDistancia;
    private ImageView imageViewViaje, imageViewStar;
    private Viaje viaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje_display);
        textViewTitulo = findViewById(R.id.textViewDisplayTitulo);
        textViewFechaLlegada = findViewById(R.id.textViewDisplayFechaLlegada);
        textViewFechaSalida = findViewById(R.id.textViewDisplayFechaSalida);
        textViewLugarSalida = findViewById(R.id.textViewDisplayLugarSalida);
        textViewPrecio = findViewById(R.id.textViewDisplayPrecio);
        imageViewStar = findViewById(R.id.imageViewStar);
        imageViewViaje = findViewById(R.id.imageViaje);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewDistancia = findViewById(R.id.textView_DistanciaDisplay);

        Intent intent = getIntent();
        if (intent != null) {
            viaje = (Viaje) intent.getSerializableExtra("Viaje");
            Picasso.get().load(viaje.getUrl()).into(imageViewViaje);
            textViewTitulo.setText(viaje.getNombre());
            textViewFechaLlegada.setText(Util.formateaFecha(viaje.getFechasFin()));
            textViewFechaSalida.setText(Util.formateaFecha(viaje.getFechasInicio()));
            textViewLugarSalida.setText(viaje.getLugarSalida());
            textViewPrecio.setText(Long.toString(viaje.getPrecio()));
            textViewDescription.setText(viaje.getDescripcion());
            textViewDistancia.setText(Integer.toString((int) viaje.getDistanciaUsuarioSalida()) + " Km");
            setImageViewStar();
        }
    }

    public void setImageViewStar(){
        if (!viaje.isSeleccionado()) {
            Drawable image = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
            imageViewStar.setImageDrawable(image);
        } else {
            Drawable image = getResources().getDrawable(R.drawable.ic_star_black_24dp);
            imageViewStar.setImageDrawable(image);
        }
    }

    public void onSelect(View view) {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        if (!viaje.isSeleccionado()) {
            firebaseDatabaseService.addUserTravel(viaje.getId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null ) {
                        viaje.setSeleccionado(!viaje.isSeleccionado());
                        setImageViewStar();
                    } else {
                        Log.i("App", "Error viaje no insertado");
                    }
                }
            });
        } else {
            firebaseDatabaseService.deleteUserTravel(viaje.getId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null ) {
                        viaje.setSeleccionado(!viaje.isSeleccionado());
                        setImageViewStar();
                    } else {
                        Log.i("App", "Error viaje no insertado");
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
