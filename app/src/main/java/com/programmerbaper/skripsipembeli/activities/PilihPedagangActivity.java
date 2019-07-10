package com.programmerbaper.skripsipembeli.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.PedagangAdapter;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PASSWORD;
import static com.programmerbaper.skripsipembeli.misc.Config.USERNAME;

public class PilihPedagangActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PedagangAdapter pedagangAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_pedagang);

        initProgressDialog();
        recyclerView = findViewById(R.id.rvPedagang);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getPedagang();
        setTitle("Pilihan Pedagang");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pilih_pedagang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                logout();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Anda sudah di menu utama", Toast.LENGTH_SHORT).show();
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Pedagang");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    private void getPedagang() {
        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<List<Pedagang>> call = apiInterface.pilihanPedagangGet() ;
        call.enqueue(new Callback<List<Pedagang>>() {
            @Override
            public void onResponse(Call<List<Pedagang>> call, Response<List<Pedagang>> response) {
                dialog.dismiss();
                List<Pedagang> list = response.body();

                pedagangAdapter = new PedagangAdapter(getApplicationContext(), list, PilihPedagangActivity.this);
                recyclerView.setAdapter(pedagangAdapter);
                pedagangAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Pedagang>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(PilihPedagangActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {

        //flush shared preferences
        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        editor.putString(ID_PEMBELI, "");
        editor.putString(USERNAME, "");
        editor.putString(PASSWORD, "");

        editor.commit();

        Intent intent = new Intent(PilihPedagangActivity.this, LoginActivity.class);
        startActivity(intent);


    }

}
