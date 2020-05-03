package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entrega1.entity.Util;
import com.example.entrega1.entity.Viaje;
import com.example.entrega1.resttypes.WeatherResponse;
import com.example.entrega1.resttypes.WeatherRetrofitInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViajeDisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView textViewTitulo, textViewFechaLlegada, textViewFechaSalida, textViewLugarSalida,
            textViewPrecio, textViewDescription, textViewDistancia, textViewTemperatura;
    private ImageView imageViewViaje, imageViewStar, imageViewWeather;
    private Viaje viaje;
    private GoogleMap googleMap;
    Retrofit retrofit;

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
        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        imageViewWeather = findViewById(R.id.imageViewWeather);

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

        //Map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrentWeather(  (float) viaje.getLatitudeSalida(),
                                    (float) viaje.getLongitudeSalida(),
                                    getString(R.string.open_weather_map_api_key));

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int degree = (int) ((response.body().getMain().getTemp() - 273.15 ));
                    Log.i("TEMP", "" + (response.body().getMain().getTemp()));
                    textViewTemperatura.setText(Integer.toString(degree) + " C°");
                    setIconWeather(response.body().getWeather().get(0).getMain());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("ERROR", "REST: error en la llama. " + t.getMessage());
            }
        });
    }

    public void setImageViewStar() {
        if (!viaje.isSeleccionado()) {
            Drawable image = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
            imageViewStar.setImageDrawable(image);
        } else {
            Drawable image = getResources().getDrawable(R.drawable.ic_star_black_24dp);
            imageViewStar.setImageDrawable(image);
        }
    }

    public void setIconWeather(String weather) {
        Drawable image = getResources().getDrawable(R.drawable.clear);
        switch (weather) {
            case "Clouds":
                image = getResources().getDrawable(R.drawable.cloud);
                break;
            case "Drizzle":
                image = getResources().getDrawable(R.drawable.drizzle);
                break;
            case "Rain":
                image = getResources().getDrawable(R.drawable.rain);
                break;
            case "Snow":
                image = getResources().getDrawable(R.drawable.snow);
                break;
            case "Thunderstorm":
                image = getResources().getDrawable(R.drawable.thunderstorm);
                break;
        }
        imageViewWeather.setImageDrawable(image);
    }

    public void onSelect(View view) {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        if (!viaje.isSeleccionado()) {
            firebaseDatabaseService.addUserTravel(viaje.getId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
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
                    if (databaseError == null) {
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng location = new LatLng(viaje.getLatitudeSalida(), viaje.getLongitudeSalida());
        googleMap.addMarker(new MarkerOptions().title("Posición del lugar de salida").position(location));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }
}

