package com.programmerbaper.skripsipembeli.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.activities.PilihMakananActivity;
import com.programmerbaper.skripsipembeli.model.Pedagang;

import java.util.List;

import static com.programmerbaper.skripsipembeli.misc.Config.BASE_URL;
import static com.programmerbaper.skripsipembeli.misc.Config.DATA_PEDAGANG_TERPILIH;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PREORDER;

public class PedagangAdapter extends RecyclerView.Adapter<PedagangAdapter.PedagangViewHolder> {

    private Context context;
    private List<Pedagang> listPedagang;
    private Activity parentActivity;
    private PedagangAdapter adapter;
    private ProgressDialog progressDialog = null;

    public PedagangAdapter(Context context, List<Pedagang> listPedagang, Activity parentActivity) {
        this.context = context;
        this.listPedagang = listPedagang;
        this.parentActivity = parentActivity;
        this.adapter = this;
    }

    @Override
    public PedagangAdapter.PedagangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_pilih_pedagang, null, false);
        PedagangAdapter.PedagangViewHolder adapter = new PedagangAdapter.PedagangViewHolder(view);

        return adapter;
    }


    private void initProgressDialog() {
        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Memproses..");
        progressDialog.setCancelable(false);
    }


    @Override
    public void onBindViewHolder(final PedagangAdapter.PedagangViewHolder pedagangViewHolder, int i) {
        initProgressDialog();
        final Pedagang pedagang = listPedagang.get(i);
        Glide.with(context)
                .load(BASE_URL + "storage/pedagang-profiles/" + pedagang.getFoto())
                .placeholder(R.drawable.pedagang_holder)
                .into(pedagangViewHolder.image);


        pedagangViewHolder.nama.setText(pedagang.getJenis() + " " + pedagang.getNama());
        pedagangViewHolder.jenis.setText(pedagang.getJenis());


        pedagangViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO add detail pedagang here

            }
        });

        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String preOrder = pref.getString(PREORDER, "");
        if (preOrder.equals(PREORDER)) {

            pedagangViewHolder.tombol.setText("PRE ORDER");

        } else {

            pedagangViewHolder.tombol.setText("PESAN");


        }

        pedagangViewHolder.tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PilihMakananActivity.class);
                intent.putExtra(DATA_PEDAGANG_TERPILIH, pedagang);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return listPedagang.size();
    }

    public class PedagangViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView nama, jenis;
        private View view;
        private Button tombol;

        public PedagangViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            image = itemView.findViewById(R.id.image);
            nama = itemView.findViewById(R.id.nama);
            jenis = itemView.findViewById(R.id.jenis);
            tombol = itemView.findViewById(R.id.tombol);

        }
    }
}

