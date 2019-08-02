package com.programmerbaper.skripsipembeli.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.PesananAdapter;
import com.programmerbaper.skripsipembeli.misc.CurrentActivityContext;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Transaksi;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.DATA_TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.FCM_TOKEN;
import static com.programmerbaper.skripsipembeli.misc.Config.ID_PEMBELI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PASSWORD;
import static com.programmerbaper.skripsipembeli.misc.Config.PREORDER;
import static com.programmerbaper.skripsipembeli.misc.Config.TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.USERNAME;

public class ConfirmActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String catatan = "";
    private String alamat = "";
    private int idPedagangTerpilih;
    private ArrayList<Makanan> pesanan;
    private double latitude;
    private double longitude;
    private int idTransaksi;
    private ProgressDialog dialog;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public static boolean permission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        if (!permission) {
            initPermission();
        }

        markPhoneToGetLatLong();

        Bundle bundle = getIntent().getExtras();
        pesanan = bundle.getParcelableArrayList("pesanan");

        idPedagangTerpilih = bundle.getInt("id_pedagang");

        PesananAdapter pesananAdapter =
                new PesananAdapter(this, pesanan);

        RecyclerView recyclerView = findViewById(R.id.rvPesanan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

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

                    dialogueAlamat();


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

        initProgressDialog();
        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String preOrder = pref.getString(PREORDER, "");
        if (preOrder.equals(PREORDER)) {
            setTitle("Konfirmasi Pesanan Pre Order");
        } else {
            setTitle("Konfirmasi Pesanan");
        }

    }


    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Mengambil Detail Transaksi");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }


    private int hitungSub(ArrayList<Makanan> listMakanan) {
        int sub = 0;
        for (Makanan makananNow : listMakanan) {
            sub += makananNow.getHarga() * makananNow.getJumlah();
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


        TextView ok = rootDialog.findViewById(R.id.konfirmasi_catatan);
        if (kode == 1) {
            ok.setText("Tambah catatan & pesan sekarang");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ConfirmActivity.this.catatan = keterangan.getText().toString();
                    dialogueAlamat();

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

    private void dialogueAlamat() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootDialog = LayoutInflater.from(this).inflate(R.layout.dialogue_lokasi, null);
        final EditText alamat = rootDialog.findViewById(R.id.alamat);
        alamat.setText(getAlamat());
        alamat.setSelection(alamat.getText().length());

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();


        TextView ok = rootDialog.findViewById(R.id.konfirmasi_alamat);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                ConfirmActivity.this.alamat = alamat.getText().toString();

                SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                String preOrder = pref.getString(PREORDER, "");
                if (preOrder.equals(PREORDER)) {
                    showDatePicker();
                } else {
                    pesanPedagangKelilingOnline(getTodayTanggal(), 0);
                }


            }
        });


    }

    private JSONArray parsePesananToJSONArray(ArrayList<Makanan> pesanan) {

        JSONArray jsonArray = new JSONArray();

        for (Makanan makananNow : pesanan) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("id_makanan", makananNow.getIdMakanan());
                jsonObject.accumulate("jumlah", makananNow.getJumlah());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

        }

        return jsonArray;
    }

    private void initPermission() {


        Log.v("cikann", "init");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        } else {
            // Permission has already been granted
            permission = true;
            Toast.makeText(this, "Izin Lokasi diberikan", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("MissingPermission")
    private void markPhoneToGetLatLong() {

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {


            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        getLastLocation(mLocationManager);


        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, mLocationListener);


    }

    private void getLastLocation(LocationManager locationManager) {
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private String getAlamat() {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses.get(0).getAddressLine(0);

    }

    private String getArea() {

        List<String> alamatList = Arrays.asList(alamat.split(",[ ]*"));

        return alamatList.get(1);

    }

    private String getTodayTanggal() {

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);

    }


    private void getTransaksi(final int idTransaksi) {

        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Transaksi> call = apiInterface.transaksiByIDGet(idTransaksi);
        call.enqueue(new Callback<Transaksi>() {
            @Override
            public void onResponse(Call<Transaksi> call, Response<Transaksi> response) {
                dialog.dismiss();
                Transaksi transaksi = response.body();

                pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                editor = pref.edit();
                editor.putString(TRANSAKSI, idTransaksi + "");
                editor.apply();

                Intent intent = new Intent(ConfirmActivity.this, DetailTransaksiActivity.class);
                intent.putExtra(DATA_TRANSAKSI, transaksi);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Transaksi> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(ConfirmActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void pesanPedagangKelilingOnline(String tanggal, int preOrderStatus) {


        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        String idPembeli = pref.getString(ID_PEMBELI, "");

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.pesanPedagangBerkelilingPost
                (Integer.parseInt(idPembeli), idPedagangTerpilih, catatan, alamat, getArea(), latitude, longitude, tanggal, parsePesananToJSONArray(pesanan), preOrderStatus);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.body() != null) {
                    idTransaksi = Integer.parseInt(response.body());

                    if (preOrderStatus == 0) {
                        notifPesanan();
                    } else {
                        Toast.makeText(ConfirmActivity.this, "Pre Order Telah Dicatat", Toast.LENGTH_SHORT).show();

                        //flush shared preferences
                        SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREORDER, "");
                        editor.commit();

                        //intent to main menu
                        ConfirmActivity.this.startActivity(new Intent(ConfirmActivity.this, PilihPedagangActivity.class));
                    }


                } else {

                    try {
                        Log.v("cik", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v("cik", t.toString());
            }
        });


    }

    private void notifPesanan() {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<String> call = apiInterface.notifPesanPost(getArea(), hitungSub(pesanan) + "", idPedagangTerpilih, idTransaksi);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().equals("Pesanan berhasil di notif")) {
                    Toast.makeText(ConfirmActivity.this, "Pesanan Telah Terkirim", Toast.LENGTH_SHORT).show();
                    getTransaksi(idTransaksi);

                } else if (response.body().equals("Pedagang tidak tersedia (Token pedagang tidak tersedia)")) {
                    Toast.makeText(ConfirmActivity.this, "Pedagang tidak tersedia (Token pedagang tidak tersedia)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrentActivityContext.setActualContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrentActivityContext.setActualContext(null);
    }

    private void showDatePicker() {

        Calendar now = Calendar.getInstance();

        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                ConfirmActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) + 1 // Inital day selection
        );
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", new Locale("ID"));
        SimpleDateFormat sql = new SimpleDateFormat("yyyy-MM-dd");
        String month;

        monthOfYear++;
        if (monthOfYear < 10)
            month = "0" + monthOfYear;
        else
            month = "" + monthOfYear;

        SimpleDateFormat sqlformat = new SimpleDateFormat("yyyyMMdd", new Locale("EN"));
        String tanggal = year + month + dayOfMonth;
        try {
            String tanggalDf = df.format(sqlformat.parse(tanggal));
            String tanggalSql = sql.format(df.parse(tanggalDf));
            Log.v("cikandesu", tanggalSql);

            Date pickan = sql.parse(tanggalSql);
            Date today = Calendar.getInstance().getTime();


            if (pickan.before(today) || pickan.equals(today)) {


                Log.v("cikandes", sql.format(today));
                Log.v("cikandes", sql.format(pickan));


                Toast.makeText(ConfirmActivity.this, "Tanggal pre order harus besok atau setelahnya", Toast.LENGTH_SHORT).show();
                showDatePicker();

            } else {

                pesanPedagangKelilingOnline(tanggalSql, 1);


            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
