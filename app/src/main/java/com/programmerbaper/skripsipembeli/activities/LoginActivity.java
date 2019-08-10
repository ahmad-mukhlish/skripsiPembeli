package com.programmerbaper.skripsipembeli.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.model.Pembeli;
import com.programmerbaper.skripsipembeli.model.Transaksi;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.DATA_TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.FCM_TOKEN;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PASSWORD;
import static com.programmerbaper.skripsipembeli.misc.Config.TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.USERNAME;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private ProgressDialog dialog;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        bind();
        initProgressDialog();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("") && pass.equals("")) {
                Toast.makeText(LoginActivity.this, "Username dan Password Harus Diisi", Toast.LENGTH_SHORT).show();
            } else {
                requestLogin(user, pass);
            }

        }
    }

    private void bind() {

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView daftar = findViewById(R.id.daftar);
        daftar.setOnClickListener(view -> {

            startActivity(new Intent(this,RegisterActivity.class));

        });

        btnLogin.setOnClickListener(this);
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Sedang Memeriksa..");
        dialog.setCancelable(false);
    }




    private void writePembeliToFirebase(int idPembeli, String username) {

        DatabaseReference root = FirebaseDatabase.getInstance().getReference()
                .child("pembeli").child("pbl" + idPembeli
                );

        root.child("username").setValue(username);

    }

    private void requestLogin(String user, String pass) {

        dialog.show();

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<Pembeli> call = apiInterface.login(user, pass);
        call.enqueue(new Callback<Pembeli>() {
            @Override
            public void onResponse(Call<Pembeli> call, Response<Pembeli> response) {
                Pembeli pembeli = response.body();

                if (!pembeli.getNama().equals("Password Salah")) {
                    dialog.dismiss();
                    pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putString(ID_PEMBELI, String.valueOf(pembeli.getIdPembeli()));
                    editor.putString(USERNAME, String.valueOf(pembeli.getUsername()));
                    editor.putString(PASSWORD, String.valueOf(pembeli.getPassword()));
                    editor.apply();

                    tokenize();

                    writePembeliToFirebase(pembeli.getIdPembeli(), pembeli.getUsername());

                    String transaksi = pref.getString(TRANSAKSI, "");

                    if (!transaksi.equals("")) {
                        getTransaksi(Integer.parseInt(transaksi));
                    } else {
                        Intent intent = new Intent(LoginActivity.this, PilihPedagangActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pembeli> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Log.v("cik", t.getMessage());
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tokenize() {

        //Check wether token exist or not at shared pref and dbase
        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        if (pref.getString(FCM_TOKEN, "").isEmpty()) {

            Log.v("cik", "empty mang");
            APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
            Call<String> call = apiInterface.retrieveTokenByIDGet(Integer.parseInt(pref.getString(ID_PEMBELI, "")));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.body().isEmpty()) {

                        getTokenFromFcm();

                    } else {
                        editor = pref.edit();
                        editor.putString(FCM_TOKEN, response.body());
                        editor.apply();

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    dialog.dismiss();
                    t.printStackTrace();
                    Log.v("cik", t.getMessage());
                    Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga Pada FCM", Toast.LENGTH_SHORT).show();

                }
            });


        }

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }

    private void getTokenFromFcm() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();

                editor = pref.edit();
                editor.putString(FCM_TOKEN, token);
                editor.apply();

                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                Call<String> call = apiInterface.saveTokenByIDPost(Integer.parseInt(pref.getString(ID_PEMBELI, "")), token);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.v("cik", response.body());

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.v("cik", t.getMessage());
                    }
                });


            }
        });
    }


    private void getTransaksi(final int idTransaksi) {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Transaksi> call = apiInterface.transaksiByIDGet(idTransaksi);
        call.enqueue(new Callback<Transaksi>() {
            @Override
            public void onResponse(Call<Transaksi> call, Response<Transaksi> response) {
                Transaksi transaksi = response.body();
                Intent intent = new Intent(LoginActivity.this, DetailTransaksiActivity.class);
                intent.putExtra(DATA_TRANSAKSI, transaksi);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Transaksi> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
