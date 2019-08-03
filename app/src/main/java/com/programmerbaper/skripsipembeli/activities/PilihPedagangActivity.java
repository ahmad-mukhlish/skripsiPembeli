package com.programmerbaper.skripsipembeli.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.PedagangAdapter;
import com.programmerbaper.skripsipembeli.misc.CurrentActivityContext;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.FCM_TOKEN;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PASSWORD;
import static com.programmerbaper.skripsipembeli.misc.Config.PREORDER;
import static com.programmerbaper.skripsipembeli.misc.Config.USERNAME;

public class PilihPedagangActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PedagangAdapter pedagangAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog dialog;
    private Menu menu;


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
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                logout();
            }
            case R.id.preOrder: {


                SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                if (pref.getString(PREORDER, "").equals("")) {
                    editor.putString(PREORDER, PREORDER);
                    menu.findItem(R.id.preOrder).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pesan));

                } else {
                    editor.putString(PREORDER, "");
                    menu.findItem(R.id.preOrder).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_preorder));
                }

                editor.commit();
                getPedagang();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String preOrder = pref.getString(PREORDER, "");
        if (preOrder.equals(PREORDER)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREORDER, "");
            editor.commit();
            getPedagang();
            menu.findItem(R.id.preOrder).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_preorder));

        } else {

            Toast.makeText(this, "Anda sudah di menu utama", Toast.LENGTH_SHORT).show();


        }
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
        Call<List<Pedagang>> call = apiInterface.pilihanPedagangGet();
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
        editor.putString(FCM_TOKEN, "");


        editor.commit();

        renullTokenPost();
        Intent intent = new Intent(PilihPedagangActivity.this, LoginActivity.class);
        startActivity(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("cikandes","ex");
        CurrentActivityContext.setActualContext(this);
        Log.v("cikandes",CurrentActivityContext.getActualContext().getPackageName());
    }

    @Override
    protected void onDestroy() {
        Log.v("cikandes","death");
        super.onDestroy();
        CurrentActivityContext.setActualContext(null);
    }

    private void renullTokenPost() {
//
//        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
//        String id = pref.getString(ID_PEMBELI, "");
//
//        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
//        Call<String> call =  apiInterface.renullTokenPost(Integer.parseInt(id));
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.v("cik",response.body());
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });


    }

}
