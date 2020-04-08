package com.example.entrega1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.entrega1.adapter.ViajesAdapter;
import com.example.entrega1.entity.Viaje;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ViajesSeleccionadosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Viaje> viajes;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_seleccionados);

        recyclerView = findViewById(R.id.recyclerViewSeleccionados);
        // Recuperacion de los viajes si hay algunos almacenados
        SharedPreferences prefs = this.getSharedPreferences("Viaje", Context.MODE_PRIVATE);
        String viajesJSONString = prefs.getString("viajes", null);

        if (!(viajesJSONString == null)) {
            Type type = new TypeToken<List<Viaje>>() {}.getType();
            viajes = new Gson().fromJson(viajesJSONString, type);
        }

        viajes = getViajesSeleccionados(viajes);

        if (viajes.size() == 0) {
            Toast.makeText(this, "No hay viajes seleccionados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Hay " + viajes.size() + " viajes seleccionados", Toast.LENGTH_LONG).show();
        }

        ViajesAdapter adapter = new ViajesAdapter(viajes, this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Viaje> getViajesSeleccionados(List<Viaje> viajes) {
        return viajes.stream().filter(Viaje::isSeleccionado).collect(Collectors.toList());
    }
}
