package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 05-Aug-15.
 */
public class Pancawara {
    private String id, pancawara;

    public Pancawara(){}

    public Pancawara(String id, String pancawara) {
        this.id = id;
        this.pancawara = pancawara;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPancawara() {
        return pancawara;
    }

    public void setPancawara(String pancawara) {
        this.pancawara = pancawara;
    }
}
