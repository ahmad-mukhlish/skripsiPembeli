package com.programmerbaper.skripsipembeli.retrofit.api;

import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.model.Pembeli;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @FormUrlEncoded
    @POST("login")
    Call<Pembeli> login(@Field("username") String username, @Field("password") String password);

    @GET("pilihanPedagangGet")
    Call<List<Pedagang>> pilihanPedagangGet();

    @GET("makananPedagangGet/{id_pedagang}")
    Call<ArrayList<Makanan>> makananPedagangGet(@Path("id_pedagang") int idPedagang);

    @FormUrlEncoded
    @POST("pesanPedagangBerkelilingPost")
    Call<String> pesanPedagangBerkelilingPost(@Field("id_pembeli") int idPembeli,
                                              @Field("id_pedagang") int idPedagang,
                                              @Field("catatan") String catatan,
                                              @Field("alamat") String alamat,
                                              @Field("area") String area,
                                              @Field("latitude") double latitude,
                                              @Field("longitude") double longitude,
                                              @Field("tanggal") String tanggal,
                                              @Field("listPesanan") JSONArray listPesanan);

    @GET("retrieveTokenByIDGet/{id_pembeli}")
    Call<String> retrieveTokenByIDGet(@Path("id_pembeli") int idPembeli);

    @FormUrlEncoded
    @POST("saveTokenByIDPost")
    Call<String> saveTokenByIDPost(@Field("id_pembeli") int idPembeli,
                                   @Field("fcm_token") String fcmToken) ;

    @FormUrlEncoded
    @POST("notifPesan")
    Call<String> notifPesan(@Field("area") String area,
                            @Field("nilai") String nilai,
                            @Field("id_pedagang") int idPedagang);

}
