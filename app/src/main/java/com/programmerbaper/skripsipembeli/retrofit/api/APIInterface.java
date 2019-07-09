package com.programmerbaper.skripsipembeli.retrofit.api;

import com.programmerbaper.skripsipembeli.model.Makanan;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.model.Pembeli;

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
    Call<Pembeli> getUser(@Field("username") String username, @Field("password") String password) ;

    @GET("pilihanPedagangGet")
    Call<List<Pedagang>> pilihanPedagangGet();

    @GET("makananPedagangGet/{id_pedagang}")
    Call<List<Makanan>> makananPedagangGet(@Path("id_pedagang") int idPedagang);
}
