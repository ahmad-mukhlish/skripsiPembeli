package com.programmerbaper.skripsipembeli.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaksi implements Parcelable {

    @SerializedName("id_transaksi")
    @Expose
    private Integer idTransaksi;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("id_pemilik")
    @Expose
    private Integer idPemilik;
    @SerializedName("id_pedagang")
    @Expose
    private Integer idPedagang;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("no_telp")
    @Expose
    private String noTelp;
    @SerializedName("catatan")
    @Expose
    private String catatan;

    public Integer getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(Integer idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(Integer idPemilik) {
        this.idPemilik = idPemilik;
    }

    public Integer getIdPedagang() {
        return idPedagang;
    }

    public void setIdPedagang(Integer idPedagang) {
        this.idPedagang = idPedagang;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }


    protected Transaksi(Parcel in) {
        idTransaksi = in.readByte() == 0x00 ? null : in.readInt();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        idPemilik = in.readByte() == 0x00 ? null : in.readInt();
        idPedagang = in.readByte() == 0x00 ? null : in.readInt();
        nama = in.readString();
        noTelp = in.readString();
        catatan = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idTransaksi == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idTransaksi);
        }
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        if (idPemilik == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idPemilik);
        }
        if (idPedagang == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idPedagang);
        }
        dest.writeString(nama);
        dest.writeString(noTelp);
        dest.writeString(catatan);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transaksi> CREATOR = new Parcelable.Creator<Transaksi>() {
        @Override
        public Transaksi createFromParcel(Parcel in) {
            return new Transaksi(in);
        }

        @Override
        public Transaksi[] newArray(int size) {
            return new Transaksi[size];
        }
    };
}