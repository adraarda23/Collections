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

}
