package com.programmerbaper.skripsipembeli.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Makanan implements Parcelable {

    @SerializedName("id_makanan")
    @Expose
    private int idMakanan;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("harga")
    @Expose
    private int harga;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("id_pemilik")
    @Expose
    private int idPemilik;

    private int jumlah;

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getIdMakanan() {
        return idMakanan;
    }

    public void setIdMakanan(int idMakanan) {
        this.idMakanan = idMakanan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(int idPemilik) {
        this.idPemilik = idPemilik;
    }


    protected Makanan(Parcel in) {
        idMakanan = in.readInt();
        nama = in.readString();
        foto = in.readString();
        harga = in.readInt();
        deskripsi = in.readString();
        idPemilik = in.readInt();
        jumlah = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idMakanan);
        dest.writeString(nama);
        dest.writeString(foto);
        dest.writeInt(harga);
        dest.writeString(deskripsi);
        dest.writeInt(idPemilik);
        dest.writeInt(jumlah);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Makanan> CREATOR = new Parcelable.Creator<Makanan>() {
        @Override
        public Makanan createFromParcel(Parcel in) {
            return new Makanan(in);
        }

        @Override
        public Makanan[] newArray(int size) {
            return new Makanan[size];
        }
    };
}