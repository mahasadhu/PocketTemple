package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 05-Aug-15.
 */
public class PurnamaTilem {
    private String id, purnamaTilem;

    public PurnamaTilem() {
    }

    public PurnamaTilem(String id, String purnamaTilem) {
        this.id = id;
        this.purnamaTilem = purnamaTilem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurnamaTilem() {
        return purnamaTilem;
    }

    public void setPurnamaTilem(String purnamaTilem) {
        this.purnamaTilem = purnamaTilem;
    }
}
