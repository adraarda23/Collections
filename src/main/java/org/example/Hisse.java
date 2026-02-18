package org.example;

public class Hisse{
    String sembol;
    String sirketAdi;
    String sektor;
    double fiyat;
    double gunlukDegisimYuzdesi;
    long   hacim;

    public Hisse(String sembol, String sirketAdi, String sektor, double fiyat, double gunlukDegisimYuzdesi, long hacim) {
        this.sembol = sembol;
        this.sirketAdi = sirketAdi;
        this.sektor = sektor;
        this.fiyat = fiyat;
        this.gunlukDegisimYuzdesi = gunlukDegisimYuzdesi;
        this.hacim = hacim;
    }

    public String getSembol() {
        return sembol;
    }

    public void setSembol(String sembol) {
        this.sembol = sembol;
    }

    public String getSirketAdi() {
        return sirketAdi;
    }

    public void setSirketAdi(String sirketAdi) {
        this.sirketAdi = sirketAdi;
    }

    public String getSektor() {
        return sektor;
    }

    public void setSektor(String sektor) {
        this.sektor = sektor;
    }

    public double getFiyat() {
        return fiyat;
    }

    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }

    public double getGunlukDegisimYuzdesi() {
        return gunlukDegisimYuzdesi;
    }

    public void setGunlukDegisimYuzdesi(double gunlukDegisimYuzdesi) {
        this.gunlukDegisimYuzdesi = gunlukDegisimYuzdesi;
    }

    public long getHacim() {
        return hacim;
    }

    public void setHacim(long hacim) {
        this.hacim = hacim;
    }
}
