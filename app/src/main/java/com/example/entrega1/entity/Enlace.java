package com.example.entrega1.entity;

import com.example.entrega1.CreateViajeActivity;
import com.example.entrega1.R;
import com.example.entrega1.ViajesActivity;
import com.example.entrega1.ViajesSeleccionadosActivity;

import java.util.ArrayList;
import java.util.List;

public class Enlace {

    private String description;
    private int recursoImageView;
    private Class clase;

    public Enlace(String description, int recursoImageView, Class clase) {
        this.description = description;
        this.recursoImageView = recursoImageView;
        this.clase = clase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRecursoImageView() {
        return recursoImageView;
    }

    public void setRecursoImageView(int recursoImageView) {
        this.recursoImageView = recursoImageView;
    }

    public Class getClase() {
        return clase;
    }

    public void setClase(Class clase) {
        this.clase = clase;
    }

    public static List<Enlace> generaEnlaces() {
        List<Enlace> enlaces = new ArrayList<>();
        enlaces.add(new Enlace("Viajes disponibles", R.drawable.trip, ViajesActivity.class));
        enlaces.add(new Enlace("Viajes seleccionados", R.drawable.selec, ViajesSeleccionadosActivity.class));
        enlaces.add(new Enlace("Crear un viaje", R.drawable.selec, CreateViajeActivity.class));

        return enlaces;
    }
}
