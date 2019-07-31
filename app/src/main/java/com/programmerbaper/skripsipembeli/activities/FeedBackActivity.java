package com.programmerbaper.skripsipembeli.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.BASE_URL;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PEDAGANG;

public class FeedBackActivity extends AppCompatActivity {

    private int idTransaksi;
    private RatingBar ratingBar;
    private Pedagang pedagang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        ratingBar = findViewById(R.id.ratingnya);
        idTransaksi = Integer.parseInt(getIntent().getExtras().getString(ID_TRANSAKSI));

        pedagang = getIntent().getExtras().getParcelable(PEDAGANG);

        TextView label = findViewById(R.id.label);
        label.setText("Rating Anda untuk " + pedagang.getNama());

        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                Call<String> call = apiInterface.ratingPedagangPost(idTransaksi, Math.round(ratingBar.getRating() * 2));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("Simpan Rating Berhasil")) {
                            cekSubscribe();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });


            }
        });
    }

    private void dialogSubs() {

        AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackActivity.this);
        View rootDialog = LayoutInflater.from(FeedBackActivity.this).inflate(R.layout.dialogue_subs, null);

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final ImageView image = rootDialog.findViewById(R.id.dialogue_image);

        Glide.with(FeedBackActivity.this)
                .load(BASE_URL + "storage/pedagang-profiles/" + pedagang.getFoto())
                .placeholder(R.drawable.pedagang_holder)
                .into(image);

        TextView label = rootDialog.findViewById(R.id.dialogue_label);
        label.setText("Subscribe " + pedagang.getNama() + "?");


        TextView jenis = rootDialog.findViewById(R.id.dialogue_jenis);
        jenis.setText(pedagang.getJenis());


        Button no = rootDialog.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(FeedBackActivity.this, PilihPedagangActivity.class);
                startActivity(intent);
            }
        });

        Button ok = rootDialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveSubscribeToDb();


            }
        });


    }

    private void saveSubscribeToDb() {

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_PEMBELI, "");

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.subscribePost(Integer.parseInt(id), pedagang.getIdPedagang());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().equals("subscribe berhasil")) {
                    sendNotifSubscribe();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }


    private void sendNotifSubscribe() {

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_PEMBELI, "");

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.notifSubscribePost(Integer.parseInt(id), pedagang.getIdPedagang());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().equals("notif subscribe berhasil")) {
                    Toast.makeText(FeedBackActivity.this,"Subscribe Berhasil",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FeedBackActivity.this, PilihPedagangActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void cekSubscribe() {

        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_PEMBELI, "");

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.cekSubscribeGet(pedagang.getIdPedagang(),Integer.parseInt(id));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.v("cikkkk",response.body());
                if (response.body().equals("true")) {
                    Intent intent = new Intent(FeedBackActivity.this, PilihPedagangActivity.class);
                    startActivity(intent);
                } else {
                    dialogSubs();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
}
