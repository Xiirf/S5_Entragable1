package com.example.entrega1.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrega1.R;
import com.example.entrega1.ViajeDisplayActivity;
import com.example.entrega1.entity.Util;
import com.example.entrega1.entity.Viaje;

import java.io.Serializable;
import java.util.List;

import com.squareup.picasso.Picasso;

public class ViajesAdapter extends RecyclerView.Adapter<ViajesAdapter.ViewHolder> {

    private static final int PICK_DISPLAY_VIAJE = 19;
    private List<Viaje> viajes;
    private LayoutInflater layoutInflater;
    private Activity activity;

    public ViajesAdapter(List<Viaje> viajes, Activity activity) {
        this.viajes = viajes;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viajeView = layoutInflater.inflate(R.layout.item_viaje, parent, false);
        return new ViewHolder(viajeView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Viaje viaje = viajes.get(position);
        holder.textViewTitulo.setText(viaje.getNombre());
        Picasso.get().load(viaje.getUrl()).into(holder.imageView);
        holder.textViewPrecioViaje.setText(String.format("Precio: %d €",
                viaje.getPrecio()));
        holder.textViewSalidaViaje.setText(String.format("Salida: %s",
                Util.formateaFecha(viaje.getFechasInicio())));
        holder.textViewLlegadaViaje.setText(String.format("Llegada: %s",
                Util.formateaFecha(viaje.getFechasFin())));
        holder.distanciaViaje.setText("Distancia: " + Integer.toString((int) viaje.getDistanciaUsuarioSalida()) + " Km");
        //Checkbouton
        Drawable image = activity.getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        // TODO méthode qui vérifie si l'utilisateur actuel possède ce voyage
        if (this.activity.getClass().getSimpleName().equals("ViajesActivity") && viaje.isSeleccionado()) {
            image = activity.getResources().getDrawable(R.drawable.ic_star_black_24dp);
        } else if (this.activity.getClass().getSimpleName().equals("ViajesSeleccionadosActivity")){
            image = activity.getResources().getDrawable(R.drawable.ic_add_shopping_cart_black_24dp);
        }
        holder.imageViewComprarOrStar.setImageDrawable(image);
    }

    @Override
    public int getItemCount() {
        return viajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, imageViewComprarOrStar;
        TextView textViewTitulo, textViewPrecioViaje, textViewSalidaViaje, textViewLlegadaViaje,
                distanciaViaje;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewViaje);
            textViewTitulo = itemView.findViewById(R.id.textViewDisplayTitulo);
            textViewPrecioViaje = itemView.findViewById(R.id.textViewPrecioViaje);
            textViewSalidaViaje = itemView.findViewById(R.id.textViewSalidaViaje);
            textViewLlegadaViaje = itemView.findViewById(R.id.textViewLlegadaViaje);
            imageViewComprarOrStar = itemView.findViewById(R.id.imageViewComprarOrStar);
            distanciaViaje = itemView.findViewById(R.id.textView_Distancia);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ViajeDisplayActivity.class);
                    intent.putExtra("Viaje", (Serializable) viajes.get(getAdapterPosition()));
                    activity.startActivityForResult(intent, PICK_DISPLAY_VIAJE);;
                }
            });
            imageViewComprarOrStar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (activity.getClass().getSimpleName().equals("ViajesSeleccionadosActivity")) {
                        Toast.makeText(v.getContext(), "Viaje " + getAdapterPosition()+ "( " +
                                viajes.get(getAdapterPosition()).getNombre() +") comprado",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
