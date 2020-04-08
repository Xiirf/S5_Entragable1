package com.example.entrega1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entrega1.entity.Util;
import com.example.entrega1.entity.Viaje;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class ViajeDisplayActivity extends AppCompatActivity {

    TextView textViewTitulo, textViewFechaLlegada, textViewFechaSalida, textViewLugarSalida,
    textViewPrecio, textViewDescription;
    ImageView imageViewViaje, imageViewStar;
    Viaje viaje;


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
        viaje.setSeleccionado(!viaje.isSeleccionado());
        // For the moment we dont have API so we have to actualize the trip list in preferences
        List<Viaje> viajes;
        SharedPreferences prefs = this.getSharedPreferences("Viaje", Context.MODE_PRIVATE);
        String viajesJSONString = prefs.getString("viajes", null);
        Type type = new TypeToken<List<Viaje>>() {}.getType();
        viajes = new Gson().fromJson(viajesJSONString, type);
        for (Viaje v : viajes){
            if (v.getId().equals(viaje.getId())){
                v.setSeleccionado(!v.isSeleccionado());
            }
        }
        viajesJSONString = new Gson().toJson(viajes);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("viajes", viajesJSONString);
        edit.apply();
        // Change image view
        setImageViewStar();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
