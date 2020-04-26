package com.example.entrega1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entrega1.entity.Enlace;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        EnlaceAdapter enlaceAdapter = new EnlaceAdapter(Enlace.generaEnlaces(), this);
        gridView.setAdapter(enlaceAdapter);
    }

    public void disconnect(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        try {
            mAuth.signOut();
            Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();;
        }catch (Exception e) {
            Toast.makeText(this, getString(R.string.logout_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void goToProfilePage(View view) {
        startActivity(new Intent(this, FirebaseStorageProfileActivity.class));
    }
}

class EnlaceAdapter extends BaseAdapter {

    List<Enlace> enlaces;
    Context context;

    public EnlaceAdapter(List<Enlace> enlaces, Context context) {
        this.enlaces = enlaces;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.enlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return this.enlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.enlaces.get(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Enlace enlace = enlaces.get(position);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.enlace_item, viewGroup, false);
        }
        CardView cardview = view.findViewById(R.id.cardView);
        ImageView imageView = view.findViewById(R.id.enlace_picture);
        TextView textView = view.findViewById(R.id.enlace_name);

        imageView.setImageResource(enlace.getRecursoImageView());
        textView.setText(enlace.getDescription());

        cardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, enlace.getClase()));
            }
        });
        return view;
    }
}
