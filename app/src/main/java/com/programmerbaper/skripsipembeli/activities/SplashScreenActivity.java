package com.programmerbaper.skripsipembeli.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.model.Transaksi;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.DATA_TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.TRANSAKSI;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        initPreferences();
    }


    private void initPreferences() {

        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_PEMBELI, "");
        String transaksi = pref.getString(TRANSAKSI, "");
        if (!id.equals("") && !transaksi.equals("")) {
            getTransaksi(Integer.parseInt(transaksi));
        } else if (!id.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, PilihPedagangActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }

    }

    private void getTransaksi(final int idTransaksi) {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Transaksi> call = apiInterface.transaksiByIDGet(idTransaksi);
        call.enqueue(new Callback<Transaksi>() {
            @Override
            public void onResponse(Call<Transaksi> call, final Response<Transaksi> response) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Transaksi transaksi = response.body();
                        Intent intent = new Intent(SplashScreenActivity.this, DetailTransaksiActivity.class);
                        intent.putExtra(DATA_TRANSAKSI, transaksi);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);

            }

            @Override
            public void onFailure(Call<Transaksi> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });


    }


}
