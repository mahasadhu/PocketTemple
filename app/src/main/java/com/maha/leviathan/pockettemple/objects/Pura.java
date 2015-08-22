package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 5/2/2015.
 */
public class Pura {
    private String namaPura;
    private String alamatPura;
    private String desaPura;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    private double lat, lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public Pura(){

    }

    public Pura(String namaPura, String alamatPura, String desaPura){
        this.alamatPura = alamatPura;
        this.desaPura = desaPura;
        this.namaPura = namaPura;
    }

    public String getNamaPura() {
        return namaPura;
    }

    public void setNamaPura(String namaPura) {
        this.namaPura = namaPura;
    }

    public String getAlamatPura() {
        return alamatPura;
    }

    public void setAlamatPura(String alamatPura) {
        this.alamatPura = alamatPura;
    }

    public String getDesaPura() {
        return desaPura;
    }

    public void setDesaPura(String desaPura) {
        this.desaPura = desaPura;
    }
}
