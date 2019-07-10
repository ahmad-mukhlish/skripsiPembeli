package com.programmerbaper.skripsipembeli.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.PesananAdapter;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.model.Makanan;

import java.util.ArrayList;

public class ConfirmActivity extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String catatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Bundle bundle = getIntent().getExtras();
        ArrayList<Makanan> pesanan = bundle.getParcelableArrayList("pesanan");

        PesananAdapter pesananAdapter =
                new PesananAdapter(this, pesanan);

        recyclerView = findViewById(R.id.rvCart);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pesananAdapter);
        pesananAdapter.notifyDataSetChanged();

        TextView total = findViewById(R.id.total);
        total.setText(Helper.formatter("" + hitungSub(pesanan)));

        Button order = findViewById(R.id.id_btn_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!catatan.isEmpty()) {

                    //TODO POST PESANAN INTO TABLE TRANSAKSI HERE
                    //TODO START TRACK THIS DEVICE BY STREAM LOKASI AT PEMBELI FOLDER IN FIREBASE


                } else {

                    dialogueKeterangan(1);
                }

            }
        });

        Button add = findViewById(R.id.id_btn_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmActivity.super.onBackPressed();
            }
        });


        setTitle("Konfirmasi Pesanan");
    }

    private int hitungSub(ArrayList<Makanan> listMakanan) {
        int sub = 0;
        for (Makanan makananNow : listMakanan) {
            sub += makananNow.getHarga() * makananNow.getQty();
        }
        return sub;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note) {
            dialogueKeterangan(0);
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogueKeterangan(int kode) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootDialog = LayoutInflater.from(this).inflate(R.layout.dialogue_keterangan, null);
        final EditText keterangan = rootDialog.findViewById(R.id.keterangan);
        keterangan.setText(this.catatan);
        keterangan.setSelection(keterangan.getText().length());

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();


        TextView ok = rootDialog.findViewById(R.id.ok);
        if (kode == 1) {
            ok.setText("Tambah catatan & pesan sekarang");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ConfirmActivity.this.catatan = keterangan.getText().toString();
                    //TODO POST PESANAN HERE

                }
            });
        } else {
            if (!catatan.isEmpty()) {
                ok.setText(R.string.button_edit_chef);
            } else {
                ok.setText("Tambah Catatan");
            }
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ConfirmActivity.this.catatan = keterangan.getText().toString();
                }
            });
        }

    }

}
