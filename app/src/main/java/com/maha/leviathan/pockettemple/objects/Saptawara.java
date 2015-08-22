package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 05-Aug-15.
 */
public class Saptawara {
    private String id, saptawara;

    public Saptawara() {
    }

    public Saptawara(String id, String saptawara) {
        this.id = id;
        this.saptawara = saptawara;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSaptawara() {
        return saptawara;
    }

    public void setSaptawara(String saptawara) {
        this.saptawara = saptawara;
    }
}
