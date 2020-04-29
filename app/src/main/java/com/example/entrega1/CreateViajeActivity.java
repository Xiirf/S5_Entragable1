package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entrega1.entity.Constantes;
import com.example.entrega1.entity.Util;
import com.example.entrega1.entity.Viaje;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class CreateViajeActivity extends AppCompatActivity {

    private TextView textViewFechaInicio, textViewFechaFin;
    private DatePickerDialog dialogInicio, dialogFin;
    private Calendar fechaInicio, fechaFin;
    private AutoCompleteTextView nombreTrip, lugarSalida, precioTrip, descriptionTrip,
            latitudeSalida, longitudeSalida;
    private TextInputLayout nombreTripParent, lugarSalidaParent, precioTripParent,
            descriptionTripParent, fechaInicioParent, fechaFinParent, latitudeSalidaParent,
            longitudeSalidaParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_viaje);

        latitudeSalida = findViewById(R.id.latitude_trip_et);
        latitudeSalidaParent = findViewById(R.id.latitude_trip);

        longitudeSalida = findViewById(R.id.longitude_trip_et);
        longitudeSalidaParent = findViewById(R.id.longitude_trip);

        textViewFechaInicio = findViewById(R.id.textView_fechaInicio_form);
        fechaInicioParent = findViewById(R.id.fechaInicio_parent);

        textViewFechaFin = findViewById(R.id.textView_fechaFin_form);
        fechaFinParent = findViewById(R.id.fechaFin_parent);

        nombreTrip = findViewById(R.id.nombre_trip_et);
        nombreTripParent = findViewById(R.id.nombre_trip);

        lugarSalida = findViewById(R.id.lugar_salida_trip_et);
        lugarSalidaParent = findViewById(R.id.lugar_salida_trip);

        precioTrip = findViewById(R.id.precio_trip_et);
        precioTripParent = findViewById(R.id.precio_trip);

        descriptionTrip = findViewById(R.id.description_trip_et);
        descriptionTripParent = findViewById(R.id.description_trip);
    }

    public void setFechaInicioForm(View view) {
        Calendar calendarInicio;

        if (fechaInicio != null) {
            calendarInicio = fechaInicio;
        } else {
            calendarInicio = Calendar.getInstance();
        }

        int yy = calendarInicio.get(Calendar.YEAR);
        int mm = calendarInicio.get(Calendar.MONTH);
        int dd = calendarInicio.get(Calendar.DAY_OF_MONTH);

        dialogInicio = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = day+"/" + (month + 1)+"/"+year;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                try {
                    cal.setTime(sdf.parse(date));
                    textViewFechaInicio.setText(Util.formateaFecha(cal));
                    fechaInicio = cal;
                    //Check if start date (fechaInicio) is before end date (fechaFin)
                    if (!textViewFechaFin.getText().toString().equals("")) {
                        SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(f.parse(textViewFechaFin.getText().toString()));
                        Date date1 = cal.getTime();
                        Date date2 = cal2.getTime();
                        if (date1.after(date2)) {
                            textViewFechaFin.setText("");
                            fechaFin = null;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, yy,mm,dd);
        dialogInicio.getDatePicker().setMinDate(calendarInicio.getTimeInMillis());
        dialogInicio.show();
    }

    public void setFechaFinForm(View view) {
        Calendar calendarFin;

        if (fechaFin != null) {
            calendarFin = fechaFin;
        } else {
            calendarFin = Calendar.getInstance();
        }

        int yy = calendarFin.get(Calendar.YEAR);
        int mm = calendarFin.get(Calendar.MONTH);
        int dd = calendarFin.get(Calendar.DAY_OF_MONTH);
        dialogFin = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = day+"/" + (month + 1)+"/"+year;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                try {
                    cal.setTime(sdf.parse(date));
                    textViewFechaFin.setText(Util.formateaFecha(cal));
                    fechaFin = cal;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, yy,mm,dd);
        if (fechaInicio != null) {
            dialogFin.getDatePicker().setMinDate(fechaInicio.getTimeInMillis());
        }
        dialogFin.show();
    }

    private void crearTrip() {
        Long fechaInicioViaje = Util.Calendar2long(fechaInicio);
        Long fechaFinViaje = Util.Calendar2long(fechaFin);
        String nombreViaje = nombreTrip.getText().toString();
        String lugarSalidaViaje = lugarSalida.getText().toString();
        Long precio = Long.parseLong(precioTrip.getText().toString());
        String description = descriptionTrip.getText().toString();
        double latitude = Double.parseDouble(latitudeSalida.getText().toString());
        double longitude = Double.parseDouble(longitudeSalida.getText().toString());

        int numeroUrlImagenes = Constantes.urlImagenes.length;
        String url = Constantes.urlImagenes[new Random().nextInt(numeroUrlImagenes)];

        Viaje viaje = new Viaje(fechaInicioViaje, fechaFinViaje, nombreViaje, lugarSalidaViaje, url,
                                precio, description, false, longitude, latitude);

        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();

        firebaseDatabaseService.createTravel(viaje, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null ) {
                    Toast.makeText(CreateViajeActivity.this, "Viaje creado", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateViajeActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.i("App", "Error viaje no insertado");
                }
            }
        });
    }


    public void attemptCreateTrip(View view) {
        boolean isOk = true;
        nombreTripParent.setError(null);
        lugarSalidaParent.setError(null);
        precioTripParent.setError(null);
        descriptionTripParent.setError(null);

        if (nombreTrip.getText().length() == 0) {
            nombreTripParent.setErrorEnabled(true);
            nombreTripParent.setError(getString(R.string.nombre_form_error));
        } else if (lugarSalida.getText().length() == 0) {
            lugarSalidaParent.setErrorEnabled(true);
            lugarSalidaParent.setError(getString(R.string.lugarSalida_form_error));
        } else if (precioTrip.getText().length() == 0) {
            precioTripParent.setErrorEnabled(true);
            precioTripParent.setError(getString(R.string.precio_form_error));
        } else if (descriptionTrip.getText().length() == 0) {
            descriptionTripParent.setErrorEnabled(true);
            descriptionTripParent.setError(getString(R.string.description_form_error));
        } else if (fechaInicio == null) {
            fechaInicioParent.setErrorEnabled(true);
            fechaInicioParent.setError(getString(R.string.fechaInicio_form_error));
        } else if (fechaFin == null) {
            fechaFinParent.setErrorEnabled(true);
            fechaFinParent.setError(getString(R.string.fechaFin_form_error));
        } else if (latitudeSalida.getText().length() == 0) {
            latitudeSalidaParent.setErrorEnabled(true);
            latitudeSalidaParent.setError(getString(R.string.latitude_required));
        } else if (longitudeSalida.getText().length() == 0) {
            longitudeSalidaParent.setErrorEnabled(true);
            longitudeSalidaParent.setError(getString(R.string.longitude_required));
        } else {
            if(latitudeSalida.getText().length() != 0) {
                double latitude = Double.parseDouble(latitudeSalida.getText().toString());
                if (latitude >= 90 || latitude <= -90) {
                    isOk = false;
                    latitudeSalidaParent.setErrorEnabled(true);
                    latitudeSalidaParent.setError(getString(R.string.latitude_invalid));
                }
            }
            if(longitudeSalida.getText().length() != 0) {
                double longitude = Double.parseDouble(longitudeSalida.getText().toString());
                if (longitude >= 180 || longitude <= -180) {
                    isOk = false;
                    longitudeSalidaParent.setErrorEnabled(true);
                    longitudeSalidaParent.setError(getString(R.string.longitude_invalid));
                }
            }
            if (isOk) {
                crearTrip();
            }
        }
    }
}
