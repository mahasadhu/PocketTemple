package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 05-Aug-15.
 */
public class Wuku {
    private String id, wuku;

    public Wuku() {
    }

    public Wuku(String id, String wuku) {
        this.id = id;
        this.wuku = wuku;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWuku() {
        return wuku;
    }

    public void setWuku(String wuku) {
        this.wuku = wuku;
    }
}
