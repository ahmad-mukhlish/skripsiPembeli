package com.programmerbaper.skripsipembeli.retrofit.api;

import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.model.Pembeli;
import com.programmerbaper.skripsipembeli.model.Transaksi;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
                                              @Field("listPesanan") JSONArray listPesanan,
                                              @Field("pre_order_status") int preOrderStatus);

    @GET("retrieveTokenByIDGet/{id_pembeli}")
    Call<String> retrieveTokenByIDGet(@Path("id_pembeli") int idPembeli);

    @FormUrlEncoded
    @POST("saveTokenByIDPost")
    Call<String> saveTokenByIDPost(@Field("id_pembeli") int idPembeli,
                                   @Field("fcm_token") String fcmToken);

    @FormUrlEncoded
    @POST("notifPesanPost")
    Call<String> notifPesanPost(@Field("area") String area,
                                @Field("nilai") String nilai,
                                @Field("id_pedagang") int idPedagang,
                                @Field("id_transaksi") int idTransaksi);

    @GET("transaksiByIDGet/{id_transaksi}")
    Call<Transaksi> transaksiByIDGet(@Path("id_transaksi") int idTransaksi);

    @GET("detailTransaksiGet/{id_transaksi}")
    Call<ArrayList<Makanan>> detailTransaksiGet(@Path("id_transaksi") int idTransaksi);

    @FormUrlEncoded
    @POST("ratingPedagangPost")
    Call<String> ratingPedagangPost(@Field("id_transaksi") int idTransaksi,
                                    @Field("rating") int rating);

    @GET("pedagangByIDGet/{id_pedagang}")
    Call<Pedagang> pedagangByIDGet(@Path("id_pedagang") int idPedagang);

    @FormUrlEncoded
    @POST("subscribePost")
    Call<String> subscribePost(@Field("id_pembeli") int idPembeli,
                               @Field("id_pedagang") int idPedagang);

    @FormUrlEncoded
    @POST("notifSubscribePost")
    Call<String> notifSubscribePost(@Field("id_pembeli") int idPembeli,
                                    @Field("id_pedagang") int idPedagang);


    @GET("cekSubscribeGet/{id_pedagang}/{id_pembeli}")
    Call<String> cekSubscribeGet(@Path("id_pedagang") int idPedagang,
                                 @Path("id_pembeli") int idPembeli);

    @FormUrlEncoded
    @POST("deleteTransaksiPost")
    Call<String> deleteTransaksiPost(@Field("id_transaksi") int idTransaksi);

    @FormUrlEncoded
    @POST("notifDeleteTransaksiPost")
    Call<String> notifDeleteTransaksiPost(@Field("id_pembeli") int idPembeli,
                                          @Field("id_pedagang") int idPedagang);

    @POST("registerPembeliPost")
    Call<String> registerPembeliPost(@Body RequestBody file);

}
