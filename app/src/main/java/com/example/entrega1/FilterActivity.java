package com.example.entrega1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.entrega1.entity.Constantes;
import com.example.entrega1.entity.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    private TextView textViewFechaInicio, textViewFechaFin;
    private Calendar calendar = Calendar.getInstance();
    private EditText editTextPrecioMin, editTextPrecioMax;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        prefs = this.getSharedPreferences(Constantes.filtroPreferences, Context.MODE_PRIVATE);
        edit = prefs.edit();

        textViewFechaInicio = findViewById(R.id.textViewFechaInicioSet);
        textViewFechaFin = findViewById(R.id.textViewFechaFinSet);

        editTextPrecioMin = findViewById(R.id.editTextPrecioMin);
        editTextPrecioMax = findViewById(R.id.editTextPrecioMax);

        setFilter();
    }

    public void setFechaInicio(View view) {
        int yy = this.calendar.get(Calendar.YEAR);
        int mm = this.calendar.get(Calendar.MONTH);
        int dd = this.calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = day+"/" + (month + 1)+"/"+year;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    cal.setTime(sdf.parse(date));
                    textViewFechaInicio.setText(Util.formateaFecha(cal));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, yy,mm,dd);
        dialog.show();
    }

    public void setFechaFin(View view) {
        int yy = this.calendar.get(Calendar.YEAR);
        int mm = this.calendar.get(Calendar.MONTH);
        int dd = this.calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = day+"/" + (month + 1)+"/"+year;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    cal.setTime(sdf.parse(date));
                    textViewFechaFin.setText(Util.formateaFecha(cal));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, yy,mm,dd);
        dialog.show();
    }

    public void saveFilter(View view) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

        // Save FechaInicio or delete it
        String fechaInicio = textViewFechaInicio.getText().toString();
        if (fechaInicio.equals("")) {
            edit.remove(Constantes.fechaInicio);
        } else {
            try {
                cal.setTime(sdf.parse(fechaInicio));
                edit.putLong(Constantes.fechaInicio, Util.Calendar2long(cal));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Save FechaFin or delete it
        String fechaFin = textViewFechaFin.getText().toString();
        if (fechaFin.equals("")) {
            edit.remove(Constantes.fechaFin);
        } else {
            try {
                cal.setTime(sdf.parse(fechaFin));
                edit.putLong(Constantes.fechaFin, Util.Calendar2long(cal));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Save PrecioMin or delete it
        String precioMin = editTextPrecioMin.getText().toString().trim();
        if (precioMin.equals("")) {
            edit.remove(Constantes.precioMin);
        } else {
            edit.putLong(Constantes.precioMin, Long.parseLong(precioMin));
        }

        // Save PrecioMax or delete it
        String precioMax = editTextPrecioMax.getText().toString().trim();
        if (precioMax.equals("")) {
            edit.remove(Constantes.precioMax);
        } else {
            edit.putLong(Constantes.precioMax, Long.parseLong(precioMax));
        }

        edit.apply();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setFilter() {
        if (!(prefs.getLong(Constantes.fechaInicio, 0) == 0 )) {
            textViewFechaInicio.setText(Util.formateaFecha(prefs.getLong(Constantes.fechaInicio, 0)));
        }
        if (!(prefs.getLong(Constantes.fechaFin, 0) == 0)) {
            textViewFechaFin.setText(Util.formateaFecha(prefs.getLong(Constantes.fechaFin, 0)));
        }
        editTextPrecioMin.setText(Long.toString(prefs.getLong(Constantes.precioMin, 0)));
        editTextPrecioMax.setText(Long.toString(prefs.getLong(Constantes.precioMax, 0)));
    }

    public void clearFilter(View view) {
        textViewFechaInicio.setText("");
        textViewFechaFin.setText("");
        editTextPrecioMin.setText("");
        editTextPrecioMax.setText("");
    }
}
