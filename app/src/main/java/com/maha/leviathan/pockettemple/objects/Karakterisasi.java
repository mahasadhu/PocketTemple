package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 7/1/2015.
 */
public class Karakterisasi {
    private String idKarakterisasi, karakterisasi;

    public Karakterisasi(){}

    public Karakterisasi(String idKarakterisasi, String karakterisasi){
        this.idKarakterisasi = idKarakterisasi;
        this.karakterisasi = idKarakterisasi;
    }

    public String getIdKarakterisasi() {
        return idKarakterisasi;
    }

    public void setIdKarakterisasi(String idKarakterisasi) {
        this.idKarakterisasi = idKarakterisasi;
    }

    public String getKarakterisasi() {
        return karakterisasi;
    }

    public void setKarakterisasi(String karakterisasi) {
        this.karakterisasi = karakterisasi;
    }
}
