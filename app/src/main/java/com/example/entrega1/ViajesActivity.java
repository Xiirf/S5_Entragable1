package com.example.entrega1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.entrega1.adapter.ViajesAdapter;
import com.example.entrega1.entity.Constantes;
import com.example.entrega1.entity.Viaje;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ViajesActivity extends AppCompatActivity {

    private static final int PICK_FILTER_LIST = 18;
    private static final int PICK_DISPLAY_VIAJE = 19;
    private Switch switchCol;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    List<Viaje> viajes;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);

        switchCol = findViewById(R.id.switchCol);

        recyclerView = findViewById(R.id.recyclerViajes);
        // Recuperacion de los viajes si hay algunos almacenados
        SharedPreferences prefs = this.getSharedPreferences("Viaje", Context.MODE_PRIVATE);
        String viajesJSONString = prefs.getString("viajes", null);
        if (viajesJSONString == null) {
            viajes = Viaje.generarViaje(50);
            viajesJSONString = new Gson().toJson(viajes);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("viajes", viajesJSONString);
            edit.apply();
        } else {
            Type type = new TypeToken<List<Viaje>>() {}.getType();
            viajes = new Gson().fromJson(viajesJSONString, type);
        }

        // Use filter
        viajes = filterList(viajes);

        if (viajes.size() == 0) {
            Toast.makeText(this, "No hay viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Hay " + viajes.size() + " viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        }

        ViajesAdapter adapter = new ViajesAdapter(viajes, this);

        // Setup column number
        if (switchCol.isChecked()) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 1);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Viaje> filterList(List<Viaje> viajes) {
        SharedPreferences prefs = this.getSharedPreferences(Constantes.filtroPreferences, Context.MODE_PRIVATE);
        List<Viaje> viajesFiltrados = null;

        final Long fechaInicioPref = prefs.getLong(Constantes.fechaInicio, 0);
        Long fechaFinPref = prefs.getLong(Constantes.fechaFin, 0);
        Long precioMin = prefs.getLong(Constantes.precioMin, 0);
        Long precioMax = prefs.getLong(Constantes.precioMax, 0);
        // First use date filter
        if (fechaInicioPref != 0 && fechaFinPref == 0) {
            viajesFiltrados = viajes.stream().filter(viaje -> viaje.getFechasInicio() >= fechaInicioPref).collect(Collectors.toList());
        } else if (fechaInicioPref == 0 && fechaFinPref != 0) {
            viajesFiltrados = viajes.stream().filter(viaje -> viaje.getFechasFin() < fechaFinPref).collect(Collectors.toList());
        } else if (fechaInicioPref != 0 && fechaFinPref != 0){
            viajesFiltrados = viajes.stream().filter(viaje -> viaje.getFechasInicio() >= fechaInicioPref &&
                            viaje.getFechasFin() < fechaFinPref).collect(Collectors.toList());
        } else {
            viajesFiltrados = viajes;
        }
        //Then use price filter
        if (precioMin != 0 && precioMax == 0) {
            viajesFiltrados = viajesFiltrados.stream().filter(viaje -> viaje.getPrecio() >= precioMin).collect(Collectors.toList());
        } else if (precioMin == 0 && precioMax != 0) {
            viajesFiltrados = viajesFiltrados.stream().filter(viaje -> viaje.getPrecio() < precioMax).collect(Collectors.toList());
        } else if (precioMin != 0 && precioMax != 0){
            viajesFiltrados = viajesFiltrados.stream().filter(viaje -> viaje.getPrecio() >= precioMin &&
                    viaje.getPrecio() < precioMax).collect(Collectors.toList());
        }
        return viajesFiltrados;
    }

    public void toFilter(View view) {
        Intent intent = new Intent(view.getContext(), FilterActivity.class);
        startActivityForResult(intent, PICK_FILTER_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILTER_LIST || requestCode == PICK_DISPLAY_VIAJE) {
            if (resultCode == RESULT_OK) {
                this.recreate();
            }
        }
    }

    public void changeGridCol(View view) {
        if (switchCol.isChecked()) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 1);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
    }
}
