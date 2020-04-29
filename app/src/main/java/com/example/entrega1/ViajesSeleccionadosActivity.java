package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.entrega1.adapter.ViajesAdapter;
import com.example.entrega1.entity.Viaje;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViajesSeleccionadosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabaseService firebaseDatabaseService;
    private DatabaseReference refViaje, refUserViajes;
    private List<Viaje> viajes;
    private List<String> userViajesId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_seleccionados);

        recyclerView = findViewById(R.id.recyclerViewSeleccionados);
        // Recuperacion de los viajes si hay algunos almacenados
        //Recuperacion de todos los viajes from BD
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        refViaje = firebaseDatabaseService.getTravels();
        refUserViajes = firebaseDatabaseService.getUserTravels();
        //Generate some trips and save them in database
        //genTravelAndSaveThem(20);
        getViajes(new ViajesActivity.FirebaseCallback() {
            @Override
            public void onCallback(List<Viaje> listV) {
                viajes = listV;

                getUserViajesId(new ViajesActivity.FirebaseCallbackIdViajes() {
                    @Override
                    public void onCallback(List<String> listId) {
                        userViajesId = listId;

                        //Check which trips are selected by the actual user
                        checkSelectedTrip();

                        viajes = getViajesSeleccionados(viajes);

                        ViajesAdapter adapter = new ViajesAdapter(viajes, ViajesSeleccionadosActivity.this);

                        recyclerView.setLayoutManager(new GridLayoutManager(ViajesSeleccionadosActivity.this, 1));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Viaje> getViajesSeleccionados(List<Viaje> viajes) {
        return viajes.stream().filter(Viaje::isSeleccionado).collect(Collectors.toList());
    }

    public void getViajes(ViajesActivity.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viajes = new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        // id
                        String id = ds.getKey();
                        String descripcion = ds.child("descripcion").getValue().toString();
                        Long fechasInicio = Long.parseLong(ds.child("fechasInicio").getValue().toString());
                        Long fechasFin = Long.parseLong(ds.child("fechasFin").getValue().toString());
                        Long precio = Long.parseLong(ds.child("precio").getValue().toString());
                        String lugarSalida = ds.child("lugarSalida").getValue().toString();
                        String nombre = ds.child("nombre").getValue().toString();
                        Double longitude = Double.parseDouble(ds.child("longitudeSalida").getValue().toString());
                        Double latitude = Double.parseDouble(ds.child("latitudeSalida").getValue().toString());
                        String url = ds.child("url").getValue().toString();

                        Viaje viaje= new Viaje(fechasInicio, fechasFin, nombre, lugarSalida, url,
                                precio, descripcion, false, longitude, latitude);
                        viaje.setId(id);
                        viajes.add(viaje);
                    }
                }
                firebaseCallback.onCallback(viajes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERR", databaseError.getMessage());
            }
        };
        refViaje.addValueEventListener(valueEventListener);
    }

    public void getUserViajesId(ViajesActivity.FirebaseCallbackIdViajes firebaseCallbackIdViajes) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userViajesId = new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        // id
                        String id = ds.getValue().toString();

                        userViajesId.add(id);
                    }
                }
                firebaseCallbackIdViajes.onCallback(userViajesId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERR", databaseError.getMessage());
            }
        };
        refUserViajes.addValueEventListener(valueEventListener);
    }

    public interface FirebaseCallback {
        void onCallback(List<Viaje> listV);
    }

    public interface FirebaseCallbackIdViajes {
        void onCallback(List<String> listId);
    }

    public void checkSelectedTrip() {
        for (Viaje v : viajes) {
            for (String id : userViajesId) {
                if (v.getId().equals(id)){
                    v.setSeleccionado(true);
                }
            }
        }
    }
}
