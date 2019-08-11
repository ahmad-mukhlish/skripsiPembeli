package com.programmerbaper.skripsipembeli.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.model.Pembeli;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.BASE_URL;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;

public class ProfilActivity extends AppCompatActivity {

    private ImageView imageView;
    private MaterialEditText nama;
    private MaterialEditText telfon;
    private MaterialEditText email;
    private MaterialEditText alamat;
    private MaterialEditText username;
    private MaterialEditText password;
    private MaterialEditText confirm;
    private CheckBox checkBox;
    private Button button;
    private File file = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Registrasi");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            ImagePicker.create(this).single()
                    .toolbarImageTitle("Pilih Foto Anda")
                    .toolbarDoneButtonText("OK").start();


        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.profile);
        nama = findViewById(R.id.reg_nama);
        telfon = findViewById(R.id.reg_telfon);
        email = findViewById(R.id.reg_email);
        alamat = findViewById(R.id.reg_alamat);
        username = findViewById(R.id.reg_username);
        password = findViewById(R.id.reg_password);
        confirm = findViewById(R.id.reg_confirm);
        checkBox = findViewById(R.id.reg_check);
        button = findViewById(R.id.reg_button);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            if (!bundle.getBoolean("register")) {

                LinearLayout linearLayout = findViewById(R.id.privacy);
                linearLayout.setVisibility(View.GONE);
                button.setText("Simpan Data Akun");

                SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                String idPembeli = pref.getString(ID_PEMBELI, "");
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                Call<Pembeli> call = apiInterface.profilGet(Integer.parseInt(idPembeli));

                call.enqueue(new Callback<Pembeli>() {
                    @Override
                    public void onResponse(Call<Pembeli> call, Response<Pembeli> response) {

                        Pembeli pembeli = response.body();

                        Glide.with(ProfilActivity.this)
                                .load(BASE_URL + "storage/pembeli-profiles/" + pembeli.getFoto())
                                .placeholder(R.drawable.placeholder)
                                .into(imageView);

                        nama.setText(pembeli.getNama());
                        telfon.setText(pembeli.getNoTelp());
                        email.setText(pembeli.getEmail());
                        alamat.setText(pembeli.getAlamat());
                        username.setText(pembeli.getUsername());
                        password.setText(pembeli.getPassword());

                    }

                    @Override
                    public void onFailure(Call<Pembeli> call, Throwable t) {

                    }
                });


            }

        }

        button.setOnClickListener(view -> {

            if (nama.getText().toString().isEmpty() || telfon.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() || alamat.getText().toString().isEmpty()
                    || username.getText().toString().isEmpty() || password.getText().toString().isEmpty()
                    || confirm.getText().toString().isEmpty()) {
                Toast.makeText(ProfilActivity.this, "Data masih Ada yang kosong", Toast.LENGTH_SHORT).show();
            } else if (telfon.getText().toString().length() < 12) {
                Toast.makeText(ProfilActivity.this, "Nomor Telefon minimal 12 digit", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().length() < 8) {
                Toast.makeText(ProfilActivity.this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(confirm.getText().toString())) {
                Toast.makeText(ProfilActivity.this, "Password dan Konfirmasi belum cocok", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                Toast.makeText(ProfilActivity.this, "Email tidak valid", Toast.LENGTH_SHORT).show();
            } else if (!checkBox.isChecked()) {
                Toast.makeText(ProfilActivity.this, "Silakan setujui Terms of Service dan Privacy Policy", Toast.LENGTH_SHORT).show();
            } else {

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("username", username.getText().toString());
                builder.addFormDataPart("password", password.getText().toString());
                builder.addFormDataPart("nama", nama.getText().toString());
                builder.addFormDataPart("no_telp", telfon.getText().toString());
                builder.addFormDataPart("email", email.getText().toString());
                builder.addFormDataPart("alamat", alamat.getText().toString());


                if (file != null) {
                    try {
                        file = new Compressor(this).compressToFile(file);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Error", e.getMessage());
                    }
                    builder.addFormDataPart("foto", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                }


                MultipartBody requestBody = builder.build();
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                Call<String> call = apiInterface.registerPembeliPost(requestBody);


                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(ProfilActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        if (response.body().equals("Register berhasil")) {
                            ProfilActivity.this.startActivity(new Intent(ProfilActivity.this, LoginActivity.class));
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            file = new File(image.getPath());
            Uri uri = null;
            try {
                uri = Uri.fromFile(new Compressor(this).compressToFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageURI(uri);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
