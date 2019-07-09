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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.model.Pembeli;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PASSWORD;
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
        initPreferences();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {

            Log.v("cik","cukk");

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

        btnLogin.setOnClickListener(this);
    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Sedang Memeriksa..");
        dialog.setCancelable(false);
    }

    private void initPreferences() {
        pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String id = pref.getString(ID_PEMBELI, "");
        if (!id.equals("")) {
            Intent intent = new Intent(LoginActivity.this, PilihPedagangActivity.class);
            startActivity(intent);
        }
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
        Call<Pembeli> call = apiInterface.getUser(user, pass);
        call.enqueue(new Callback<Pembeli>() {
            @Override
            public void onResponse(Call<Pembeli> call, Response<Pembeli> response) {
                Pembeli pembeli = response.body();

                if (!pembeli.getNama().equals("Password Salah")) {
                    dialog.dismiss();
                    editor = pref.edit();
                    editor.putString(ID_PEMBELI, String.valueOf(pembeli.getIdPembeli()));
                    editor.putString(USERNAME, String.valueOf(pembeli.getUsername()));
                    editor.putString(PASSWORD, String.valueOf(pembeli.getPassword()));
                    editor.apply();

                    writePembeliToFirebase(pembeli.getIdPembeli(),pembeli.getUsername());

                    Intent intent = new Intent(LoginActivity.this, PilihPedagangActivity.class);
                    startActivity(intent);
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
                Log.v("cik",t.getMessage());
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
