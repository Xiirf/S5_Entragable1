package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.entrega1.adapter.ViajesAdapter;
import com.example.entrega1.entity.Constantes;
import com.example.entrega1.entity.Viaje;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViajesActivity extends AppCompatActivity {

    private static final int PICK_FILTER_LIST = 18;
    private static final int PICK_DISPLAY_VIAJE = 19;
    private Switch switchCol;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private FirebaseDatabaseService firebaseDatabaseService;
    private DatabaseReference refViaje, refUserViajes;
    private List<Viaje> viajes;
    private List<String> userViajesId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);

        switchCol = findViewById(R.id.switchCol);

        recyclerView = findViewById(R.id.recyclerViajes);

        viajes = new ArrayList<>();
        //Recuperacion de todos los viajes from BD
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        refViaje = firebaseDatabaseService.getTravels();
        refUserViajes = firebaseDatabaseService.getUserTravels();
        //Generate some trips and save them in database
        genTravelAndSaveThem(20);
        getViajes(new FirebaseCallback() {
            @Override
            public void onCallback(List<Viaje> listV) {
                viajes = listV;

                // Use filter
                viajes = filterList(viajes);

                getUserViajesId(new FirebaseCallbackIdViajes() {
                    @Override
                    public void onCallback(List<String> listId) {
                        userViajesId = listId;

                        //Check which trips are selected by the actual user
                        checkSelectedTrip();

                        ViajesAdapter adapter = new ViajesAdapter(viajes, ViajesActivity.this);

                        getUserLocation(new LocationUserCallback() {
                            @Override
                            public void onCallback(Location location) {
                                // Add distancia
                                String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
                                if (ContextCompat.checkSelfPermission(ViajesActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                                    for (Viaje viaje :viajes) {
                                        Location locationViaje = new Location("");
                                        locationViaje.setLatitude(viaje.getLatitudeSalida());
                                        locationViaje.setLongitude(viaje.getLongitudeSalida());

                                        viaje.setDistanciaUsuarioSalida(location.distanceTo(locationViaje) / 1000);
                                    }
                                }
                                // Setup column number
                                if (switchCol.isChecked()) {
                                    gridLayoutManager = new GridLayoutManager(ViajesActivity.this, 2);
                                } else {
                                    gridLayoutManager = new GridLayoutManager(ViajesActivity.this, 1);
                                }
                                recyclerView.setLayoutManager(gridLayoutManager);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                });
            }
        });
    }

    public void getViajes(FirebaseCallback firebaseCallback) {
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
                        String url = ds.child("url").getValue().toString();
                        Double longitude = Double.parseDouble(ds.child("longitudeSalida").getValue().toString());
                        Double latitude = Double.parseDouble(ds.child("latitudeSalida").getValue().toString());
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

    public void getUserViajesId(FirebaseCallbackIdViajes firebaseCallbackIdViajes) {
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

    public void getUserLocation(LocationUserCallback locationUserCallback) {
        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(ViajesActivity.this);
        locationServices.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                locationUserCallback.onCallback(location);
            }
        });
    }

    public interface FirebaseCallback {
        void onCallback(List<Viaje> listV);
    }

    public interface FirebaseCallbackIdViajes {
        void onCallback(List<String> listId);
    }

    public interface LocationUserCallback {
        void onCallback(Location location);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Viaje> genTravelAndSaveThem(Integer numViaje) {
        viajes = Viaje.generarViaje(numViaje);
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();

        for (Viaje viaje :viajes) {
            firebaseDatabaseService.createTravel(viaje, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null ) {
                        Log.i("App", "Viaje insertado");
                    } else {
                        Log.i("App", "Error viaje no insertado");
                    }
                }
            });
        }

        return viajes;
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
