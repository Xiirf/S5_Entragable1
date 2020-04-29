package com.example.entrega1.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Viaje implements Serializable {
    private Long fechasInicio, fechasFin;
    private String nombre, url, lugarSalida, id, descripcion;
    private Long precio;
    private boolean isSeleccionado;
    private double longitudeSalida, latitudeSalida;
    private float distanciaUsuarioSalida;

    public Viaje(Long fechasInicio, Long fechasFin, String nombre, String lugarSalida,
                 String url, Long precio, String descripcion, boolean isSeleccionado, double longitudeSalida, double latitudeSalida) {
        this.fechasInicio = fechasInicio;
        this.fechasFin = fechasFin;
        this.lugarSalida = lugarSalida;
        this.nombre = nombre;
        this.url = url;
        this.precio = precio;
        this.descripcion = descripcion;
        this.isSeleccionado = isSeleccionado;
        this.latitudeSalida = latitudeSalida;
        this.longitudeSalida = longitudeSalida;
    }

    public float getDistanciaUsuarioSalida() {
        return distanciaUsuarioSalida;
    }

    public void setDistanciaUsuarioSalida(float distanciaUsuarioSalida) {
        this.distanciaUsuarioSalida = distanciaUsuarioSalida;
    }

    public double getLongitudeSalida() {
        return longitudeSalida;
    }

    public void setLongitudeSalida(double longitudeSalida) {
        this.longitudeSalida = longitudeSalida;
    }

    public double getLatitudeSalida() {
        return latitudeSalida;
    }

    public void setLatitudeSalida(double latitudeSalida) {
        this.latitudeSalida = latitudeSalida;
    }

    public boolean isSeleccionado() {
        return isSeleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        isSeleccionado = seleccionado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugarSalida() {
        return lugarSalida;
    }

    public void setLugarSalida(String lugarSalida) {
        this.lugarSalida = lugarSalida;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFechasInicio() {
        return fechasInicio;
    }

    public void setFechasInicio(Long fechasInicio) {
        this.fechasInicio = fechasInicio;
    }

    public Long getFechasFin() {
        return fechasFin;
    }

    public void setFechasFin(Long fechasFin) {
        this.fechasFin = fechasFin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Viaje{" +
                "fechasInicio=" + fechasInicio +
                ", fechasFin=" + fechasFin +
                ", nombre='" + nombre + '\'' +
                ", url='" + url + '\'' +
                ", lugarSalida='" + lugarSalida + '\'' +
                ", id='" + id + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", isSeleccionado=" + isSeleccionado +
                ", longitudeSalida=" + longitudeSalida +
                ", latitudeSalida=" + latitudeSalida +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Viaje> generarViaje(int max) {
        ArrayList<Viaje> viajes = new ArrayList<>();
        int numeroCiudades = Constantes.ciudades.length;
        int numeroLugarSalida = Constantes.lugarSalida.length;
        int numeroUrlImagenes = Constantes.urlImagenes.length;
        
        for(int i=0; i < max; i++){
            Long[] fechas = randomDate();
            Long fechasInicio = fechas[0];
            Long fechasFin = fechas[1];
            String nombre = Constantes.ciudades[new Random().nextInt(numeroCiudades)];
            String url = Constantes.urlImagenes[new Random().nextInt(numeroUrlImagenes)];
            int salida = new Random().nextInt(numeroLugarSalida);
            String lugarSalida = Constantes.lugarSalida[salida];
            double latitude = Constantes.latitude[salida];
            double longitude = Constantes.longitude[salida];
            Long precio = new Long(new Random().nextInt(5000) +1);
            String descripcion = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc sit amet libero vehicula, molestie sapien id, placerat nulla. Nullam volutpat augue sed lorem pellentesque dapibus. Duis maximus, dolor et accumsan blandit, velit ex fringilla risus, sed congue augue justo at nisl. Integer volutpat maximus fermentum. Donec nec suscipit libero, eu maximus dui. Aenean imperdiet porttitor nisl, tristique pharetra massa eleifend eget. Mauris ullamcorper turpis sit amet volutpat cursus. Donec at arcu non ante commodo interdum quis eu leo. Fusce mollis tellus nibh, id tempus nibh fermentum eu. Morbi ut enim rhoncus, interdum felis vitae, mattis justo. Aliquam semper lacus at risus pharetra elementum.\n" +
                    "\n" +
                    "Mauris magna arcu, volutpat vel malesuada sit amet, efficitur a justo. Praesent aliquet euismod pellentesque. Phasellus facilisis ante vitae massa pretium, ut ullamcorper lorem tincidunt. Sed tempus ac ex non interdum. Aenean tellus nunc, porttitor ut dui in, tempor placerat quam. Integer nec urna blandit, aliquet arcu vitae, venenatis magna. Nullam mattis libero justo, vel consequat elit mollis at. Curabitur finibus lacinia bibendum. Phasellus euismod, eros et vehicula luctus, mi nisi consequat justo, at varius magna mi id nisi. Maecenas ipsum ante, blandit vitae ante quis, posuere venenatis nisi. Suspendisse placerat lorem sed massa rhoncus maximus. Nunc in mauris accumsan, placerat quam vitae, facilisis diam. Cras vitae nunc diam. Aliquam in rutrum nisi.";
            viajes.add(new Viaje(fechasInicio, fechasFin, lugarSalida, nombre, url, precio, descripcion, false, longitude, latitude));
        }
        return viajes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Viaje viaje = (Viaje) o;

        if (isSeleccionado != viaje.isSeleccionado) return false;
        if (Double.compare(viaje.longitudeSalida, longitudeSalida) != 0) return false;
        if (Double.compare(viaje.latitudeSalida, latitudeSalida) != 0) return false;
        if (fechasInicio != null ? !fechasInicio.equals(viaje.fechasInicio) : viaje.fechasInicio != null)
            return false;
        if (fechasFin != null ? !fechasFin.equals(viaje.fechasFin) : viaje.fechasFin != null)
            return false;
        if (nombre != null ? !nombre.equals(viaje.nombre) : viaje.nombre != null) return false;
        if (url != null ? !url.equals(viaje.url) : viaje.url != null) return false;
        if (lugarSalida != null ? !lugarSalida.equals(viaje.lugarSalida) : viaje.lugarSalida != null)
            return false;
        if (id != null ? !id.equals(viaje.id) : viaje.id != null) return false;
        if (descripcion != null ? !descripcion.equals(viaje.descripcion) : viaje.descripcion != null)
            return false;
        return precio != null ? precio.equals(viaje.precio) : viaje.precio == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = fechasInicio != null ? fechasInicio.hashCode() : 0;
        result = 31 * result + (fechasFin != null ? fechasFin.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (lugarSalida != null ? lugarSalida.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (precio != null ? precio.hashCode() : 0);
        result = 31 * result + (isSeleccionado ? 1 : 0);
        temp = Double.doubleToLongBits(longitudeSalida);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitudeSalida);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long[] randomDate() {

        int year = new Random().nextInt(2) + 2020;
        int dayOfYear = new Random().nextInt(365) + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);

        Long[] fechas = new Long[2];
        Long start = Util.Calendar2long(calendar);

        int timeViaje = new Random().nextInt(14) + 7;
        if ((timeViaje + dayOfYear) > 365) {
            year = year + 1;
            dayOfYear = timeViaje + dayOfYear - 365;
        } else {
            dayOfYear = timeViaje + dayOfYear;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, year);
        calendar2.set(Calendar.DAY_OF_YEAR, dayOfYear);

        Long end = Util.Calendar2long(calendar2);


        fechas[0] = start;
        fechas[1] = end;
        return fechas;
    }
}
