package com.programmerbaper.skripsipembeli.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.MakananAdapter;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.DATA_PEDAGANG_TERPILIH;

public class PilihMakananActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private MakananAdapter makananAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog;
    private Pedagang pedagangTerpilih ;

    private static ArrayList<Makanan> listMakanan;
    public static Button estimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_makanan);

        initProgressDialog();
        recyclerView = findViewById(R.id.rvMakanan);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
        pedagangTerpilih = bundle.getParcelable(DATA_PEDAGANG_TERPILIH);

        estimated = findViewById(R.id.estimate);


        getMakanan();
        setTitle("Pilihan Dagangan");

    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Dagangan");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    private void getMakanan() {
        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<ArrayList<Makanan>> call = apiInterface.makananPedagangGet(pedagangTerpilih.getIdPedagang()) ;
        call.enqueue(new Callback<ArrayList<Makanan>>() {
            @Override
            public void onResponse(Call<ArrayList<Makanan>> call, Response<ArrayList<Makanan>> response) {
                dialog.dismiss();
                ArrayList<Makanan> listMakanan = response.body();

                makananAdapter = new MakananAdapter(PilihMakananActivity.this, listMakanan);
                recyclerView.setAdapter(makananAdapter);
                makananAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ArrayList<Makanan>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(PilihMakananActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void updateEstimatedPrice(Long estimated) {



        if (estimated == 0) {
            PilihMakananActivity.estimated.setVisibility(View.GONE);
        } else {
            PilihMakananActivity.estimated.setVisibility(View.VISIBLE);
            PilihMakananActivity.estimated.setText("Perkiraan Harga\t : \t" + Helper.formatter(estimated + ""));

        }

    }


}
