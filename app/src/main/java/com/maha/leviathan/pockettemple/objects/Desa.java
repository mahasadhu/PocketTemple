package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 7/1/2015.
 */
public class Desa {
    private String idDesa, desa;

    public Desa(){}

    public Desa(String idDesa, String desa){
        this.idDesa = idDesa;
        this.desa = desa;
    }

    public String getIdDesa() {
        return idDesa;
    }

    public void setIdDesa(String idDesa) {
        this.idDesa = idDesa;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }
}
