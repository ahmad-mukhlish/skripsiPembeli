package com.programmerbaper.skripsipembeli.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pedagang implements Parcelable {

    @SerializedName("id_pedagang")
    @Expose
    private int idPedagang;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("no_telp")
    @Expose
    private String noTelp;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("jenis")
    @Expose
    private String jenis;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("id_pemilik")
    @Expose
    private int idPemilik;

    public int getIdPedagang() {
        return idPedagang;
    }

    public void setIdPedagang(int idPedagang) {
        this.idPedagang = idPedagang;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(int idPemilik) {
        this.idPemilik = idPemilik;
    }


    protected Pedagang(Parcel in) {
        idPedagang = in.readInt();
        nama = in.readString();
        alamat = in.readString();
        noTelp = in.readString();
        username = in.readString();
        password = in.readString();
        email = in.readString();
        jenis = in.readString();
        foto = in.readString();
        idPemilik = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPedagang);
        dest.writeString(nama);
        dest.writeString(alamat);
        dest.writeString(noTelp);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(jenis);
        dest.writeString(foto);
        dest.writeInt(idPemilik);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Pedagang> CREATOR = new Parcelable.Creator<Pedagang>() {
        @Override
        public Pedagang createFromParcel(Parcel in) {
            return new Pedagang(in);
        }

        @Override
        public Pedagang[] newArray(int size) {
            return new Pedagang[size];
        }
    };
}