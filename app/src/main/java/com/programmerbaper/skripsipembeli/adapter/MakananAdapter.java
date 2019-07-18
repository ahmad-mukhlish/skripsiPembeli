package com.programmerbaper.skripsipembeli.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.activities.ConfirmActivity;
import com.programmerbaper.skripsipembeli.activities.PilihMakananActivity;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.model.Makanan;

import java.util.ArrayList;

import static com.programmerbaper.skripsipembeli.misc.Config.BASE_URL;

public class MakananAdapter extends RecyclerView.Adapter<MakananAdapter.MakananViewHolder> {


    private Context context;
    private ArrayList<Makanan> listMakanan;
    private int idPedagangTerpilih ;

    public MakananAdapter(Context context, ArrayList<Makanan> listMakanan, int idPedagangTerpilih) {
        this.context = context;
        this.listMakanan = listMakanan;
        this.idPedagangTerpilih = idPedagangTerpilih;
    }

    @Override
    public MakananViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_pilih_makanan, parent, false);
        return new MakananViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MakananViewHolder holder, int position) {
        final Makanan makananNow = listMakanan.get(position);

        Glide.with(context).
                load(BASE_URL + "storage/makanan-photos/" + makananNow.getFoto()).
                placeholder(R.drawable.placeholder_makanan).
                into(holder.foto);


        holder.foto.setOnClickListener(new ProdukListener(position));

        holder.nama.setText(makananNow.getNama());
        holder.jumlah.setText(makananNow.getJumlah() + "");
        holder.harga.setText(Helper.formatter("" + makananNow.getHarga()));


        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = makananNow.getJumlah();
                qty++;
                makananNow.setJumlah(qty);
                holder.jumlah.setText(qty + "");

                countEstimatedPrice();

            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = makananNow.getJumlah();

                if (qty > 1) {
                    qty--;
                    makananNow.setJumlah(qty);
                    holder.jumlah.setText(qty + "");
                } else {
                    makananNow.setJumlah(0);
                    holder.setJumlah.setVisibility(View.GONE);
                    holder.tambah.setVisibility(View.VISIBLE);
                }

                countEstimatedPrice();
            }
        });

        holder.tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tambah.setVisibility(View.GONE);
                holder.setJumlah.setVisibility(View.VISIBLE);
                makananNow.setJumlah(1);
                holder.jumlah.setText(makananNow.getJumlah() + "");

                countEstimatedPrice();


            }
        });

    }

    @Override
    public int getItemCount() {
        return listMakanan.size();
    }

    public class MakananViewHolder extends RecyclerView.ViewHolder {

        ImageView foto;
        TextView nama, harga, jumlah;
        View view;
        Button plus, minus, tambah;
        RelativeLayout setJumlah;
        CardView card;


        MakananViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.foto);
            nama = itemView.findViewById(R.id.nama);
            harga = itemView.findViewById(R.id.harga);
            jumlah = itemView.findViewById(R.id.jumlah);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            tambah = itemView.findViewById(R.id.btn_tambah);
            setJumlah = itemView.findViewById(R.id.set_jumlah);
            card = itemView.findViewById(R.id.card);
            view = itemView;
        }


    }

    private class ProdukListener implements View.OnClickListener {

        private int position;

        ProdukListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {

            dialogueDetail();
        }

        private void dialogueDetail() {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View rootDialog = LayoutInflater.from(context).inflate(R.layout.dialogue_detail, null);
            Makanan clickedMakanan = listMakanan.get(position);

            final TextView judul = rootDialog.findViewById(R.id.nama);

            TextView deskripsi = rootDialog.findViewById(R.id.deskripsi);
            TextView harga = rootDialog.findViewById(R.id.harga);
            ImageView imageView = rootDialog.findViewById(R.id.foto);


            Glide.with(context).
                    load(BASE_URL + "storage/makanan-photos/" + clickedMakanan.getFoto()).
                    placeholder(R.drawable.placeholder_makanan)
                    .into(imageView);


            judul.setText(clickedMakanan.getNama());
            judul.setWidth(imageView.getWidth());

            deskripsi.setText(clickedMakanan.getDeskripsi());
            harga.setText(Helper.formatter("" + clickedMakanan.getHarga()));
            builder.setView(rootDialog);
            final AlertDialog dialog = builder.create();
            dialog.show();

            TextView ok = rootDialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }


    }

    private void countEstimatedPrice() {

        Long hasil = 0l;

        for (Makanan makananNow : listMakanan) {

            hasil += makananNow.getHarga() * makananNow.getJumlah();

        }

        PilihMakananActivity.updateEstimatedPrice(hasil);
        PilihMakananActivity.estimated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ConfirmActivity.class);
                intent.putParcelableArrayListExtra("pesanan", arrangePesanan(listMakanan));
                intent.putExtra("id_pedagang",idPedagangTerpilih);
                context.startActivity(intent);

            }
        });

    }

    private ArrayList<Makanan> arrangePesanan(ArrayList<Makanan> listMakanan) {

        ArrayList<Makanan> hasil = new ArrayList<>();

        for (Makanan makananNow : listMakanan) {

            if (makananNow.getJumlah() > 0) {

                hasil.add(makananNow) ;
            }

        }

        return hasil;
    }

}
