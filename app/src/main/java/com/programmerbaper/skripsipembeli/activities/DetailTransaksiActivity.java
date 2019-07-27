package com.programmerbaper.skripsipembeli.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.adapter.PesananAdapter;
import com.programmerbaper.skripsipembeli.misc.CurrentActivityContext;
import com.programmerbaper.skripsipembeli.misc.Helper;
import com.programmerbaper.skripsipembeli.misc.directionhelpers.FetchURL;
import com.programmerbaper.skripsipembeli.misc.directionhelpers.TaskLoadedCallback;
import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Transaksi;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.DATA_TRANSAKSI;

public class DetailTransaksiActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private Transaksi transaksi;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private PesananAdapter pesananAdapter;
    private LinearLayoutManager layoutManager;

    private GoogleMap googleMap;
    private Marker marker;
    private double latitude;
    private double longitude;
    private LatLng posPembeli;
    private LatLng posPedagang;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        initProgressDialog();
        recyclerView = findViewById(R.id.rvDetail);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
        transaksi = bundle.getParcelable(DATA_TRANSAKSI);

        //set list of pesanan to be scrollable
        ((SlidingUpPanelLayout) findViewById(R.id.sliding_layout))
                .setScrollableView(findViewById(R.id.scrollView));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dialog.show();
        getDetail();
    }

    private void initProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Daftar Detail Pesanan");
        dialog.setMessage("Sedang Memuat..");
        dialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_transaksi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note) {
            dialogueKeterangan();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogueKeterangan() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootDialog = LayoutInflater.from(this).inflate(R.layout.dialogue_read_keterangan, null);
        TextView keterangan = rootDialog.findViewById(R.id.keterangan);

        if (transaksi.getCatatan() == null) {
            keterangan.setText("Catatan Kosong");
        } else {
            keterangan.setText(transaksi.getCatatan());
        }

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();


        TextView ok = rootDialog.findViewById(R.id.konfirmasi_catatan);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


    private void getDetail() {

        dialog.show();
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<ArrayList<Makanan>> call = apiInterface.detailTransaksiGet(transaksi.getIdTransaksi());
        call.enqueue(new Callback<ArrayList<Makanan>>() {
            @Override
            public void onResponse(Call<ArrayList<Makanan>> call, Response<ArrayList<Makanan>> response) {
                dialog.dismiss();
                ArrayList<Makanan> listMakanan = response.body();

                pesananAdapter = new PesananAdapter(DetailTransaksiActivity.this, listMakanan);
                recyclerView.setAdapter(pesananAdapter);
                pesananAdapter.notifyDataSetChanged();

                Button done = findViewById(R.id.done);
                done.setText("Transaksi Selesai (" + Helper.formatter(hitungSub(listMakanan) + "") + ")");


            }

            @Override
            public void onFailure(Call<ArrayList<Makanan>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(DetailTransaksiActivity.this, "Terjadi Kesalahan Tidak Terduga", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(10, 180, 10, 10);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        dialog.show();
        getPedagangLocation();

        posPembeli = new LatLng(transaksi.getLatitude(), transaksi.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(posPembeli).title("Posisi Anda"));
    }


    private void getPedagangLocation() {


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("pemilik")
                .child("pmk" + transaksi.getIdPemilik()).child("lokasi").child("pdg" + transaksi.getIdPedagang());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double latPedagang = (Double) dataSnapshot.child("latitude").getValue();
                double longPedagang = (Double) dataSnapshot.child("longitude").getValue();

                Log.v("cikk", latPedagang + "");
                Log.v("cikk", longPedagang + "");


                posPedagang = new LatLng(latPedagang, longPedagang);

                if (marker != null) {
                    marker.remove();
                    marker = googleMap.addMarker(new MarkerOptions().position(posPedagang).title("Posisi Pedagang").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posPedagang, 15.0f));

                } else {
                    marker = googleMap.addMarker(new MarkerOptions().position(posPedagang).title("Posisi Pedagang").
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gerobag)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posPedagang, 15.0f));
                }


                new FetchURL(DetailTransaksiActivity.this)
                        .execute(getRouteUrl(posPembeli, posPedagang, "walking"), "walking");

                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("cik", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        root.addValueEventListener(postListener);

    }

    private String getRouteUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = googleMap.addPolyline((PolylineOptions) values[0]);
        currentPolyline.setColor(R.color.colorPrimaryDark);
    }


    private int hitungSub(ArrayList<Makanan> listMakanan) {
        int sub = 0;
        for (Makanan makananNow : listMakanan) {
            sub += makananNow.getHarga() * makananNow.getJumlah();
        }
        return sub;
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
}
