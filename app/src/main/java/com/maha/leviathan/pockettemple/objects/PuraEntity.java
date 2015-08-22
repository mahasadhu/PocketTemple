package com.maha.leviathan.pockettemple.objects;

/**
 * Created by Leviathan on 6/3/2015.
 */
public class PuraEntity {

    private String nama;
    private String definisi;
    private String tambahan;
    private String id;
    private String separator;

    public PuraEntity(String nama, String definisi, String tambahan, String id, String separator) {
        this.nama = nama;
        this.definisi = definisi;
        this.tambahan = tambahan;
        this.id = id;
        this.separator = separator;
    }

    public PuraEntity(){

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDefinisi() {
        return definisi;
    }

    public void setDefinisi(String definisi) {
        this.definisi = definisi;
    }

    public String getTambahan() {
        return tambahan;
    }

    public void setTambahan(String tambahan) {
        this.tambahan = tambahan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
