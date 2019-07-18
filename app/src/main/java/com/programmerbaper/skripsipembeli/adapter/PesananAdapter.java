package com.programmerbaper.skripsipembeli.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.model.Makanan;

import java.util.ArrayList;


public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    private Context context;
    private ArrayList<Makanan> listPesanan;

    public PesananAdapter(Context context, ArrayList<Makanan> listMakanan) {
        this.context = context;
        this.listPesanan = listMakanan;
    }

    @Override
    public PesananViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_confirm, parent, false);
        return new PesananViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PesananViewHolder holder, int position) {
        final Makanan makananNow = listPesanan.get(position);
        holder.jumlah.setText(makananNow.getNama());
        holder.harga.setText(Helper.formatter("" + (makananNow.getHarga() * makananNow.getJumlah())));
        holder.nama.setText(makananNow.getJumlah() + "");
        if (position % 2 != 0) {
            holder.mItemView.setBackgroundColor(Color.rgb(255, 255, 255));
        }


    }

    @Override
    public int getItemCount() {
        return listPesanan.size();
    }

    class PesananViewHolder extends RecyclerView.ViewHolder {

        TextView jumlah, harga, nama;
        View mItemView;


        PesananViewHolder(View itemView) {
            super(itemView);
            jumlah = itemView.findViewById(R.id.nama);
            harga = itemView.findViewById(R.id.harga);
            nama = itemView.findViewById(R.id.jumlah);
            mItemView = itemView;
        }


    }
}
