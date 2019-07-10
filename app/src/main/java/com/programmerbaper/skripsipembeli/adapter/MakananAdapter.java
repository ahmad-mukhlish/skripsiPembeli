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

    public MakananAdapter(Context context, ArrayList<Makanan> listMakanan) {
        this.context = context;
        this.listMakanan = listMakanan;
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
                into(holder.mGambar);


        holder.mGambar.setOnClickListener(new ProdukListener(position));

        holder.mJudul.setText(makananNow.getNama());
        holder.mQty.setText(makananNow.getQty() + "");
        holder.mPrice.setText(Helper.formatter("" + makananNow.getHarga()));


        holder.mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = makananNow.getQty();
                qty++;
                makananNow.setQty(qty);
                holder.mQty.setText(qty + "");

                countEstimatedPrice();

            }
        });

        holder.mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = makananNow.getQty();

                if (qty > 1) {
                    qty--;
                    makananNow.setQty(qty);
                    holder.mQty.setText(qty + "");
                } else {
                    makananNow.setQty(0);
                    holder.mQtySet.setVisibility(View.GONE);
                    holder.mAdd.setVisibility(View.VISIBLE);
                }

                countEstimatedPrice();
            }
        });

        holder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mAdd.setVisibility(View.GONE);
                holder.mQtySet.setVisibility(View.VISIBLE);
                makananNow.setQty(1);
                holder.mQty.setText(makananNow.getQty() + "");

                countEstimatedPrice();


            }
        });

    }

    @Override
    public int getItemCount() {
        return listMakanan.size();
    }

    public class MakananViewHolder extends RecyclerView.ViewHolder {

        ImageView mGambar;
        TextView mJudul, mPrice, mQty;
        View mItemView;
        Button mPlus, mMinus, mAdd;
        RelativeLayout mQtySet;
        CardView mCard;


        MakananViewHolder(View itemView) {
            super(itemView);
            mGambar = itemView.findViewById(R.id.gambar);
            mJudul = itemView.findViewById(R.id.judul);
            mPrice = itemView.findViewById(R.id.price);
            mQty = itemView.findViewById(R.id.qty);
            mPlus = itemView.findViewById(R.id.plus);
            mMinus = itemView.findViewById(R.id.minus);
            mAdd = itemView.findViewById(R.id.btn_add_order);
            mQtySet = itemView.findViewById(R.id.qty_set);
            mCard = itemView.findViewById(R.id.card);
            mItemView = itemView;
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

            final TextView judul = rootDialog.findViewById(R.id.judul);

            TextView deskripsi = rootDialog.findViewById(R.id.deskripsi);
            TextView harga = rootDialog.findViewById(R.id.harga);
            ImageView imageView = rootDialog.findViewById(R.id.gambar);


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

            hasil += makananNow.getHarga() * makananNow.getQty();

        }

        PilihMakananActivity.updateEstimatedPrice(hasil);
        PilihMakananActivity.estimated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ConfirmActivity.class);
                intent.putParcelableArrayListExtra("pesanan", arrangePesanan(listMakanan));
                context.startActivity(intent);

            }
        });

    }

    private ArrayList<Makanan> arrangePesanan(ArrayList<Makanan> listMakanan) {

        ArrayList<Makanan> hasil = new ArrayList<>();

        for (Makanan makananNow : listMakanan) {

            if (makananNow.getQty() > 0) {

                hasil.add(makananNow) ;
            }

        }

        return hasil;
    }

}