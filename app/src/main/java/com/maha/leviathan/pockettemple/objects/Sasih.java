package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 05-Aug-15.
 */
public class Sasih {
    private String id, sasih;

    public Sasih() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSasih() {
        return sasih;
    }

    public void setSasih(String sasih) {
        this.sasih = sasih;
    }

    public Sasih(String id, String sasih) {

        this.id = id;
        this.sasih = sasih;
    }
}
